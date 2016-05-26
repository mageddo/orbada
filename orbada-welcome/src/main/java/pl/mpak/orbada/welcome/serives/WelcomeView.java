/*
 * DerbyDbObjectsView.java
 * 
 * Created on 2007-10-28, 16:42:01
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.welcome.serives;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.welcome.OrbadaWelcomePlugin;
import pl.mpak.orbada.welcome.gui.WelcomeViewPanel;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class WelcomeView extends ViewProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("welcome");

  public Component createView(IViewAccesibilities accesibilities) {
    return new WelcomeViewPanel(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("WelcomeView-public-name");
  }
  
  public String getViewId() {
    return "orbada-welcome-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/orbada16.png");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return true;
    }
    return false;
  }

  public String getDescription() {
    return stringManager.getString("WelcomeView-description");
  }

  public String getGroupName() {
    return OrbadaWelcomePlugin.todoGroupName;
  }
  
}
