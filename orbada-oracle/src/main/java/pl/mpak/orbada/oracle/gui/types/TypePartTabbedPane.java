package pl.mpak.orbada.oracle.gui.types;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.oracle.gui.StoredParametersPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class TypePartTabbedPane extends OrbadaTabbedPane {
  
  private String title;
  
  public TypePartTabbedPane(IViewAccesibilities accesibilities, String title) {
    super("TYPE",
      new Component[] {
        new TypeAttrsPanel(accesibilities),
        new TypeMethodsPanel(accesibilities),
        new TypeGrantsPanel(accesibilities),
        new TypeDetailsPanel(accesibilities)
    });
    this.title = title;
  }
  
  public TypePartTabbedPane(IViewAccesibilities accesibilities, String type, String title) {
    super(type,
      new Component[] {
        new TypeSourcePanel(accesibilities, type),
        new TypeErrorsPanel(accesibilities, type),
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
