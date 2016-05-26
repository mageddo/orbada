/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.packages;

import java.awt.event.ComponentEvent;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.StoredParametersPanel;
import java.awt.Component;
import java.awt.event.ComponentListener;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class PackagePartTabbedPane extends OrbadaTabbedPane {
  
  private String title;
  
  public PackagePartTabbedPane(IViewAccesibilities accesibilities, String title) {
    super("PACKAGE",
      new Component[] {
        new PackageMethodsPanel(accesibilities),
        new PackageGrantsPanel(accesibilities),
        new PackageDetailsPanel(accesibilities)
    });
    this.title = title;
  }
  
  public PackagePartTabbedPane(IViewAccesibilities accesibilities, String type, String title) {
    super(type,
      new Component[] {
        new PackageSourcePanel(accesibilities, type),
        new PackageErrorsPanel(accesibilities, type),
        new ObjectUsedByPanel(accesibilities, type),
        new ObjectUsingPanel(accesibilities, type),
        new StoredParametersPanel(accesibilities, type),
    });
    this.title = title;
    addComponentListener(new ComponentListener() {
      public void componentResized(ComponentEvent e) {
      }
      public void componentMoved(ComponentEvent e) {
      }
      public void componentShown(ComponentEvent e) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            if (isVisible() && getSelectedComponent() != null) {
              getSelectedComponent().requestFocusInWindow();
            }
          }
        });
      }
      public void componentHidden(ComponentEvent e) {
      }
    });
  }
  
  @Override
  public String getTitle() {
    return title;
  }

}
