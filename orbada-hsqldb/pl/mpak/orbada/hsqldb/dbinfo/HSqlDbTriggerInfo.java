/*
 * DerbyDbTriggerInfo.java
 *
 * Created on 2007-11-15, 20:26:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import java.sql.Timestamp;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbTriggerInfo extends DbObjectIdentified {
  
  private String tableName;
  private String state;
  private String firingTime;
  private String event;
  
  public HSqlDbTriggerInfo(String name, HSqlDbTriggerListInfo owner) {
    super(name, owner);
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getFiringTime() {
    return firingTime;
  }

  public void setFiringTime(String firingTime) {
    this.firingTime = firingTime;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String[] getMemberNames() {
    return new String[] {"Tabela", "Stan", "Zda¿enie", "Kiedy"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getTableName()), 
      new Variant(getState()),
      new Variant(getEvent()),
      new Variant(getFiringTime())
    };
  }

  public void refresh() throws Exception {
  }
  
}
