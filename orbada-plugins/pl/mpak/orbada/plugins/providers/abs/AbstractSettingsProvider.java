/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers.abs;

import javax.swing.Icon;
import pl.mpak.orbada.plugins.IApplication;

/**
 *
 * @author akaluza
 */
public abstract class AbstractSettingsProvider implements IOrbadaPluginProvider {

  protected IApplication application;

  public void setApplication(IApplication application) {
    this.application = application;
  }

  public boolean isSharedProvider() {
    return true;
  }
  
  /**
   * <p>Powinna zwracaæ œcie¿kê do ustawieñ, elementy musz¹ byæ rozdzielone
   * ukoœnikami / lub \ <br>
   * np. "Apache Derby/Ogólne"
   */
  public abstract String getSettingsPath();

  public abstract Icon getIcon();
  
}
