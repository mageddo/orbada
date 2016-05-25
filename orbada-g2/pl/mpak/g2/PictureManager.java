package pl.mpak.g2;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

public class PictureManager {

  private HashMap<String, BufferedImage> list;
  
  public PictureManager() {
    super();
    list = new HashMap<String, BufferedImage>(); 
  }
  
  public void add(String name, BufferedImage image) {
    list.put(name, image);
  }
  
  public void add(String name, String fileName) {
    add(name, getClass().getResource(fileName));
  }
  
  public void add(String name, URL url) {
    list.put(name, G2Util.createImage(url));
  }
  
  public BufferedImage get(String name) {
    return list.get(name);
  }

}
