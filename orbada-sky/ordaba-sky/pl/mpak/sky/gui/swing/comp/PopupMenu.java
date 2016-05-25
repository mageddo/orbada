package pl.mpak.sky.gui.swing.comp;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pl.mpak.sky.gui.swing.SwingUtil;

public class PopupMenu extends JPopupMenu {
  private static final long serialVersionUID = -8130420233097255762L;
  
  private JComponent popupComponent;

  public PopupMenu() {
    super(null);
    init();
  }

  public PopupMenu(String label) {
    super(label);
    init();
  }

  public PopupMenu(JComponent popupComponent) {
    super(null);
    init();
    setPopupComponent(popupComponent);
  }
  
  private void init() {
  }
  
  public void show(Component invoker, int x, int y) {
    updateActions();
    super.show(invoker, x, y);
  }

  public void show(MouseEvent evt) {
    Point pt = SwingUtil.computeLocation(evt, evt.getComponent(), this);
    show(evt.getComponent(), pt.x, pt.y);
  }

  public void show() {
    if (getPopupComponent() != null) {
      Point pt = SwingUtil.computeLocation(0, 0, getPopupComponent(), this);
      show(getPopupComponent(), pt.x, pt.y);
    }
  }
  
  public JMenuItem add(Action a) {
    SwingUtil.addAction(getPopupComponent(), a);
    return super.add(a);
  }

  public void insert(Action a, int index) {
    SwingUtil.addAction(getPopupComponent(), a);
    super.insert(a, index);
  }

  protected void updateActions() {
    
  }

  public JComponent getPopupComponent() {
    return popupComponent;
  }
  
  public void setPopupComponent(JComponent componentPopup) {
    this.popupComponent = componentPopup;
  }
  
}
