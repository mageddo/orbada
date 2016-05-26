package pl.mpak.g2;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import pl.mpak.util.Controlable;
import pl.mpak.util.Drawable;

public class VisibleItemList implements Controlable, Drawable {

  private ArrayList<VisibleItem> list;
  
  public VisibleItemList() {
    super();
    list = new ArrayList<VisibleItem>();
  }
  
  public void add(VisibleItem item) {
    list.add(item);
  }

  public void remove(VisibleItem item) {
    list.remove(item);
  }

  public void control() {
    for (int i=0; i<list.size(); i++) {
      list.get(i).control();
    }
  }

  public void draw(Graphics g, int x, int y, ImageObserver observer) {
    for (int i=0; i<list.size(); i++) {
      list.get(i).draw(g, x, y, observer);
    }
  }
  
  public int getWidth() {
    int maxwidth = 0;
    for (int i=0; i<list.size(); i++) {
      if (list.get(i).getLeft() +list.get(i).getWidth() > maxwidth) {
        maxwidth = list.get(i).getLeft() +list.get(i).getWidth();
      }
    }
    return maxwidth;
  }

  public int getHeight() {
    if (list.size() > 0) {
      return list.get(list.size() -1).getTop() +list.get(list.size() -1).getHeight();
    }
    return 0;
  }

}
