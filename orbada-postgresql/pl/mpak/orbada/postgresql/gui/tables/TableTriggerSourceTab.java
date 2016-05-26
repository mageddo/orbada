/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.tables;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.util.SourceCreator;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropSourceTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class TableTriggerSourceTab extends UniversalPropSourceTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public TableTriggerSourceTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-table-triggers";
  }

  @Override
  public String getObjectColumnName() {
    return "object_name";
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
      new QueryTableColumn("trigger_name", stringManager.getString("trigger-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("enable_mode", stringManager.getString("trigger-enable-mode"), 130, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        @Override
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
            if (StringUtil.nvl((String)value, "").equals("DISABLED")) {
              ((JLabel)renderer).setForeground(Color.DARK_GRAY);
            }
            else if (StringUtil.nvl((String)value, "").equals("ENABLED")) {
              ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
            }
        }
      })),
      new QueryTableColumn("action_timing", stringManager.getString("trigger-action-timing"), 130),
      new QueryTableColumn("trigger_event", stringManager.getString("trigger-trigger-event"), 150),
      new QueryTableColumn("full_function_name", stringManager.getString("trigger-function-call"), 150),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTriggerList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("trigger_name", stringManager.getString("trigger-name"), (String[])null),
      new SqlFilterDefComponent("enable_mode", stringManager.getString("trigger-enable-mode"), new String[] {"'ENABLED'", "'DISABLED'", "'REPLICA'", "'ALWAYS'"}),
      new SqlFilterDefComponent("action_timing", stringManager.getString("trigger-action-timing"), (String[])null),
      new SqlFilterDefComponent("trigger_event", stringManager.getString("trigger-trigger-event"), (String[])null),
      new SqlFilterDefComponent("function_call", stringManager.getString("trigger-function-call"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null)
    };
  }

  @Override
  public void updateBody(OrbadaSyntaxTextArea textArea) {
    new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "TRIGGER", currentPropName, currentObjectName);
  }

  @Override
  public String getTitle() {
    return stringManager.getString("triggers");
  }
  
}
