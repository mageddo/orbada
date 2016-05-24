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
public class OracleTypeAttrListInfo extends DbObjectContainer<OracleTypeAttrInfo> {
  
  public OracleTypeAttrListInfo(OracleTypeInfo owner) {
    super("ATTRIBUTES", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleTypeInfo getType() {
    DbObjectIdentified o = getOwner(OracleTypeInfo.class);
    if (o != null) {
      return (OracleTypeInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Lp", "Typ", "D³ugoœæ", "Precyzja", "Skala"};
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
        query.setSqlText(Sql.getTypeAttrList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.paramByName("type_name").setString(getType().getName());
        query.open();
        while (!query.eof()) {
          OracleTypeAttrInfo column = new OracleTypeAttrInfo(query.fieldByName("attr_name").getString(), this);
          column.setAttrTypeName(query.fieldByName("attr_type_name").getString());
          column.setAttrNo(query.fieldByName("attr_no").getLong());
          column.setLength(query.fieldByName("length").isNull() ? null : query.fieldByName("length").getLong());
          column.setPrecision(query.fieldByName("precision").isNull() ? null : query.fieldByName("precision").getLong());
          column.setScale(query.fieldByName("scale").isNull() ? null : query.fieldByName("scale").getLong());
          put(column);
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
