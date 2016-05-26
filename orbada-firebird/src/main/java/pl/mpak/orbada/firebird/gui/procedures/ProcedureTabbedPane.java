/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.firebird.gui.procedures;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class ProcedureTabbedPane extends OrbadaTabbedPane {
  
  public ProcedureTabbedPane(IViewAccesibilities accesibilities) {
    super("PROCEDURE",
      new Component[] {
        new ProcedureSourcePanel(accesibilities),
        new ProcedureArgumentsPanel(accesibilities),
        new ProcedureGrantsPanel(accesibilities)
    });
  }
  
}
