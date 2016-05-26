/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.views;

import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.util.SourceCreator;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropSourceTab;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ViewTriggerFunctionSourceTab extends UniversalPropSourceTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public ViewTriggerFunctionSourceTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-view-trigger-functions";
  }

  @Override
  public String getObjectColumnName() {
    return "object_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getPropColumnName() {
    return "function_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("function_schema_name", stringManager.getString("schema-name"), 130),
      new QueryTableColumn("function_name", stringManager.getString("function-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("function_arguments", stringManager.getString("function-arguments"), 130),
      new QueryTableColumn("triggers", stringManager.getString("triggers"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTriggerFunctionList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("function_schema_name", stringManager.getString("schema-name"), (String[])null),
      new SqlFilterDefComponent("function_name", stringManager.getString("function-name"), (String[])null),
      new SqlFilterDefComponent("triggers", stringManager.getString("triggers"), (String[])null)
    };
  }

  @Override
  public void updateBody(OrbadaSyntaxTextArea textArea) {
    try {
      String functionSchema = getQuery().fieldByName("function_schema_name").getString();
      String functionName = getQuery().fieldByName("function_name").getString();
      new SourceCreator(getDatabase(), textArea).getSource(functionSchema, "FUNCTION", functionName, null);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  @Override
  public String getTitle() {
    return stringManager.getString("trigger-functions");
  }
  
}
