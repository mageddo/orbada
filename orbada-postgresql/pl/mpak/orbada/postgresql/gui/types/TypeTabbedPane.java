/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.types;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class TypeTabbedPane extends OrbadaTabbedPane {
  
  public TypeTabbedPane(IViewAccesibilities accesibilities) {
    super("TYPE",
      new Component[] {
        new TypeAttributeListTab(accesibilities),
        new TypeDetailsTab(accesibilities),
        new TypeDependsTab(accesibilities),
        new TypeSourceTab(accesibilities)
    });
  }
  
}
