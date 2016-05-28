package pl.mpak.orbada.reports.gui;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.reports.OrbadaReportsPlugin;
import pl.mpak.orbada.reports.db.ReportRecord;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author proznicki
 */
public class ReportViewService extends ViewProvider {
  
  private ReportRecord report;
  
  public ReportViewService(ReportRecord report) {
    this.report = report;
  }

  public Component createView(IViewAccesibilities accesibilities) {
    return new TableReportPanel(accesibilities, report);
  }
  
  public String getPublicName() {
    return report.getName();
  }
  
  public String getViewId() {
    return null;
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/reports.gif");
  }

  public boolean isForDatabase(Database database) {
    return database != null;
  }

  public String getDescription() {
    return report.getDescription();
  }

  public String getGroupName() {
    return OrbadaReportsPlugin.pluginGroupName;
  }
  
}
