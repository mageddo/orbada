/*
 * DatabaseInfoView.java
 * 
 * Created on 2007-10-28, 11:50:54
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.services;

import pl.mpak.orbada.universal.*;
import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.universal.gui.DatabaseInfoPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DatabaseInfoView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  public Component createView(IViewAccesibilities accesibilities) {
    return new DatabaseInfoPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("DatabaseInfoView-public-name");
  }
  
  public String getViewId() {
    return "orbada-universal-database-info-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_info16.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return true;
  }

  public String getDescription() {
    return stringManager.getString("DatabaseInfoView-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }
  
}
