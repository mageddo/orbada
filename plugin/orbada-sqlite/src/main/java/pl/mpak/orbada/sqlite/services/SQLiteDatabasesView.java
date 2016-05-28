package pl.mpak.orbada.sqlite.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.gui.databases.DatabasesPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SQLiteDatabasesView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  public Component createView(IViewAccesibilities accesibilities) {
    return new DatabasesPanelView(accesibilities);
  }
  
  @Override
  public boolean isDefaultView() {
    return false;
  }
  
  public String getPublicName() {
    return stringManager.getString("SQLiteDatabasesView-public-name");
  }
  
  public String getViewId() {
    return "orbada-sqlite-databases-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/databases.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaSQLitePlugin.driverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("SQLiteDatabasesView-description");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return null;
  }
  
}
