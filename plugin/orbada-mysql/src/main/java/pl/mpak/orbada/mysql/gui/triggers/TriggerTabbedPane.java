package pl.mpak.orbada.mysql.gui.triggers;

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
        new TriggerSourcePanel(accesibilities)
    });
  }
  
}
