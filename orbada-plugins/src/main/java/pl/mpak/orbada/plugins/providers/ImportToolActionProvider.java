package pl.mpak.orbada.plugins.providers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.providers.abs.ActionProvider;
import pl.mpak.usedb.core.Database;

/**
 * <p>Akcja zostanie dodana do listy narzêdzi w menu "Narzêdzia" (Tools)
 * @author akaluza
 */
public abstract class ImportToolActionProvider extends ActionProvider {

  public ImportToolActionProvider() {
    addActionListener(createActionListener());
    setTooltip(getDescription());
  }
  
  @Override
  public boolean isSharedProvider() {
    return true;
  }

  /**
   * <p>Pozwla okreœliæ czy akcja ma oprócz miejsca w menu "Narzêdzia/Import" znaleŸæ siê równie¿ jako
   * przycisk na pasku przycisków narzêdzi.
   * @return
   */
  public boolean isButton() {
    return false;
  }
  
  abstract protected void doImport(Database database);
  
  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        IPerspectiveAccesibilities perspective = application.getActivePerspective();
        if (perspective != null && perspective.getDatabase() != null) {
          doImport(perspective.getDatabase());
        }
      }
    };
  }

}
