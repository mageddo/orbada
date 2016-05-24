/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.types.cm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.oracle.gui.types.TypeTabbedPane;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.SwingUtil;

/**
 *
 * @author akaluza
 */
public class PageDownAction extends Action {

  private Component owner;
  
  public PageDownAction(Component owner) {
    super("PageDownAction");
    setShortCut(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_MASK);
    this.owner = owner;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    TypeTabbedPane pane = (TypeTabbedPane)SwingUtil.getOwnerComponent(TypeTabbedPane.class, owner);
    if (pane != null) {
      if (pane.getSelectedIndex() < pane.getComponentCount() -1) {
        pane.setSelectedIndex(pane.getSelectedIndex() +1);
      }
      else {
        pane.setSelectedIndex(0);
      }
    }
  }

}
