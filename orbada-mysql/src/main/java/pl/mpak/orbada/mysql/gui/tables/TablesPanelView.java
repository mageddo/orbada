package pl.mpak.orbada.mysql.gui.tables;

import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.services.MySQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TablesPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TablesPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  public Component getTabbedPane() {
    return new TableTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return MySQLDbInfoProvider.getCurrentDatabase(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "mysql-tables";
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
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("table_name", stringManager.getString("table-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("engine", stringManager.getString("engine"), 100, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("row_format", stringManager.getString("row-format"), 100, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("table_type", stringManager.getString("table-type"), 100),
      new QueryTableColumn("create_time", stringManager.getString("created"), 120),
      new QueryTableColumn("update_time", stringManager.getString("updated"), 120),
      new QueryTableColumn("table_comment", stringManager.getString("comment"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("table_name", stringManager.getString("table-name"), (String[])null),
      new SqlFilterDefComponent("engine", stringManager.getString("engine"), (String[])null),
      new SqlFilterDefComponent("row_format", stringManager.getString("row-format"), (String[])null),
      new SqlFilterDefComponent("table_type", stringManager.getString("table-type"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return MySQLDbInfoProvider.instance.getSchemas(getDatabase());
  }

}
