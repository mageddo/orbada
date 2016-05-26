package pl.mpak.orbada.mysql.gui.functions;

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
public class FunctionParametersPanel extends UniversalPropTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public FunctionParametersPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("FunctionParametersPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-function-prameters";
  }

  @Override
  public String getObjectColumnName() {
    return "object_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getPropColumnName() {
    return "param_no";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("param_no", stringManager.getString("pos"), 30),
      new QueryTableColumn("param", stringManager.getString("parameter"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("param_type", stringManager.getString("mode"), 70, new QueryTableCellRenderer(SwingUtil.Color.NAVY))
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getRoutineParameterList(filter.getSqlText());
  }
  
  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("object_type").setString("FUNCTION");
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("param", stringManager.getString("parameter"), (String[])null),
      new SqlFilterDefComponent("param_type", stringManager.getString("mode"), new String[] {"", "'IN'", "'OUT'", "'INOUT'", "'RETURNS'"})
    };
  }
  
}
