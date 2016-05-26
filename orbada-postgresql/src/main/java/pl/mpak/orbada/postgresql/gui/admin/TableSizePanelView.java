package pl.mpak.orbada.postgresql.gui.admin;

import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.comp.RefreshEvent;
import pl.mpak.sky.gui.swing.comp.RefreshListener;
import pl.mpak.sky.gui.swing.comp.RefreshPanel;
import pl.mpak.sky.gui.swing.comp.SearchEvent;
import pl.mpak.sky.gui.swing.comp.SearchListener;
import pl.mpak.sky.gui.swing.comp.SearchPanel;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableSizePanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);
  
  private SearchPanel searchPanel;
  private RefreshPanel refreshPanel;

  public TableSizePanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    searchPanel = new SearchPanel();
    searchPanel.setTable(getTable());
    searchPanel.addSearchListener(new SearchListener() {
      @Override
      public void search(SearchEvent e) {
        refresh();
      }
    });
    getToolBar().add(searchPanel); 
    
    refreshPanel = new RefreshPanel();
    refreshPanel.addRefreshListener(new RefreshListener() {
      @Override
      public void refresh(RefreshEvent e) {
        TableSizePanelView.this.refresh();
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
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-table-sizes";
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
      new QueryTableColumn("tablespace_name", stringManager.getString("tablespace-name"), 150),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 150),
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 150),
      new QueryTableColumn("table_name", stringManager.getString("table-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("total_size_b", stringManager.getString("total-size-b"), 70),
      new QueryTableColumn("indexes_size_b", stringManager.getString("indexes-size-b"), 70),
      new QueryTableColumn("index_count", stringManager.getString("index-count"), 70),
      new QueryTableColumn("total_size_mb", stringManager.getString("total-size-mb"), 70),
      new QueryTableColumn("prc_size", stringManager.getString("prc-size"), 50),
      new QueryTableColumn("stat_count", stringManager.getString("record-count"), 70),
      new QueryTableColumn("record_length", stringManager.getString("record-length"), 50),
      new QueryTableColumn("last_analyze", stringManager.getString("last-analyze"), 130),
      new QueryTableColumn("last_vacuum", stringManager.getString("last-vacuum"), 130),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableSizeList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("tablespace_name", stringManager.getString("tablespace-name"), (String[])null),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("schema_name", stringManager.getString("schema-name"), (String[])null),
      new SqlFilterDefComponent("table_name", stringManager.getString("table-name"), (String[])null),
      new SqlFilterDefComponent("total_size_b", stringManager.getString("total-size-b"), (String[])null),
      new SqlFilterDefComponent("indexes_size_b", stringManager.getString("indexes-size-b"), (String[])null),
      new SqlFilterDefComponent("index_count", stringManager.getString("index-count"), (String[])null),
      new SqlFilterDefComponent("prc_size", stringManager.getString("prc-size"), (String[])null),
      new SqlFilterDefComponent("stat_count", stringManager.getString("record-count"), (String[])null),
      new SqlFilterDefComponent("record_length", stringManager.getString("record-length"), (String[])null),
      new SqlFilterDefComponent("last_analyze", stringManager.getString("last-analyze"), (String[])null),
      new SqlFilterDefComponent("last_vacuum", stringManager.getString("last-vacuum"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return PostgreSQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("table_name").setString(null);
    qc.paramByName("search_text").setString(searchPanel.getText());
  }
  
}
