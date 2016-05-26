/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.views;

import java.awt.Component;
import orbada.gui.ContentPanel;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class ViewTabbedPane extends OrbadaTabbedPane {
  
  public ViewTabbedPane(IViewAccesibilities accesibilities) {
    super("VIEW",       
      new Component[] {
        new ViewColumnsPanel(accesibilities),
        new ViewTriggersPanel(accesibilities),
        new ObjectUsedByPanel(accesibilities, "VIEW"),
        new ObjectUsingPanel(accesibilities, "VIEW"),
        new ViewDetailsPanel(accesibilities),
        new ViewGrantsPanel(accesibilities),
        new ContentPanel(accesibilities),
        new ViewSourcePanel(accesibilities)
    });
  }
  
}
