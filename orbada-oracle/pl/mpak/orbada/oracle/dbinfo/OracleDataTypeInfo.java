/*
 * DerbyDbTableTypeInfo.java
 *
 * Created on 2007-11-21, 21:36:59
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
public class OracleDataTypeInfo extends DbObjectIdentified {
  
  private int sqlType;
  private int precision;
  private String createParams;
  
  public OracleDataTypeInfo(String name, OracleDataTypeListInfo owner) {
    super(name, owner);
  }

  public int getSqlType() {
    return sqlType;
  }

  public void setSqlType(int sqlType) {
    this.sqlType = sqlType;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }

  public String getCreateParams() {
    return createParams;
  }

  public void setCreateParams(String createParams) {
    this.createParams = createParams;
  }

  public String[] getMemberNames() {
    return new String[] {"Typ SQL", "Wielkoœæ", "Parametry"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getSqlType()),
      new Variant(getPrecision()),
      new Variant(getCreateParams())
    };
  }

  public void refresh() throws Exception {
  }
  
}
