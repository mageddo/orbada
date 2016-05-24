/*
 * DerbyDbObjectsView.java
 * 
 * Created on 2007-10-28, 16:42:01
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.dblinks.DbLinksPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleDbLinksView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public Component createView(IViewAccesibilities accesibilities) {
    return new DbLinksPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("OracleDbLinksView-public-name");
  }
  
  public String getViewId() {
    return "orbada-oracle-dblinks-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_link.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("OracleDbLinksView-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaOraclePlugin.advancedGroup};
  }
  
}
