/*
 * DerbyDbViewInfo.java
 *
 * Created on 2007-11-15, 19:31:48
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
public class OracleRecyclebinInfo extends DbObjectIdentified {
  
  private boolean onSchema;
  private String schemaName;
  private String originalName;
  private String type;
  private String operation;
  private String createTime;
  private String dropTime;
  
  public OracleRecyclebinInfo(String name, OracleRecyclebinListInfo owner) {
    super(name, owner);
    onSchema = getSchema() != null;
  }
  
  public OracleSchemaInfo getSchema() {
    DbObjectIdentified o = getOwner(OracleSchemaInfo.class);
    if (o != null) {
      return (OracleSchemaInfo)o;
    }
    return null;
  }
  
  public String[] getMemberNames() {
    if ("TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false")) && !onSchema) {
      return new String[] {"Nazwa schematu", "Nazwa oryginalna", "Typ obiektu", "Operacja", "Utworzony", "Usuniêty"};
    }
    else {
      return new String[] {"Nazwa oryginalna", "Typ obiektu", "Operacja", "Utworzony", "Usuniêty"};
    }
  }

  public Variant[] getMemberValues() {
    if ("TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false")) && !onSchema) {
      return new Variant[] {
        new Variant(schemaName),
        new Variant(originalName),
        new Variant(type),
        new Variant(operation),
        new Variant(createTime),
        new Variant(dropTime)
      };
    }
    else {
      return new Variant[] {
        new Variant(originalName),
        new Variant(type),
        new Variant(operation),
        new Variant(createTime),
        new Variant(dropTime)
      };
    }
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getDropTime() {
    return dropTime;
  }

  public void setDropTime(String dropTime) {
    this.dropTime = dropTime;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  @Override
  public void refresh() throws Exception {
  }

}
