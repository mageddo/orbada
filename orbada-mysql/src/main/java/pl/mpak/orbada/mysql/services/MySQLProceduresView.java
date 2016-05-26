package pl.mpak.orbada.mysql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.procedures.ProceduresPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MySQLProceduresView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public Component createView(IViewAccesibilities accesibilities) {
    return new ProceduresPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("MySQLProceduresView-public-name");
  }
  
  public String getViewId() {
    return "orbada-mysql-procedures-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/procedure.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public boolean isDefaultView() {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("MySQLProceduresView-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }
  
}
