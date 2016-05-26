/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.triggers;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.oracle.gui.StoredParametersPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class TriggerTabbedPane extends OrbadaTabbedPane {
  
  public TriggerTabbedPane(IViewAccesibilities accesibilities) {
    super("TRIGGER",
      new Component[] {
        new TriggerSourcePanel(accesibilities),
        new TriggerErrorsPanel(accesibilities),
        new TriggerColumnsPanel(accesibilities),
        new ObjectUsedByPanel(accesibilities, "TRIGGER"),
        new ObjectUsingPanel(accesibilities, "TRIGGER"),
        new StoredParametersPanel(accesibilities, "TRIGGER"),
        new TriggerDetailsPanel(accesibilities)
    });
  }
  
}
