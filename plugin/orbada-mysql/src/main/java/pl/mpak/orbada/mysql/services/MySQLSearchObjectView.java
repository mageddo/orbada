package pl.mpak.orbada.mysql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.SearchObjectPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MySQLSearchObjectView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public Component createView(IViewAccesibilities accesibilities) {
    return new SearchObjectPanelView(accesibilities);
  }
  
  @Override
  public boolean isDefaultView() {
    return false;
  }
  
  public String getPublicName() {
    return stringManager.getString("MySQLSearchObjectView-public-name");
  }
  
  public String getViewId() {
    return "orbada-mysql-search-objects-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/find_object16.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("MySQLSearchObjectView-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }
  
}
