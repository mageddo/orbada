/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.gui.util.helpers;

/**
 *
 * @author akaluza
 */
public class TableColumnInfo {

  private int ordinalPos;
  private String databaseName;
  private String tableName;
  private String name;
  private String defaultValue;
  private boolean nullable;
  private String dataType;
  private String columnType;
  private String characterSetName;
  private String collationName;
  private String columnKey;
  private String extra;
  private String comment;

  public TableColumnInfo() {
  }

  public TableColumnInfo(int ordinalPos, String databaseName, String tableName, String name, String defaultValue, boolean nullable, String dataType, String columnType, String characterSetName, String collationName, String columnKey, String extra, String comment) {
    this.ordinalPos = ordinalPos;
    this.databaseName = databaseName;
    this.tableName = tableName;
    this.name = name;
    this.defaultValue = defaultValue;
    this.nullable = nullable;
    this.dataType = dataType;
    this.columnType = columnType;
    this.characterSetName = characterSetName;
    this.collationName = collationName;
    this.columnKey = columnKey;
    this.extra = extra;
    this.comment = comment;
  }

  public int getOrdinalPos() {
    return ordinalPos;
  }

  public void setOrdinalPos(int ordinalPos) {
    this.ordinalPos = ordinalPos;
  }

  public String getCharacterSetName() {
    return characterSetName;
  }

  public void setCharacterSetName(String characterSetName) {
    this.characterSetName = characterSetName;
  }

  public String getCollationName() {
    return collationName;
  }

  public void setCollationName(String collationName) {
    this.collationName = collationName;
  }

  public String getColumnKey() {
    return columnKey;
  }

  public void setColumnKey(String columnKey) {
    this.columnKey = columnKey;
  }

  public String getColumnType() {
    return columnType;
  }

  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getExtra() {
    return extra;
  }

  public void setExtra(String extra) {
    this.extra = extra;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  @Override
  public String toString() {
    return name;
  }

}
