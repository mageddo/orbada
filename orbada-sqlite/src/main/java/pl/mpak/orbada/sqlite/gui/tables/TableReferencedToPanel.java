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
public class TableReferencedToPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public TableReferencedToPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableReferencedToPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "sqlite-table-referenced-to";
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
    return "from";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("table", stringManager.getString("table"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("from", stringManager.getString("column"), 120),
      new QueryTableColumn("to", stringManager.getString("ref-column"), 120)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getReferencesList(currentSchemaName, currentObjectName);
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
