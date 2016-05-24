package pl.mpak.orbada.plugins.dbinfo.jdbc;

import java.sql.DatabaseMetaData;
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
public class JdbcDbProcedureInfo extends DbObjectContainer {
  
  private String objectName;
  private String catalog;

  public JdbcDbProcedureInfo(String name,JdbcDbProcedureListInfo owner) {
    super(name, owner);
    put(new DefaultDbObjectContainer<JdbcDbProcedureParameterInfo>("PARAMETERS", this) {
      public String[] getColumnNames() {
        return new String[] {"Pozycja", "Typ", "Metoda", "Komentarz"};
      }
    });
    put(new DefaultDbObjectContainer<JdbcDbPrivilegeInfo>("PRIVILEGES", this) {
      public String[] getColumnNames() {
        return new String[] {"Nada³", "Dla", "Admin"};
      }
    });
    put(new DefaultDbObjectIdentified("CALL", this));
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
      DbObjectContainer params = (DbObjectContainer)get("PARAMETERS");
      params.setRefreshing(true);
      params.clear();
      Query query = getDatabase().createQuery();
      try {
        JdbcDbSchemaInfo schema = getSchema();
        query.setResultSet(getDatabase().getMetaData().getProcedureColumns(getCatalog(), schema == null ? null : schema.getName(), getObjectName(), null));
        while (!query.eof()) {
          JdbcDbProcedureParameterInfo param = new JdbcDbProcedureParameterInfo(query.fieldByName("COLUMN_NAME").getString(), this);
          if (query.fieldByName("COLUMN_TYPE").getInteger() == DatabaseMetaData.procedureColumnResult) {
            param.setInOutReturn("{result}");
          }
          else if (query.fieldByName("COLUMN_TYPE").getInteger() == DatabaseMetaData.procedureColumnReturn) {
            param.setInOutReturn("{return}");
          }
          else if (query.fieldByName("COLUMN_TYPE").getInteger() == DatabaseMetaData.procedureColumnIn) {
            param.setInOutReturn("IN");
          }
          else if (query.fieldByName("COLUMN_TYPE").getInteger() == DatabaseMetaData.procedureColumnOut) {
            param.setInOutReturn("OUT");
          }
          else if (query.fieldByName("COLUMN_TYPE").getInteger() == DatabaseMetaData.procedureColumnInOut) {
            param.setInOutReturn("IN/OUT");
          }
          else {
            param.setInOutReturn("{unknown}");
          }
          param.setType(query.fieldByName("TYPE_NAME").getString());
          param.setRemarks(query.fieldByName("remarks").getString());
          params.put(param);
          query.next();
        }
      }
      finally {
        query.close();
        params.setRefreshed(true);
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
