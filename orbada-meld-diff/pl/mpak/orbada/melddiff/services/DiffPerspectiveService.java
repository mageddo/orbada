package pl.mpak.orbada.melddiff.services;

import pl.mpak.orbada.melddiff.OrbadaMeldDiffPlugin;
import pl.mpak.orbada.melddiff.cm.MeldDiffAction;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DiffPerspectiveService extends PerspectiveProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMeldDiffPlugin.class);

  public boolean isForDatabase(Database database) {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("DiffPerspectiveService-description");
  }

  public String getGroupName() {
    return OrbadaMeldDiffPlugin.pluginGroupName;
  }

  public void initialize() {
    accesibilities.addAction(new MeldDiffAction(accesibilities));
    
  }
  
}
