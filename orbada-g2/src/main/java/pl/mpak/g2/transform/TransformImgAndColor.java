package pl.mpak.g2.transform;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class TransformImgAndColor extends TransformImg {

  public TransformImgAndColor(BufferedImage pattern) {
    super(pattern);
  }

  public BufferedImage process(BufferedImage image, BufferedImage destination) {
    if (destination == null) {
      destination = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    }
    
    Raster pr = pattern.getRaster();
    Raster rr = image.getRaster();
    WritableRaster wr = destination.getRaster();
    int data[] = new int[4];
    int alpha[] = new int[4];
    int pWidth = pattern.getWidth();
    int pHeight = pattern.getHeight();
    for (int y=0; y<rr.getHeight(); y++) {
      for (int x=0; x<rr.getWidth(); x++) {
        rr.getPixel(x, y, data);
        pr.getPixel(x % pWidth, y % pHeight, alpha);
        data[0] = Math.min(data[0] +alpha[0], 255);
        data[1] = Math.min(data[1] +alpha[1], 255);
        data[2] = Math.min(data[2] +alpha[2], 255);
        //data[3] = Math.min(data[3] +alpha[3], 255);
        wr.setPixel(x, y, data);
      }
    }
    
    return destination;
  }

}
