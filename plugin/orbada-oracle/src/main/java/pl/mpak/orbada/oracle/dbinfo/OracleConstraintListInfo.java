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
public class OracleConstraintListInfo extends DbObjectContainer<OracleConstraintInfo> {
  
  private boolean onTable;
  
  public OracleConstraintListInfo(DbObjectContainer owner) {
    super("CONSTRAINTS", owner);
    onTable = getTable() != null;
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public OracleTableInfo getTable() {
    DbObjectIdentified o = getOwner(OracleTableInfo.class);
    if (o != null) {
      return (OracleTableInfo)o;
    }
    return null;
  }

  public String[] getColumnNames() {
    if (onTable) {
      return new String[] {"Nazwa kolumny", "Pozycja", "Status", "W³¹czony", "Schemat ref.", "Ograncizenie ref.", "Akcja usuwania", "Akcja sprawdzania", "Typ"};
    }
    else {
      return new String[] {"Nazwa tabeli", "Nazwa kolumny", "Pozycja", "Status", "W³¹czony", "Schemat ref.", "Ograncizenie ref.", "Akcja usuwania", "Akcja sprawdzania", "Typ"};
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
        query.setSqlText(Sql.getConstraintList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        if (onTable) {
          query.paramByName("table_name").setString(getTable().getName());
        }
        query.open();
        while (!query.eof()) {
          OracleConstraintInfo info = new OracleConstraintInfo(query.fieldByName("constraint_name").getString(), this);
          info.setCheckRule(query.fieldByName("search_condition").getString());
          info.setType(query.fieldByName("constraint_type").getString());
          info.setTableName(query.fieldByName("table_name").getString());
          info.setColumnName(query.fieldByName("column_name").getString());
          info.setPosition(query.fieldByName("position").getInteger());
          info.setStatus(query.fieldByName("status").getString());
          info.setValid(query.fieldByName("validated").getString());
          info.setRSchemaName(query.fieldByName("r_schema_name").getString());
          info.setRConstraint(query.fieldByName("r_constraint_name").getString());
          info.setDeleteRule(query.fieldByName("delete_rule").getString());
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
