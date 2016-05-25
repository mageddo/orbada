package pl.mpak.usedb.util;

public class SqlColumnName {

  private String catalogName;
  private String schemaName;
  private String tableName;
  private String columnName;
  private String aliasName;
  
  public SqlColumnName(String catalogName, String schemaNam, String tableName, String columnName, String aliasName) {
    this.catalogName = catalogName;
    this.schemaName = schemaNam;
    this.tableName = tableName;
    this.columnName = columnName;
    this.aliasName = aliasName;
  }

  public SqlColumnName(String catalogName, String schemaNam, String tableName, String columnName) {
    this(catalogName, schemaNam, tableName, columnName, null);
  }

  public String getAliasName() {
    return aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }

  public String getCatalogName() {
    return catalogName;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public String getTableName() {
    return tableName;
  }
  
  public String getColumnName() {
    return columnName;
  }

  public String toString() {
    return SQLUtil.createSqlName(catalogName, schemaName, tableName, columnName);
  }

}
