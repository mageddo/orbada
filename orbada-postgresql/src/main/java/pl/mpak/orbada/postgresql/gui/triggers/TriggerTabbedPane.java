/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.triggers;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class TriggerTabbedPane extends OrbadaTabbedPane {
  
  public TriggerTabbedPane(IViewAccesibilities accesibilities) {
    super("TRIGGER",       
      new Component[] {
        new TriggerSourceTab(accesibilities),
        new TriggerFunctionSourceTab(accesibilities),
        new TriggerDependsTab(accesibilities),
        new TriggerDetailsTab(accesibilities)
    });
  }

}
