/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.gui.nodes;

import pl.mpak.orbada.reports.db.ReportRecord;

/**
 *
 * @author akaluza
 */
public class ReportTreeNodeInfo {

  private ReportRecord report;
  
  public ReportTreeNodeInfo(ReportRecord report) {
    this.report = report;
  }
  
  public String getName() {
    return report.getName();
  }
  
  public String getDescription() {
    return report.getDescription();
  }
  
  public String getTooltip() {
    return report.getTooltip();
  }

  public ReportRecord getReport() {
    return report;
  }
  
  @Override
  public String toString() {
    return report.getName();
  }

}
