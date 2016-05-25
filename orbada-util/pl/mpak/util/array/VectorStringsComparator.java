package pl.mpak.util.array;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Vector;

import pl.mpak.util.Order;
import pl.mpak.util.StringUtil;

public class VectorStringsComparator implements Comparator<Vector>, Serializable {
  private static final long serialVersionUID = 7110807541548549157L;

  private int column = -1;
  private Order order = Order.ASCENDING;
  
  public VectorStringsComparator(int column) {
    super();
    this.column = column;
  }

  public VectorStringsComparator(int column, Order order) {
    this(column);
    this.order = order;
  }

  public int compare(Vector o1, Vector o2) {
    if (order == Order.DESCENDING) {
      return StringUtil.nvl((String)o2.get(column), "").compareToIgnoreCase(StringUtil.nvl((String)o1.get(column), ""));
    }
    else {
      return StringUtil.nvl((String)o1.get(column), "").compareToIgnoreCase(StringUtil.nvl((String)o2.get(column), ""));
    }
  }

}
