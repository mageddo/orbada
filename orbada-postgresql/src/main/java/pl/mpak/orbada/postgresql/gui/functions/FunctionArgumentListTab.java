/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.functions;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FunctionArgumentListTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public FunctionArgumentListTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-function-arguments";
  }

  @Override
  public String getObjectColumnName() {
    return "function_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getPropColumnName() {
    return "argument_no";
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getFunctionArgumentList(filter.getSqlText());
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("argument_no", stringManager.getString("ordinal"), 30),
      new QueryTableColumn("argument_name", stringManager.getString("argument-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("argument_type", stringManager.getString("data-type"), 130, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("argument_mode", stringManager.getString("argument-mode"), 70),
      new QueryTableColumn("default_value", stringManager.getString("default-value"), 200)
    };
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("argument_name", stringManager.getString("argument-name"), (String[])null),
      new SqlFilterDefComponent("argument_type", stringManager.getString("data-type"), (String[])null),
      new SqlFilterDefComponent("argument_mode", stringManager.getString("argument-mode"), (String[])null),
      new SqlFilterDefComponent("default_value", stringManager.getString("default-value"), (String[])null)
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("function-arguments");
  }

}
