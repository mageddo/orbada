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
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableConstraintsPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TableConstraintsPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableConstraintsPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-table-constraints";
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
    return "constraint_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("constraint_name", stringManager.getString("constraint-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("constraint_type", stringManager.getString("constraint-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("columns", stringManager.getString("constraint-columns"), 200),
      new QueryTableColumn("referenced_table_schema", stringManager.getString("referenced-table-schema"), 100),
      new QueryTableColumn("referenced_table_name", stringManager.getString("referenced-table-name"), 100),
      new QueryTableColumn("ref_columns", stringManager.getString("ref-columns"), 150),
      new QueryTableColumn("update_rule", stringManager.getString("update-rule"), 200),
      new QueryTableColumn("delete_rule", stringManager.getString("delete-rule"), 200)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableConstraintsList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("constraint_name", stringManager.getString("constraint-name"), (String[])null),
      new SqlFilterDefComponent("constraint_type", stringManager.getString("constraint-type"), new String[] {"", "'PRIMARY KEY'", "'UNIQUE'", "'UNIQUE'"}),
      new SqlFilterDefComponent("columns", stringManager.getString("constraint-columns"), (String[])null),
      new SqlFilterDefComponent("referenced_table_schema", stringManager.getString("referenced-table-schema"), (String[])null),
      new SqlFilterDefComponent("referenced_table_name", stringManager.getString("referenced-table-name"), (String[])null)
    };
  }
  
}
