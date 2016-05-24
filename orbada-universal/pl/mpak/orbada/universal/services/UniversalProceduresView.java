package pl.mpak.orbada.universal.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.procedures.ProceduresPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalProceduresView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  public Component createView(IViewAccesibilities accesibilities) {
    return new ProceduresPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("UniversalProceduresView-public-name");
  }
  
  public String getViewId() {
    return "orbada-universal-procedures-view";
  }
  
  public Icon getIcon() {
    return null;
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return true;
  }

  public String getDescription() {
    return stringManager.getString("UniversalProceduresView-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }
  
}
