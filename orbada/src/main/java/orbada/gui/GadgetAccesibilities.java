/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui;

import java.io.Closeable;
import orbada.core.Application;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IGadgetAccesibilities;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public abstract class GadgetAccesibilities implements IGadgetAccesibilities, Closeable {

  private PerspectivePanel perspective;

  public GadgetAccesibilities(PerspectivePanel perspective) {
    this.perspective = perspective;
  }
  
  public Database getDatabase() {
    return perspective.getDatabase();
  }

  public IApplication getApplication() {
    return Application.get();
  }

  public IPerspectiveAccesibilities getPerspectiveAccesibilities() {
    return perspective.perspectiveAccesibilities;
  }
  
  public void close() {
    
  }
  
}
