package pl.mpak.orbada.mysql.gui.triggers;

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
public class TriggersPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TriggersPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  public Component getTabbedPane() {
    return new TriggerTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return MySQLDbInfoProvider.getCurrentDatabase(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "mysql-triggers";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getObjectColumnName() {
    return "trigger_name";
  }

  @Override
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("action_order", stringManager.getString("action-order"), 40),
      new QueryTableColumn("trigger_name", stringManager.getString("trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("event_object_schema", stringManager.getString("database"), 120),
      new QueryTableColumn("event_object_table", stringManager.getString("table-name"), 120, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("action_timing", stringManager.getString("action-timing"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("event_manipulation", stringManager.getString("event-manipulation"), 150, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("action_orientation", stringManager.getString("action-orientation"), 120),
      new QueryTableColumn("action_condition", stringManager.getString("action-condition"), 120)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTriggersList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("event_object_schema", stringManager.getString("database"), (String[])null),
      new SqlFilterDefComponent("event_object_table", stringManager.getString("table-name"), (String[])null),
      new SqlFilterDefComponent("trigger_name", stringManager.getString("trigger-name"), (String[])null),
      new SqlFilterDefComponent("action_timing", stringManager.getString("action-timing"), (String[])null),
      new SqlFilterDefComponent("event_manipulation", stringManager.getString("event-manipulation"), (String[])null),
      new SqlFilterDefComponent("action_orientation", stringManager.getString("action-orientation"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return MySQLDbInfoProvider.instance.getSchemas(getDatabase());
  }

}
