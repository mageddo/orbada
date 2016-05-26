package pl.mpak.g2.transform;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class TransformImgOrColorAndAlpha extends TransformImg {

  public TransformImgOrColorAndAlpha(BufferedImage pattern) {
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
        data[0] = (data[0] +alpha[0]) /2;
        data[1] = (data[1] +alpha[1]) /2;
        data[2] = (data[2] +alpha[2]) /2;
        data[3] = (data[3] +alpha[3]) /2;
        wr.setPixel(x, y, data);
      }
    }
    
    return destination;
  }

}
