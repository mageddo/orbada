/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.types;

import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TypeListView extends UniversalViewTabs {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public TypeListView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public Component getTabbedPane() {
    return new TypeTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-types";
  }

  @Override
  public String getObjectColumnName() {
    return "type_name";
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
  public String getSql(SqlFilter filter) {
    return Sql.getTypeList(filter.getSqlText(), PostgreSQLDbInfoProvider.instance.getVersion(getDatabase()));
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("type_name", stringManager.getString("type-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 130),
      new QueryTableColumn("display_type", stringManager.getString("display-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("type_name", stringManager.getString("function-name"), (String[])null),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null),
      new SqlFilterDefComponent("display_type", stringManager.getString("display-type"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return PostgreSQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
}
