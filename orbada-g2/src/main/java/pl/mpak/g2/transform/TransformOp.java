package pl.mpak.g2.transform;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class TransformOp implements Transform {

  private BufferedImageOp biop;
  private BufferedImageOp[] biopList;
  private Transform[] transform;
  
  public TransformOp(BufferedImageOp biop) {
    this.biop = biop;
  }
  
  public TransformOp(BufferedImageOp[] biopList) {
    this.biopList = biopList;
  }
  
  public TransformOp(Transform[] transform) {
    this.transform = transform;
  }
  
  public BufferedImage process(BufferedImage image, BufferedImage destination) {
    if (biop != null) {
      return biop.filter(image, destination);
    }
    else if (transform != null) {
      BufferedImage bi = transform[0].process(image, destination);
      for (int i=1; i<transform.length; i++) {
        bi = transform[i].process(bi, destination);
      }
      return bi;
    }
    else {
      BufferedImage bi = biopList[0].filter(image, destination);
      for (int i=1; i<biopList.length; i++) {
        bi = biopList[i].filter(bi, destination);
      }
      return bi;
    }
  }
  
}
