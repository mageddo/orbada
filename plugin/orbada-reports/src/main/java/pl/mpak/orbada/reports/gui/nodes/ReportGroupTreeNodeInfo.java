/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.gui.nodes;

import pl.mpak.orbada.reports.db.ReportsGroupRecord;

/**
 *
 * @author akaluza
 */
public class ReportGroupTreeNodeInfo {

  private ReportsGroupRecord reportsGroup;
  
  public ReportGroupTreeNodeInfo(ReportsGroupRecord reportsGroup) {
    this.reportsGroup = reportsGroup;
  }
  
  public String getName() {
    return reportsGroup.getName();
  }
  
  public String getDescription() {
    return reportsGroup.getDescription();
  }
  
  public String getTooltip() {
    return reportsGroup.getTooltip();
  }

  public ReportsGroupRecord getReportsGroup() {
    return reportsGroup;
  }
  
  @Override
  public String toString() {
    return reportsGroup.getName();
  }

}
