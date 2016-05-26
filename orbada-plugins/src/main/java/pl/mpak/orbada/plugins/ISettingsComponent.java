/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

/**
 * <p>Interfejs do obs³ugi ustawieñ wtyczki i perspektywy.
 * <p>Komponent, panel ustawieñ mo¿e implementowaæ równie¿ interfejs java.io.Closeable.
 * Przy zamykaniu okna ustawieñ bêdzie on wywo³any jeœli zosta³ utworzony.
 * @see SettingProvider
 * @see PerspectiveSettingProvider
 * @author akaluza
 */
public interface ISettingsComponent {

  public void restoreSettings();
  
  public void applySettings();
  
  public void cancelSettings();

}
