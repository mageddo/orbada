package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleSequenceListInfo extends DbObjectContainer<OracleSequenceInfo> {
  
  public OracleSequenceListInfo(OracleSchemaInfo owner) {
    super("SEQUENCES", owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getColumnNames() {
    return new String[] {"Wartoœæ", "Wartoœæ min.", "Wartoœæ max.", "Inc", "Cykl.", "Porz¹d.", "Bufor"};
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
        query.setSqlText(Sql.getSequenceList(getFilter()));
        query.paramByName("schema_name").setString(getSchema().getName());
        query.open();
        while (!query.eof()) {
          OracleSequenceInfo info = new OracleSequenceInfo(query.fieldByName("sequence_name").getString(), this);
          info.setMinimumValue(query.fieldByName("min_value").getBigDecimal().toBigInteger());
          info.setMaximumValue(query.fieldByName("max_value").getBigDecimal().toBigInteger());
          info.setIncrement(query.fieldByName("increment_by").getBigDecimal().toBigInteger());
          info.setLastValue(query.fieldByName("last_number").getBigDecimal().toBigInteger());
          info.setBufferSize(query.fieldByName("cache_size").getBigDecimal().toBigInteger());
          info.setCycle(query.fieldByName("cycle_flag").getString());
          info.setOrdered(query.fieldByName("order_flag").getString());
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
