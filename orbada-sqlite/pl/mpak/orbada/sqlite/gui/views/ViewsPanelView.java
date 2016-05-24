package pl.mpak.orbada.sqlite.gui.views;

import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.Sql;
import pl.mpak.orbada.sqlite.services.SQLiteDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class ViewsPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public ViewsPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  public Component getTabbedPane() {
    return new ViewTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return "main";
  }

  @Override
  public String getPanelName() {
    return "sqlite-views";
  }

  @Override
  public String getObjectColumnName() {
    return "name";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("name", stringManager.getString("view-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD))
    };        
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getViewList(currentSchemaName);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return null;
  }

  @Override
  public String[] getSchemaList() {
    return SQLiteDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
}
