package pl.mpak.sky.gui.swing;

import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * Klasa ³aduj¹ca (o ile to konieczne) obrazki z zasobów programu
 * 
 * @author Andrzej Ka³u¿a
 *
 */
public final class ImageManager {
  
  private static HashMap<String, ImageIcon> imageList = new HashMap<String, ImageIcon>();
  public static String iconPath = "/pl/mpak/res/icons";
  
  /**
   * Zwraca obrazek z listy lub jeœli jeszcze nie jest za³adowany ³aduje go
   * 
   * @param resName nazwa obrazka do pobrania
   * @return pobrany obrazek
   */
  public final static ImageIcon getImage(String resName) {
    return getImage(resName, Toolkit.getDefaultToolkit().getClass());
  }
  
  private final static ImageIcon internalGetImage(String resName, Class<?> rootClass) {
    try {
      URL url;
      File file = new File(resName);
      if (file.exists()) {
        url = file.toURI().toURL();
      }
      else {
        url = rootClass.getResource(resName);
      }
      return new ImageIcon(url);
    }
    catch (Throwable e) {
      return null;
    }
  }
  
  public final static ImageIcon getImage(String resName, Class<?> rootClass) {
    synchronized (imageList) {
      ImageIcon ii = imageList.get(resName);
      if (ii != null) {
        return ii;
      }
      ii = internalGetImage(resName, rootClass);
      if (ii == null) {
        ii = internalGetImage(iconPath +"/" +resName +".png", rootClass);
        if (ii == null) {
          ii = internalGetImage(iconPath +"/" +resName +".gif", rootClass);
        }
      }
      if (ii != null) {
        imageList.put(resName, ii);
        return ii;
      }
      imageList.put(resName, null);
      return null;
    }
  }
  
}
