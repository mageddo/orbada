/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IGadgetAccesibilities;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.usedb.core.Database;

/**
 * 
 * @author akaluza
 */
public abstract class PerpectiveGadgetProvider implements IOrbadaPluginProvider {

  protected IApplication application;

  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return false;
  }

  /**
   * <p>Powinna zwróciæ informacjê czy serwis przeznaczony jest dla tej bazy danych
   * @param database 
   * @return 
   */
  public abstract boolean isForDatabase(Database database);
  
  /**
   * <p>Powinna utworzyæ narzêdzie w postaci komponentu.
   * <p>Jeœli dodany zostanie listener ComponentListener to przy minimalizacji zostanie wywo³any medota hide, a przy
   * przywróceniu widocznoœci metoda show.
   * <p>Component mo¿e implementowaæ interfejsy:
   * <li><b>pl.mpak.util.Titleable</b> - pozwoli ustawiæ tytu³ narzêdzia w perspektywie</li>
   * <li><b>java.io.Closeable</b> - przy zamykaniu gad¿etu wywo³any bêdzie close</li>
   * <li><b>pl.mpak.util.Configurable</b> - obs³uga okna konfiguracyjnego</li>
   * @param accesibilities
   * @return
   */
  public abstract Component createGadget(IGadgetAccesibilities accesibilities);
  
  /**
   * <p>Nazwa publiczna widoku przeznaczona dla u¿ytkownika.
   * <p>Pozycja w menu, w ustawieniach, etc
   * @return
   */
  public abstract String getPublicName();
  
  /**
   * <p>Ikonka wyœwietlana w pozycji menu, wyboru aktywnego widoku i na
   * zak³adkach otwartych widoków.
   * @return
   */
  public abstract Icon getIcon();
  
  /**
   * <p>Wewnêtrzna, unikalna nazwa gad¿etu dla identyfikacji bezpoœredniej.
   * <p>Nie powinna siê zmieniaæ.
   * @return
   */
  public abstract String getGadgetId();
  
}
