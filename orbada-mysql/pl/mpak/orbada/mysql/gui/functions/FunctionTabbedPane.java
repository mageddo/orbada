package pl.mpak.orbada.mysql.gui.functions;

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
        new FunctionSourcePanel(accesibilities),
        new FunctionParametersPanel(accesibilities),
        new FunctionPrivilegesPanel(accesibilities)
    });
  }
  
}
