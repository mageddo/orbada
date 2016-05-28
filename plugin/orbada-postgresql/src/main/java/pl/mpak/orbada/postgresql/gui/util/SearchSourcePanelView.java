package pl.mpak.orbada.postgresql.gui.util;

import java.awt.Component;
import java.util.HashMap;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.SearchEvent;
import pl.mpak.sky.gui.swing.comp.SearchListener;
import pl.mpak.sky.gui.swing.comp.SearchPanel;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class SearchSourcePanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");
  
  private SearchPanel searchPanel;

  public SearchSourcePanelView(IViewAccesibilities accesibilities) {
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
    super.init();
  }
  
  @Override
  public boolean isHorizontal() {
    return false;
  }
  
  @Override
  public Component getTabbedPane() {
    return new SearchSourceSourcePanel(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-search-sources";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getObjectColumnName() {
    return "object_name";
  }

  @Override
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  protected HashMap<String, Variant> getUserData() {
    HashMap<String, Variant> map = new HashMap<String, Variant>();
    try {
      map.put("object_type", new Variant(getQuery().fieldByName("object_type").getString()));
      map.put("object_owner", new Variant(getQuery().fieldByName("object_owner").getString()));
      map.put("line", new Variant(getQuery().fieldByName("line").getInteger()));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return map;
  }
  
  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSearchSourceList(filter.getSqlText(), PostgreSQLDbInfoProvider.instance.getVersion(getDatabase()));
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 150),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 150),
      new QueryTableColumn("object_type", stringManager.getString("object-type"), 150, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("object_name", stringManager.getString("object-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("line", stringManager.getString("line-no"), 70),
      new QueryTableColumn("source_line", stringManager.getString("source-line"), 450)
    };
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null),
      new SqlFilterDefComponent("object_type", stringManager.getString("object-type"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return PostgreSQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("search_text").setString(searchPanel.getText());
  }
  
  @Override
  public boolean canOpen(ParametrizedCommand qc) {
    return !"".equals(searchPanel.getText());
  }
    
}
