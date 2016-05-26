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
public class TablePrivilegesPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TablePrivilegesPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TablePrivilegesPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-table-privileges";
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
    return "privilege_type";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("grantee", stringManager.getString("grantee"), 150),
      new QueryTableColumn("privilege_type", stringManager.getString("privilege-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("is_grantable", stringManager.getString("is-grantable"), 40)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTablePrivilegesList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("grantee", stringManager.getString("grantee"), (String[])null),
      new SqlFilterDefComponent("privilege_type", stringManager.getString("privilege-type"), (String[])null)
    };
  }
  
}
