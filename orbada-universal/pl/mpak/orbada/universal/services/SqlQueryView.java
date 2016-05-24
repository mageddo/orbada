/*
 * SQLQueryView.java
 *
 * Created on 2007-10-18, 20:23:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.SqlQueryPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SqlQueryView extends ViewProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  public Component createView(IViewAccesibilities accesibilities) {
    return new SqlQueryPanelView(accesibilities);
  }
  
  @Override
  public boolean isDefaultView() {
    return true;
  }
  
  public String getPublicName() {
    return stringManager.getString("SqlQueryView-public-name");
  }
  
  public String getViewId() {
    return "orbada-universal-sqlquery-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute16.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return true;
  }

  public String getDescription() {
    return stringManager.getString("SqlQueryView-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }
  
}
