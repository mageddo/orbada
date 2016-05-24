/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.tables;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableReferencedToListTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public TableReferencedToListTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-table-referenced-to";
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
    return "constraint_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("constraint_name", stringManager.getString("constraint-name"), 150),
      new QueryTableColumn("columns", stringManager.getString("columns"), 200),
      new QueryTableColumn("r_schema_name", stringManager.getString("r-schema-name"), 130),
      new QueryTableColumn("r_object_name", stringManager.getString("r-object-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("r_constraint_name", stringManager.getString("r-constraint-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("r_columns", stringManager.getString("r-columns"), 200)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getUsingList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("constraint_name", stringManager.getString("constraint-name"), (String[])null),
      new SqlFilterDefComponent("columns", stringManager.getString("columns"), (String[])null),
      new SqlFilterDefComponent("r_schema_name", stringManager.getString("r-schema-name"), (String[])null),
      new SqlFilterDefComponent("r_object_name", stringManager.getString("r-object-name"), (String[])null),
      new SqlFilterDefComponent("r_constraint_name", stringManager.getString("r-constraint-name"), (String[])null),
      new SqlFilterDefComponent("r_columns", stringManager.getString("r-columns"), (String[])null)
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("referenced-to");
  }
  
}
