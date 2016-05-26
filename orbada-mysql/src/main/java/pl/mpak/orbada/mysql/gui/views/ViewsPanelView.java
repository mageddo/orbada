package pl.mpak.orbada.mysql.gui.views;

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
import pl.mpak.usedb.gui.swing.crf.BooleanCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class ViewsPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public ViewsPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  public Component getTabbedPane() {
    return new ViewTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return MySQLDbInfoProvider.getCurrentDatabase(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "mysql-views";
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
      new QueryTableColumn("is_updatable", stringManager.getString("is-updatable"), 40, new QueryTableCellRenderer(new BooleanCellRendererFilter())),
      new QueryTableColumn("definer", stringManager.getString("definer"), 100),
      new QueryTableColumn("check_option", stringManager.getString("check-option"), 100, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("security_type", stringManager.getString("security-type"), 120)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getViewList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("table_name", stringManager.getString("table-name"), (String[])null),
      new SqlFilterDefComponent("is_updatable = 'YES'", stringManager.getString("is-updatable"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return MySQLDbInfoProvider.instance.getSchemas(getDatabase());
  }

}
