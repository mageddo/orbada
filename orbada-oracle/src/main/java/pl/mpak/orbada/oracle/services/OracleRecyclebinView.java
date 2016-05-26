package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.trash.RecyclebinPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleRecyclebinView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  public Component createView(IViewAccesibilities accesibilities) {
    return new RecyclebinPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("OracleRecyclebinView-public-name");
  }
  
  public String getViewId() {
    return "orbada-oracle-recyclebin-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    boolean result = OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
    if (result && !"true".equalsIgnoreCase(database.getUserProperties().getProperty("Ora10+"))) {
      result = false;
    }
    return result;
  }

  public String getDescription() {
    return stringManager.getString("OracleRecyclebinView-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaOraclePlugin.advancedGroup};
  }
  
}
