/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.packages.cm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.oracle.gui.packages.PackageTabbedPane;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.SwingUtil;

/**
 *
 * @author akaluza
 */
public class PageUpAction extends Action {

  private Component owner;
  
  public PageUpAction(Component owner) {
    super("PageUpAction");
    setShortCut(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_MASK);
    this.owner = owner;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    PackageTabbedPane pane = (PackageTabbedPane)SwingUtil.getOwnerComponent(PackageTabbedPane.class, owner);
    if (pane != null) {
      if (pane.getSelectedIndex() > 0) {
        pane.setSelectedIndex(pane.getSelectedIndex() -1);
      }
      else {
        pane.setSelectedIndex(pane.getComponentCount() -1);
      }
    }
  }

}
