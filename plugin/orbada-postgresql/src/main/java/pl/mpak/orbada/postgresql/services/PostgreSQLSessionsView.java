package pl.mpak.orbada.postgresql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.gui.admin.SessionsPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PostgreSQLSessionsView extends ViewProvider {
 
  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    return new SessionsPanelView(accesibilities);
  }
  
  @Override
  public String getPublicName() {
    return stringManager.getString("PostgreSQLSessionsView-public-name");
  }
  
  @Override
  public String getViewId() {
    return "orbada-postgresql-sessions-view";
  }
  
  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/sessions.gif");
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    boolean result = OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
    return result;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLSessionsView-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaPostgreSQLPlugin.adminGroup};
  }
  
}
