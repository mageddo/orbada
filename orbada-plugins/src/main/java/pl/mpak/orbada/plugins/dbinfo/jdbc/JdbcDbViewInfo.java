/*
 * DerbyDbViewInfo.java
 *
 * Created on 2007-11-15, 19:31:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DefaultDbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DefaultDbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbViewInfo extends DbObjectContainer {
  
  private String objectName;
  private String catalog;
  
  public JdbcDbViewInfo(String name, JdbcDbViewListInfo owner) {
    super(name, owner);
    put(new DefaultDbObjectContainer("COLUMNS", this) {
      public String[] getColumnNames() {
        return new String[] {"Pozycja", "Typ", "Rozmiar", "Prec.", "Null?", "Komentarz"};
      }
    });
    put(new DefaultDbObjectContainer<JdbcDbPrivilegeInfo>("PRIVILEGES", this) {
      public String[] getColumnNames() {
        return new String[] {"Nada³", "Dla", "Admin"};
      }
    });
    put(new DefaultDbObjectIdentified("CONTENT", this));
  }
  
  public JdbcDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(JdbcDbSchemaInfo.class);
    if (o != null) {
      return (JdbcDbSchemaInfo)o;
    }
    return null;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
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
    return new String[] {"Katalog", "Komentarz"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {new Variant(getCatalog()), new Variant(getRemarks())};
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      DbObjectContainer columns = (DbObjectContainer)get("COLUMNS");
      columns.setRefreshing(true);
      columns.clear();
      Query query = getDatabase().createQuery();
      try {
        JdbcDbSchemaInfo schema = getSchema();
        query.setResultSet(getDatabase().getMetaData().getColumns(getCatalog(), schema == null ? null : schema.getName(), getObjectName(), "%"));
        while (!query.eof()) {
          JdbcDbViewColumnInfo column = new JdbcDbViewColumnInfo(query.fieldByName("column_name").getString(), this);
          column.setDefaultValue(query.fieldByName("column_def").getString());
          column.setPosition(query.fieldByName("ordinal_position").getInteger());
          column.setDataType(query.fieldByName("type_name").getString());
          column.setDataSize(query.fieldByName("column_size").getInteger());
          column.setDigits(query.fieldByName("decimal_digits").getInteger());
          column.setNullable(query.fieldByName("nullable").getInteger() == 1);
          column.setRemarks(query.fieldByName("remarks").getString());
          columns.put(column);
          query.next();
        }
      }
      finally {
        query.close();
        columns.setRefreshed(true);
      }
      DbObjectContainer privileges = (DbObjectContainer)get("PRIVILEGES");
      privileges.setRefreshing(true);
      privileges.clear();
      try {
        JdbcDbSchemaInfo schema = getSchema();
        query.setResultSet(getDatabase().getMetaData().getTablePrivileges(getCatalog(), schema == null ? null : schema.getName(), getObjectName()));
        while (!query.eof()) {
          JdbcDbPrivilegeInfo privilege = new JdbcDbPrivilegeInfo(query.fieldByName("PRIVILEGE").getString(), this);
          privilege.setGrantor(query.fieldByName("GRANTOR").getString());
          privilege.setGrantee(query.fieldByName("GRANTEE").getString());
          privilege.setGrantable(query.fieldByName("IS_GRANTABLE").getString());
          privileges.put(privilege);
          query.next();
        }
      }
      finally {
        query.close();
        privileges.setRefreshing(true);
      }
    }
    finally {
      setRefreshed(true);
    }
  }
  
}
