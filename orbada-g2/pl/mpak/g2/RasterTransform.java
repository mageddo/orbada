package pl.mpak.g2;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;

import pl.mpak.g2.transform.Transform;
import pl.mpak.g2.transform.TransformOp;

public class RasterTransform {

  public static Transform BLURE(int size) {
    float[] data = new float[size * size];
    float value = 1 / (float) (size * size);
    for (int i = 0; i < data.length; i++) {
        data[i] = value;
    }
    return new TransformOp(new ConvolveOp(new Kernel(size, size, data)));
  }
  
  public static Transform BLURE = BLURE(3);
  
  public static Transform MORE_BLURE = BLURE(5);

  public static Transform GAUSSIAN_BLURE(int size, float deviation) {
    float[] data = new float[size * size];
    double second = 2.0 * Math.pow(deviation, 2.0);
    double first = 1.0 / (Math.PI * second);
    int r = size / 2;
    int x, y;
    for (int i = -r; i <= r; i++) {
      x = i + r;
      for (int j = -r; j <= r; j++) {
        y = j + r;
        data[x + y * size] = (float) (first * Math.exp(-(Math.pow(i, 2.0) + Math.pow(j, 2.0)) / second));
      }
    }
    return new TransformOp(new ConvolveOp(new Kernel(size, size, data), ConvolveOp.EDGE_NO_OP, null));
  }

  public static Transform GAUSSIAN_BLURE = GAUSSIAN_BLURE(5, 1.0f);
  
  private static short[] brighten = new short[256];
  private static short[] betterBrighten = new short[256];
  private static short[] posterize = new short[256];
  private static short[] invert = new short[256];
  private static short[] straight = new short[256];
  private static short[] zero = new short[256];
  
  static {
    for (int i = 0; i < 256; i++) {
      brighten[i] = (short) (128 + i / 2);
      betterBrighten[i] = (short) (Math.sqrt((double) i / 255.0) * 255.0);
      posterize[i] = (short) (i - (i % 32));
      invert[i] = (short) (255 - i);
      straight[i] = (short) i;
      zero[i] = (short) 0;
    }
  }
  
  public static Transform BRIGHTEN = new TransformOp(new LookupOp(new ShortLookupTable(0, new short[][] {brighten, brighten, brighten, straight}), null));
  public static Transform BETTER_BRIGHTEN = new TransformOp(new LookupOp(new ShortLookupTable(0, new short[][] {betterBrighten, betterBrighten, betterBrighten, straight}), null));
  
  public static Transform GRAYSCALE = new TransformOp(new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null));
  
  public static Transform DARKEN = new TransformOp(new RescaleOp(new float[] {0.7f, 0.7f, 0.7f, 1f}, new float[] {0f, 0f, 0f, 0f}, null));
  public static Transform MORE_DARKEN = new TransformOp(new RescaleOp(new float[] {0.5f, 0.5f, 0.5f, 1f}, new float[] {0f, 0f, 0f, 0f}, null));
  public static Transform LIGHTEN = new TransformOp(new RescaleOp(new float[] {1.2f, 1.2f, 1.2f, 1f}, new float[] {0f, 0f, 0f, 0f}, null));
  public static Transform MORE_LIGHTEN = new TransformOp(new RescaleOp(new float[] {1.5f, 1.5f, 1.5f, 1f}, new float[] {0f, 0f, 0f, 0f}, null));

  public static Transform SHADOW = new TransformOp(new Transform[] {
      new TransformOp(new RescaleOp(new float[] {0f, 0f, 0f, 0.5f}, new float[] {0f, 0f, 0f, 0f}, null)), 
      MORE_BLURE});
  
  public static Transform POSTERIZE = new TransformOp(new LookupOp(new ShortLookupTable(0, new short[][] {posterize, posterize, posterize, straight}), null));
  public static Transform INVERT = new TransformOp(new LookupOp(new ShortLookupTable(0, invert), null));
  public static Transform SHARPEN = new TransformOp(new ConvolveOp(new Kernel(3, 3, new float[] { 0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f })));
  public static Transform EDGE = new TransformOp(new ConvolveOp(new Kernel(3, 3, new float[] { 0f, -1f, 0f, -1f, 4f, -1f, 0f, -1f, 0f }), ConvolveOp.EDGE_NO_OP, null));
  
  public static Transform LAPLACIAN = new TransformOp(new BufferedImageOp[] {
    new ConvolveOp(new Kernel(3, 3, new float[] { 0, 1, 0, 1, -4, 1, 0, 1, 0 }), ConvolveOp.EDGE_NO_OP, null),
    new ConvolveOp(new Kernel(3, 3, new float[] { 1, 1, 1, 1, -8, 1, 1, 1, 1 }), ConvolveOp.EDGE_NO_OP, null),
    new ConvolveOp(new Kernel(3, 3, new float[] { -1, 2, -1, 2, -4, 2, -1, 2, -1 }), ConvolveOp.EDGE_NO_OP, null)
  });
  
  public static Transform EMBOSE = new TransformOp(new ConvolveOp(new Kernel(3, 3, new float[] { -2f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 2f })));

  public static BufferedImage process(BufferedImage image, BufferedImage destination, Transform filter) {
    return filter.process(image, destination);
  }
  
}
