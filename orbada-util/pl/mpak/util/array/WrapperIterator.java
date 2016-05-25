package pl.mpak.util.array;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class WrapperIterator implements Iterator {

  private static final Object[] emptyelements = new Object[0];
  private Object[] elements;
  private int i;

  private boolean chained;
  private Iterator it1;
  private Iterator it2;

  private boolean notNull;

  public WrapperIterator() {
    this.elements = emptyelements;
  }

  public WrapperIterator(Object[] elements) {
    this.elements = elements;
  }

  public WrapperIterator(Object[] elements, boolean notNull) {
    this.elements = elements;
    this.notNull = notNull;
  }

  public WrapperIterator(Object element) {
    this.elements = new Object[] { element };
  }

  public WrapperIterator(Iterator it1, Iterator it2) {

    this.it1 = it1;
    this.it2 = it2;
    chained = true;
  }

  public boolean hasNext() {

    if (chained) {
      if (it1 == null) {
        if (it2 == null) {
          return false;
        }

        if (it2.hasNext()) {
          return true;
        }

        it2 = null;

        return false;
      }
      else {
        if (it1.hasNext()) {
          return true;
        }

        it1 = null;

        return hasNext();
      }
    }

    if (elements == null) {
      return false;
    }

    for (; notNull && i < elements.length && elements[i] == null; i++) {
    }

    if (i < elements.length) {
      return true;
    }
    else {

      elements = null;

      return false;
    }
  }

  public Object next() {

    if (chained) {
      if (it1 == null) {
        if (it2 == null) {
          throw new NoSuchElementException();
        }

        if (it2.hasNext()) {
          return it2.next();
        }

        it2 = null;

        next();
      }
      else {
        if (it1.hasNext()) {
          return it1.next();
        }

        it1 = null;

        next();
      }
    }

    if (hasNext()) {
      return elements[i++];
    }

    throw new NoSuchElementException();
  }

  public int nextInt() {
    throw new NoSuchElementException();
  }

  public long nextLong() {
    throw new NoSuchElementException();
  }

  public void remove() {
    throw new NoSuchElementException();
  }
}
