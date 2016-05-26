package pl.mpak.util;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

public interface Drawable {
  public void draw(Graphics g, int x, int y, ImageObserver observer);
}
