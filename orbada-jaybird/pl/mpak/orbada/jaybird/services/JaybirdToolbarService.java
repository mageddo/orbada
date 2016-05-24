/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jaybird.services;

import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.jaybird.cm.BackupDatabaseAction;
import pl.mpak.orbada.jaybird.cm.CreateDatabaseAction;
import pl.mpak.orbada.jaybird.cm.DatabaseUsersAction;
import pl.mpak.orbada.jaybird.cm.RestoreDatabaseAction;
import pl.mpak.orbada.plugins.providers.GlobalToolBarProvider;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class JaybirdToolbarService extends GlobalToolBarProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJaybirdPlugin.class);
  private static JaybirdToolbarService instance;
  private boolean actionInited = false;

  public JaybirdToolbarService() {
    instance = this;
  }

  public static JaybirdToolbarService get() {
    return instance;
  }

  @Override
  public void initialize() {
    setVisible(false);
    getToolBar().setFloatable(true);
  }

  public void setVisible(boolean value) {
    if (value && !actionInited) {
      getToolBar().add(new ToolButton(new CreateDatabaseAction()));
      getToolBar().add(new ToolButton(new BackupDatabaseAction()));
      getToolBar().add(new ToolButton(new RestoreDatabaseAction()));
      getToolBar().add(new ToolButton(new DatabaseUsersAction()));
      actionInited = true;
    }
    getToolBar().setVisible(value);
  }

  public String getDescription() {
    return OrbadaJaybirdPlugin.firebirdDriverType +" Jaybird Toolbar Service";
  }

  public String getGroupName() {
    return OrbadaJaybirdPlugin.firebirdDriverType;
  }

}
