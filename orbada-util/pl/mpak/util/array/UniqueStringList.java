package pl.mpak.util.array;

public class UniqueStringList extends StringList {
  private static final long serialVersionUID = 6097822845712354786L;
  
  public UniqueStringList(boolean ignoreCase, int initialCapacity) {
    super(ignoreCase, initialCapacity);
  }

  public UniqueStringList(boolean ignoreCase) {
    super(ignoreCase);
  }

  public UniqueStringList() {
    super();
  }

  public String set(int index, String element) {
    if (!exists(element)) {
      return super.set(index, element);
    }
    return element;
  }
  
  public boolean add(String o) {
    if (!exists(o)) {
      return super.add(o);
    }
    return false;
  }

}
