package pl.mpak.g2;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import pl.mpak.util.Drawable;

public class CoordinatedObject implements Drawable {

  private Image image;
  private Animation animation;
  private Drawable drawable;
  private int x;
  private int y;
  
  public CoordinatedObject(Image image) {
    this(0, 0, image);
  }

  public CoordinatedObject(int x, int y, Image image) {
    this.image = image;
    this.x = x;
    this.y = y;
  }

  public CoordinatedObject(Animation animation) {
    this(0, 0, animation);
  }

  public CoordinatedObject(int x, int y, Animation animation) {
    this.animation = animation;
    this.x = x;
    this.y = y;
  }

  public CoordinatedObject(Drawable drawable) {
    this(0, 0, drawable);
  }

  public CoordinatedObject(int x, int y, Drawable drawable) {
    this.drawable = drawable;
    this.x = x;
    this.y = y;
  }

  public void draw(Graphics g, int x, int y, ImageObserver observer) {
    if (image != null) {
      g.drawImage(image, x == -1 ? this.x : x, y == -1 ? this.y : y, observer);
    }
    else if (animation != null) {
      animation.draw(g, x == -1 ? this.x : x, y == -1 ? this.y : y, observer);
    }
    else if (drawable != null) {
      drawable.draw(g, x == -1 ? this.x : x, y == -1 ? this.y : y, observer);
    }
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getX() {
    return x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getY() {
    return y;
  }

}
