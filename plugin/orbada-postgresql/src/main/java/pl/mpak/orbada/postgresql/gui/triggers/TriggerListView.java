/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.triggers;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class TriggerListView extends UniversalViewTabs {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public TriggerListView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public Component getTabbedPane() {
    return new TriggerTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-triggers";
  }

  @Override
  public String getObjectColumnName() {
    return "trigger_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getDescriptionColumnName() {
    return "description";
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTriggerList(filter.getSqlText());
  }

  @Override
  protected void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("object_name").setString(null);
  }
  
  @Override
  protected HashMap<String, Variant> getUserData() {
    HashMap<String, Variant> map = new HashMap<String, Variant>();
    try {
      map.put("table_name", new Variant(getQuery().fieldByName("object_name").getString()));
      map.put("full_function_name", new Variant(getQuery().fieldByName("full_function_name").getString()));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return map;
  }
  
  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("trigger_name", stringManager.getString("trigger-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("object_name", stringManager.getString("object-name"), 130),
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
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("trigger_name", stringManager.getString("trigger-name"), (String[])null),
      new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null),
      new SqlFilterDefComponent("enable_mode", stringManager.getString("trigger-enable-mode"), new String[] {"'ENABLED'", "'DISABLED'", "'REPLICA'", "'ALWAYS'"}),
      new SqlFilterDefComponent("action_timing", stringManager.getString("trigger-action-timing"), (String[])null),
      new SqlFilterDefComponent("trigger_event", stringManager.getString("trigger-trigger-event"), (String[])null),
      new SqlFilterDefComponent("function_call", stringManager.getString("trigger-function-call"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return PostgreSQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
}
