package pl.mpak.orbada.reports.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.reports.OrbadaReportsPlugin;
import pl.mpak.orbada.reports.gui.ReportsPanelView;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class ReportsView extends ViewProvider {

  public Component createView(IViewAccesibilities accesibilities) {
    return new ReportsPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("orbada_reports");
  }
  
  public String getViewId() {
    return "orbada-reports-list-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/reports.gif");
  }

  public boolean isForDatabase(Database database) {
    return database != null;
  }

  public String getDescription() {
    return java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("reports_for_connection");
  }

  public String getGroupName() {
    return OrbadaReportsPlugin.pluginGroupName;
  }
  
}
