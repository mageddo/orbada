package pl.mpak.g2.comp;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ResourceBundle;

import pl.mpak.g2.G2Util;
import pl.mpak.g2.RasterTransform;
import pl.mpak.g2.transform.Transform;
import pl.mpak.game.Collisionable;
import pl.mpak.util.Controlable;
import pl.mpak.util.Drawable;

public class Frame implements Drawable, Controlable {

  private BufferedImage pattern;
  private BufferedImage image;
  private Rectangle outside;
  private Rectangle inside;
  private int width;
  private int height;
  private boolean buffered = false;
  private boolean animated = true;
  private boolean updateRequred = false;
  private Transform transform;
  
  private int progrSize = -1;
  private int srcWidth;
  private int srcHeight;
  private int destWidth;
  private int destHeight;
  private boolean hideCenter;
  
  private int left = 0;
  private int top = 0;
  private boolean staticPosition = false;
  
  public static float[] BOUNCE_SIZE = {0.0f, 0.01f, 0.0f, 0.1f, 0.4f, 0.8f, 1.025f, 1.1f, 1.025f, 1.01f};
  public static float[] FAST_SIZE = {0.1f, 0.3f, 0.6f, 0.75f, 0.85f, 0.9f, 0.95f};
  public static float[] FAST_SIZE_REV = {0.01f, 0.02f, 0.05f, 0.1f, 0.2f, 0.7f};
  public static float[] LINEAR_SIZE = {0.05f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.95f};
  
  private float[] sizeProp = BOUNCE_SIZE;
  
  public Frame(BufferedImage image, Rectangle outside, Rectangle inside, int width, int height) {
    this.pattern = image;
    this.outside = outside;
    this.inside = inside;
    this.width = width;
    this.height = height;
    updateImage();
  }

  public Frame(BufferedImage image, Rectangle outside, Rectangle inside) {
    this(image, outside, inside, image.getWidth(), image.getHeight());
  }
  
  public static Frame createInstance(String name, int width, int height) {
    ResourceBundle res = ResourceBundle.getBundle(name);
    //System.out.println(res.getClass().getResource(res.getString("pattern")));
    BufferedImage pattern = G2Util.createImage(res.getClass().getResource(res.getString("pattern")));
    Frame frame = new Frame(pattern, 
        new Rectangle(Integer.parseInt(res.getString("outside.x")), Integer.parseInt(res.getString("outside.y")), Integer.parseInt(res.getString("outside.w")), Integer.parseInt(res.getString("outside.h"))),
        new Rectangle(Integer.parseInt(res.getString("inside.x")), Integer.parseInt(res.getString("inside.y")), Integer.parseInt(res.getString("inside.w")), Integer.parseInt(res.getString("inside.h"))),
        width, height);
    if ("fast-rev".equalsIgnoreCase(res.getString("resize"))) {
      frame.sizeProp = FAST_SIZE_REV;
    }
    else if ("fast".equalsIgnoreCase(res.getString("resize"))) {
      frame.sizeProp = FAST_SIZE;
    }
    else if ("bounce".equalsIgnoreCase(res.getString("resize"))) {
      frame.sizeProp = BOUNCE_SIZE;
    }
    else if ("linear".equalsIgnoreCase(res.getString("resize"))) {
      frame.sizeProp = LINEAR_SIZE;
    }
    return frame;
  }
  
