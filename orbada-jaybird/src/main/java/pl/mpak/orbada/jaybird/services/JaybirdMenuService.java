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
import pl.mpak.orbada.plugins.providers.GlobalMenuProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class JaybirdMenuService extends GlobalMenuProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJaybirdPlugin.class);
  private static JaybirdMenuService instance;
  private boolean actionInited = false;

  public JaybirdMenuService() {
    instance = this;
  }

  public static JaybirdMenuService get() {
    return instance;
  }

  @Override
  public void initialize() {
    setVisible(false);
    getMenu().setText("Jaybird");
  }

  public void setVisible(boolean value) {
    if (value && !actionInited) {
      getMenu().add(new CreateDatabaseAction());
      getMenu().add(new BackupDatabaseAction());
      getMenu().add(new RestoreDatabaseAction());
      getMenu().add(new DatabaseUsersAction());
      actionInited = true;
    }
    getMenu().setVisible(value);
  }

  public String getDescription() {
    return OrbadaJaybirdPlugin.firebirdDriverType +" Jaybird Menu Service";
  }

  public String getGroupName() {
    return OrbadaJaybirdPlugin.firebirdDriverType;
  }

}
