/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.util;

import orbada.core.Application;
import orbada.gui.comps.OrbadaJavaSyntaxTextArea;
import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.settings.SourceCreatorSettingsPanel;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Command;
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

  private ISettings settings;
  private Database database;
  private SyntaxTextArea textArea;
  
  public SourceCreator(Database database) {
    this.database = database;
  }
  
  public SourceCreator(Database database, SyntaxTextArea textArea) {
    this(database);
    this.textArea = textArea;
    settings = Application.get().getSettings(database.getUserProperties().getProperty("schemaId"), SourceCreatorSettingsPanel.settingsName);
  }
  
  /**
   * <p>Pozwala pobraæ Ÿród³o PL/SQL lub DLL i jeœli zosta³ ustawiony textArea to aktualizuje go
   * @param schemaName
   * @param objectType FUNCTION, PROCEDURE, etc ale mo¿e te¿ byæ CALL czyli wywo³anie funkcji/procedury
   * @param objectName
   * @return pobrane Ÿród³o
   */
  public String getSource(String schemaName, String objectType, String objectName) {
    return getSource(schemaName, objectType, objectName, null);
  }

  /**
   * <p>Pozwala pobraæ Ÿród³o PL/SQL lub DLL i jeœli zosta³ ustawiony textArea to aktualizuje go
   * @param schemaName
   * @param objectType FUNCTION, PROCEDURE, etc ale mo¿e te¿ byæ CALL czyli wywo³anie funkcji/procedury
   * @param objectName w przypadku CALL i pakietu/typu powinien zawieraæ pe³n¹ nazwê wraz z nazw¹ pakietu/typu
   * @param overload dla objectType = CALL i obiekcie typu pakiet lub typ, wersja funkcji/procedury
   * @return
   */
  public String getSource(final String schemaName, final String objectType, final String objectName, String overload) {
    final String source;
    final PleaseWait wait;
    if (textArea != null) {
      wait = PleaseWait.createSqlWait();
      Application.get().startPleaseWait(wait);
    }
    else {
      wait = null;
    }
    try {
      if (!StringUtil.equals(objectName, "")) {
        if ("CALL".equalsIgnoreCase(objectType)) {
          source = getCall(schemaName, objectName, overload);
        }
        else if (StringUtil.equalAnyOfString(objectType, new String[] {"FUNCTION", "PROCEDURE", "PACKAGE", "PACKAGE SPEC", "PACKAGE BODY", "TYPE", "TYPE BODY", "TRIGGER", "JAVA SOURCE"}, true)) {
          source = getPlSql(schemaName, objectType, objectName);
        }
        else {
          setTransformSettings(null);
          source = getDDL(schemaName, objectType, objectName);
        }
      }
      else {
        source = "";
      }
    }
    finally {
      if (wait != null) {
        Application.get().stopPleaseWait(wait);
      }
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

  private String getCall(String schemaName, String objectName, String overload) {
    String resultTo = "";
    StringBuffer sqlText = new StringBuffer();
    Query args = database.createQuery();
    try {
      String objectCall = "UNKNOWN";
      Query ocall = database.createQuery();
      try {
        ocall.setSqlText(Sql.getObjectForCall());
        ocall.paramByName("SCHEMA_NAME").setString(schemaName);
        ocall.paramByName("OBJECT_NAME").setString(objectName);
        ocall.open();
        if (!ocall.eof()) {
          objectCall = SQLUtil.createSqlName(
            ocall.fieldByName("schema_name").getString(),
            ocall.fieldByName("package_name").getString(),
            ocall.fieldByName("object_name").getString());
        }
        else {
          return null;
        }
      }
      finally {
        ocall.close();
      }
      args.setSqlText(Sql.getArgumentsForCall());
      args.paramByName("SCHEMA_NAME").setString(schemaName);
      args.paramByName("OBJECT_NAME").setString(objectName);
      args.paramByName("OVERLOAD").setString(overload);
      args.open();
      sqlText.append("BEGIN\n");
      if (!args.eof()) {
        if (args.fieldByName("position").getInteger() == 0) {
          sqlText.setLength(0);
          sqlText.append("DECLARE\n");
          sqlText.append("  " +args.fieldByName("argument_name").getString() +" VARCHAR(4000);\n");
          sqlText.append("BEGIN\n");
          resultTo = "  DBMS_OUTPUT.PUT_LINE( " +args.fieldByName("argument_name").getString() +" );\n";
          sqlText.append("  " +args.fieldByName("argument_name").getString() +" := /* " +args.fieldByName("data_type").getString() +" */\n");
          args.next();
        }
      }
      sqlText.append("    " +objectCall +"(");
      int ic = 0;
      while (!args.eof()) {
        if (ic > 0) {
          sqlText.append(",");
        }
        sqlText.append("\n      :" +args.fieldByName("argument_name").getString() +" /* " +args.fieldByName("data_type").getString() +" " +args.fieldByName("in_out").getString() +" */");
        args.next();
        ic++;
      }
      sqlText.append((ic > 0 ? "\n    " : "") +");\n");
      sqlText.append(resultTo);
      sqlText.append("END;");
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
    finally {
      args.close();
    }
    return sqlText.toString();
  }
  
  private void setTransformSettings(String obejctType) {
    if (obejctType == null) {
      Command command = database.createCommand();
      try {
        command.setSqlText(Sql.getSourceCreatorAll());
        command.paramByName("&PRETTY").setBoolean(settings.getValue("ALL-PRETTY", true));
        command.paramByName("&SQLTERMINATOR").setBoolean(settings.getValue("ALL-SQLTERMINATOR", false));
        command.execute();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    if ("TABLE".equals(obejctType)) {
      Command command = database.createCommand();
      try {
        command.setSqlText(Sql.getSourceCreatorTable());
        command.paramByName("&SEGMENT_ATTRIBUTES").setBoolean(settings.getValue("TABLE-SEGMENT_ATTRIBUTES", true));
        command.paramByName("&STORAGE").setBoolean(settings.getValue("TABLE-STORAGE", true));
        command.paramByName("&TABLESPACE").setBoolean(settings.getValue("TABLE-TABLESPACE", true));
        command.paramByName("&CONSTRAINTS").setBoolean(settings.getValue("TABLE-CONSTRAINTS", true));
        command.paramByName("&REF_CONSTRAINTS").setBoolean(settings.getValue("TABLE-REF_CONSTRAINTS", true));
        command.paramByName("&CONSTRAINTS_AS_ALTER").setBoolean(settings.getValue("TABLE-CONSTRAINTS_AS_ALTER", false));
        command.paramByName("&SIZE_BYTE_KEYWORD").setBoolean(settings.getValue("TABLE-SIZE_BYTE_KEYWORD", false));
        command.execute();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    if ("INDEX".equals(obejctType)) {
      Command command = database.createCommand();
      try {
        command.setSqlText(Sql.getSourceCreatorIndex());
        command.paramByName("&SEGMENT_ATTRIBUTES").setBoolean(settings.getValue("INDEX-SEGMENT_ATTRIBUTES", true));
        command.paramByName("&STORAGE").setBoolean(settings.getValue("INDEX-STORAGE", true));
        command.paramByName("&TABLESPACE").setBoolean(settings.getValue("INDEX-TABLESPACE", true));
        command.execute();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    if ("VIEW".equals(obejctType)) {
      Command command = database.createCommand();
      try {
        command.setSqlText(Sql.getSourceCreatorView());
        command.paramByName("&FORCE").setBoolean(settings.getValue("VIEW-FORCE", false));
        command.execute();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  public String getDDL(String schemaName, String objectType, String objectName) {
    setTransformSettings(objectType);
    StringBuffer sqlText = new StringBuffer();
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getObjectDDL());
      source.paramByName("SCHEMA_NAME").setString(schemaName);
      source.paramByName("OBJECT_TYPE").setString(objectType);
      source.paramByName("OBJECT_NAME").setString(objectName);
      if (settings.getValue("ALL-LATEST", false)) {
        source.paramByName("VERSION").setString("LATEST");
      }
      else {
        source.paramByName("VERSION").setString("COMPATIBLE");
      }
      source.open();
      if (source.eof()) {
        return null;
      }
      sqlText.append(source.fieldByName("SOURCE").getString().trim());
      sqlText.append("\n/\n");
    }
    catch (Exception ex) {
      if ("VIEW".equals(objectType)) {
        sqlText.append(createView(schemaName, objectName));
      }
      else if ("TABLE".equals(objectType)) {
        sqlText.append(createTable(schemaName, objectName));
      }
      else {
        sqlText.append("-- Nie mo¿na pobraæ skryptu " +objectType +" przy pomocy DBMS_METADATA.GET_DDL()\n");
        ExceptionUtil.processException(ex);
      }
    }
    finally {
      source.close();
    }
      
    if (StringUtil.equalAnyOfString(objectType, new String[] {"TABLE", "MATERIALIZED_VIEW"}, true)) {
      setTransformSettings("INDEX");
      Query indexes = database.createQuery();
      try {
        indexes.setSqlText(Sql.getIndexSource());
        indexes.paramByName("SCHEMA_NAME").setString(schemaName);
        indexes.paramByName("TABLE_NAME").setString(objectName);
        if (settings.getValue("ALL-LATEST", false)) {
          indexes.paramByName("VERSION").setString("LATEST");
        }
        else {
          indexes.paramByName("VERSION").setString("COMPATIBLE");
        }
        indexes.open();
        while (!indexes.eof()) {
          sqlText.append(indexes.fieldByName("SOURCE").getString().trim());
          sqlText.append("\n/\n");
          indexes.next();
        }
      }
      catch (Exception ex) {
        sqlText.append(createIndexes(schemaName, objectName));
//        sqlText.append("-- Nie mo¿na pobraæ skryptu kluczy przy pomocy DBMS_METADATA.GET_DDL()\n");
//        ExceptionUtil.processException(ex);
      }
      finally {
        indexes.close();
      }
    }

    if (StringUtil.equalAnyOfString(objectType, new String[] {"TABLE", "VIEW", "MATERIALIZED_VIEW"}, true)) {
      appendComments(sqlText, schemaName, objectName);
    }

    return sqlText.toString();
  }
  
  private void appendComments(StringBuffer sqlText, String schemaName, String objectName) {
    Query comments = database.createQuery();
    try {
      comments.setSqlText(Sql.getTableComments());
      comments.paramByName("SCHEMA_NAME").setString(schemaName);
      comments.paramByName("TABLE_NAME").setString(objectName);
      comments.open();
      while (!comments.eof()) {
        sqlText.append(comments.fieldByName("SOURCE").getString());
        sqlText.append("\n/\n");
        comments.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      comments.close();
    }
  }

  private String createView(String schemaName, String objectName) {
    String refrMode = "";
    StringBuffer columns = new StringBuffer();
    String view;
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getViewColumnDestList());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("VIEW_NAME").setString(objectName);
      query.open();
      int clength = 0;
      while (!query.eof()) {
        if (columns.length() > 0) {
          columns.append(", ");
          clength += 2;
          if (clength > 60) {
            columns.append("\n");
            clength = 0;
          }
        }
        columns.append("\"" +query.fieldByName("COLUMN_NAME").getString() +"\"");
        clength += query.fieldByName("COLUMN_NAME").getString().length() +2;
        query.next();
      }
      query.setSqlText(Sql.getViewSource());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("VIEW_NAME").setString(objectName);
      query.open();
      if (!query.eof()) {
        view = query.fieldByName("text").getString();
        return String.format("CREATE OR REPLACE VIEW %s (\n%s) AS\n%s\n/\n", new Object[] {SQLUtil.createSqlName(schemaName, objectName), columns, view});
      }
      else {
        query.setSqlText(Sql.getSnapshotSource());
        query.paramByName("SCHEMA_NAME").setString(schemaName);
        query.paramByName("VIEW_NAME").setString(objectName);
        query.open();
        view = query.fieldByName("text").getString();
        if (StringUtil.equalAnyOfString(query.fieldByName("REFRESH_MODE").getString(), new String[] {"DEMAND", "COMMIT"}, true)) {
          refrMode = " ON " +query.fieldByName("REFRESH_MODE").getString();
        }
        else if (StringUtil.equalAnyOfString(query.fieldByName("REFRESH_MODE").getString(), new String[] {"PRIMARY KEY", "ROWID"}, true)) {
          refrMode = " WITH " +query.fieldByName("REFRESH_MODE").getString();
        }
        else if (!query.fieldByName("REFRESH_MODE").isNull()) {
          refrMode = " " +query.fieldByName("REFRESH_MODE").getString();
        }
        return String.format("CREATE OR REPLACE MATERIALIZED VIEW %s%s AS\n%s\n/\n", new Object[] {SQLUtil.createSqlName(schemaName, objectName), refrMode, view});
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return null;
  }

  private String createIndexColumnList(String schemaName, String indexName ) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getIndexColumnListForScript());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("INDEX_NAME").setString(indexName);
      query.open();
      String result = "(";
      boolean firstTime = true;
      while (!query.eof()) {
        if (!firstTime) {
          result = result +", ";
        }
        result = result +"\"" +query.fieldByName("COLUMN_NAME").getString() +"\"";
        if (!"ASC".equals(query.fieldByName("DESCEND").getString())) {
          result = result +" DESC";
        }
        firstTime = false;
        query.next();
      }
      return result +")";
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }
  
  private String createIndex(String schemaName, String indexName, String unique, String tableOwner, String tableName) {
    String result = "CREATE " +
      ("UNIQUE".equals(unique) ? "UNIQUE " : "") +"INDEX " +
      SQLUtil.createSqlName( schemaName, indexName );
    result = result +" ON " +
      SQLUtil.createSqlName( tableOwner, tableName ) +
      createIndexColumnList( schemaName, indexName );
    return result +"\n/\n";
  }

  private String createIndexes(String schemaName, String objectName) {
    StringBuffer sb = new StringBuffer();
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getIndexListForScript());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("TABLE_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        sb.append(createIndex(
          query.fieldByName("OWNER").getString(),
          query.fieldByName("INDEX_NAME").getString(),
          query.fieldByName("UNIQUENESS").getString(),
          query.fieldByName("TABLE_OWNER").getString(),
          query.fieldByName("TABLE_NAME").getString()));
        query.next();
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String createConstraintColumnList(String schemaName, String constrName, Boolean ref ) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getConstraintColumnListForScript());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("CONSTRAINT_NAME").setString(constrName);
      query.open();
      String result = "";
      if (ref) {
        result = result +SQLUtil.createSqlName(query.fieldByName("OWNER").getString(), query.fieldByName("TABLE_NAME").getString());
      }
      result = result +"(";
      boolean firstTime = true;
      while (!query.eof()) {
        if (!firstTime) {
          result = result +", ";
        }
        result = result +"\"" +query.fieldByName("COLUMN_NAME").getString() +"\"";
        firstTime = false;
        query.next();
      }
      return result +")";
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String createConstraint(
    String schemaName, String tableName, String constrName, String rOwner, String rConstrName, 
    String constrType, String deleteRule, String condition, String status, String deferrable,
    String deferred
  ) {
    String state = "";
    String result = "ALTER TABLE " +SQLUtil.createSqlName( schemaName, tableName ) +" ADD CONSTRAINT " +SQLUtil.createSqlName( schemaName, constrName ) +"\n";

    if ("C".equals(constrType)) {
      result = result +"  CHECK (" +condition +")";
    }
    else if ("U".equals(constrType)) {
      result = result +"  UNIQUE " +createConstraintColumnList( schemaName, constrName, false );
    }
    else if ("P".equals(constrType)) {
      result = result +"  PRIMARY KEY " +createConstraintColumnList( schemaName, constrName, false );
    }
    else if ("R".equals(constrType)) {
      result = result +"  FOREIGN KEY " +createConstraintColumnList( schemaName, constrName, false ) +" REFERENCES " +createConstraintColumnList( rOwner, rConstrName, true );
      if (!"NO ACTION".equals(deleteRule )) {
        result = result +" ON DELETE " +deleteRule;
      }
    }
    if (!"NOT DEFERRABLE".equals(deferrable)) {
      state = state +" " +deferrable;
    }
    if (!"IMMEDIATE".equals(deferred)) {
     state = state +" INITIALLY " +deferred;
    }
    if (!"ENABLED".equals(status)) {
     state = state +" DISABLE ";
    }
    if (!"".equals(state)) {
      result = result +"\n " +state;
    }

    return result +"\n/\n";
  }

  private String createConstraints(String schemaName, String objectName) {
    StringBuffer sb = new StringBuffer();
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getConstraintListForScript());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("TABLE_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        String searchCond = query.fieldByName("SEARCH_CONDITION").getString();
        sb.append(createConstraint(
          query.fieldByName("OWNER").getString(),
          objectName,
          query.fieldByName("CONSTRAINT_NAME").getString(),
          query.fieldByName("R_OWNER").getString(),
          query.fieldByName("R_CONSTRAINT_NAME").getString(),
          query.fieldByName("CONSTRAINT_TYPE").getString(),
          query.fieldByName("DELETE_RULE").getString(),
          searchCond,
          query.fieldByName("STATUS").getString(),
          query.fieldByName("DEFERRABLE").getString(),
          query.fieldByName("DEFERRED").getString()));
        query.next();
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String createTable(String schemaName, String objectName) {
    StringBuilder columns = new StringBuilder();
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getColumnList(null));
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("TABLE_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        if (columns.length() > 0) {
          columns.append(",\n");
        }
        String defaultValue = query.fieldByName("data_default").getString().trim();
        columns.append("  \"" +query.fieldByName("COLUMN_NAME").getString() +"\" " +query.fieldByName("display_type").getString());
        if (!StringUtil.isEmpty(defaultValue)) {
          columns.append(" DEFAULT " +defaultValue);
        }
        if (!"YES".equals(query.fieldByName("nullable").getString())) {
          columns.append(" NOT NULL");
        }
        query.next();
      }
      return String.format(
        "CREATE TABLE %s (\n%s\n)\n/\n%s",
        new Object[] {
          SQLUtil.createSqlName(schemaName, objectName),
          columns.toString(),
          createConstraints(schemaName, objectName)
      });
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getPlSql(String schemaName, String objectType, String objectName) {
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getObjectPlSql());
      source.paramByName("SCHEMA_NAME").setString(schemaName);
      if ("PACKAGE SPEC".equals(objectType)) {
        objectType = "PACKAGE";
      }
      source.paramByName("OBJECT_TYPE").setString(objectType);
      source.paramByName("OBJECT_NAME").setString(objectName);
      source.open();
      StringBuffer sb = new StringBuffer();
      if (!source.eof()) {
        if ("JAVA SOURCE".equals(objectType)) {
          sb.append("CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED " +SQLUtil.createSqlName(schemaName, objectName) +" AS\n");
        }
        else {
          sb.append("CREATE OR REPLACE ");
        }
        while (!source.eof()) {
          sb.append(source.fieldByName("SOURCE").getString());
          source.next();
        }
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
