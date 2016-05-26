/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.functions;

import java.awt.Component;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.oracle.gui.StoredParametersPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class FunctionTabbedPane extends OrbadaTabbedPane {
  
  public FunctionTabbedPane(IViewAccesibilities accesibilities) {
    super("FUNCTION",
      new Component[] {
        new FunctionSourcePanel(accesibilities, "FUNCTION"),
        new FunctionArgumentsPanel(accesibilities),
        new FunctionErrorsPanel(accesibilities, "FUNCTION"),
        new FunctionGrantsPanel(accesibilities),
        new ObjectUsedByPanel(accesibilities, "FUNCTION"),
        new ObjectUsingPanel(accesibilities, "FUNCTION"),
        new StoredParametersPanel(accesibilities, "FUNCTION"),
        new FunctionDetailsPanel(accesibilities)
    });
  }

}
