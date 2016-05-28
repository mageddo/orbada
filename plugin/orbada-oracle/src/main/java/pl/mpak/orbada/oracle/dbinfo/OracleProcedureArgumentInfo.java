package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleProcedureArgumentInfo extends DbObjectIdentified {
  
  private Long position;
  private String inOut;
  private String dataType;
  private String defaultValue;
  
  public OracleProcedureArgumentInfo(String name, OracleProcedureArgumentListInfo owner) {
    super(name, owner);
  }

  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public OracleProcedureInfo getProcedure() {
    DbObjectIdentified o = getOwner(OracleProcedureInfo.class);
    if (o != null) {
      return (OracleProcedureInfo)o;
    }
    return null;
  }
  
  public String[] getMemberNames() {
    return new String[] {"Lp", "In/Out", "Typ", "Wartoœæ domyœlna"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(position),
      new Variant(inOut),
      new Variant(dataType),
      new Variant(defaultValue)
    };
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getInOut() {
    return inOut;
  }

  public void setInOut(String inOut) {
    this.inOut = inOut;
  }

  public Long getPosition() {
    return position;
  }

  public void setPosition(Long position) {
    this.position = position;
  }

  public void refresh() throws Exception {
  }
  
}
