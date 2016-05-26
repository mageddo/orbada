package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleIndexListInfo extends DbObjectContainer<OracleIndexInfo> {
  
  private boolean onTable;
  
  public OracleIndexListInfo(DbObjectContainer owner) {
    super("INDEXES", owner);
    DbObjectInfo info = getObjectOwner();
    onTable = info != null && StringUtil.equalAnyOfString(info.getObjectType(), new String[] {"TABLE", "MATERIALIZED VIEW"}, true);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    if (onTable) {
      return new String[] {"Nazwa kolumny", "Typ", "Unikalny", "Przestrzeñ danych"};
    }
    else {
      return new String[] {"Tabela", "Typ", "Unikalny", "Przestrzeñ danych"};
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
        if (onTable) {
          query.setSqlText(Sql.getIndexList(getFilter(), false));
          query.paramByName("table_name").setString(((DbObjectIdentified)getObjectOwner()).getName());
        }
        else {
          query.setSqlText(Sql.getAllIndexList(null));
        }
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleIndexInfo info = new OracleIndexInfo(query.fieldByName("index_name").getString(), this);
          info.setTableName(query.fieldByName("table_name").getString());
          if (onTable) {
            info.setColumnName(query.fieldByName("column_name").getString());
          }
          info.setType(query.fieldByName("index_type").getString());
          info.setUnique("YES".equals(query.fieldByName("uniqueness").getString()));
          info.setTablespaceName(query.fieldByName("tablespace_name").getString());
          put(info.getName() +"." +info.getColumnName(), info);
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
