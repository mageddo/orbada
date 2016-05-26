package pl.mpak.orbada.oracle.dbinfo;

import java.util.ArrayList;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleContentListInfo extends DbObjectContainer<OracleContentInfo> {
  
  private String[] columns;
  
  public OracleContentListInfo(DbObjectContainer owner) {
    super("CONTENT", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    return columns;
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
        DbObjectIdentified object = (DbObjectIdentified)getObjectOwner();
        String name = null;
        if (getSchema() == null) {
          name = SQLUtil.createSqlName(object.getName());
        }
        else {
          name = SQLUtil.createSqlName(getSchema().getName(), object.getName());
        }
        query.setSqlText(
          "select rownum, t.* from " +name +" t" +
          (getFilter() == null ? "" : " where " +getFilter()));
        query.open();
        ArrayList<String> columnList = new ArrayList<String>();
        for (int c = 1; c<query.getFieldCount(); c++) {
          columnList.add(query.getField(c).getDisplayName());
        }
        columns = columnList.toArray(new String[columnList.size()]);
        int maxRows = 100000 /columns.length;
        while (!query.eof()) {
          ArrayList<Variant> values = new ArrayList<Variant>();
          for (int c = 1; c<query.getFieldCount(); c++) {
            values.add(query.getField(c).getValue());
          }
          OracleContentInfo info = new OracleContentInfo(
            query.fieldByName("rownum").getString(),
            columns, values.toArray(new Variant[values.size()]),
            this);
          put(info);
          if (--maxRows == 0) {
            break;
          }
          query.next();
        }
      } 
      finally {
        query.close();
      }
    } 
    finally {
      setRefreshed(false);
    }
  }
  
}
