package pl.mpak.sky.gui.swing.comp;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ExceptionUtil;

public class ImagePanel extends JPanel {
  private static final long serialVersionUID = 5206332360181549231L;

  private Image image = null;
  private boolean autoSize = true;

  public ImagePanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }

  public ImagePanel(LayoutManager layout) {
    super(layout);
  }

  public ImagePanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  public ImagePanel() {
    super();
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    if (image != this.image) {
      this.image = image;
      if (this.image != null && this.image.getWidth(this) == -1) {
        try {
          SwingUtil.loadImage(this.image);
        } catch (IllegalArgumentException ex) {
          ExceptionUtil.processException(ex);
        } catch (InterruptedException ex) {
          ExceptionUtil.processException(ex);
        }
      }
      updateSize();
      updateImage();
    }
  }
  
  private void updateImage() {
    repaint();
  }

  private void updateSize() {
    if (image != null && autoSize) {
      setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
      //setBounds(getX(), getY(), image.getWidth(this), image.getHeight(this));
    }
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      g.drawImage(image, 0, (getPreferredSize().height -image.getHeight(this)) /2, this);
    }
  }

  public void setAutoSize(boolean autoSize) {
    if (this.autoSize != autoSize) {
      this.autoSize = autoSize;
      updateSize();
      updateImage();
    }
  }

  public boolean getAutoSize() {
    return autoSize;
  }
}
