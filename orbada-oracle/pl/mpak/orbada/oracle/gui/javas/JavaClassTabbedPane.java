/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.javas;

import java.awt.Component;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class JavaClassTabbedPane extends OrbadaTabbedPane {
  
  public JavaClassTabbedPane(IViewAccesibilities accesibilities) {
    super("JAVA CLASS",
      new Component[] {
        new JavaMethodsPanel(accesibilities),
        new JavaFieldsPanel(accesibilities),
        new JavaImplementsPanel(accesibilities),
        new JavaInnersPanel(accesibilities),
        new JavaSourceGrantsPanel(accesibilities),
        new ObjectUsedByPanel(accesibilities, "JAVA CLASS"),
        new ObjectUsingPanel(accesibilities, "JAVA CLASS"),
        new JavaDetailsPanel(accesibilities, "JAVA CLASS")
    });
  }
  
}
