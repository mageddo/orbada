package pl.mpak.mpak.oracle.tune.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.gui.SqlPlanPanelView;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class OracleTuneSqlPlanView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-tune");

  public Component createView(IViewAccesibilities accesibilities) {
    if (StringUtil.toBoolean(accesibilities.getDatabase().getUserProperties().getProperty("v$sql_plan"))) {
      return new SqlPlanPanelView(accesibilities);
    }
    MessageBox.show(getPublicName(), stringManager.getString("OracleTuneSqlPlanView-access-info"));
    return null;
  }
  
  public String getPublicName() {
    return stringManager.getString("OracleTuneSqlPlanView-public-name");
  }
  
  public String getViewId() {
    return "orbada-oracle-tune-sql-plan-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/explain_plan.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("OracleTuneSqlPlanView-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaOracleTunePlugin.tuneGroup};
  }
  
}
