package pl.mpak.g2;

import java.util.HashMap;

public class PictureListManager {

  private HashMap<String, PictureList> list;
  
  public PictureListManager() {
    super();
    list = new HashMap<String, PictureList>();
  }
  
  public void add(String name, PictureList pictureList) {
    list.put(name, pictureList);
  }
  
  public PictureList get(String name) {
    return list.get(name);
  }
  
}
