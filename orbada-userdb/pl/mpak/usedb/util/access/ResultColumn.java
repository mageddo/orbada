package pl.mpak.usedb.util.access;

import pl.mpak.usedb.util.SQLUtil;

public class ResultColumn  {

  private String name;
  private int type = java.sql.Types.NULL; // java.sql.Types
  private int length = 0; // string length
  private int precision = 0; // numeric precision
  private int scale = 0; // numeric scale
  
  public ResultColumn() {
  }

  public ResultColumn(String name) {
    this();
    this.name = name;
  }

  public ResultColumn(String name, int type) {
    this(name);
    this.type = type;
  }

  public ResultColumn(String name, int type, int length) {
    this(name, type);
    this.length = length;
  }

  public ResultColumn(String name, int type, int precision, int scale) {
    this(name, type);
    this.precision = precision;
    this.scale = scale;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }

  public int getScale() {
    return scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }
  
  public String getInfo() {
    return SQLUtil.typeToString(type) +
        (length > 0 ? "(" +length +")" : "") +
        (precision > 0 ? "(" +precision +(scale > 0 ? "," +scale : "") +")" : "");
  }

  @Override
  public String toString() {
    return name +" " +getInfo();
  }

}
