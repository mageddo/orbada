/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.types;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
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
 * @author akaluza
 */
public class TypeAttributeListTab extends UniversalPropTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public TypeAttributeListTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-type-attributes";
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
  public String getPropColumnName() {
    return "att_no";
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTypeAttributeList(filter.getSqlText(), PostgreSQLDbInfoProvider.instance.getVersion(getDatabase()));
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("att_no", stringManager.getString("ordinal"), 30),
      new QueryTableColumn("attribute_name", stringManager.getString("attribute-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("display_datatype", stringManager.getString("data-type"), 130, new QueryTableCellRenderer(SwingUtil.Color.GREEN)),
      new QueryTableColumn("att_collation", stringManager.getString("collation"), 100)
    };
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("attribute_name", stringManager.getString("attribute-name"), (String[])null),
      new SqlFilterDefComponent("display_datatype", stringManager.getString("data-type"), (String[])null),
      new SqlFilterDefComponent("att_collation", stringManager.getString("collation"), (String[])null)
    };
  }

  @Override
  public String getTitle() {
    return stringManager.getString("type-attributes");
  }

}
