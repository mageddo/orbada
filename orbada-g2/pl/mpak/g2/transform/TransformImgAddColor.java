package pl.mpak.g2.transform;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class TransformImgAddColor extends TransformImg {

  private Color color;
  
  public TransformImgAddColor(Color color) {
    super(null);
    this.color = color;
  }

  public TransformImgAddColor(BufferedImage pattern) {
    super(pattern);
  }

  public BufferedImage process(BufferedImage image, BufferedImage destination) {
    if (destination == null) {
      destination = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    }
    
    Raster rr = image.getRaster();
    WritableRaster wr = destination.getRaster();
    int data[] = new int[4];
    for (int y=0; y<rr.getHeight(); y++) {
      for (int x=0; x<rr.getWidth(); x++) {
        rr.getPixel(x, y, data);
        data[0] = Math.min(data[0] +color.getRed(), 255);
        data[1] = Math.min(data[1] +color.getGreen(), 255);
        data[2] = Math.min(data[2] +color.getBlue(), 255);
        data[3] = Math.min(data[3] +color.getAlpha(), 255);
        wr.setPixel(x, y, data);
      }
    }
    
    return destination;
  }

}
