package pl.mpak.g2;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.net.URL;

import pl.mpak.util.ExceptionUtil;

public class G2Util {

  /**
   * <p>£aduje obrazek do pamiêci.
   * @param image
   * @throws InterruptedException
   * @throws IllegalArgumentException
   */
  public static void loadImage(Image image) throws InterruptedException, IllegalArgumentException {
    Component dummy = new Component(){
      private static final long serialVersionUID = 1L;
    };
    MediaTracker tracker = new MediaTracker(dummy);
    tracker.addImage(image, 0);
    tracker.waitForID(0);
    if (tracker.isErrorID(0))
        throw new IllegalArgumentException();
  }
  
  public static BufferedImage createImage(String fileName) {
    return G2Util.image2BufferedImage(Toolkit.getDefaultToolkit().createImage(fileName));
  }
  
  public static BufferedImage createImage(URL url) {
    return G2Util.image2BufferedImage(Toolkit.getDefaultToolkit().createImage(url));
  }
  
  public static ColorModel getColorModel(Image image) {
    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    return pg.getColorModel();
}

  /**
   * <p>Tworzy BufferedImage na podstawie Image. Image musi byæ za³adowany do pamiêci.
   * @param image
   * @return
   */
  public static BufferedImage image2BufferedImage(Image image) {
    return image2BufferedImage(image, BufferedImage.TYPE_INT_ARGB);
  }
  
  public static BufferedImage image2BufferedImage(Image image, int imageType) {
    return image2BufferedImage(image, imageType, 0);
  }
  
  public static BufferedImage image2BufferedImage(Image image, int imageType, int incSize) {
    if (image.getHeight(null) == -1 || image.getWidth(null) == -1) {
      try {
        loadImage(image);
      }
      catch (IllegalArgumentException e) {
        ExceptionUtil.processException(e);
      }
      catch (InterruptedException e) {
        ExceptionUtil.processException(e);
      }
    }
    BufferedImage bfi = new BufferedImage(image.getWidth(null) +incSize *2, image.getHeight(null) +incSize *2, imageType);
    Graphics2D g2 = (Graphics2D)bfi.createGraphics();
    g2.drawImage(image, incSize, incSize, null);
    g2.dispose();
    return bfi;
  }
  
  public static boolean isColumnEmpty(BufferedImage bfimage, int x) {
    Raster wr = bfimage.getRaster();
    int data[] = new int[4];
    for (int y=0; y<wr.getHeight(); y++) {
      wr.getPixel(x, y, data);
      if (data[3] > 0) { // checking alpha channel
        return false;
      }
    }
    return true;
  }
  
  public static boolean isRowEmpty(BufferedImage bfimage, int y) {
    Raster wr = bfimage.getRaster();
    int data[] = new int[4];
    for (int x=0; x<wr.getWidth(); x++) {
      wr.getPixel(x, y, data);
      if (data[3] > 0) { // checking alpha channel
        return false;
      }
    }
    return true;
  }
  
  /**
   * <p>Zwraca kwadrat zajêty przez jakieœ dane, nie przeŸroczysty.
   * @param bfimage
   * @return
   */
  public static Rectangle usedRectangle(BufferedImage bfimage) {
    Rectangle rect = new Rectangle(bfimage.getWidth(), bfimage.getHeight());
    int y = 0;
    while (y < bfimage.getHeight()) {
      if (!isRowEmpty(bfimage, y)) {
        rect.y = y;
        break;
      }
      y++;
    }
    y = bfimage.getHeight() -1;
    while (y >= 0) {
      if (!isRowEmpty(bfimage, y)) {
        rect.height = y -rect.y +1;
        break;
      }
      y--;
    }
    int x = 0;
    while (x < bfimage.getWidth()) {
      if (!isColumnEmpty(bfimage, x)) {
        rect.x = x;
        break;
      }
      x++;
    }
    x = bfimage.getWidth() -1;
    while (x >= 0) {
      if (!isColumnEmpty(bfimage, x)) {
        rect.width = x -rect.x +1;
        break;
      }
      x--;
    }
    return rect;
  }
  
  /**
   * <p>Testuje kolizjê dwóch obrazków sprawdzaj¹c ich na³o¿enie i kana³y Alpha.
   * @param x1
   * @param y1
   * @param image1
   * @param x2
   * @param y2
   * @param image2
   * @return true jeœli obrazki nachodz¹ przynajmniej jednym punktem
   */
  public static boolean collidedImage(
      int x1, int y1, BufferedImage image1,
      int x2, int y2, BufferedImage image2) {
    if (image1 == null || image2 == null) {
      return false;
      //throw new IllegalArgumentException("image1 or image2 is null");
    }
    return collidedImage(x1, y1, image1.getRaster(), x2, y2, image2.getRaster());
  }

  public static boolean collidedImage(
      int x1, int y1, Raster r1,
      int x2, int y2, Raster r2) {
    int d1[] = new int[4];
    int d2[] = new int[4];
    Rectangle inter = new Rectangle();
    Rectangle.intersect(
        new Rectangle(x1, y1, r1.getWidth(), r1.getHeight()), 
        new Rectangle(x2, y2, r2.getWidth(), r2.getHeight()), 
        inter);
    if (inter.width > 0 && inter.height > 0) {
      for (int x=inter.x; x<inter.x +inter.width; x++) {
        for (int y=inter.y; y<inter.y +inter.height; y++) {
          r1.getPixel(x -x1, y -y1, d1);
          r2.getPixel(x -x2, y -y2, d2);
          if (d1[3] > 0 && d2[3] > 0) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
}
