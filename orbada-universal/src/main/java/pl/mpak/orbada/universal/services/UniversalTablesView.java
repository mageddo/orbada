/*
 * DerbyDbObjectsView.java
 * 
 * Created on 2007-10-28, 16:42:01
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
import pl.mpak.orbada.universal.gui.tables.TablesPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalTablesView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  public Component createView(IViewAccesibilities accesibilities) {
    return new TablesPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("UniversalTablesView-table-name");
  }
  
  public String getViewId() {
    return "orbada-universal-tables-view";
  }
  
  public Icon getIcon() {
    return null;
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return true;
  }

  public String getDescription() {
    return stringManager.getString("UniversalTablesView-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }
  
}
