/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.aggregates;

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
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class AggregateListView extends UniversalViewTabs {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public AggregateListView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public Component getTabbedPane() {
    return new AggregateTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-aggregates";
  }

  @Override
  public String getObjectColumnName() {
    return "full_aggregate_name";
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
    return Sql.getAggregateList(filter.getSqlText());
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("aggregate_name", stringManager.getString("aggregate-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 130),
      new QueryTableColumn("aggreagte_arguments", stringManager.getString("aggreagte-arguments"), 100),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("aggregate_name", stringManager.getString("aggregate-name"), (String[])null),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null),
      new SqlFilterDefComponent("aggreagte_arguments", stringManager.getString("aggreagte-arguments"), (String[])null)
    };
  }

  @Override
  public String[] getSchemaList() {
    return PostgreSQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
}
