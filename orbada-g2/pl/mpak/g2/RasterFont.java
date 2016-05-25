package pl.mpak.g2;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import pl.mpak.g2.transform.Transform;
import pl.mpak.util.StringUtil;

public class RasterFont {

  private String chars;
  private BufferedImage imageChars[];
  private BufferedImage space;
  private int kerning = 1;
  
  public RasterFont(BufferedImage image, String chars, int kering) {
    this(image, chars);
    this.kerning = kering;
  }
  
  public RasterFont(BufferedImage image, String chars) {
    super();
    this.chars = chars;
    imageChars = new BufferedImage[this.chars.length()];
    prepareChars(image);
  }
  
  private void prepareChars(BufferedImage image) {
    BufferedImage bfimage = image;
    int x = 0;
    int xstart;
    int i = 0;
    int width = image.getWidth(null);
    int sumWidth = 0;
    
    while (x < width) {
      while (G2Util.isColumnEmpty(bfimage, x) && x < width -1) {
        x++;
      }
      xstart = x;
      while (!G2Util.isColumnEmpty(bfimage, x) && x < width -1) {
        x++;
      }
      
      if (xstart == x) {
        break;
      }
      sumWidth += x -xstart;
      imageChars[i] = new BufferedImage(x -xstart +1, bfimage.getHeight(), bfimage.getType());
      Graphics2D g2 = (Graphics2D)imageChars[i].createGraphics();
      g2.drawImage(bfimage, 0, 0, imageChars[i].getWidth() -1, imageChars[i].getHeight(), xstart, 0, x, bfimage.getHeight(), null);
      g2.dispose();
      i++;
    }
    space = new BufferedImage((int)(sumWidth /imageChars.length), bfimage.getHeight(), bfimage.getType());
    bfimage = null;
  }
  
  public BufferedImage getSpace() {
    return space;
  }
  
  public BufferedImage getChar(char ch) {
    int p = chars.indexOf(ch);
    if (p == -1) {
      return space;
    }
    return imageChars[p];
  }
  
  public int getWidth(char ch) {
    int p = chars.indexOf(ch);
    if (p == -1) {
      return space.getWidth();
    }
    return imageChars[p].getWidth();
  }
  
  public int getWidth(String text) {
    int width = 0;
    int maxWidth = 0;
    for (int i=0; i<text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == '\n') {
        if (width > maxWidth) {
          maxWidth = width;
        }
        width = 0;
      }
      width += (getWidth(text.charAt(i)) +kerning);
    }
    return (maxWidth == 0 ? width : maxWidth) -kerning;
  }
  
  public int getHeight(String text) {
    return space.getHeight() *(StringUtil.nlineCount(text) +1);
  }

  public int getHeight() {
    return space.getHeight();
  }
  
  public void draw(int x, int y, Graphics g, String text) {
    draw(x, y, g, text, null);
  }
  
  public void draw(int x, int y, int line, Graphics g, String text) {
    draw(x, y, line, g, text, null);
  }
  
  public void draw(int x, int y, Graphics g, String text, Transform filter) {
    draw(x, y, 0, g, text, filter);
  }
  
  public void draw(Graphics g, String text) {
    draw(0, 0, g, text, null);
  }
  
  public void draw(int x, int y, int line, Graphics g, String text, Transform filter) {
    int startx = x;
    for (int i=0; i<text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == '\n') {
        x = startx;
        y += getHeight();
      }
      else {
        if (filter != null) {
          g.drawImage(RasterTransform.process(getChar(ch), null, filter), x, y +line *getHeight(), null);
        }
        else {
          g.drawImage(getChar(ch), x, y +line *getHeight(), null);
        }
        x+= (getWidth(ch) +kerning);
      }
    }
  }
  
  public BufferedImage createTextImage(String text) {
    int width = getWidth(text);
    int height = getHeight(text);
    BufferedImage bfImage = new BufferedImage(width, height, space.getType());
    Graphics2D g2 = (Graphics2D)bfImage.getGraphics();
    draw(g2, text);
    g2.dispose();
    
    return bfImage;
  }

  public String getChars() {
    return chars;
  }

}
