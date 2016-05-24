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
public class JdbcDbTableInfo extends DbObjectContainer {
  
  private String objectName;
  private String catalog;
  
  public JdbcDbTableInfo(String name,JdbcDbTableListInfo owner) {
    super(name, owner);
    put(new JdbcDbTableColumnListInfo(this));
    put(new DefaultDbObjectContainer<JdbcDbPrivilegeInfo>("PRIVILEGES", this) {
      public String[] getColumnNames() {
        return new String[] {"Nada³", "Dla", "Admin"};
      }
    });
    put(new JdbcDbTableIndexListInfo(this));
    put(new DefaultDbObjectIdentified("CONTENT", this));
  }
  
  public JdbcDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(JdbcDbSchemaInfo.class);
    if (o != null) {
      return (JdbcDbSchemaInfo)o;
    }
    return null;
  }

  public String getCatalog() {
    return catalog;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
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
      DbObjectContainer privileges = (DbObjectContainer)get("PRIVILEGES");
      privileges.setRefreshing(true);
      privileges.clear();
      Query query = getDatabase().createQuery();
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
