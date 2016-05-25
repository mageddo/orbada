package pl.mpak.g2;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import pl.mpak.util.Drawable;

public class RasterText extends BufferedImage implements Drawable {

  public RasterText(RasterFont font, String text) {
    super(font.getWidth(text), font.getHeight(text), font.getSpace().getType());
    Graphics g = getGraphics();
    font.draw(g, text);
    g.dispose();
  }

  public void draw(Graphics g, int x, int y, ImageObserver observer) {
    g.drawImage(this, x, y, observer);
  }

}
