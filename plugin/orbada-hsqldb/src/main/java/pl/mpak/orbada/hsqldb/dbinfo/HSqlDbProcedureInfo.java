/*
 * DbProcedureInfo.java
 *
 * Created on 2007-11-13, 19:50:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

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
public class HSqlDbProcedureInfo extends DbObjectContainer {
  
  public HSqlDbProcedureInfo(String name, HSqlDbProcedureListInfo owner) {
    super(name, owner);
    put(new DefaultDbObjectContainer<HSqlDbProcedureParameterInfo>("PARAMETERS", this) {
      public String[] getColumnNames() {
        return new String[] {"Pozycja", "Typ", "Metoda"};
      }
    });
    put(new DefaultDbObjectIdentified("SOURCE", this));
    put(new DefaultDbObjectIdentified("CALL", this));
  }
  
  public HSqlDbSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(HSqlDbSchemaInfo.class);
    if (o != null) {
      return (HSqlDbSchemaInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return new String[] {};
  }
  
  public String[] getMemberNames() {
    return new String[] {};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {};
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      DbObjectContainer params = (DbObjectContainer)get("PARAMETERS");
      params.clear();
      Query query = getDatabase().createQuery();
      try {
        query.setResultSet(getDatabase().getMetaData().getProcedureColumns(null, getSchema().getName(), getName(), null));
        while (!query.eof()) {
          HSqlDbProcedureParameterInfo param = new HSqlDbProcedureParameterInfo(query.fieldByName("COLUMN_NAME").getString(), this);
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
          params.put(param);
          query.next();
        }
      }
      finally {
        query.close();
      }
    }
    finally {
      setRefreshed(true);
    }
  }

}
