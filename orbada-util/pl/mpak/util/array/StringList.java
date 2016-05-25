package pl.mpak.util.array;

import java.util.ArrayList;
import java.util.Collections;

import pl.mpak.util.Order;

public class StringList extends ArrayList<String> {
  private static final long serialVersionUID = -976775035168107305L;

  private boolean ignoreCase = true;

  public StringList(boolean ignoreCase, int initialCapacity) {
    super(initialCapacity);
    setIgnoreCase(ignoreCase);
  }

  public StringList(boolean ignoreCase) {
    super();
    setIgnoreCase(ignoreCase);
  }

  public StringList() {
    super();
  }

  public Object clone() {
    Object result = super.clone();
    ((StringList)result).ignoreCase = this.ignoreCase;
    return result;
  }

  public int indexOf(Object elem) {
    if (elem == null) {
      for (int i = 0; i < size(); i++)
        if (get(i) == null)
          return i;
    }
    else {
      for (int i = 0; i < size(); i++) {
        if (ignoreCase) {
          if (get(i).equalsIgnoreCase((String)elem)) {
            return i;
          }
        }
        else {
          if (get(i).equals(elem)) {
            return i;
          }
        }
      }
    }
    return -1;
  }
  
  public boolean exists(String elem) {
    return indexOf(elem) != -1;
  }

  public void setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  public String getText() {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<size(); i++) {
      sb.append(get(i));
      if (i != size() -1) {
        sb.append('\n');
      }
    }
    return sb.toString();
  }

  public void setText(String text) {
    StringBuilder sb = new StringBuilder();
    
    clear();
    if (text == null) {
      return;
    }
    
    int i = 0;
    while(i<text.length()) {
      char ch = text.charAt(i);
      if (ch == '\n') {
        add(sb.toString());
        sb.setLength(0);
      }
      else if (ch != '\r') {
        sb.append(ch);
      }
      i++;
    }
    add(sb.toString());
  }
  
  public void sort(Order order) {
    Collections.sort(this, new AlphabeticComparator(ignoreCase, order));
  }
  
  public void sort() {
    sort(Order.ASCENDING);
  }
  
}
