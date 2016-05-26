/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.tables;

import java.awt.Color;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.ColorListCellRendererFilter;
import pl.mpak.usedb.gui.swing.crf.YesNoCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableColumnListTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public TableColumnListTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-table-columns";
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
    return "column_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("column_no", stringManager.getString("ordinal"), 30),
      new QueryTableColumn("column_name", stringManager.getString("column-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("display_datatype", stringManager.getString("data-type"), 130, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("default_value", stringManager.getString("default-value"), 200),
      new QueryTableColumn("nullable", stringManager.getString("nullable-q"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(Color.RED, SwingUtil.Color.GREEN))),
      new QueryTableColumn("pk", stringManager.getString("pk"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(SwingUtil.Color.GREEN, null))),
      new QueryTableColumn("fk", stringManager.getString("fk"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(SwingUtil.Color.GREEN, null))),
      new QueryTableColumn("accessible", stringManager.getString("accessible"), 100, new QueryTableCellRenderer(new ColorListCellRendererFilter(new String[] {"NO"}, new Color[] {Color.RED}))),
      new QueryTableColumn("storage_type", stringManager.getString("storage-type"), 100),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableColumnList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("column_name", stringManager.getString("column-name"), (String[])null),
      new SqlFilterDefComponent("display_datatype", stringManager.getString("data-type"), (String[])null),
      new SqlFilterDefComponent("nullable = 'Y'", stringManager.getString("nullable-q")),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null),
      new SqlFilterDefComponent("accessible", stringManager.getString("accessible"), (String[])null),
      new SqlFilterDefComponent("storage_type", stringManager.getString("storage-type"), (String[])null)
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("columns");
  }

}
