/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.functions;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class TriggerFunctionTabbedPane extends OrbadaTabbedPane {
  
  public TriggerFunctionTabbedPane(IViewAccesibilities accesibilities) {
    super("TRIGGER FUNCTION",
      new Component[] {
        new FunctionSourceTab(accesibilities),
        new FunctionArgumentListTab(accesibilities),
        new FunctionTriggerSourceTab(accesibilities),
        new FunctionPrivilegeListTab(accesibilities),
        new FunctionDetailsTab(accesibilities),
        new FunctionDependsTab(accesibilities)
    });
  }
  
}
