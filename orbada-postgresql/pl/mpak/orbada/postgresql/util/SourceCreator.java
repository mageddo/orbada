package pl.mpak.orbada.postgresql.util;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SourceCreator {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

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
   * <p>Pozwala pobraæ Ÿród³o i jeœli zosta³ ustawiony textArea to aktualizuje go
   * @param schemaName
   * @param objectType FUNCTION, PROCEDURE, etc ale mo¿e te¿ byæ CALL czyli wywo³anie funkcji/procedury
   * @param objectName w przypadku CALL i pakietu/typu powinien zawieraæ pe³n¹ nazwê wraz z nazw¹ pakietu/typu
   * @param onObjectName np. dla wyzwalaczy nazwa tabeli z któr¹ jest zwi¹zany
   * @return
   */
  public String getSource(final String schemaName, final String objectType, final String objectName, final String onObjectName) {
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
        if ("TRIGGER".equalsIgnoreCase(objectType)) {
          source = getTrigger(schemaName, objectName, onObjectName);
        }
        else if ("RULE".equalsIgnoreCase(objectType)) {
          source = getRule(schemaName, objectName, onObjectName);
        }
        else if ("FUNCTION".equalsIgnoreCase(objectType) || "TRIGGER FUNCTION".equalsIgnoreCase(objectType)) {
          source = getFunction(schemaName, objectName);
        }
        else if ("VIEW".equalsIgnoreCase(objectType)) {
          source = getView(schemaName, objectName);
        }
        else if ("TYPE".equalsIgnoreCase(objectType)) {
          source = getType(schemaName, objectName);
        }
        else if ("AGGREGATE".equalsIgnoreCase(objectType)) {
          source = getAggregate(schemaName, objectName);
        }
        else {
          source = "";
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
        @Override
        public void run() {
          if (textArea instanceof AbsOrbadaSyntaxTextArea) {
            ((AbsOrbadaSyntaxTextArea)textArea).setDatabaseObject(schemaName, objectType, objectName, source);
          }
          textArea.getEditorArea().setCaretPosition(0);
        }
      });
    }
    return source;
  }

  private String getTrigger(final String schemaName, final String objectName, final String onObjectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getTriggerSource());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(onObjectName);
      query.paramByName("TRIGGER_NAME").setString(objectName);
      query.open();
      if (!query.eof()) {
        return
          "DROP TRIGGER " +query.fieldByName("trigger_name").getString() +" ON " +query.fieldByName("object_name").getString() +"\n/\n" +
          StringUtil.breakBefore(query.fieldByName("source").getString(), new String[] {" AFTER ", " BEFORE ", " INSTED ", " ON ", " EXECUTE "}, "\n ") +"\n/\n" +
          "COMMENT ON TRIGGER " +query.fieldByName("trigger_name").getString() +" ON " +query.fieldByName("object_name").getString() +" IS " +query.fieldByName("description").getString() +"\n/";
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getFunction(final String schemaName, final String objectName) {
    Query query = database.createQuery();
    try {
      String grants = "";
      query.setSqlText(Sql.getFunctionPrivilegeCommandList(null));
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        grants = grants +query.fieldByName("command").getString() +"\n/\n";
        query.next();
      }
      query.setSqlText(Sql.getFunctionSource());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("FUNCTION_NAME").setString(objectName);
      query.open();
      if (!query.eof()) {
        return
          "--DROP FUNCTION " +query.fieldByName("function_prot").getString() +"\n--/\n" +
          query.fieldByName("source").getString() +"/\n" +
          "ALTER FUNCTION " +query.fieldByName("function_prot").getString() +" OWNER TO " +query.fieldByName("owner_name").getString() +"\n/\n" +
          grants +
          "COMMENT ON FUNCTION " +query.fieldByName("function_prot").getString() +" IS " +query.fieldByName("description").getString() +"\n/"
          ;
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getRule(final String schemaName, final String objectName, final String onObjectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getRuleSource());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(onObjectName);
      query.paramByName("RULE_NAME").setString(objectName);
      query.open();
      if (!query.eof()) {
        return
          "DROP RULE " +query.fieldByName("rule_name").getString() +" ON " +query.fieldByName("object_name").getString() +"\n/\n" +
          query.fieldByName("source").getString() +"\n/\n" +
          "COMMENT ON RULE " +query.fieldByName("rule_name").getString() +" ON " +query.fieldByName("object_name").getString() +" IS " +query.fieldByName("description").getString() +"\n/";
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getView(final String schemaName, final String objectName) {
    Query query = database.createQuery();
    try {
      String grants = "";
      query.setSqlText(Sql.getPrivilegeCommandList(null));
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        grants = grants +query.fieldByName("command").getString() +"\n/\n";
        query.next();
      }
      query.setSqlText(Sql.getViewSource());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("VIEW_NAME").setString(objectName);
      query.open();
      if (!query.eof()) {
        return
          "--DROP VIEW " +query.fieldByName("view_name").getString() +"\n--/\n" +
          "---- !!! " +stringManager.getString("SourceCreator-extra-alter-view") +"\n" +
          "--delete from pg_attribute where attrelid = '" +query.fieldByName("view_name").getString() +"'::regclass\n--/\n" +
          "--update pg_class set relnatts = 0 where oid = '" +query.fieldByName("view_name").getString() +"'::regclass\n--/\n" +
          "CREATE OR REPLACE VIEW " +query.fieldByName("view_name").getString() +" AS\n" +
          query.fieldByName("source").getString() +"\n/\n" +
          "ALTER VIEW " +query.fieldByName("view_name").getString() +" OWNER TO " +query.fieldByName("owner_name").getString() +"\n/\n" +
          grants +
          "COMMENT ON VIEW " +query.fieldByName("view_name").getString() +" IS " +query.fieldByName("description").getString() +"\n/";
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getType(final String schemaName, final String objectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getTypeSource(PostgreSQLDbInfoProvider.instance.getVersion(database)));
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("TYPE_NAME").setString(objectName);
      query.open();
      if (!query.eof()) {
        return query.fieldByName("source").getString();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getAggregate(final String schemaName, final String objectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getAggregateSource());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("AGGREGATE_NAME").setString(objectName);
      query.open();
      if (!query.eof()) {
        return query.fieldByName("source").getString();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

}
