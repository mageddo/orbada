/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.gadgets.QuickSearchObject;
import pl.mpak.orbada.plugins.IGadgetAccesibilities;
import pl.mpak.orbada.plugins.providers.PerpectiveGadgetProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleQuickSearchObject extends PerpectiveGadgetProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  @Override
  public Component createGadget(IGadgetAccesibilities accesibilities) {
    return new QuickSearchObject(accesibilities);
  }

  @Override
  public String getPublicName() {
    return stringManager.getString("OracleQuickSearchObject-public-name");
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_object16.gif");
  }

  public String getDescription() {
    return stringManager.getString("OracleQuickSearchObject-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  @Override
  public String getGadgetId() {
    return "orbada-oracle-gadget-quick-search-object";
  }

}
