package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import pl.mpak.util.StringUtil;

public class CodeElementList<E extends CodeElement> extends CodeElement implements List<E> {

  private ArrayList<E> list;
  
  public CodeElementList(CodeElement owner) {
    super(owner, "List");
    list = new ArrayList<E>();
  }

  public CodeElementList(CodeElement owner, String codeName) {
    super(owner, codeName);
    list = new ArrayList<E>();
  }

  public CodeElementList(CodeElement owner, String codeName, Collection<? extends E> c) {
    super(owner, codeName);
    list = new ArrayList<E>(c);
  }
  
  public int size() {
    return list.size();
  }
  
  public boolean isEmpty() {
    return list.isEmpty();
  }
  
  public boolean contains(Object o) {
    return list.contains(o);
  }
  
  public int indexOf(Object o) {
    return list.indexOf(o);
  }
  
  public Object[] toArray() {
    return list.toArray();
  }
  
  public <T> T[] toArray(T[] a) {
    return list.toArray(a);
  }
  
  public E get(int index) {
    return list.get(index);
  }
  
  public E set(int index, E element) {
    return list.set(index, element);
  }
  
  public boolean add(E e) {
    return list.add(e);
  }
  
  public void add(int index, E element) {
    list.add(index, element);
  }
  
  public E remove(int index) {
    return list.remove(index);
  }
  
  public boolean remove(Object o) {
    return list.remove(o);
  }
  
  public void clear() {
    list.clear();
  }

  @Override
  public Iterator<E> iterator() {
    return list.iterator();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return list.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return list.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    return list.addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return list.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return list.retainAll(c);
  }

  @Override
  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator() {
    return list.listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return list.listIterator(index);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }

  public CodeElement getElementAt(int offset) {
    CodeElement result;
    for (E e : list) {
      if (e != null && (result = e.getElementAt(offset)) != null) {
        return result;
      }
    }
    return super.getElementAt(offset);
  }
  
  public CodeElement[] find(Class<?> clazz) {
    Set<CodeElement> set = new HashSet<CodeElement>();
    for (E e : list) {
      if (e != null) {
        set.addAll(Arrays.asList(e.find(clazz)));
      }
      if (clazz.isInstance(e)) {
        set.add(e);
      }
    }
    set.addAll(Arrays.asList(super.find(clazz)));
    return set.toArray(new CodeElement[set.size()]);
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    for (CodeElement e : list) {
      sb.append(e.toString() +"\n");
    }
    return sb.toString();
  }
  
  public BlockElement getRootBlock() {
    CodeElement e = getOwner();
    while (e != null) {
      if (e.getOwner() == null && e instanceof BlockElement) {
        return (BlockElement)e;
      }
      e = e.getOwner();
    }
    return null;
  }

  public String toSource(int level) {
    StringBuilder sb = new StringBuilder();
    for (CodeElement e : this) {
      if (sb.length() != 0) {
        sb.append(", ");
      }
      sb.append(e.toSource(level));
    }
    String keywords = keywordsToSource(level, "");
    return (StringUtil.isEmpty(keywords) ? "" : keywords +" ") +sb.toString();
  }
  
}
