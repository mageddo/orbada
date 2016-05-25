package pl.mpak.util.array;

import java.io.Serializable;
import java.util.Comparator;

import pl.mpak.util.Order;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Klasa która s³u¿y do porównania dwóch stringów na liœcie w trakcie sortowania
 * Array.sort(String[], new AlphabeticComparator());
 *
 */
public class AlphabeticComparator implements Comparator<String>, Serializable {
  private static final long serialVersionUID = 6732507117930675404L;

  private boolean ignoreCase = true;
  private Order order = Order.ASCENDING;
  
  public AlphabeticComparator() {
    super();
  }

  public AlphabeticComparator(boolean ignoreCase) {
    super();
    this.ignoreCase = ignoreCase;
  }

  public AlphabeticComparator(boolean ignoreCase, Order order) {
    super();
    this.ignoreCase = ignoreCase;
    this.order = order;
  }

  public AlphabeticComparator(Order order) {
    super();
    this.order = order;
  }

  public int compare(String o1, String o2) {
    if (order == Order.DESCENDING) {
      if (ignoreCase) {
        return o2.compareToIgnoreCase(o1);
      }
      else {
        return o2.compareTo(o1);
      }
    }
    else {
      if (ignoreCase) {
        return o1.compareToIgnoreCase(o2);
      }
      else {
        return o1.compareTo(o2);
      }
    }
  }

}
