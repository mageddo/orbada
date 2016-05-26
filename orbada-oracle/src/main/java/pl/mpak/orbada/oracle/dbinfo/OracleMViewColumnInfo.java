package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleMViewColumnInfo extends DbObjectIdentified {
  
  private int position;
  private String type;
  private boolean nullable;
  private boolean updatable;
  
  public OracleMViewColumnInfo(String name, OracleMViewColumnListInfo owner) {
    super(name, owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public OracleMViewInfo getMView() {
    DbObjectIdentified o = getOwner(OracleMViewInfo.class);
    if (o != null) {
      return (OracleMViewInfo)o;
    }
    return null;
  }
  
  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public boolean isUpdatable() {
    return updatable;
  }

  public void setUpdatable(boolean updatable) {
    this.updatable = updatable;
  }

  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Typ", "Null?", "Zmiana"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(position), 
      new Variant(type),
      new Variant(nullable),
      new Variant(updatable)};
  }

  public void refresh() throws Exception {
  }
  
}
