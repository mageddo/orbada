package pl.mpak.orbada.mysql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.admin.SessionsPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MySQLSessionsView extends ViewProvider {
 
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public Component createView(IViewAccesibilities accesibilities) {
    return new SessionsPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("MySQLSessionsView-public-name");
  }
  
  public String getViewId() {
    return "orbada-mysql-sessions-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/sessions.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("MySQLSessionsView-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaMySQLPlugin.adminGroup};
  }
  
}
