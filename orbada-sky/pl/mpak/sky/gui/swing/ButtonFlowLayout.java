package pl.mpak.sky.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.io.Serializable;

import javax.swing.AbstractButton;

import pl.mpak.util.StringUtil;
import sun.swing.SwingUtilities2;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Layout p³ywaj¹cych komponentów, które na nim le¿¹
 * Dzia³a w ten sposób, ¿e dobiera na podstawie szerokoœci tekstu i obrazka maksymalny
 * rozmiar komponentu. Na tej podstawie wylicza maksymaln¹ szerokoœæ komponentu
 * nadrzêdnego i jego wysokoœæ. Nastêpnie uk³ada je na nim.
 *
 */
public class ButtonFlowLayout implements Serializable, LayoutManager2 {
  private static final long serialVersionUID = 3964199209470555040L;

  private int hgap = 0;
  private int vgap = 0;
  private int width = 0;
  private int height = 0;
  
  public ButtonFlowLayout() {
    super();
  }

  public ButtonFlowLayout(int hgap, int vgap) {
    super();
    this.hgap = hgap;
    this.vgap = vgap;
  }

  public void addLayoutComponent(Component comp, Object constraints) {
  }

  @Deprecated
  public void addLayoutComponent(String name, Component comp) {
    throw new IllegalArgumentException("unknown constraint \"" + name +"\"");
  }

  public float getLayoutAlignmentX(Container target) {
    return 0.5f;
  }

  public float getLayoutAlignmentY(Container target) {
    return 0.5f;
  }

  public void invalidateLayout(Container target) {
  }

  public void removeLayoutComponent(Component comp) {
  }

  public Dimension preferredLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {
      int awidth = 0;
      int left = vgap;
      int top = hgap;

      width = 0;
      height = 0;
      for (int i=0; i<parent.getComponentCount(); i++) {
        Component c = parent.getComponent(i);
        Graphics g = c.getGraphics();
        if (g != null) {
          FontMetrics fm = g.getFontMetrics();
          Dimension max = c.getMaximumSize();
          Dimension min = c.getMinimumSize();
          Dimension pref = c.getPreferredSize();
          int twidth;
          int theight;
  
          if (c instanceof AbstractButton) {
            twidth = SwingUtilities2.stringWidth(null, fm, StringUtil.nvl(((AbstractButton)c).getText(), "")) +4;
            if (((AbstractButton)c).getIcon() != null) {
              twidth += ((AbstractButton)c).getIcon().getIconWidth() +4;
            }
            twidth += (((AbstractButton)c).getMargin().right +((AbstractButton)c).getMargin().left +4);
            theight = fm.getHeight();
          }
          else {
            twidth = pref.width;
            theight = pref.height;
          }
          twidth = Math.min(twidth, max.width);
          twidth = Math.max(twidth, min.width);
          theight = Math.min(theight, max.height);
          theight = Math.max(theight, min.height);
          
          width = Math.max(twidth, width);
          height = Math.max(theight, height);
        }
      }
      
      for (int i=0; i<parent.getComponentCount(); i++) {
        if (left +width +vgap > parent.getWidth()) {
          awidth = Math.max(awidth, left +vgap);
          left = vgap;
          top += (height +hgap); 
        }
        left += (width +vgap);
      }

      return new Dimension(awidth, top +height +hgap);
    }
  }

  public Dimension maximumLayoutSize(Container target) {
    return preferredLayoutSize(target);
  }

  public Dimension minimumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }

  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {
      int left = vgap;
      int top = hgap;

      for (int i=0; i<parent.getComponentCount(); i++) {
        Component c = parent.getComponent(i);
        if (left +width +vgap > parent.getWidth()) {
          left = vgap;
          top += (height +vgap);
        }
        c.setBounds(left, top, width, height);
        left += (width +vgap);
      }
    }
  }

  public void setHgap(int hgap) {
    this.hgap = hgap;
  }

  public int getHgap() {
    return hgap;
  }

  public void setVgap(int vgap) {
    this.vgap = vgap;
  }

  public int getVgap() {
    return vgap;
  }

  public String toString() {
    return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
  }
  
}
