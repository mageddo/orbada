package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleIndexInfo extends DbObjectIdentified {
  
  private boolean onTable;
  private String tableName;
  private String columnName;
  private String type;
  private boolean unique;
  private String tablespaceName;
  
  public OracleIndexInfo(String name, OracleIndexListInfo owner) {
    super(name, owner);
    DbObjectInfo info = getObjectOwner();
    onTable = info != null && StringUtil.equalAnyOfString(info.getObjectType(), new String[] {"TABLE", "MATERIALIZED VIEW"}, true);
  }
  
  public String[] getMemberNames() {
    if (onTable) {
      return new String[] {"Nazwa kolumny", "Typ", "Unikalny", "Przestrzeñ danych"};
    }
    else {
      return new String[] {"Tabela", "Typ", "Unikalny", "Przestrzeñ danych"};
    }
  }

  public Variant[] getMemberValues() {
    if (onTable) {
      return new Variant[] {
        new Variant(getColumnName()), 
        new Variant(getType()), 
        new Variant(isUnique()), 
        new Variant(getTablespaceName())};
    }
    else {
      return new Variant[] {
        new Variant(getTableName()), 
        new Variant(getType()), 
        new Variant(isUnique()), 
        new Variant(getTablespaceName())};
    }
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  public String getTablespaceName() {
    return tablespaceName;
  }

  public void setTablespaceName(String tablespaceName) {
    this.tablespaceName = tablespaceName;
  }

  public void refresh() throws Exception {
  }

}
