/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public abstract class PostgreSQLDependsTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public PostgreSQLDependsTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-object-depends";
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
    return "object_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("object_type", stringManager.getString("object-type"), 150, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 130),
      new QueryTableColumn("object_owner", stringManager.getString("object-owner"), 130),
      new QueryTableColumn("object_name", stringManager.getString("object-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("r_object_type", stringManager.getString("depend-object-type"), 150, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("r_object_name", stringManager.getString("depend-object-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("r_object_attribute", stringManager.getString("depend-object-attribute"), 200),
    };
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("object_type", stringManager.getString("object-type"), (String[])null),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("object_owner", stringManager.getString("object-owner"), (String[])null),
      new SqlFilterDefComponent("object_name", stringManager.getString("object-name"), (String[])null),
      new SqlFilterDefComponent("r_object_type", stringManager.getString("depend-object-type"), (String[])null),
      new SqlFilterDefComponent("r_object_name", stringManager.getString("depend-object-name"), (String[])null)
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("depends");
  }
  
}
