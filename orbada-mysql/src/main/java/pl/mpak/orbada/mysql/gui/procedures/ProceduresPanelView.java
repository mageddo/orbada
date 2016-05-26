package pl.mpak.orbada.mysql.gui.procedures;

import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.services.MySQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
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
public class ProceduresPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public ProceduresPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  public Component getTabbedPane() {
    return new ProcedureTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return MySQLDbInfoProvider.getCurrentDatabase(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "mysql-procedures";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getObjectColumnName() {
    return "routine_name";
  }

  @Override
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("routine_name", stringManager.getString("procedure-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("definer", stringManager.getString("definer"), 100),
      new QueryTableColumn("is_deterministic", stringManager.getString("is-deterministic"), 40),
      new QueryTableColumn("sql_data_access", stringManager.getString("sql-data-access"), 100, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("security_type", stringManager.getString("security-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.NAVY)),
      new QueryTableColumn("created", stringManager.getString("created"), 120),
      new QueryTableColumn("routine_comment", stringManager.getString("comment"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getProcedureList(filter.getSqlText());
  }

  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("object_type").setString("PROCEDURE");
  }
  
  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("routine_name", stringManager.getString("procedure-name"), (String[])null),
      new SqlFilterDefComponent("is_deterministic = 'YES'", stringManager.getString("is-deterministic"))
    };
  }

  @Override
  public String[] getSchemaList() {
    return MySQLDbInfoProvider.instance.getSchemas(getDatabase());
  }

}
