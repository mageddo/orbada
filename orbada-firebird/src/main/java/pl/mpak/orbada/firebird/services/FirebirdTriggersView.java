package pl.mpak.orbada.firebird.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.gui.triggers.TriggersPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FirebirdTriggersView extends ViewProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  public Component createView(IViewAccesibilities accesibilities) {
    return new TriggersPanelView(accesibilities);
  }
  
  @Override
  public boolean isDefaultView() {
    return false;
  }
  
  public String getPublicName() {
    return stringManager.getString("FirebirdTriggersView-public-name");
  }
  
  public String getViewId() {
    return "orbada-firebird-triggers-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaFirebirdPlugin.firebirdDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("FirebirdTriggersView-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return null;
  }
  
}
