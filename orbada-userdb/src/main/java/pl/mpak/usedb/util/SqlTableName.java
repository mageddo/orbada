package pl.mpak.usedb.util;

public class SqlTableName {

  private String catalogName;
  private String schemaName;
  private String tableName;
  private String aliasName;
  
  public SqlTableName(String catalogName, String schemaNam, String tableName, String aliasName) {
    this.catalogName = catalogName;
    this.schemaName = schemaNam;
    this.tableName = tableName;
    this.aliasName = aliasName;
  }

  public SqlTableName(String catalogName, String schemaNam, String tableName) {
    this(catalogName, schemaNam, tableName, null);
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
  
  public String toString() {
    return SQLUtil.createSqlName(catalogName, schemaName, tableName);
  }

}
