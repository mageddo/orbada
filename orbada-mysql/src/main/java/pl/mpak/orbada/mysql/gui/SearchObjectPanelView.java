package pl.mpak.orbada.mysql.gui;

import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.services.MySQLDbInfoProvider;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
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
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);
  
  private SearchPanel searchPanel;

  public SearchObjectPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    searchPanel = new SearchPanel();
    searchPanel.setTable(getTable());
    searchPanel.addSearchListener(new SearchListener() {
      public void search(SearchEvent e) {
        refresh();
      }
    });
    getToolBar().add(searchPanel); 
    super.init();
  }
  
  @Override
  public OrbadaTabbedPane getTabbedPane() {
    return null;
  }

  @Override
  public String getCurrentSchemaName() {
    return MySQLDbInfoProvider.getCurrentDatabase(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "mysql-search-objects";
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
      new QueryTableColumn("object_schema", stringManager.getString("database"), 150),
      new QueryTableColumn("object_type", stringManager.getString("object-type"), 150),
      new QueryTableColumn("object_name", stringManager.getString("object-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("table_schema", stringManager.getString("table-database"), 150),
      new QueryTableColumn("table_name", stringManager.getString("table-name"), 150),
      new QueryTableColumn("comment", stringManager.getString("comment"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getObjectsSearch(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null),
      new SqlFilterDefComponent("object_type", stringManager.getString("object-type"), new String[] {"('FUNCTION', 'PROCEDURE', 'TABLE', 'VIEW', 'TRIGGER', 'INDEX')"}),
      new SqlFilterDefComponent("table_schema", stringManager.getString("table-database"), (String[])null),
      new SqlFilterDefComponent("table_name", stringManager.getString("table-name"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return MySQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("object_name").setString(searchPanel.getText());
  }
  
}
