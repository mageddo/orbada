/*
 * DerbyDbPerspectiveProvider.java
 *
 * Created on 2007-11-11, 19:20:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.services;

import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalPerspectiveProvider extends PerspectiveProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  public boolean isForDatabase(Database database) {
    return database != null;
  }

  public String getDescription() {
    return stringManager.getString("UniversalPerspectiveProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

  public void initialize() {
  }
  
}
