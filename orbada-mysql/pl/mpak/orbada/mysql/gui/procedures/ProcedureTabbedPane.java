package pl.mpak.orbada.mysql.gui.procedures;

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
        new ProcedureParametersPanel(accesibilities),
        new ProcedurePrivilegesPanel(accesibilities)
    });
  }
  
}
