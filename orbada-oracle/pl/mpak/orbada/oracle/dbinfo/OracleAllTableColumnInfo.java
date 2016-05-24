package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleAllTableColumnInfo extends DbObjectIdentified {
  
  private int position;
  private String type;
  private boolean nullable;
  
  public OracleAllTableColumnInfo(String name, OracleAllTableColumnListInfo owner) {
    super(name, owner);
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
    setNullable(type.indexOf("NOT NULL") > 0);
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Typ", "Null?"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getPosition()), 
      new Variant(getType()),
      new Variant(isNullable())};
  }

  public void refresh() throws Exception {
  }
  
}
