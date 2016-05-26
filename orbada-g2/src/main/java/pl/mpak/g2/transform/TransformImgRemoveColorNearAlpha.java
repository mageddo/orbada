package pl.mpak.g2.transform;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class TransformImgRemoveColorNearAlpha extends TransformImg {

  private Color color;
  
  public TransformImgRemoveColorNearAlpha(Color color) {
    super(null);
    this.color = color;
  }

  public TransformImgRemoveColorNearAlpha(BufferedImage pattern) {
    super(pattern);
  }

  public BufferedImage process(BufferedImage image, BufferedImage destination) {
    if (destination == null) {
      destination = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    }
    
    Raster rr = image.getRaster();
    WritableRaster wr = destination.getRaster();
    int data[] = new int[4];
    int td[] = new int[4];
    int h = rr.getHeight();
    int w = rr.getWidth();
    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        boolean transformed = false;
        rr.getPixel(x, y, data);
        if (data[0] == color.getRed() && data[1] == color.getGreen() && data[2] == color.getBlue()) {
          if (y > 0) {
            rr.getPixel(x, y -1, td);
            if (td[3] == 0) {
              data[3] = 0;
              transformed = true;
            }
          }
          else {
            data[3] = 0;
            transformed = true;
          }
          if (!transformed) {
            if (y +1 < h) {
              rr.getPixel(x, y +1, td);
              if (td[3] == 0) {
                data[3] = 0;
                transformed = true;
              }
            }
            else {
              data[3] = 0;
              transformed = true;
            }
            if (!transformed) {
              if (x > 0) {
                rr.getPixel(x -1, y, td);
                if (td[3] == 0) {
                  data[3] = 0;
                  transformed = true;
                }
              }
              else {
                data[3] = 0;
                transformed = true;
              }
              if (!transformed) {
                if (x +1 < w) {
                  rr.getPixel(x +1, y, td);
                  if (td[3] == 0) {
                    data[3] = 0;
                  }
                }
                else {
                  data[3] = 0;
                }
              }
            }
          }
        }
        wr.setPixel(x, y, data);
      }
    }
    
    return destination;
  }

}
