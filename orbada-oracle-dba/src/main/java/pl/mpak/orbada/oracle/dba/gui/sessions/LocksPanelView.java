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
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class LocksPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-dba");

  private boolean clusters;
  private RefreshPanel refreshPanel;
  
  public LocksPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    clusters = StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("rac-detect", "false"));
    refreshPanel = new RefreshPanel();
    refreshPanel.addRefreshListener(new RefreshListener() {
      @Override
      public void refresh(RefreshEvent e) {
        LocksPanelView.this.refresh();
      }
    });
    getToolBar().add(refreshPanel);
    super.init();
  }
  
  @Override
  public Component getTabbedPane() {
    return null;
  }

  @Override
  public String getCurrentSchemaName() {
    return null;
  }

  @Override
  public String getPanelName() {
    return "oracle-session-locks";
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
            if (!"ACTIVE".equals(getQuery().fieldByName("lockwait").getString())) {
              label.setBackground(new Color(251, 121, 121));
            }
            else if (getQuery().fieldByName("sid").getString().equals(getDatabase().getUserProperties().getProperty("sid"))) {
              label.setBackground(new Color(180, 205, 240));
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
    columns.add(new QueryTableColumn("serial#", stringManager.getString("serial-hash"), 50, renderer));
    columns.add(new QueryTableColumn("sid", stringManager.getString("sid"), 50, renderer));
    columns.add(new QueryTableColumn("audsid", stringManager.getString("audsid"), 50, renderer));
    columns.add(new QueryTableColumn("oracle_username", stringManager.getString("oracle-user-name"), 100, renderer));
    columns.add(new QueryTableColumn("os_username", stringManager.getString("os-user-name"), 120, renderer));
    columns.add(new QueryTableColumn("machine", stringManager.getString("machine"), 120, renderer));
    columns.add(new QueryTableColumn("program", stringManager.getString("user-program"), 120, renderer));
    columns.add(new QueryTableColumn("object_type", stringManager.getString("object-type"), 100, renderer));
    columns.add(new QueryTableColumn("object", stringManager.getString("objects"), 150, renderer));
    columns.add(new QueryTableColumn("lockwait", stringManager.getString("lock-wait"), 120, renderer));
    columns.add(new QueryTableColumn("locked_mode", stringManager.getString("locked-mode"), 120, renderer));
    return (QueryTableColumn[])columns.toArray(new QueryTableColumn[columns.size()]);
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionLockList(filter.getSqlText(), clusters);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("l.oracle_username", stringManager.getString("oracle-user-name"), (String[])null),
      new SqlFilterDefComponent("o.owner", stringManager.getString("object-schema"), (String[])null),
      new SqlFilterDefComponent("locked_mode", stringManager.getString("only-lock-sessions"), new String[] {"None", "Null", "Row-Shared", "Row-Exclusive", "Share", "Share Row-Exclusive", "Exclusive"})
    };
  }

  @Override
  public String[] getSchemaList() {
    return null;
  }
  
}
