package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.usedb.core.Database;

/**
 * <p>Akcja zostanie dodana do odpowiedniego przycisku Akcje zwi¹zanego z komponentem table, edytora, etc.
 * @author akaluza
 * @see pl.mpak.orbada.gui.cm.ComponentActionAction
 */
public abstract class ComponentActionsProvider implements IOrbadaPluginProvider {
  
  protected IApplication application;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return true;
  }
  
  /**
   * <p>Testuje czy akcja jest przeznaczona dla konkretnej bazy danych i konkretnego typu akcji</p>
   * @param database
   * @param actionType
   * @return
   */
  public abstract ComponentAction[] getForComponent(Database database, String actionType);
  
}
