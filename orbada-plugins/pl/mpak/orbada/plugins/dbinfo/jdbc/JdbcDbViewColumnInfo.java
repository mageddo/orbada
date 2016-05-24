package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbViewColumnInfo extends DbObjectIdentified {
  
  private int position;
  private String dataType;
  private Integer dataSize;
  private Integer digits;
  private boolean nullable;
  private String defaultValue;
  
  public JdbcDbViewColumnInfo(String name, JdbcDbViewInfo owner) {
    super(name, owner);
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Integer getDataSize() {
    return dataSize;
  }

  public void setDataSize(Integer dataSize) {
    this.dataSize = dataSize;
  }

  public Integer getDigits() {
    return digits;
  }

  public void setDigits(Integer digits) {
    this.digits = digits;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String[] getMemberNames() {
    return new String[] {"Pozycja", "Typ", "Rozmiar", "Prec.", "Null?", "Komentarz"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getPosition()), 
      new Variant(getDataType()), 
      new Variant(getDataSize()), 
      new Variant(getDigits()), 
      new Variant(isNullable()), 
      new Variant(getRemarks())
    };
  }

  public void refresh() throws Exception {
  }
  
}
