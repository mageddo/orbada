package pl.mpak.g2.transform;

import java.awt.image.BufferedImage;

public interface Transform {

  public BufferedImage process(BufferedImage image, BufferedImage destination);
  
}
