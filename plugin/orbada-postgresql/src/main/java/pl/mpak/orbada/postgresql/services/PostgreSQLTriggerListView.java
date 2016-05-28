package pl.mpak.orbada.postgresql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.gui.triggers.TriggerListView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PostgreSQLTriggerListView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    return new TriggerListView(accesibilities);
  }
  
  @Override
  public String getPublicName() {
    return stringManager.getString("PostgreSQLTriggerListView-public-name");
  }
  
  @Override
  public String getViewId() {
    return "orbada-postgresql-triggers-view";
  }
  
  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trigger.gif");
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public boolean isDefaultView() {
    return false;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLTriggerListView-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }
  
}
