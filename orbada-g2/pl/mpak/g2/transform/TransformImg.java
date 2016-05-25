package pl.mpak.g2.transform;

import java.awt.image.BufferedImage;

public abstract class TransformImg implements Transform {
  
  protected BufferedImage pattern;

  public TransformImg(BufferedImage pattern) {
    this.pattern = pattern;
  }

}
