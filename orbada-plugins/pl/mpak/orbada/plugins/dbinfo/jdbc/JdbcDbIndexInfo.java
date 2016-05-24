package pl.mpak.orbada.plugins.dbinfo.jdbc;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class JdbcDbIndexInfo extends DbObjectIdentified {
  
  private int position;
  private String tableName;
  private String columnName;
  private String ascDesc;
  private boolean unique;
  
  public JdbcDbIndexInfo(String name, JdbcDbIndexListInfo owner) {
    super(name, owner);
  }
  
  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getAscDesc() {
    return ascDesc;
  }

  public void setAscDesc(String ascDesc) {
    this.ascDesc = ascDesc;
  }

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  public String[] getMemberNames() {
    return new String[] {"Tabela", "Pozycja", "Nazwa kolumny", "Unikalny", "AscDesc"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getTableName()),
      new Variant(getPosition()),
      new Variant(getColumnName()),
      new Variant(isUnique()),
      new Variant(getAscDesc())
    };
  }

  public void refresh() throws Exception {
  }
  
}
