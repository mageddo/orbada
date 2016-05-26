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
public class OracleTypeAttrInfo extends DbObjectIdentified {
  
  private Long attrNo;
  private String attrTypeName;
  private Long length;
  private Long precision;
  private Long scale;
  
  public OracleTypeAttrInfo(String name, OracleTypeAttrListInfo owner) {
    super(name, owner);
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
  
  public String[] getMemberNames() {
    return new String[] {"Lp", "Typ", "D³ugoœæ", "Precyzja", "Skala"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(attrNo), 
      new Variant(attrTypeName),
      new Variant(length),
      new Variant(precision),
      new Variant(scale)};
  }

  public Long getAttrNo() {
    return attrNo;
  }

  public void setAttrNo(Long attrNo) {
    this.attrNo = attrNo;
  }

  public String getAttrTypeName() {
    return attrTypeName;
  }

  public void setAttrTypeName(String attrTypeName) {
    this.attrTypeName = attrTypeName;
  }

  public Long getLength() {
    return length;
  }

  public void setLength(Long length) {
    this.length = length;
  }

  public Long getPrecision() {
    return precision;
  }

  public void setPrecision(Long precision) {
    this.precision = precision;
  }

  public Long getScale() {
    return scale;
  }

  public void setScale(Long scale) {
    this.scale = scale;
  }

  public void refresh() throws Exception {
  }
  
}
