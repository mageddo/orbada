/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps;

import javax.swing.JComponent;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.cm.MovePerspectiveLeftAction;
import pl.mpak.orbada.gui.cm.MovePerspectiveRightAction;
import pl.mpak.orbada.gui.MainFrame;
import pl.mpak.orbada.gui.cm.ClosePerspectiveAction;
import pl.mpak.orbada.gui.cm.PerspectivePropertiesAction;
import pl.mpak.orbada.gui.cm.MovePerspectiveRightAction;
import pl.mpak.sky.gui.swing.comp.PopupMenu;

/**
 *
 * @author akaluza
 */
public class PerspectiveContextMenu extends PopupMenu {

  private PerspectivePropertiesAction cmPerspectiveProperties;
  private ClosePerspectiveAction cmClosePerspective;
  private MovePerspectiveLeftAction cmMovePerspectiveLeft;
  private MovePerspectiveRightAction cmMovePerspectiveRight;
  
  public PerspectiveContextMenu(JComponent popupComponent) {
    super(popupComponent);
    add(cmPerspectiveProperties = new PerspectivePropertiesAction());
    add(cmMovePerspectiveLeft = new MovePerspectiveLeftAction());
    add(cmMovePerspectiveRight = new MovePerspectiveRightAction());
    addSeparator();
    add(cmClosePerspective = new ClosePerspectiveAction());
  }
  
  @Override
  protected void updateActions() {
    MainFrame frame = Application.get().getMainFrame();
    cmPerspectiveProperties.setEnabled(frame.getActivePerspective() != null);
    cmClosePerspective.setEnabled(frame.getActivePerspective() != null);
    cmMovePerspectiveLeft.setEnabled(frame.getTabbedPerpectives().getTabCount() > 1);
    cmMovePerspectiveRight.setEnabled(frame.getTabbedPerpectives().getTabCount() > 1);
  }
  
}
