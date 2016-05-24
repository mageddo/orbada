/*
 * DerbyDbTableColumnInfo.java
 *
 * Created on 2007-11-15, 19:41:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleContentInfo extends DbObjectIdentified {
  
  private Variant[] values;
  private String[] columns;
  
  public OracleContentInfo(String name, String[] columns, Variant[] values, OracleContentListInfo owner) {
    super(name, owner);
    this.columns = columns;
    this.values = values;
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }

  public String[] getMemberNames() {
    return columns;
  }

  public Variant[] getMemberValues() {
    return values;
  }

  public void refresh() throws Exception {
  }
  
}
