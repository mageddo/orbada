package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
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
public class OracleTriggerListInfo extends DbObjectContainer<OracleTriggerInfo> {
  
  private boolean onTable;
  
  public OracleTriggerListInfo(DbObjectContainer owner) {
    super("TRIGGERS", owner);
    DbObjectInfo info = getObjectOwner();
    onTable = info != null && StringUtil.equalAnyOfString(info.getObjectType(), new String[] {"TABLE", "VIEW", "MATERIALIZED VIEW"}, true);
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
      return new String[] {"Stan", "W³¹czony", "Zda¿enie", "Typ", "Kiedy"};
    }
    else {
      return new String[] {"Nazwa tabeli", "Stan", "W³¹czony", "Zda¿enie", "Typ", "Kiedy"};
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
          query.setSqlText(Sql.getTableTriggerList(getFilter()));
          query.paramByName("table_name").setString(((DbObjectIdentified)getObjectOwner()).getName());
        }
        else {
          query.setSqlText(Sql.getAllTriggerList(null, OracleDbInfoProvider.instance.getMajorVersion(getDatabase())));
        }
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleTriggerInfo info = new OracleTriggerInfo(query.fieldByName("trigger_name").getString(), this);
          info.setTableName(query.fieldByName("table_name").getString());
          info.setEnabled(query.fieldByName("enabled").getString());
          info.setState(query.fieldByName("status").getString());
          info.setEvent(query.fieldByName("triggering_event").getString());
          info.setType(query.fieldByName("trigger_type").getString());
          info.setWhen(query.fieldByName("when_clause").getString());
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
