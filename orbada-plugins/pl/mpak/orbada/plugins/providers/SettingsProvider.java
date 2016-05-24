/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.Component;
import pl.mpak.orbada.plugins.providers.abs.AbstractSettingsProvider;

/**
 * <p>Klasa us³gui ustawieñ wtyczki.
 * <p>W getDescription() powinien byæ opis ustawieñ. Pokazany on bêdzie
 * w nag³ówku panelu ustawieñ.
 * @see ISettingsComponent
 * @author akaluza
 */
public abstract class SettingsProvider extends AbstractSettingsProvider {

  /**
   * <p>Jeœli maj¹ byæ wykonane jakieœ akcje przy akceptacji lub anulowaniu
   * ustawieñ to zwracany komponent powinien implementowaæ interfejs
   * ISettingsComponent
   * @return
   */
  public abstract Component getSettingsComponent();

}
