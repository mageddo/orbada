package pl.mpak.orbada.sqlite.gui.tables;

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
public class TablesPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  public TablesPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  public Component getTabbedPane() {
    return new TableTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return "main";
  }

  @Override
  public String getPanelName() {
    return "sqlite-tables";
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
      new QueryTableColumn("name", stringManager.getString("table-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD))
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableList(currentSchemaName);
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
