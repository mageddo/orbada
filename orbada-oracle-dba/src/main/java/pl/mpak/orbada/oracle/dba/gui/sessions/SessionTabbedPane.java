/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.gui.sessions;

import java.awt.Component;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SessionTabbedPane extends OrbadaTabbedPane {

  public SessionTabbedPane(IViewAccesibilities accesibilities) {
    super("SESSION",       
      new Component[] {
        (StringUtil.toBoolean(accesibilities.getDatabase().getUserProperties().getProperty("sql-view-role", "false")) ? new SessionSqlsTabPanel(accesibilities) : null),
        (StringUtil.toBoolean(accesibilities.getDatabase().getUserProperties().getProperty("lock-view-role", "false")) ? new SessionLocksTabPanel(accesibilities) : null),
        (StringUtil.toBoolean(accesibilities.getDatabase().getUserProperties().getProperty("sess-stat-view-role", "false")) ? new SessionStatsTabPanel(accesibilities) : null)
    });
  }
  
}
