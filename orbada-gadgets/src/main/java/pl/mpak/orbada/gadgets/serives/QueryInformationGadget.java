/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gadgets.serives;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.gadgets.OrbadaGadgetsPlugin;
import pl.mpak.orbada.gadgets.gui.QueryInformationPanel;
import pl.mpak.orbada.plugins.IGadgetAccesibilities;
import pl.mpak.orbada.plugins.providers.PerpectiveGadgetProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class QueryInformationGadget extends PerpectiveGadgetProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaGadgetsPlugin.class);

  @Override
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  @Override
  public Component createGadget(IGadgetAccesibilities accesibilities) {
    return new QueryInformationPanel(accesibilities);
  }

  @Override
  public String getPublicName() {
    return stringManager.getString("QueryInformationGadget-public-name");
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_info16.gif");
  }

  public String getDescription() {
    return stringManager.getString("QueryInformationGadget-description");
  }

  public String getGroupName() {
    return OrbadaGadgetsPlugin.gadgetsGroupName;
  }

  @Override
  public String getGadgetId() {
    return "orbada-query-info-gadget";
  }

}
