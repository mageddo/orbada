/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.views;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.YesNoCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ViewPrivilegeListTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public ViewPrivilegeListTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-view-privileges";
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
  public String getPropColumnName() {
    return "privilege";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("grantor", stringManager.getString("grantor"), 130),
      new QueryTableColumn("grantee", stringManager.getString("grantee"), 130),
      new QueryTableColumn("privilege", stringManager.getString("privilege"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("is_grantable", stringManager.getString("is_grantable"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(SwingUtil.Color.GREEN, null))),
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getPrivilegeList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("grantor", stringManager.getString("grantor"), (String[])null),
      new SqlFilterDefComponent("grantee", stringManager.getString("grantee"), (String[])null),
      new SqlFilterDefComponent("privilege", stringManager.getString("privilege"), (String[])null),
      new SqlFilterDefComponent("is_grantable = 'Y'", stringManager.getString("is_grantable"))
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("privileges");
  }
  
}
