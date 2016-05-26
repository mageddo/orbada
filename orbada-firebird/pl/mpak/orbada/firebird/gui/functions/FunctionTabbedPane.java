/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.firebird.gui.functions;

import java.awt.Component;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class FunctionTabbedPane extends OrbadaTabbedPane {
  
  public FunctionTabbedPane(IViewAccesibilities accesibilities) {
    super("FUNCTION",
      new Component[] {
        new FunctionArgumentsPanel(accesibilities),
        new FunctionSourcePanel(accesibilities)
    });
  }
  
}
