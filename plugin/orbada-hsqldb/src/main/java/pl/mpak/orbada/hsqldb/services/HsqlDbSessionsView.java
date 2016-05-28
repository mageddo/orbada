package pl.mpak.orbada.hsqldb.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.gui.dba.SessionsPanelView;
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
public class HsqlDbSessionsView extends ViewProvider {
 
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  public Component createView(IViewAccesibilities accesibilities) {
    return new SessionsPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("HsqlDbSessionsView-public-name");
  }
  
  public String getViewId() {
    return "orbada-hsqldb-sessions-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/sessions.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    boolean result = OrbadaHSqlDbPlugin.hsqlDbDriverType.equals(database.getDriverType());
    if (result && !StringUtil.toBoolean(database.getUserProperties().getProperty("session-admin-role"))) {
      result = false;
    }
    return result;
  }

  public String getDescription() {
    return stringManager.getString("HsqlDbSessionsView-description");
  }

  public String getGroupName() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return null; //new String[] {OrbadaOraclePlugin.adminGroup};
  }
  
}
