package pl.mpak.orbada.hsqldb.gui.dba;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;
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
public class SessionsPanelView extends UniversalViewTabs {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaHSqlDbPlugin.class);
  
  private RefreshPanel refreshPanel;

  public SessionsPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
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
  public void close() throws IOException {
    refreshPanel.cancel();
    super.close();
  }
  
  @Override
  public OrbadaTabbedPane getTabbedPane() {
    return null;
  }

  @Override
  public String getCurrentSchemaName() {
    return null;
  }

  @Override
  public String getPanelName() {
    return "hsqldb-sessions-view";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return "SESSION_ID";
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
        int version = HSqlDbInfoProvider.getVersionTest(getDatabase());
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
          try {
            if (version >= HSqlDbInfoProvider.hsqlDb20 &&
                (!StringUtil.isEmpty(getQuery().fieldByName("WAITING_FOR_THIS").getString()) || !StringUtil.isEmpty(getQuery().fieldByName("THIS_WAITING_FOR").getString()))) {
              label.setBackground(new Color(251, 121, 121));
            }
            else if (StringUtil.equals(getQuery().fieldByName("THIS_SESSION").getString(), "T")) {
              label.setBackground(new Color(180, 205, 240));
            }
          } catch (Exception ex) {
            label.setText(label.getText() +" [" +ex.getMessage() +"]");
          }
        }
        return label;
      }
    };
    columns.add(new QueryTableColumn("SESSION_ID", stringManager.getString("session-id"), 50, renderer));
    columns.add(new QueryTableColumn("CONNECTED", stringManager.getString("connected"), 120, renderer));
    columns.add(new QueryTableColumn("USER_NAME", stringManager.getString("user-name"), 100, renderer));
    columns.add(new QueryTableColumn("IS_ADMIN", stringManager.getString("is-admin"), 60, renderer));
    columns.add(new QueryTableColumn("AUTOCOMMIT", stringManager.getString("auto-commit"), 60, renderer));
    columns.add(new QueryTableColumn("READONLY", stringManager.getString("read-only"), 60, renderer));
    if (HSqlDbInfoProvider.getVersionTest(getDatabase()) == HSqlDbInfoProvider.hsqlDb18) {
      columns.add(new QueryTableColumn("MAXROWS", stringManager.getString("max-rows"), 70, renderer));
    }
    if (HSqlDbInfoProvider.getVersionTest(getDatabase()) >= HSqlDbInfoProvider.hsqlDb20) {
      columns.add(new QueryTableColumn("TRANSACTION", stringManager.getString("transaction"), 60, renderer));
      columns.add(new QueryTableColumn("WAITING_FOR_THIS", stringManager.getString("waiting-for-this"), 50, renderer));
    }
    columns.add(new QueryTableColumn("LAST_IDENTITY", stringManager.getString("last-identity"), 70, renderer));
    columns.add(new QueryTableColumn("TRANSACTION_SIZE", stringManager.getString("transaction-size"), 70, renderer));
    columns.add(new QueryTableColumn("SCHEMA", stringManager.getString("schema"), 100, renderer));
    if (HSqlDbInfoProvider.getVersionTest(getDatabase()) >= HSqlDbInfoProvider.hsqlDb20) {
      columns.add(new QueryTableColumn("CURRENT_STATEMENT", stringManager.getString("current-statement"), 250, renderer));
    }
    return (QueryTableColumn[])columns.toArray(new QueryTableColumn[columns.size()]);
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSessionList(filter.getSqlText(), HSqlDbInfoProvider.getVersionTest(getDatabase()));
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("USER_NAME", stringManager.getString("user-name"), (String[])null),
      new SqlFilterDefComponent("SCHEMA", stringManager.getString("schema"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return null;
  }

}
