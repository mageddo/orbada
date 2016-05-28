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
public class TableColumnPrivilegesPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TableColumnPrivilegesPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableColumnPrivilegesPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-table-columns-privileges";
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
      new QueryTableColumn("grantee", stringManager.getString("grantee"), 150),
      new QueryTableColumn("column_name", stringManager.getString("column-name"), 100, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("privilege_type", stringManager.getString("privilege-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("is_grantable", stringManager.getString("is-grantable"), 40)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getColumnPrivilegesList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("grantee", stringManager.getString("grantee"), (String[])null),
      new SqlFilterDefComponent("column_name", stringManager.getString("column-name"), (String[])null),
      new SqlFilterDefComponent("privilege_type", stringManager.getString("privilege-type"), (String[])null)
    };
  }
  
}
