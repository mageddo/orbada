/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.gui.nodes;

/**
 *
 * @author akaluza
 */
public class RootGroupTreeNodeInfo {

  private String title;
  private String dtp_id;
  private String sch_id;
  private String usr_id;
  private boolean shared;
  
  public RootGroupTreeNodeInfo(String title, String dtp_id, String sch_id, String usr_id, boolean shared) {
    this.dtp_id = dtp_id;
    this.sch_id = sch_id;
    this.usr_id = usr_id;
    this.shared = shared;
    this.title = title;
  }

  public String getDtpId() {
    return dtp_id;
  }

  public String getSchId() {
    return sch_id;
  }

  public boolean isShared() {
    return shared;
  }

  public String getTitle() {
    return title;
  }

  public String getUsrId() {
    return usr_id;
  }
  
  @Override
  public String toString() {
    return title;
  }

}
