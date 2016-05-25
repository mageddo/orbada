package pl.mpak.sky.gui.swing.comp;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class IconPanel extends JPanel implements SwingConstants {
  private static final long serialVersionUID = 4037043492868103803L;

  private Icon icon = null;
  private boolean autoSize = true;
  private boolean stretch = false;
  private int verticalAlignment = CENTER;
  private int horizontalAlignment = CENTER;
  
  public IconPanel() {
    super();
  }

  public IconPanel(LayoutManager layout) {
    super(layout);
  }

  public IconPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  public IconPanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }

  public void setIcon(Icon icon) {
    if (this.icon != icon) {
      this.icon = icon;
      updateSize();
      repaint();
    }
  }

  public Icon getIcon() {
    return icon;
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (icon != null) {
      int x = 0;
      int y = 0;
      switch (horizontalAlignment) {
        case RIGHT:
        case TRAILING: {
          x = getPreferredSize().width -icon.getIconWidth(); 
          break;
        }
        case CENTER: {
          x = (getPreferredSize().width -icon.getIconWidth()) /2;
          break;
        }
      }
      switch (verticalAlignment) {
        case BOTTOM: {
          y = getPreferredSize().height -icon.getIconHeight(); 
          break;
        }
        case CENTER: {
          y = (getPreferredSize().height -icon.getIconHeight()) /2;
          break;
        }
      }
      if (icon instanceof ImageIcon) {
        Image image = ((ImageIcon)icon).getImage();
        if (stretch) {
          g.drawImage(image, 0, 0, getPreferredSize().width, getPreferredSize().height, this);
        }
        else {
          g.drawImage(image, x, y, this);
        }
      }
      else {
        icon.paintIcon(this, g, x, y);
      }
    }
  }

  private void updateSize() {
    if (icon != null && autoSize) {
      setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }
  }

  /**
   * <p>Ustawione na true dopasowuje obrazek do rozmiaru panelu.
   * <p>Jeœli Icon nie jest typu ImageIcon to stretch nie dzia³a.
   * @param stretch
   */
  public void setStretch(boolean stretch) {
    if (this.stretch != stretch) {
      this.stretch = stretch;
      updateSize();
      repaint();
    }
  }

  public boolean isStretch() {
    return stretch;
  }

  public void setVerticalAlignment(int verticalAlignment) {
    if (this.verticalAlignment != verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
      updateSize();
      repaint();
    }
  }

  public int getVerticalAlignment() {
    return verticalAlignment;
  }

  public void setHorizontalAlignment(int horizontalAlignment) {
    if (this.horizontalAlignment != horizontalAlignment) {
      this.horizontalAlignment = horizontalAlignment;
      updateSize();
      repaint();
    }
  }

  public int getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * <p>Ustawione na true dopasowuje panel do rozmiaru obrazka.
   * @param autoSize
   */
  public void setAutoSize(boolean autoSize) {
    if (this.autoSize = autoSize) {
      this.autoSize = autoSize;
      updateSize();
      repaint();
    }
  }

  public boolean isAutoSize() {
    return autoSize;
  }

}
