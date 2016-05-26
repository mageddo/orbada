/*
 * DbObjectIdentified.java
 *
 * Created on 2007-11-13, 17:30:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo;

import java.util.StringTokenizer;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public abstract class DbObjectIdentified implements Comparable<DbObjectIdentified> {
  
  private volatile boolean refreshed;
  private volatile boolean refreshing;
  private String name;
  private String remarks;
  private DbObjectIdentified owner;
  private String filter;
  
  public DbObjectIdentified(String name) {
    this(name, null);
  }

  public DbObjectIdentified(String name, DbObjectIdentified owner) {
    this.name = name;
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public DbObjectIdentified getOwner() {
    return owner;
  }
  
  /**
   * <p>Powinna zwróciæ listê w³aœciwoœci obiektu z pominiêciem name i remarks
   * @return 
   */
  public abstract String[] getMemberNames();
  
  /**
   * <p>Powinna zwróciæ wartoœci w/w w³aœciwoœci w takiej w³aœnie kolejnoœci
   * @return 
   */
  public abstract Variant[] getMemberValues();
  
  /**
   * <p>Powinna odœwierzyæ w³aœciwoœci obiektu oraz listy jeœli ni¹ jest
   * @throws java.lang.Exception 
   */
  public abstract void refresh() throws Exception;
  
  public void saveRefresh() {
    try {
      refresh();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public boolean isRefreshing() {
    return refreshing;
  }

  public void setRefreshing(boolean refreshing) {
    this.refreshing = refreshing;
  }

  public boolean isRefreshed() {
    return refreshed;
  }

  public void setRefreshed(boolean refreshed) {
    this.refreshed = refreshed;
    this.refreshing = false;
  }
  
  public DbObjectIdentified getObjectInfo(String path) {
    if (path == null) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(path, "/\\");
    return getObjectInfo(st);
  }
  
  public DbObjectIdentified getObjectInfo(StringTokenizer tokenizer) {
    if (tokenizer.hasMoreTokens()) {
      return null;
    }
    return this;
  }
  
  public String getPath() {
    if (getOwner() != null) {
      return getOwner().getPath() +"/" +getName();
    }
    return "/";
  }
  
  public Database getDatabase() {
    DbObjectIdentified o = getOwner(DbDatabaseInfo.class);
    if (o != null) {
      return o.getDatabase();
    }
    return null;
  }
  
  public DbObjectIdentified getOwner(Class<? extends DbObjectIdentified> classOwner) {
    DbObjectIdentified o = getOwner();
    while (o != null) {
      if (classOwner.isInstance(o)) {
        return o;
      }
      o = o.getOwner();
    }
    return null;
  }
  
  public DbObjectInfo getObjectOwner() {
    DbObjectIdentified o = getOwner();
    while (o != null) {
      if (o instanceof DbObjectInfo) {
        return (DbObjectInfo)o;
      }
      o = o.getOwner();
    }
    return null;
  }
  
  @Override
  public String toString() {
    return name;
  }
  
  public int compareTo(DbObjectIdentified o) {
    return name.compareTo(o.getName());
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof String) {
      return getName().equals(o);
    }
    else if (o instanceof DbObjectIdentified) {
      return getName().equals(((DbObjectIdentified)o).getName());
    }
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 61 * hash + (this.owner != null ? this.owner.hashCode() : 0);
    return hash;
  }

}
