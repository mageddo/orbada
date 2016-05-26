/*
 * DerbyDbObjectsView.java
 * 
 * Created on 2007-10-28, 16:42:01
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.gui.aliases.AliasesPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class HSqlDbAliasesView extends ViewProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaHSqlDbPlugin.class);

  public Component createView(IViewAccesibilities accesibilities) {
    return new AliasesPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("HSqlDbAliasesView-public-name");
  }
  
  public String getViewId() {
    return "orbada-hsqldb-aliases-view";
  }
  
  public Icon getIcon() {
    return null;
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return 
      OrbadaHSqlDbPlugin.hsqlDbDriverType.equals(database.getDriverType()) &&
      HSqlDbInfoProvider.getVersionTest(database) == HSqlDbInfoProvider.hsqlDb18;
  }

  public String getDescription() {
    return stringManager.getString("HSqlDbAliasesView-description");
  }

  public String getGroupName() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType;
  }
  
}
