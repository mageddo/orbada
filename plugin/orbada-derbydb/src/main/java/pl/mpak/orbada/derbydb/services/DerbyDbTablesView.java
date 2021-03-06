/*
 * DerbyDbObjectsView.java
 * 
 * Created on 2007-10-28, 16:42:01
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.services;

import pl.mpak.orbada.derbydb.*;
import pl.mpak.orbada.derbydb.tables.TablesPanelView;
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
public class DerbyDbTablesView extends ViewProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  public Component createView(IViewAccesibilities accesibilities) {
    return new TablesPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("DerbyDbTablesView-public-name");
  }
  
  public String getViewId() {
    return "orbada-derbydb-tables-view";
  }
  
  public Icon getIcon() {
    return null;
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return String.format(stringManager.getString("DerbyDbTablesView-description"), new Object[] {OrbadaDerbyDbPlugin.apacheDerbyDriverType});
  }

  public String getGroupName() {
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType;
  }
  
}
