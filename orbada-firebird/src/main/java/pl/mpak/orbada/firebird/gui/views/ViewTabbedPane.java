package pl.mpak.orbada.firebird.gui.views;

import java.awt.Component;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.ContentPanel;

/**
 *
 * @author akaluza
 */
public class ViewTabbedPane extends OrbadaTabbedPane {
  
  public ViewTabbedPane(IViewAccesibilities accesibilities) {
    super("TABLE",       
      new Component[] {
        new ViewColumnsPanel(accesibilities),
        new ViewGrantsPanel(accesibilities),
        new ViewUsingPanel(accesibilities),
        new ViewSourcePanel(accesibilities),
        new ContentPanel(accesibilities)
    });
  }
  
}
