package pl.mpak.g2;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import pl.mpak.util.Controlable;
import pl.mpak.util.Drawable;

public abstract class VisibleItem implements Controlable, Drawable {

  protected Component component;
  protected int left;
  protected int top; 
  private boolean inside;
  private MouseInputListener inputListener;
  private boolean enabled;
  
  public VisibleItem(int left, int top, Component component) {
    super();
    this.component = component;
    this.left = left;
    this.top = top;
    inputListener = new MouseInputListener() {
      public void mouseClicked(MouseEvent e) {
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
      public void mousePressed(MouseEvent e) {
        doMousePressed(e);
      }
      public void mouseReleased(MouseEvent e) {
      }
      public void mouseDragged(MouseEvent e) {
      }
      public void mouseMoved(MouseEvent e) {
        doMouseMotion(e);
      }
    };
    setEnabled(true);
  }
  
  private void doMouseMotion(MouseEvent e) {
    if (isInside(e.getX(), e.getY())) {
      if (!inside) {
        mouseEnter();
      }
      inside = true;
      mouseMotion(e.getX(), e.getY());
    }
    else if (inside) {
      mouseLeave();
      inside = false;
    }
  }
  
  private void doMousePressed(MouseEvent e) {
    if (isInside(e.getX(), e.getY())) {
      mousePressed(e.getX(), e.getY());
    }
  }
  
  public int getLeft() {
    return left;
  }
  
  public int getTop() {
    return top;
  }
  
  protected boolean isInside(int x, int y) {
    return x >= left && x <= left +getWidth() -1 && y >= top && y <= top +getHeight() -1;
  }
  
  public abstract int getWidth(); 

  public abstract int getHeight(); 

  public abstract void mouseMotion(int x, int y);
  
  public abstract void mousePressed(int x, int y);
  
  public abstract void mouseEnter();
  
  public abstract void mouseLeave();

  public void setEnabled(boolean enabled) {
    if (this.enabled = enabled) {
      this.enabled = enabled;
      if (this.enabled) {
        component.addMouseListener(inputListener);
      }
      else {
        component.removeMouseListener(inputListener);
      }
    }
  }

  public boolean isEnabled() {
    return enabled;
  }
  
  public boolean isInside() {
    return inside;
  }
  
}
