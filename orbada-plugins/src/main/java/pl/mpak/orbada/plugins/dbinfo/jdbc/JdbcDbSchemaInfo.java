/*
 * DerbyDbSchemaInfo.java
 *
 * Created on 2007-11-13, 21:40:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbSchemaInfo extends DbObjectContainer<DbObjectContainer> {
  
  private String catalog;
  
  public JdbcDbSchemaInfo(String name, JdbcDbSchemaListInfo owner) {
    super(name, owner);
  }

  public String getCatalog() {
    return catalog;
  }

  public void setCatalog(String catalog) {
    this.catalog = catalog;
  }
  
  public String[] getColumnNames() {
    return new String[] {};
  }

  public String[] getMemberNames() {
    return new String[] {"Katalog"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {new Variant(getCatalog())};
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      put(new JdbcDbTableListInfo(this));
      put(new JdbcDbViewListInfo(this));
      put(new JdbcDbFunctionListInfo(this));
      put(new JdbcDbProcedureListInfo(this));
      put(new JdbcDbIndexListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }
  
  public boolean equals(Object o) {
    if (o instanceof JdbcDbSchemaInfo) {
      return getName().equals(((JdbcDbSchemaInfo)o).getName());
    }
    else if (o instanceof String) {
      return getName().equals(o);
    }
    return false;
  }
  
}
