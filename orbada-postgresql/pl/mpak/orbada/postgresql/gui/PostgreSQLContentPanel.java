/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui;

import orbada.gui.ContentPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PostgreSQLContentPanel extends ContentPanel {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  private String lastSchemaName;
  private String lastTableName;
  private String tableAccesible;
  private String tableColumnList;

  public PostgreSQLContentPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  public String getSqlText(String filter) {
    if (lastSchemaName == null || !lastSchemaName.equals(currentSchemaName) ||
        lastTableName == null || !lastTableName.equals(currentTableName)) {
      Query qry = getDatabase().createQuery();
      try {
        qry.setSqlText(Sql.getTableAccessible());
        qry.paramByName("schema_name").setString(currentSchemaName);
        qry.paramByName("table_name").setString(currentTableName);
        qry.open();
        if (!qry.eof()) {
          tableAccesible = qry.fieldByName("accessible").getString();
          if ("COLUMN".equals(tableAccesible)) {
            qry.setSqlText(Sql.getTableAccessibleColumnList());
            qry.paramByName("schema_name").setString(currentSchemaName);
            qry.paramByName("table_name").setString(currentTableName);
            qry.open();
            if (!qry.eof()) {
              tableColumnList = qry.fieldByName("columns").getString();
            }
          }
        }
        lastSchemaName = currentSchemaName;
        lastTableName = currentTableName;
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        qry.close();
      }
    }
    if ("COLUMN".equals(tableAccesible)) {
      return 
        "select " +tableColumnList +"\n" +
        "  from " +getDatabase().quoteName(currentSchemaName, currentTableName) +"\n" +
        ("".equals(filter) ? "" : " WHERE " +filter);
    }
    else if ("NO".equals(tableAccesible)) {
      return "select '" +stringManager.getString("no-right-to-view", currentTableName) +"' info";
    }
    return null;
  }
  
}
