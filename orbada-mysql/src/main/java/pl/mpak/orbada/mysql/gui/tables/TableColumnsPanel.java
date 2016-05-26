package pl.mpak.orbada.mysql.gui.tables;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.BooleanCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableColumnsPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TableColumnsPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableColumnsPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-table-columns";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getObjectColumnName() {
    return "table_name";
  }

  @Override
  public String getPropColumnName() {
    return "column_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("ordinal_position", stringManager.getString("pos"), 30),
      new QueryTableColumn("column_name", stringManager.getString("column-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("column_type", stringManager.getString("column-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("column_default", stringManager.getString("column-default"), 200),
      new QueryTableColumn("is_nullable", stringManager.getString("is-nullable"), 40, new QueryTableCellRenderer(new BooleanCellRendererFilter())),
      new QueryTableColumn("column_key", stringManager.getString("column-key"), 30),
      new QueryTableColumn("character_set_name", stringManager.getString("character-set-name"), 90),
      new QueryTableColumn("collation_name", stringManager.getString("collation-name"), 100),
      new QueryTableColumn("extra", stringManager.getString("extra-actions"), 150),
      new QueryTableColumn("column_comment", stringManager.getString("comment"), 300)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getColumnList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("column_name", stringManager.getString("column-name"), (String[])null),
      new SqlFilterDefComponent("data_type", stringManager.getString("data-type"), new String[] {"", "'VARCHAR'", "'ENUM'", "'INT'", "'TIMESTAMP'"}),
      new SqlFilterDefComponent("column_key", stringManager.getString("column-key"), (String[])null),
      new SqlFilterDefComponent("is_nullable = 'NO'", stringManager.getString("not-null"))
    };
  }
  
}
