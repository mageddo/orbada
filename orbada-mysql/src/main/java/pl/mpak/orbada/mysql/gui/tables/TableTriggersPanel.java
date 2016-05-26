package pl.mpak.orbada.mysql.gui.tables;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.util.SourceCreator;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropSourceTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableTriggersPanel extends UniversalPropSourceTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TableTriggersPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableTriggersPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-table-triggers";
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
    return "trigger_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("action_order", stringManager.getString("action-order"), 40),
      new QueryTableColumn("trigger_name", stringManager.getString("trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("action_timing", stringManager.getString("action-timing"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("event_manipulation", stringManager.getString("event-manipulation"), 150, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("action_orientation", stringManager.getString("action-orientation"), 120),
      new QueryTableColumn("action_condition", stringManager.getString("action-condition"), 120)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableTriggersList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("trigger_name", stringManager.getString("trigger-name"), (String[])null),
      new SqlFilterDefComponent("action_timing", stringManager.getString("action-timing"), (String[])null),
      new SqlFilterDefComponent("event_manipulation", stringManager.getString("event-manipulation"), (String[])null),
      new SqlFilterDefComponent("action_orientation", stringManager.getString("action-orientation"), (String[])null)
    };
  }

  @Override
  public void updateBody(OrbadaSyntaxTextArea textArea) {
    new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "TRIGGER", currentPropName);
  }
  
}
