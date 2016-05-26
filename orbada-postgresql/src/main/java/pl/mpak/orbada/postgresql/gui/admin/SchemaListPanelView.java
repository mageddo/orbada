package pl.mpak.orbada.postgresql.gui.admin;

import java.awt.Color;
import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.comp.RefreshEvent;
import pl.mpak.sky.gui.swing.comp.RefreshListener;
import pl.mpak.sky.gui.swing.comp.RefreshPanel;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.ColorListCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class SchemaListPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);
  
  private RefreshPanel refreshPanel;

  public SchemaListPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    refreshPanel = new RefreshPanel();
    refreshPanel.addRefreshListener(new RefreshListener() {
      @Override
      public void refresh(RefreshEvent e) {
        SchemaListPanelView.this.refresh();
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
    return "postgresql-schemas";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return "schema_name";
  }

  @Override
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getSchemaList(filter.getSqlText());
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 150),
      new QueryTableColumn("size_b", stringManager.getString("total-size-b"), 70),
      new QueryTableColumn("size_mb", stringManager.getString("total-size-mb"), 70),
      new QueryTableColumn("prc_size", stringManager.getString("prc-size"), 50),
      new QueryTableColumn("table_count", stringManager.getString("table-count"), 70),
      new QueryTableColumn("object_count", stringManager.getString("object-count"), 70),
      new QueryTableColumn("accessible", stringManager.getString("accessible"), 100, new QueryTableCellRenderer(new ColorListCellRendererFilter(new String[] {"NO"}, new Color[] {Color.RED}))),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("schema_name", stringManager.getString("schema-name"), (String[])null),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("size_b", stringManager.getString("total-size-b"), (String[])null),
      new SqlFilterDefComponent("prc_size", stringManager.getString("prc-size"), (String[])null),
      new SqlFilterDefComponent("table_count", stringManager.getString("table-count"), (String[])null),
      new SqlFilterDefComponent("object_count", stringManager.getString("object-count"), (String[])null),
      new SqlFilterDefComponent("accessible", stringManager.getString("accessible"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return null;
  }
  
}