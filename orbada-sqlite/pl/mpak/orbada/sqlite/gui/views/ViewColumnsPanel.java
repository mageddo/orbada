package pl.mpak.orbada.sqlite.gui.views;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class ViewColumnsPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public ViewColumnsPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("ViewColumnsPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "sqlite-view-columns";
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
      new QueryTableColumn("cid", stringManager.getString("pos"), 30),
      new QueryTableColumn("name", stringManager.getString("column-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("type", stringManager.getString("column-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("notnull", stringManager.getString("null-q"), 30)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getColumnList(currentSchemaName, currentObjectName);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return null;
  }

}