  private void draw(Graphics g, int x, int y) {
    int fWidth = width +pattern.getWidth() -outside.width;
    int fHeight = height +pattern.getHeight() -outside.height;
    int yy = fHeight -(pattern.getHeight() -(inside.y +inside.height));
    int xx = fWidth -(pattern.getWidth() -(inside.x +inside.width));
    g.drawImage(pattern, x,           y,           x +inside.x,  y +inside.y,   0, 0, inside.x, inside.y, null);
    g.drawImage(pattern, x,           y +inside.y, x +inside.x,  y +yy,         0, inside.y, inside.x, inside.y +inside.height, null);
    g.drawImage(pattern, x,           y +yy,       x +inside.x,  y +fHeight -1, 0, inside.y +inside.height, inside.x, pattern.getHeight(), null);
    g.drawImage(pattern, x +xx,       y,           x +fWidth -1, y +inside.y,   inside.x +inside.width, 0, pattern.getWidth(), inside.y, null);
    g.drawImage(pattern, x +xx,       y +inside.y, x +fWidth -1, y +yy,         inside.x +inside.width, inside.y, pattern.getWidth(), inside.y +inside.height, null);
    g.drawImage(pattern, x +xx,       y +yy,       x +fWidth -1, y +fHeight -1, inside.x +inside.width, inside.y +inside.height, pattern.getWidth(), pattern.getHeight(), null);
    g.drawImage(pattern, x +inside.x, y,           x +xx,        y +inside.y,   inside.x, 0, inside.x +inside.width, inside.y, null);
    if (!hideCenter) {
      g.drawImage(pattern, x +inside.x, y +inside.y, x +xx,        y +yy,         inside.x, inside.y, inside.x +inside.width, inside.y +inside.height, null);
    }
    g.drawImage(pattern, x +inside.x, y +yy,       x +xx,        y +fHeight -1, inside.x, inside.y +inside.height, inside.x +inside.width, pattern.getHeight(), null);
  }
  
  public void updateImage() {
    if (buffered) {
      int fWidth = width +pattern.getWidth() -outside.width;
      int fHeight = height +pattern.getHeight() -outside.height;
      image = new BufferedImage(fWidth, fHeight, pattern.getType());
      Graphics g = image.createGraphics();
      draw(g, 0, 0);
      if (transform != null) {
        image = RasterTransform.process(image, null, transform);
      }
      g.dispose();
    }
    else {
      image = null;
    }
    updateRequred = false;
  }
  
  public void draw(Graphics g, int x, int y, ImageObserver observer) {
    if (updateRequred) {
      updateImage();
    }
    if (staticPosition) {
      x = left;
      y = top;
    }
    x -= outside.x;
    y -= outside.y;
    if (buffered) {
      g.drawImage(image, x, y, observer);
    }
    else {
      draw(g, x, y);
    }
  }

  public void collidedWith(Collisionable object) {
    
  }

  public void control() {
    if (progrSize >= 0) {
      width = destWidth +(int)((srcWidth -destWidth) *sizeProp[progrSize]);
      height = destHeight +(int)((srcHeight -destHeight) *sizeProp[progrSize]);
      progrSize--;
      updateRequred = true;
    }
    if (updateRequred) {
      updateImage();
    }
  }
  
  public boolean isOnSize() {
    return progrSize < 0;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    if (animated) {
      this.srcHeight = this.height;
      this.destHeight = height;
      progrSize = sizeProp.length -1;
    }
    else {
      this.height = height;
      updateRequred = true;
    }
  }

  public Rectangle getInside() {
    return inside;
  }

  public void setInside(Rectangle inside) {
    this.inside = inside;
  }

  public Rectangle getOutside() {
    return outside;
  }

  public void setOutside(Rectangle outside) {
    this.outside = outside;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    if (animated) {
      this.srcWidth = this.width;
      this.destWidth = width;
      progrSize = sizeProp.length -1;
    }
    else {
      this.width = width;
      updateRequred = true;
    }
  }
  
  public void setBounds(int left, int top, int width, int height) {
    setLeft(left);
    setTop(top);
    setWidth(width);
    setHeight(height);
  }

  public boolean isBuffered() {
    return buffered;
  }

  public void setBuffered(boolean buffered) {
    this.buffered = buffered;
    updateRequred = true;
  }

  public Transform getTransform() {
    return transform;
  }

  public void setTransform(Transform transform) {
    this.transform = transform;
    setBuffered(this.transform != null);
    //setAnimated(this.transform == null);
    updateRequred = true;
  }

  public boolean isAnimated() {
    return animated;
  }

  public void setAnimated(boolean animated) {
    this.animated = animated;
  }

  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
    staticPosition = true;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
    staticPosition = true;
  }

  public boolean isHideCenter() {
    return hideCenter;
  }

  public void setHideCenter(boolean hideCenter) {
    this.hideCenter = hideCenter;
    updateRequred = true;
  }

}
