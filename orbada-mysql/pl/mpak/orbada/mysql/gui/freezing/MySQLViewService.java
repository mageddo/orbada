package pl.mpak.orbada.mysql.gui.freezing;

import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public abstract class MySQLViewService extends ViewProvider {

  public String getViewId() {
    return null;
  }
  
  public boolean isForDatabase(Database database) {
    return true;
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }
  
}
