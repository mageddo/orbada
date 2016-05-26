package pl.mpak.g2;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import pl.mpak.g2.background.BackgroundMover;
import pl.mpak.g2.background.BackgroundNone;
import pl.mpak.util.Controlable;
import pl.mpak.util.Drawable;

public class Background implements Drawable, Controlable {

  private BufferedImage image;
  private BackgroundMover mover;
  private Animation animation;
  
  public Background(BufferedImage image, BackgroundMover mover) {
    super();
    this.image = image;
    setMover(mover);
  }

  public Background(BufferedImage image) {
    this(image, null);
  }

  public Background(Animation animation, BackgroundMover mover) {
    super();
    this.animation = animation;
    setMover(mover);
  }

  public Background(Animation animation) {
    this(animation, null);
  }

  public void draw(Graphics g, int x, int y, ImageObserver observer) {
    Graphics2D g2 = (Graphics2D)g;
    Paint paint = g2.getPaint();
    if (image != null) {
      g2.setPaint(new TexturePaint(image, new Rectangle(mover.getShiftX() -x, mover.getShiftY() -y, image.getWidth(), image.getHeight())));
    }
    else if (animation != null) {
      BufferedImage image = animation.getImage();
      g2.setPaint(new TexturePaint(image, new Rectangle(mover.getShiftX() -x, mover.getShiftY() -y, image.getWidth(), image.getHeight())));
    }
    g2.fillRect(g.getClipBounds().x, g.getClipBounds().y, g.getClipBounds().width, g.getClipBounds().height);
    g2.setPaint(paint);
  }

  public void control() {
    mover.control();
  }

  public void setMover(BackgroundMover mover) {
    if (this.mover != mover || mover == null) {
      this.mover = (mover == null ? new BackgroundNone() : mover);
      this.mover.setImage(image);
    }
  }

  public BackgroundMover getMover() {
    return mover;
  }

}
