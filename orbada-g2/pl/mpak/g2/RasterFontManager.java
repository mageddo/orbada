package pl.mpak.g2;

import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;

public class RasterFontManager {

  private HashMap<String, RasterFont> list;
  
  public RasterFontManager() {
    super();
    list = new HashMap<String, RasterFont>(); 
  }
  
  public void add(String name, RasterFont image) {
    list.put(name, image);
  }
  
  public void add(String name, String fileName, String chars, int kerning) {
    add(name, getClass().getResource(fileName), chars, kerning);
  }
  
  public void add(String name, URL url, String chars, int kerning) {
    list.put(name, new RasterFont(G2Util.image2BufferedImage(Toolkit.getDefaultToolkit().createImage(url)), chars, kerning));
  }
  
  public RasterFont get(String name) {
    return list.get(name);
  }

}
