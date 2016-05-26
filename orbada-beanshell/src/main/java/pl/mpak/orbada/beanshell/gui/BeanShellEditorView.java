/*
 * BeanShellEditorView.java
 * 
 * Created on 2007-11-08, 19:03:05
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.beanshell.gui;

import pl.mpak.orbada.beanshell.*;
import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class BeanShellEditorView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("beanshell");

  public Component createView(IViewAccesibilities accesibilities) {
    return new BeanShellEditorPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("BeanShellEditorView-public-name");
  }
  
  public String getViewId() {
    return "orbada-beanshell-editor-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/beanshell/res/icons/icon.gif");
  }

  public boolean isForDatabase(Database database) {
//    if (database == null) {
//      return true;
//    }
    return true;
  }

  public String getDescription() {
    return stringManager.getString("BeanShellEditorView-description");
  }

  public String getGroupName() {
    return OrbadaBeanshellPlugin.beanshellGroupName;
  }
  
}
