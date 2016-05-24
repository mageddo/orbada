/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;

/**
 *
 * @author akaluza
 */
public abstract class LookAndFeelProvider implements IOrbadaPluginProvider {

  protected IApplication application;

  public void setApplication(IApplication application) {
    this.application = application;
  }

  public boolean isSharedProvider() {
    return true;
  }

  public abstract String getLookAndFeelId();

  public abstract Class<? extends ILookAndFeelStarter> getLookAndFeelClass();

}
