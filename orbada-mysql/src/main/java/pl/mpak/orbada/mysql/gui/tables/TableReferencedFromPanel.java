package pl.mpak.orbada.mysql.gui.tables;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableReferencedFromPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TableReferencedFromPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableReferencedFromPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-table-ref-from";
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
      new QueryTableColumn("constraint_schema", stringManager.getString("constraint-schema"), 150),
      new QueryTableColumn("constraint_name", stringManager.getString("constraint-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("table_name", stringManager.getString("referenced-table-name"), 150),
      new QueryTableColumn("columns", stringManager.getString("columns"), 150),
      new QueryTableColumn("ref_columns", stringManager.getString("ref-columns"), 150)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableReferencedFromList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("constraint_schema", stringManager.getString("constraint-schema"), (String[])null),
      new SqlFilterDefComponent("constraint_name", stringManager.getString("constraint-name"), (String[])null),
      new SqlFilterDefComponent("table_name", stringManager.getString("referenced-table-name"), (String[])null)
    };
  }
  
}
