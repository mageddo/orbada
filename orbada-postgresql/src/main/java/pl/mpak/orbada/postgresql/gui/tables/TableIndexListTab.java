/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.tables;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.YesNoCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableIndexListTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public TableIndexListTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-table-indexes";
  }

  @Override
  public String getObjectColumnName() {
    return "table_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getPropColumnName() {
    return "index_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("index_name", stringManager.getString("index-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("table_space", stringManager.getString("table-space"), 130),
      new QueryTableColumn("method", stringManager.getString("index-method"), 130, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("index_expr", stringManager.getString("index-expression"), 130),
      new QueryTableColumn("uniqueness", stringManager.getString("index-uniqueness"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(SwingUtil.Color.GREEN, null))),
      new QueryTableColumn("clustered", stringManager.getString("clustered"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(SwingUtil.Color.GREEN, null))),
      new QueryTableColumn("index_pk", stringManager.getString("primary-key"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(SwingUtil.Color.GREEN, null))),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getIndexList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("index_name", stringManager.getString("index-name"), (String[])null),
      new SqlFilterDefComponent("table_space", stringManager.getString("table-space"), (String[])null),
      new SqlFilterDefComponent("method", stringManager.getString("index-method"), (String[])null),
      new SqlFilterDefComponent("index_expr", stringManager.getString("index-expression"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null),
      new SqlFilterDefComponent("uniqueness = 'Y'", stringManager.getString("index-uniqueness")),
      new SqlFilterDefComponent("index_pk <> 'Y'", stringManager.getString("not-primary-key"))
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("indexes");
  }

}
