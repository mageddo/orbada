package pl.mpak.orbada.oracle.dba.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.gui.admin.ParametersPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class OracleParametersView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);

  public Component createView(IViewAccesibilities accesibilities) {
    return new ParametersPanelView(accesibilities);
  }
   
  public String getPublicName() {
    return stringManager.getString("OracleParametersView-public-name");
  }
  
  public String getViewId() {
    return "orbada-dba-parameters-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/parameter.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    boolean result = OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
    if (result && !StringUtil.toBoolean(database.getUserProperties().getProperty("dba-role"))) {
      result = false;
    }
    return result;
  }

  public String getDescription() {
    return stringManager.getString("OracleParametersView-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaOraclePlugin.adminGroup};
  }
  
}
