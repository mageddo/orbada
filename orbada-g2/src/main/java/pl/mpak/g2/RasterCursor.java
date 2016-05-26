package pl.mpak.g2;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import pl.mpak.game.Collisionable;
import pl.mpak.util.Drawable;

public class RasterCursor implements Drawable, Collisionable {

  private static BufferedImage clearImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); 

  private int mouseX = 0;
  private int mouseY = 0;
  private boolean mouseLeft;
  private boolean mouseMiddle;
  private boolean mouseRight;
  private Component component;
  private BufferedImage image;
  private Drawable drawable;
  private Point hotSpot;

  public RasterCursor(Component component, BufferedImage image, int x, int y, int shadow) {
    super();
    this.component = component;
    this.image = image;
    if (shadow > 0) {
      createShadow(shadow);
    }
    setCursor(x, y);
  }
  
  public RasterCursor(Component component, String fileName, int x, int y, int shadow) {
    this(component, G2Util.image2BufferedImage(Toolkit.getDefaultToolkit().createImage(fileName)), x, y, shadow);
  }
  
  public RasterCursor(Component component, Drawable drawable, int x, int y) {
    this(component, (BufferedImage)null, x, y, 0);
    this.drawable = drawable;
  }
  
  private void createShadow(int shadow) {
    BufferedImage temp = new BufferedImage(image.getWidth() +shadow +1, image.getHeight() +shadow, image.getType());
    temp.getGraphics().drawImage(RasterTransform.process(image, null, RasterTransform.SHADOW), shadow +1, shadow, null);
    temp.getGraphics().drawImage(image, 0, 0, null);
    image = temp;
  }
  
  private void setCursor(int x, int y) {
    component.addMouseMotionListener(new MouseMotionListener() {
      public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
      }
      public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
      }
    });
    component.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          mouseLeft = true;
        }
        if (e.getButton() == MouseEvent.BUTTON2) {
          mouseMiddle = true;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
          mouseRight = true;
        }
      }
      public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          mouseLeft = false;
        }
        if (e.getButton() == MouseEvent.BUTTON2) {
          mouseMiddle = false;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
          mouseRight = false;
        }
      }
    });
    hotSpot = new Point(x, y);
    component.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(clearImage, new Point(0, 0), "Cursor"));
  }

  /** 
   * x and y must be -1
   */
  public void draw(Graphics g, int x, int y, ImageObserver observer) {
    if (image != null) {
      g.drawImage(image, x == -1 ? mouseX : x, y == -1 ? mouseY : y, observer);
    }
    else if (drawable != null) {
      //animation.control();
      drawable.draw(g, x == -1 ? (mouseX -hotSpot.x) : x, y == -1 ? (mouseY -hotSpot.y) : y, observer);
    }
  }
  
  public int getX() {
    return mouseX;
  }
  
  public int getY() {
    return mouseY;
  }

  public void collidedWith(Collisionable object) {
    
  }

  public BufferedImage getCollisionImage() {
    if (image != null) {
      return image;
    }
    if (drawable instanceof Animation) {
      return ((Animation)drawable).getImage();
    }
    return null;
  }

  public int getCollisionX() {
    return getX() -hotSpot.x;
  }

  public int getCollisionY() {
    return getY() -hotSpot.y;
  }

  public boolean isMouseLeft() {
    return mouseLeft;
  }

  public boolean isMouseMiddle() {
    return mouseMiddle;
  }

  public boolean isMouseRight() {
    return mouseRight;
  }

}
