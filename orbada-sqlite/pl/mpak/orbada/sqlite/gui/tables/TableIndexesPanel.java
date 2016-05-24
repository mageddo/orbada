package pl.mpak.orbada.sqlite.gui.tables;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableIndexesPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public TableIndexesPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableIndexesPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "sqlite-table-indexes";
  }

  @Override
  public String getObjectColumnName() {
    return null;
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getPropColumnName() {
    return "name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("seq", stringManager.getString("pos"), 30),
      new QueryTableColumn("name", stringManager.getString("index-name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("unique", stringManager.getString("unique"), 50)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getIndexList(currentSchemaName, currentObjectName);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return null;
  }
  
  @Override
  protected boolean executeSql() {
    return true;
  }
  
}
