/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.admin;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class SessionTabbedPane extends OrbadaTabbedPane {

  public SessionTabbedPane(IViewAccesibilities accesibilities) {
    super("SESSION",       
      new Component[] {
        new SessionLocksTabPanel(accesibilities)
    });
  }
  
}
