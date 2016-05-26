/*
 * DbObjectContainer.java
 *
 * Created on 2007-11-13, 17:33:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @param T
 * @author akaluza
 */
public abstract class DbObjectContainer<T extends DbObjectIdentified> extends DbObjectIdentified {
  
  protected HashMap<String, T> objectList;
  
  public DbObjectContainer(String name, DbObjectIdentified owner, boolean refreshed) {
    this(name, owner);
    setRefreshed(refreshed);
  }
  
  public DbObjectContainer(String name, DbObjectIdentified owner) {
    super(name, owner);
    objectList = new HashMap<String, T>();
  }
  
  public T put(T item) {
    return put(item.getName(), item);
  }
  
  public T put(String name, T item) {
    synchronized (objectList) {
      objectList.put(name, item);
      return item;
    }
  }
  
  /**
   * <p>Powinna zwróciæ listê w³aœciwoœci elementów listy z pominiêciem name i remarks
   * @see getMemberNames
   * @return
   */
  public abstract String[] getColumnNames();
  
  private void refreshThis() {
    synchronized (objectList) {
      if (!isRefreshed() && !isRefreshing()) {
        try {
          refresh();
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    }
  }
  
  public T get(String name) {
    refreshThis();
    return objectList.get(name);
  }
  
  public Iterator<T> objects() {
    refreshThis();
    return objectList.values().iterator();
  }
  
  public Iterator<String> names() {
    refreshThis();
    return objectList.keySet().iterator();
  }
  
  public String[] namesArray() {
    synchronized (objectList) {
      String[] array = new String[size()];
      Iterator<String> i = names();
      int c = 0;
      while (i.hasNext()) {
        array[c++] = i.next();
      }
      return array;
    }
  }
  
  public DbObjectIdentified[] objectsArray() {
    synchronized (objectList) {
      DbObjectIdentified[] array = new DbObjectIdentified[size()];
      Iterator<T> i = objects();
      int c = 0;
      while (i.hasNext()) {
        array[c++] = i.next();
      }
      return array;
    }
  }
  
  public DbObjectIdentified[] objectsArray(boolean sortedByName) {
    synchronized (objectList) {
      DbObjectIdentified[] array = objectsArray();
      if (sortedByName) {
        Arrays.sort(array);
      }
      return array;
    }
  }
  
  public int size() {
    refreshThis();
    return objectList.size();
  }
  
  public void clear() {
    synchronized (objectList) {
      objectList.clear();
    }
  }
  
  @Override
  public DbObjectIdentified getObjectInfo(StringTokenizer tokenizer) {
    if (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      DbObjectIdentified o = null;
      if ("..".equals(token)) {
        o = getOwner();
      } 
      else if (".".equals(token)) {
        o = this;
      } 
      else if (token.indexOf('?') >= 0) {
        o = get(token.substring(0, token.indexOf('?')));
        if (o != null) {
          o.setRefreshed(false);
          o.setFilter(token.substring(token.indexOf('?') +1, token.length()));
        }
      }
      else {
        o = get(token);
      }
      if (o != null) {
        return o.getObjectInfo(tokenizer);
      }
      return null;
    } else {
      return this;
    }
  }
  
}
