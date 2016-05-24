/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.Component;
import pl.mpak.orbada.plugins.providers.abs.AbstractSettingsProvider;

/**
 *
 * @author akaluza
 */
public abstract class SchemaSettingsProvider extends AbstractSettingsProvider {

  /**
   * <p>Powinna zwróciæ informacjê czy widok przeznaczony jest dla tego schematu.
   * <p>Wywo³ywane jest przy ka¿dej zmianie sterownika, getSettingsComponent ukrywany jest lub
   * pokazywany w zale¿noœci od wartoœci zwracanej przez t¹ funkcjê
   * @param database
   * @return
   */
  public abstract boolean isForDriverType(String driverTypeName);

  /**
   * <p>Jeœli maj¹ byæ wykonane jakieœ akcje przy akceptacji lub anulowaniu
   * ustawieñ to zwracany komponent powinien implementowaæ interfejs
   * <p>Najpierw wywo³ywana jest ta funkcja, a dopiero potem isForDriver
   * ISettingsComponent
   * @return
   */
  public abstract Component getSettingsComponent(String schemaId);



}
