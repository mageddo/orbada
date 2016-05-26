/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IGadgetAccesibilities;
import pl.mpak.orbada.plugins.providers.PerpectiveGadgetProvider;
import pl.mpak.orbada.reports.OrbadaReportsPlugin;
import pl.mpak.orbada.reports.gui.ReportsGadgetPanel;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class ReportsGadget extends PerpectiveGadgetProvider {

  @Override
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  @Override
  public Component createGadget(IGadgetAccesibilities accesibilities) {
    return new ReportsGadgetPanel(accesibilities);
  }

  @Override
  public String getPublicName() {
    return java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("orbada_reports");
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/reports.gif");
  }

  public String getDescription() {
    return java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("reports_for_connection");
  }

  public String getGroupName() {
    return OrbadaReportsPlugin.pluginGroupName;
  }

  @Override
  public String getGadgetId() {
    return "orbada-reports-gadget";
  }

}
