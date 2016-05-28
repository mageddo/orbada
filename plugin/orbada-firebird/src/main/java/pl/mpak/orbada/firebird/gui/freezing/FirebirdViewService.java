package pl.mpak.orbada.firebird.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public abstract class FirebirdViewService extends ViewProvider {

  abstract public Component createView(IViewAccesibilities accesibilities);
  
  abstract public String getPublicName();
  
  public String getViewId() {
    return null;
  }
  
  abstract public Icon getIcon();

  public boolean isForDatabase(Database database) {
    return true;
  }

  public String getDescription() {
    return "\"" +OrbadaFirebirdPlugin.firebirdDriverType +"\"";
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }
  
}
