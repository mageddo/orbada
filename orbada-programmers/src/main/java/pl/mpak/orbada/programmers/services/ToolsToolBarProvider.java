/*
 * ToolsToolBarProvider.java
 * 
 * Created on 2007-11-03, 14:44:56
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.services;

import pl.mpak.orbada.plugins.providers.GlobalToolBarProvider;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ToolsToolBarProvider extends GlobalToolBarProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaProgrammersPlugin.class);

  public void initialize() {
    getToolBar().add(new ToolButton(OrbadaProgrammersPlugin.cmCopyCode));
    getToolBar().add(new ToolButton(OrbadaProgrammersPlugin.cmPasteCode));
  }

  public String getGroupName() {
    return OrbadaProgrammersPlugin.programmersGroupName;
  }

  public String getDescription() {
    return stringManager.getString("ToolsToolBarProvider-description");
  }

}
