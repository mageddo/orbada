package pl.mpak.orbada.postgresql.gui.util;

import java.awt.Component;
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
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class SearchObjectPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);
  
  private SearchPanel searchPanel;

  public SearchObjectPanelView(IViewAccesibilities accesibilities) {
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
  public Component getTabbedPane() {
    return null;
  }

  @Override
  public String getCurrentSchemaName() {
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-search-objects";
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
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("tablespace_name", stringManager.getString("tablespace-name"), 150),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 150),
      //new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 150),
      new QueryTableColumn("object_type", stringManager.getString("object-type"), 150, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("object_owner", stringManager.getString("object-owner"), 150),
      new QueryTableColumn("object_name", stringManager.getString("object-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("object_attribute", stringManager.getString("object-attribute"), 150),
      new QueryTableColumn("object_info", stringManager.getString("object-info"), 150),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSearchObjectList(filter.getSqlText(), PostgreSQLDbInfoProvider.instance.getVersion(getDatabase()));
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null),
      new SqlFilterDefComponent("object_type", stringManager.getString("object-type"), new String[] {"('INDEX', 'CONSTRAINT', 'COMPOSITE TYPE', 'BASE TYPE')"}),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("tablespace_name", stringManager.getString("tablespace-name"), (String[])null),
      new SqlFilterDefComponent("object_attribute", stringManager.getString("object-attribute"), (String[])null),
      new SqlFilterDefComponent("object_info", stringManager.getString("object-info"), (String[])null),
      new SqlFilterDefComponent("object_owner", stringManager.getString("object-owner"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null)
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
