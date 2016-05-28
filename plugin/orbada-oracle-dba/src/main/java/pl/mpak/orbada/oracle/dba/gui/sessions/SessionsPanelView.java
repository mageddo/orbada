package pl.mpak.orbada.oracle.dba.gui.sessions;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.comp.RefreshEvent;
import pl.mpak.sky.gui.swing.comp.RefreshListener;
import pl.mpak.sky.gui.swing.comp.RefreshPanel;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class SessionsPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-dba");

  private boolean clusters;
  private boolean dbaRole;
  private boolean lockRole;
  private boolean sqlRole;
  private RefreshPanel refreshPanel;
  
  public SessionsPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    clusters = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("rac-detect", "false"));
    dbaRole = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("dba-role", "false"));
    lockRole = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("lock-view-role", "false"));
    sqlRole = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("sql-view-role", "false"));
    refreshPanel = new RefreshPanel();
    refreshPanel.addRefreshListener(new RefreshListener() {
      @Override
      public void refresh(RefreshEvent e) {
        SessionsPanelView.this.refresh();
      }
    });
    getToolBar().add(refreshPanel);
    super.init();
  }
    
  @Override
  public Component getTabbedPane() {
    if (dbaRole || lockRole || sqlRole) {
      return new SessionTabbedPane(accesibilities);
    }
    return null;
  }

  @Override
  public String getCurrentSchemaName() {
    return null;
  }
  
  @Override
  protected void refreshTabbedPanes() {
    try {
      if (clusters && !getQuery().isEmpty()) {
        int rowIndex = getTable().getSelectedRow();
        if (rowIndex >= 0) {
          getQuery().getRecord(rowIndex);
          currentSchemaName = getQuery().fieldByName("inst_id").getString();
        }
      }
      else {
        currentSchemaName = "";
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    super.refreshTabbedPanes();
  }

  @Override
  public String getPanelName() {
    return "oracle-sessions";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return "sid";
  }

  @Override
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    ArrayList<QueryTableColumn> columns = new ArrayList<QueryTableColumn>();
    QueryTableCellRenderer renderer = new QueryTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
          try {
            if (!getQuery().fieldByName("lockwait").isNull()) {
              label.setBackground(new Color(251, 121, 121));
            }
            else if (getQuery().fieldByName("sid").getString().equals(getDatabase().getUserProperties().getProperty("sid"))) {
              label.setBackground(new Color(180, 205, 240));
            }
            else if (value != null && value.toString().equals("INACTIVE")) {
              label.setForeground(Color.GRAY);
            }
          } catch (Exception ex) {
            label.setText(label.getText() +" [" +ex.getMessage() +"]");
          }
        }
        return label;
      }
    };
    if (clusters) {
      columns.add(new QueryTableColumn("inst_id", stringManager.getString("instance"), 30, renderer));
    }
    columns.add(new QueryTableColumn("sid", stringManager.getString("sid"), 50, renderer));
    columns.add(new QueryTableColumn("spid", stringManager.getString("spid"), 50, renderer));
    columns.add(new QueryTableColumn("process", stringManager.getString("process"), 60, renderer));
    columns.add(new QueryTableColumn("pid", stringManager.getString("pid"), 50, renderer));
    columns.add(new QueryTableColumn("serial#", stringManager.getString("serial-hash"), 50, renderer));
    columns.add(new QueryTableColumn("audsid", stringManager.getString("audsid"), 50, renderer));
    columns.add(new QueryTableColumn("status", stringManager.getString("status"), 60, renderer));
    columns.add(new QueryTableColumn("command", stringManager.getString("command"), 70, renderer));
    columns.add(new QueryTableColumn("wait_sec", stringManager.getString("wait-sec"), 50, renderer));
    columns.add(new QueryTableColumn("type", stringManager.getString("type"), 80, renderer));
    columns.add(new QueryTableColumn("username", stringManager.getString("oracle-user-name"), 100, renderer));
    columns.add(new QueryTableColumn("osuser", stringManager.getString("os-user-name"), 120, renderer));
    columns.add(new QueryTableColumn("machine", stringManager.getString("machine"), 120, renderer));
    columns.add(new QueryTableColumn("program", stringManager.getString("user-program"), 120, renderer));
    columns.add(new QueryTableColumn("osprogram", stringManager.getString("oracle-process"), 130, renderer));
    columns.add(new QueryTableColumn("module", stringManager.getString("application-module"), 120, renderer));
    columns.add(new QueryTableColumn("action", stringManager.getString("application-action"), 120, renderer));
    columns.add(new QueryTableColumn("client_info", stringManager.getString("application-client-info"), 150, renderer));
    columns.add(new QueryTableColumn("logon_time", stringManager.getString("login-time"), 120, renderer));
    columns.add(new QueryTableColumn("lockwait", stringManager.getString("lock-wait"), 90, renderer));
    columns.add(new QueryTableColumn("blocking_session", stringManager.getString("blocking-session"), 70, renderer));
    columns.add(new QueryTableColumn("physical_reads", stringManager.getString("physical-reads"), 90, renderer));
    columns.add(new QueryTableColumn("block_gets", stringManager.getString("block-gets"), 90, renderer));
    columns.add(new QueryTableColumn("consistent_gets", stringManager.getString("consistent-gets"), 90, renderer));
    columns.add(new QueryTableColumn("block_changes", stringManager.getString("block-changes"), 90, renderer));
    columns.add(new QueryTableColumn("consistent_changes", stringManager.getString("consistent-changes"), 90, renderer));
    columns.add(new QueryTableColumn("event", stringManager.getString("event"), 150, renderer));
    return (QueryTableColumn[])columns.toArray(new QueryTableColumn[columns.size()]);
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionList(filter.getSqlText(), clusters);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("s.USERNAME is not null and s.type <> 'BACKGROUND'", stringManager.getString("only-user-sessions")),
      new SqlFilterDefComponent("s.USERNAME", stringManager.getString("oracle-user-name"), (String[])null),
      new SqlFilterDefComponent("s.osuser", stringManager.getString("os-user-name"), (String[])null),
      new SqlFilterDefComponent("s.machine", stringManager.getString("machine"), (String[])null),
      new SqlFilterDefComponent("s.module", stringManager.getString("application-module"), (String[])null),
      new SqlFilterDefComponent("s.action", stringManager.getString("application-action"), (String[])null),
      new SqlFilterDefComponent("s.client_info", stringManager.getString("application-client-info"), (String[])null),
      new SqlFilterDefComponent("p.program", stringManager.getString("user-program"), (String[])null),
      new SqlFilterDefComponent("lockwait is not null", stringManager.getString("only-lock-sessions"))
    };
  }

  @Override
  public String[] getSchemaList() {
    return null;
  }
  
  @Override
  public boolean isHorizontal() {
    return false;
  }
  
}
