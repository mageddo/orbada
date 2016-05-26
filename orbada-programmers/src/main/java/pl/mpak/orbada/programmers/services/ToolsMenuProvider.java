/*
 * ToolsMenuProvider.java
 * 
 * Created on 2007-11-03, 14:43:34
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.services;

import pl.mpak.orbada.plugins.providers.GlobalMenuProvider;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.orbada.programmers.cm.ClassesAction;
import pl.mpak.orbada.programmers.cm.DatabaseUserPropertiesAction;
import pl.mpak.orbada.programmers.cm.ResolversAction;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ToolsMenuProvider extends GlobalMenuProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaProgrammersPlugin.class);

  public void initialize() {
    getMenu().setText(SwingUtil.setButtonText(getMenu(), stringManager.getString("ToolsMenuProvider-menu-text")));
    getMenu().add(OrbadaProgrammersPlugin.cmCopyCode);
    getMenu().add(OrbadaProgrammersPlugin.cmPasteCode);
    getMenu().addSeparator();
    getMenu().add(new ResolversAction());
    getMenu().add(new DatabaseUserPropertiesAction());
    getMenu().add(new ClassesAction());
  }

  public String getGroupName() {
    return OrbadaProgrammersPlugin.programmersGroupName;
  }

}
