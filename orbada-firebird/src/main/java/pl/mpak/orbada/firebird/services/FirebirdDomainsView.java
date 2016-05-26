package pl.mpak.orbada.firebird.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.gui.domains.DomainsPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FirebirdDomainsView extends ViewProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  public Component createView(IViewAccesibilities accesibilities) {
    return new DomainsPanelView(accesibilities);
  }
  
  @Override
  public boolean isDefaultView() {
    return true;
  }
  
  public String getPublicName() {
    return stringManager.getString("FirebirdDomainsView-public-name");
  }
  
  public String getViewId() {
    return "orbada-firebird-domains-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/domain.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaFirebirdPlugin.firebirdDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("FirebirdDomainsView-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return null;
  }
  
}
