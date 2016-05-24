package pl.mpak.orbada.firebird.util;

import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.gui.comps.OrbadaJavaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SourceCreator {

  public final static int FBFT_SMALLINT         =   7;
  public final static int FBFT_INTEGER          =   8;
  public final static int FBFT_QUAD             =   9;
  public final static int FBFT_FLOAT            =  10;
  public final static int FBFT_D_FLOAT          =  11;
  public final static int FBFT_SQL_DATE         =  12;
  public final static int FBFT_SQL_TIME         =  13;
  public final static int FBFT_FCHAR            =  14;
  public final static int FBFT_FCHAR2           =  15;
  public final static int FBFT_INT64            =  16;
  public final static int FBFT_DOUBLE_PRECISION =  27;
  public final static int FBFT_TIMESTAMP        =  35;
  public final static int FBFT_VARCHAR          =  37;
  public final static int FBFT_VARCHAR2         =  38;
  public final static int FBFT_CSTRING          =  40;
  public final static int FBFT_CSTRING2         =  41;
  public final static int FBFT_BLOB_ID          =  45;
  public final static int FBFT_BLOB             = 261;
  public final static int TRIGGER_BEFORE        =   0;
  public final static int TRIGGER_AFTER         =   1;
  public final static int TRIGGER_INSERT        =   1;
  public final static int TRIGGER_UPDATE        =   2;
  public final static int TRIGGER_DELETE        =   3;
  public final static int TRIGGER_CONNECT              = 8192;
  public final static int TRIGGER_DISCONNECT           = 8193;
  public final static int TRIGGER_TRANSACTION_START    = 8194;
  public final static int TRIGGER_TRANSACTION_COMMIT   = 8195;
  public final static int TRIGGER_TRANSACTION_ROLLBACK = 8196;

  private Database database;
  private SyntaxTextArea textArea;
  
  public SourceCreator(Database database) {
    this.database = database;
  }
  
  public SourceCreator(Database database, SyntaxTextArea textArea) {
    this(database);
    this.textArea = textArea;
  }
  
  /**
   * <p>Pozwala pobraæ Ÿród³o PL/SQL lub DLL i jeœli zosta³ ustawiony textArea to aktualizuje go
   * @param schemaName
   * @param objectType
   * @param objectName
   * @return pobrane Ÿród³o
   */
  public String getSource(final String schemaName, final String objectType, final String objectName) {
    final String source;
    if (StringUtil.equals(objectType, "TRIGGER")) {
      source = createTrigger(objectName).replace((char)13, (char)10);
    }
    else if (StringUtil.equals(objectType, "VIEW")) {
      source = createView(objectName);
    }
    else if (StringUtil.equals(objectType, "PROCEDURE")) {
      source = createProcedure(objectName).replace((char)13, (char)10);
    }
    else if (StringUtil.equals(objectType, "FUNCTION")) {
      source = createFunction(objectName);
    }
    else if (StringUtil.equals(objectType, "TABLE")) {
      source = createTable(objectName);
    }
    else {
      source = "";
    }
    if (textArea != null) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          if (textArea instanceof OrbadaSyntaxTextArea) {
            ((OrbadaSyntaxTextArea)textArea).setDatabaseObject(schemaName, objectType, objectName, source);
          }
          else if (textArea instanceof OrbadaJavaSyntaxTextArea) {
            ((OrbadaJavaSyntaxTextArea)textArea).setDatabaseObject(schemaName, objectType, objectName, source);
          }
          textArea.getEditorArea().setCaretPosition(0);
        }
      });
    }
    return source;
  }
  
  private static String decodeCommandType(int trgType) {
    switch (trgType) {
      case TRIGGER_INSERT: return "INSERT";
      case TRIGGER_UPDATE: return "UPDATE";
      case TRIGGER_DELETE: return "DELETE";
      case TRIGGER_CONNECT: return "CONNECT";
      case TRIGGER_DISCONNECT: return "DISCONNECT";
      case TRIGGER_TRANSACTION_START: return "TRANSACTION START";
      case TRIGGER_TRANSACTION_COMMIT: return "TRANSACTION COMMIT";
      case TRIGGER_TRANSACTION_ROLLBACK: return "TRANSACTION ROLLBACK";
    }
    return null;
  }
  
  public static String decodeTriggerType(String trgType, int trgTypeValue) {
    if (!StringUtil.isEmpty(trgType)) {
      return trgType;
    }
    StringBuilder result = new StringBuilder();
    String tmp;
    if (trgTypeValue >= TRIGGER_CONNECT && trgTypeValue <= TRIGGER_TRANSACTION_ROLLBACK && (tmp = decodeCommandType(trgTypeValue)) != null) {
      result.append(tmp);
    }
    else {
      trgTypeValue += 1;
      if ((tmp = decodeCommandType((trgTypeValue & 96) >> 5)) != null) {
        result.append(tmp);
      }
      if ((tmp = decodeCommandType((trgTypeValue & 24) >> 3)) != null) {
        if (result.length() > 0) {
          result.append(" OR ");
        }
        result.append(tmp);
      }
      if ((tmp = decodeCommandType((trgTypeValue & 6) >> 1)) != null) {
        if (result.length() > 0) {
          result.append(" OR ");
        }
        result.append(tmp);
      }
      switch (trgTypeValue & 1) {
        case TRIGGER_BEFORE: result.insert(0, "BEFORE "); break;
        case TRIGGER_AFTER: result.insert(0, "AFTER "); break;
      }
    }
    return result.toString();
  }

  private String createTrigger(String objectName) {
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getTriggerSource());
      source.paramByName("TRIGGER_NAME").setString(objectName);
      source.open();
      StringBuilder sb = new StringBuilder();
      if (!source.eof()) {
        sb.append("CREATE OR ALTER TRIGGER " +SQLUtil.createSqlName(objectName, database) +
          (source.fieldByName("RDB$TRIGGER_TYPE").getInteger() >= TRIGGER_CONNECT && source.fieldByName("RDB$TRIGGER_TYPE").getInteger() <= TRIGGER_TRANSACTION_ROLLBACK ? " ON " : " FOR ") +
          SQLUtil.createSqlName(source.fieldByName("TABLE_NAME").getString(), database) +"\n");
        if (!StringUtil.toBoolean(source.fieldByName("TRIGGER_ACTIVE").getString())) {
          sb.append("  INACTIVE\n");
        }
        sb.append("  " +
          decodeTriggerType(
            null, 
            source.fieldByName("RDB$TRIGGER_TYPE").getInteger()) +
          " POSITION " +source.fieldByName("TRIGGER_SEQUENCE").getString() +"\n");
        sb.append(source.fieldByName("TRIGGER_SOURCE").getString());
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      source.close();
    }
    return null;
  }
  
  private String createView(String objectName) {
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getViewOnlyColumnList());
      source.paramByName("VIEW_NAME").setString(objectName);
      source.open();
      StringBuilder columns = new StringBuilder();
      while (!source.eof()) {
        if (columns.length() > 0) {
          columns.append(", ");
        }
        columns.append("\"" +source.fieldByName("FIELD_NAME").getString() +"\"");
        source.next();
      }
      
      source.setSqlText(Sql.getViewSource());
      source.paramByName("VIEW_NAME").setString(objectName);
      source.open();
      StringBuilder sb = new StringBuilder();
      if (!source.eof()) {
        sb.append("DROP VIEW " +SQLUtil.createSqlName(objectName, database) +"\n/\n");
        sb.append("CREATE VIEW " +SQLUtil.createSqlName(objectName, database) +"\n");
        sb.append("  (" +columns.toString() +") AS ");
        sb.append(source.fieldByName("VIEW_SOURCE").getString());
        sb.append("\n/\n");
      }
      source.setSqlText(Sql.getRelationDescription());
      source.paramByName("RELATION_NAME").setString(objectName);
      source.open();
      if (!source.eof()) {
        sb.append("UPDATE RDB$RELATIONS SET RDB$DESCRIPTION = '" +source.fieldByName("DESCRIPTION").getString() +"' WHERE RDB$RELATION_NAME = '" +objectName +"'\n/\n");
      }
      source.setSqlText(Sql.getRelationColumnDescriptionList());
      source.paramByName("RELATION_NAME").setString(objectName);
      source.open();
      while (!source.eof()) {
        sb.append("UPDATE RDB$RELATION_FIELDS SET RDB$DESCRIPTION = '" +source.fieldByName("DESCRIPTION").getString() +"' WHERE RDB$FIELD_NAME = '" +source.fieldByName("FIELD_NAME").getString() +"' AND RDB$RELATION_NAME = '" +objectName +"'\n/\n");
        source.next();
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      source.close();
    }
    return null;
  }

  private String createTable(String objectName) {
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getColumnList(null));
      source.paramByName("TABLE_NAME").setString(objectName);
      source.open();
      StringBuilder columns = new StringBuilder();
      while (!source.eof()) {
        if (columns.length() > 0) {
          columns.append(",");
        }
        columns.append("\n  " +SQLUtil.createSqlName(source.fieldByName("FIELD_NAME").getString(), database));
        if (source.fieldByName("DOMAIN_NAME").getString().startsWith("RDB$")) {
          if (!source.fieldByName("COMPUTED_SOURCE").isNull()) {
            columns.append(" COMPUTED " +source.fieldByName("COMPUTED_SOURCE").getString());
          }
          else {
            columns.append(" " +source.fieldByName("DISPLAY_TYPE").getString());
            if (!source.fieldByName("DEFAULT_SOURCE").isNull()) {
              columns.append(" " +source.fieldByName("DEFAULT_SOURCE").getString());
            }
            if ("N".equalsIgnoreCase(source.fieldByName("NULL_FLAG").getString())) {
              columns.append(" NOT NULL");
            }
          }
        }
        else {
          columns.append(" " +source.fieldByName("DOMAIN_NAME").getString());
        }

        source.next();
      }

      StringBuilder sb = new StringBuilder();
      sb.append("CREATE TABLE " +SQLUtil.createSqlName(objectName, database) +" (");
      sb.append(columns.toString());
      sb.append("\n)\n/\n");

      source.setSqlText(Sql.getRelationDescription());
      source.paramByName("RELATION_NAME").setString(objectName);
      source.open();
      if (!source.eof()) {
        sb.append("UPDATE RDB$RELATIONS SET RDB$DESCRIPTION = '" +source.fieldByName("DESCRIPTION").getString() +"' WHERE RDB$RELATION_NAME = '" +objectName +"'\n/\n");
      }
      source.setSqlText(Sql.getRelationColumnDescriptionList());
      source.paramByName("RELATION_NAME").setString(objectName);
      source.open();
      while (!source.eof()) {
        sb.append("UPDATE RDB$RELATION_FIELDS SET RDB$DESCRIPTION = '" +source.fieldByName("DESCRIPTION").getString() +"' WHERE RDB$FIELD_NAME = '" +source.fieldByName("FIELD_NAME").getString() +"' AND RDB$RELATION_NAME = '" +objectName +"'\n/\n");
        source.next();
      }

      source.setSqlText(Sql.getIndexList(null));
      source.paramByName("TABLE_NAME").setString(objectName);
      source.open();
      while (!source.eof()) {
        if (!source.fieldByName("INDEX_NAME").getString().startsWith("RDB$")) {
          sb.append("CREATE");
          if ("Y".equalsIgnoreCase(source.fieldByName("UNIQUINES").getString())) {
            sb.append(" UNIQUE");
          }
          if ("DESC".equalsIgnoreCase(source.fieldByName("ORDERING").getString())) {
            sb.append(" DESC");
          }
          sb.append(" INDEX");
          sb.append(" " +SQLUtil.createSqlName(source.fieldByName("INDEX_NAME").getString(), database));
          sb.append(" ON " +SQLUtil.createSqlName(objectName, database));
          if (!source.fieldByName("EXPRESSION_SOURCE").isNull()) {
            sb.append(" COMPUTED BY " +source.fieldByName("EXPRESSION_SOURCE").getString());
          }
          else {
            sb.append(" (" +source.fieldByName("COLUMNS").getString() +")");
          }
          sb.append("\n/\n");
        }
        source.next();
      }

      source.setSqlText(Sql.getConstraintList(null));
      source.paramByName("TABLE_NAME").setString(objectName);
      source.open();
      while (!source.eof()) {
        if (source.fieldByName("CONSTRAINT_TYPE").getString().equalsIgnoreCase("CHECK")) {
          sb.append("ALTER TABLE " +SQLUtil.createSqlName(objectName, database) +
            " ADD CONSTRAINT " +SQLUtil.createSqlName(source.fieldByName("CONSTRAINT_NAME").getString(), database) +
            " " +source.fieldByName("CHECK_CLAUSE").getString());
          sb.append("\n/\n");
        }
        else if (source.fieldByName("CONSTRAINT_TYPE").getString().equalsIgnoreCase("PRIMARY KEY")) {
          sb.append("ALTER TABLE " +SQLUtil.createSqlName(objectName, database) +
            " ADD CONSTRAINT " +SQLUtil.createSqlName(source.fieldByName("CONSTRAINT_NAME").getString(), database) +
            " PRIMARY KEY (" +source.fieldByName("COLUMNS").getString() +")");
          sb.append("\n/\n");
        }
        source.next();
      }

      source.setSqlText(Sql.getTableReferencesWithList(null));
      source.paramByName("TABLE_NAME").setString(objectName);
      source.open();
      while (!source.eof()) {
          sb.append(
            "ALTER TABLE " +SQLUtil.createSqlName(objectName, database) +
            " ADD CONSTRAINT " +SQLUtil.createSqlName(source.fieldByName("CONSTRAINT_NAME").getString(), database) +
            " FOREIGN KEY (" +source.fieldByName("COLUMNS").getString() +")" +
            " REFERENCES " +SQLUtil.createSqlName(source.fieldByName("REF_TABLE_NAME").getString(), database) +" (" +source.fieldByName("REF_COLUMNS").getString() +")"
            );
        sb.append("\n/\n");
        source.next();
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      source.close();
    }
    return null;
  }

  private String createProcedure(String objectName) {
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getProcedureSimpleArgumentList());
      source.paramByName("PROCEDURE_NAME").setString(objectName);
      source.paramByName("PARAMETER_TYPE").setInteger(0);
      source.open();
      String lastComment = null;
      StringBuilder paramIn = new StringBuilder();
      while (!source.eof()) {
        if (paramIn.length() > 0) {
          paramIn.append("," +(lastComment != null ? lastComment : "") +"\n");
        }
        lastComment = (source.fieldByName("DESCRIPTION").isNull() ? null : " -- " +source.fieldByName("DESCRIPTION").getString());
        paramIn.append("  " +
          source.fieldByName("PARAMETER_NAME").getString() +" " +
          source.fieldByName("DISPLAY_TYPE").getString() +
          (source.fieldByName("DEFAULT_SOURCE").isNull() ? "" : " " +source.fieldByName("DEFAULT_SOURCE").getString()));
        source.next();
      }
      if (lastComment != null) {
        paramIn.append(lastComment);
      }
      
      source.close();
      source.paramByName("PROCEDURE_NAME").setString(objectName);
      source.paramByName("PARAMETER_TYPE").setInteger(1);
      source.open();
      StringBuilder paramOut = new StringBuilder();
      while (!source.eof()) {
        if (paramOut.length() > 0) {
          paramOut.append("," +(lastComment != null ? lastComment : "") +"\n");
        }
        lastComment = (source.fieldByName("DESCRIPTION").isNull() ? null : " -- " +source.fieldByName("DESCRIPTION").getString());
        paramOut.append("  " +
          source.fieldByName("PARAMETER_NAME").getString() +" " +
          source.fieldByName("DISPLAY_TYPE").getString() +
          (source.fieldByName("DEFAULT_SOURCE").isNull() ? "" : " " +source.fieldByName("DEFAULT_SOURCE").getString()));
        source.next();
      }
      if (lastComment != null) {
        paramOut.append(lastComment);
      }
      
      source.setSqlText(Sql.getProcedureSource());
      source.paramByName("PROCEDURE_NAME").setString(objectName);
      source.open();
      StringBuilder sb = new StringBuilder();
      if (!source.eof()) {
        sb.append("CREATE OR ALTER PROCEDURE " +SQLUtil.createSqlName(objectName, database));
        if (paramIn.length() > 0) {
          sb.append(" (\n" +paramIn.toString() +"\n)");
        }
        if (paramOut.length() > 0) {
          sb.append(" RETURNS (\n" +paramOut.toString() +"\n)");
        }
        sb.append(" AS\n" +source.fieldByName("PROCEDURE_SOURCE").getString());
        sb.append("\n");
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      source.close();
    }
    return null;
  }
  
  private String createFunction(String objectName) {
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getFunctionSimpleArgumentList());
      source.paramByName("FUNCTION_NAME").setString(objectName);
      source.open();
      StringBuilder paramIn = new StringBuilder();
      StringBuilder paramOut = new StringBuilder();
      while (!source.eof()) {
        if (source.fieldByName("ARGUMENT_POSITION").getInteger() == 0) {
          paramOut.append(source.fieldByName("DISPLAY_TYPE").getString());
          if (source.fieldByName("FIELD_TYPE").getInteger() == FBFT_CSTRING) {
            paramOut.append(" FREE_IT");
          }
          else {
            paramOut.append(" BY VALUE");
          }
        }
        else {
          if (paramIn.length() > 0) {
            paramIn.append(", ");
          }
          paramIn.append(source.fieldByName("DISPLAY_TYPE").getString());
        }
        source.next();
      }
      source.close();
      source.setSqlText(Sql.getFunctionSource());
      source.paramByName("FUNCTION_NAME").setString(objectName);
      source.open();
      StringBuilder sb = new StringBuilder();
      if (!source.eof()) {
        sb.append("DECLARE EXTERNAL FUNCTION " +objectName);
        if (paramIn.length() > 0) {
          sb.append("\n  " +paramIn.toString());
        }
        if (paramOut.length() > 0) {
          sb.append("\n  RETURNS " +paramOut.toString());
        }
        sb.append("\n  ENTRY_POINT '" +source.fieldByName("ENTRYPOINT").getString() +"' MODULE_NAME '" +source.fieldByName("LIB_NAME").getString() +"'");
        sb.append("\n");
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      source.close();
    }
    return null;
  }
  
}
