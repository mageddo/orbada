package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.providers.abs.ActionProvider;

/**
 * <p>Akcja zostanie dodana do listy narzêdzi w menu "Narzêdzia" (Tools)
 * @author akaluza
 */
public abstract class ToolConfigurationActionProvider extends ActionProvider {

  @Override
  public boolean isSharedProvider() {
    return true;
  }

  /**
   * <p>Pozwla okreœliæ czy akcja ma oprócz miejsca w menu "Narzêdzia" znaleŸæ siê równie¿ jako
   * przycisk na pasku przycisków narzêdzi.
   * @return
   */
  public boolean isButton() {
    return false;
  }
  
}
