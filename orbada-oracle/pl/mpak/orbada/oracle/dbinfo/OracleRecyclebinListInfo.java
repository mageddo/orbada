package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleRecyclebinListInfo extends DbObjectContainer<OracleRecyclebinInfo> {
  
  private boolean onSchema;
  
  public OracleRecyclebinListInfo(DbObjectContainer owner) {
    super("RECYCLEBIN", owner);
    onSchema = getSchema() != null;
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    if ("TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false")) && !onSchema) {
      return new String[] {"Nazwa schematu", "Nazwa oryginalna", "Typ obiektu", "Operacja", "Utworzony", "Usuniêty"};
    }
    else {
      return new String[] {"Nazwa oryginalna", "Typ obiektu", "Operacja", "Utworzony", "Usuniêty"};
    }
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
      clear();
      Query query = getDatabase().createQuery();
      try {
        if ("TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false"))) {
          query.setSqlText(Sql.getDbaRecyclebinList(getFilter()));
          if (onSchema) {
            query.paramByName("SCHEMA_NAME").setString(getSchema().getName());
          }
        }
        else {
          query.setSqlText(Sql.getRecyclebinList(null));
        }
        query.open();
        while (!query.eof()) {
          OracleRecyclebinInfo info = new OracleRecyclebinInfo(query.fieldByName("object_name").getString(), this);
          if ("TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false"))) {
            info.setSchemaName(query.fieldByName("owner").getString());
          }
          info.setOriginalName(query.fieldByName("original_name").getString());
          info.setType(query.fieldByName("type").getString());
          info.setOperation(query.fieldByName("operation").getString());
          info.setCreateTime(query.fieldByName("createtime").getString());
          info.setDropTime(query.fieldByName("droptime").getString());
          put(info);
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
