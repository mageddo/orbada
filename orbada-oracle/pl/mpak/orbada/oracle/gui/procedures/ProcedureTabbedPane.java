/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.procedures;

import pl.mpak.orbada.oracle.gui.triggers.*;
import pl.mpak.orbada.oracle.gui.tables.*;
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
public class ProcedureTabbedPane extends OrbadaTabbedPane {
  
  public ProcedureTabbedPane(IViewAccesibilities accesibilities) {
    super("PROCEDURE",
      new Component[] {
        new ProcedureSourcePanel(accesibilities, "PROCEDURE"),
        new ProcedureArgumentsPanel(accesibilities),
        new ProcedureErrorsPanel(accesibilities, "PROCEDURE"),
        new ProcedureGrantsPanel(accesibilities),
        new ObjectUsedByPanel(accesibilities, "PROCEDURE"),
        new ObjectUsingPanel(accesibilities, "PROCEDURE"),
        new StoredParametersPanel(accesibilities, "PROCEDURE"),
        new ProcedureDetailsPanel(accesibilities)
    });
  }
  
}
