/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.tables;

import java.awt.Color;
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
public class TableConstraintListTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public TableConstraintListTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-table-constraints";
  }

  @Override
  public String getObjectColumnName() {
    return "table_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getPropColumnName() {
    return "constraint_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("constraint_name", stringManager.getString("constraint-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("columns", stringManager.getString("constraint-columns"), 130),
      new QueryTableColumn("constraint_type", stringManager.getString("constraint-type"), 130, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("deferrable", stringManager.getString("constraint-deferrable"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(null, null))),
      new QueryTableColumn("deferred", stringManager.getString("constraint-deferred"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(null, null))),
      new QueryTableColumn("validated", stringManager.getString("constraint-validated"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(null, Color.RED))),
      new QueryTableColumn("fk_table_name", stringManager.getString("constraint-fk-table-name"), 130),
      new QueryTableColumn("fk_columns", stringManager.getString("constraint-fk-columns"), 130),
      new QueryTableColumn("constraint_def", stringManager.getString("constraint-def"), 200),
      new QueryTableColumn("update_rule", stringManager.getString("constraint-update-rule"), 130),
      new QueryTableColumn("delete_rule", stringManager.getString("constraint-delete-rule"), 130),
      new QueryTableColumn("match_type", stringManager.getString("constraint-match-type"), 130),
      new QueryTableColumn("domain_name", stringManager.getString("constraint-domain-name"), 130),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getConstraintList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("constraint_name", stringManager.getString("constraint-name"), (String[])null),
      new SqlFilterDefComponent("columns", stringManager.getString("constraint-columns"), (String[])null),
      new SqlFilterDefComponent("fk_table_name", stringManager.getString("constraint-fk-table-name"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null),
      new SqlFilterDefComponent("validated = 'N'", stringManager.getString("constraint-not-valid")),
      new SqlFilterDefComponent("constraint_name is not null", stringManager.getString("constraint-only-named"))
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("constraints");
  }

}
