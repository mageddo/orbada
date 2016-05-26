/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.views;

import java.awt.Color;
import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.ColorListCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ViewListView extends UniversalViewTabs {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public ViewListView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public Component getTabbedPane() {
    return new ViewTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-views";
  }

  @Override
  public String getObjectColumnName() {
    return "view_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getDescriptionColumnName() {
    return "description";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("view_name", stringManager.getString("view-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 130),
      new QueryTableColumn("accessible", stringManager.getString("accessible"), 100, new QueryTableCellRenderer(new ColorListCellRendererFilter(new String[] {"NO"}, new Color[] {Color.RED}))),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getViewList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("view_name", stringManager.getString("view-name"), (String[])null),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null),
      new SqlFilterDefComponent("accessible", stringManager.getString("accessible"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return PostgreSQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
}
