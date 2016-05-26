package pl.mpak.orbada.mysql.gui.procedures;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class ProcedurePrivilegesPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public ProcedurePrivilegesPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("ProcedurePrivilegesPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-procedure-privileges";
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
      new QueryTableColumn("grantee", stringManager.getString("grantee"), 150),
      new QueryTableColumn("privilege", stringManager.getString("privilege-type"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("grantor", stringManager.getString("grantor"), 150)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getRoutinePrivilegeList(filter.getSqlText());
  }

  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("object_type").setString("PROCEDURE");
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("grantee", stringManager.getString("grantee"), (String[])null),
      new SqlFilterDefComponent("privilege", stringManager.getString("privilege-type"), (String[])null),
      new SqlFilterDefComponent("grantor", stringManager.getString("grantor"), (String[])null)
    };
  }
  
}
