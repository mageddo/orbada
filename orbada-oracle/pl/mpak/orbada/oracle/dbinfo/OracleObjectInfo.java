/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import java.sql.Timestamp;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleObjectInfo extends DbObjectIdentified {
  
  private String schemaName;
  private String objectName;
  private String objectType;
  private Timestamp created;
  private Timestamp lastDdlTime;
  private String timestamp;
  private String status;
  
  public OracleObjectInfo(String name, OracleObjectListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Nazwa obiektu", "Typ obiektu", "Utworzony", "Ostatnia zmiana", "Status"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(objectName),
      new Variant(objectType), 
      new Variant(created), 
      new Variant(lastDdlTime), 
      new Variant(status)
    };
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public Timestamp getLastDdlTime() {
    return lastDdlTime;
  }

  public void setLastDdlTime(Timestamp lastDdlTime) {
    this.lastDdlTime = lastDdlTime;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public void refresh() throws Exception {
  }

}
