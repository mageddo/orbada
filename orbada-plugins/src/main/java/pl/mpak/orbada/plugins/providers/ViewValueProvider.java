/*
 * TableViewValueProvider.java
 *
 * Created on 2007-11-26, 21:10:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.Component;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;

/**
 * <p>S³u¿y do implementacji podgl¹du wartoœci tabeli i innych danych
 * <p>getDescription() powinno zwracaæ opis dla menu
 * @author akaluza
 */
public abstract class ViewValueProvider implements IOrbadaPluginProvider {
  
  protected IApplication application;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return false;
  }
  
  /**
   * <p>Powinna tworzyæ np JPanel z podgl¹dem wartoœci
   * <p>value mo¿e byæ typu Variant
   * <p>Jeœli jest wymagana jakaœ specjalna akcja zwolnienia zasobów to 
   * Component powinien implementowaæ interfejs Closeable
   * @param value 
   * @return 
   */
  public abstract Component createComponent(Object value);
  
}
