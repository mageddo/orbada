/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.AbstractSettingsProvider;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.usedb.core.Database;

/**
 * <p>Klasa us³gui ustawieñ perspektywy.
 * @see ISettingsComponent
 * @author akaluza
 */
public abstract class PerspectiveSettingsProvider extends AbstractSettingsProvider {

  /**
   * <p>Powinna zwróciæ informacjê czy widok przeznaczony jest dla tej bazy danych
   * @param database 
   * @return 
   */
  public abstract boolean isForDatabase(Database database);

  /**
   * <p>Jeœli maj¹ byæ wykonane jakieœ akcje przy akceptacji lub anulowaniu
   * ustawieñ to zwracany komponent powinien implementowaæ interfejs
   * ISettingsComponent
   * @return
   */
  public abstract Component getSettingsComponent(Database database);

}
