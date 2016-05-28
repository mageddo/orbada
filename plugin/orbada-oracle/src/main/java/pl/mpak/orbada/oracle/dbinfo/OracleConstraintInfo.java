/*
 * DerbyDbTableColumnInfo.java
 *
 * Created on 2007-11-15, 19:41:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleConstraintInfo extends DbObjectIdentified {
  
  private boolean onTable;
  private String tableName;
  private String type;
  private String columnName;
  private int position;
  private String rSchemaName;
  private String rConstraint;
  private String status;
  private String deleteRule;
  private String checkRule;
  private String valid;
  
  public OracleConstraintInfo(String name, OracleConstraintListInfo owner) {
    super(name, owner);
    onTable = getTable() != null;
  }

  public OracleTableInfo getTable() {
    DbObjectIdentified o = getOwner(OracleTableInfo.class);
    if (o != null) {
      return (OracleTableInfo)o;
    }
    return null;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCheckRule() {
    return checkRule;
  }

  public void setCheckRule(String checkRule) {
    this.checkRule = checkRule;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getDeleteRule() {
    return deleteRule;
  }

  public void setDeleteRule(String deleteRule) {
    this.deleteRule = deleteRule;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getRConstraint() {
    return rConstraint;
  }

  public void setRConstraint(String rConstraint) {
    this.rConstraint = rConstraint;
  }

  public String getRSchemaName() {
    return rSchemaName;
  }

  public void setRSchemaName(String rSchemaName) {
    this.rSchemaName = rSchemaName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getValid() {
    return valid;
  }

  public void setValid(String valid) {
    this.valid = valid;
  }

  public String[] getMemberNames() {
    if (onTable) {
      return new String[] {"Nazwa kolumny", "Pozycja", "Status", "W³¹czony", "Schemat ref.", "Ograncizenie ref.", "Akcja usuwania", "Akcja sprawdzania", "Typ"};
    }
    else {
      return new String[] {"Nazwa tabeli", "Nazwa kolumny", "Pozycja", "Status", "W³¹czony", "Schemat ref.", "Ograncizenie ref.", "Akcja usuwania", "Akcja sprawdzania", "Typ"};
    }
  }

  public Variant[] getMemberValues() {
    if (onTable) {
      return new Variant[] {
        new Variant(getColumnName()),
        new Variant(getPosition()),
        new Variant(getValid()),
        new Variant(getStatus()),
        new Variant(getRSchemaName()),
        new Variant(getRConstraint()),
        new Variant(getDeleteRule()),
        new Variant(getCheckRule()),
        new Variant(getType())
      };
    }
    else {
      return new Variant[] {
        new Variant(getTableName()),
        new Variant(getColumnName()),
        new Variant(getPosition()),
        new Variant(getValid()),
        new Variant(getStatus()),
        new Variant(getRSchemaName()),
        new Variant(getRConstraint()),
        new Variant(getDeleteRule()),
        new Variant(getCheckRule()),
        new Variant(getType())
      };    
    }
  }

  public void refresh() throws Exception {
  }
  
}
