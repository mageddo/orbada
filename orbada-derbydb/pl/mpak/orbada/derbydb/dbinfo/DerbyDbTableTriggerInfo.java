/*
 * DerbyDbTriggerInfo.java
 *
 * Created on 2007-11-15, 20:26:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import java.sql.Timestamp;
import java.util.Date;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DerbyDbTableTriggerInfo extends DbObjectIdentified {
  
  private Timestamp created;
  private String state;
  private String firingTime;
  private String event;
  private String type;
  
  public DerbyDbTableTriggerInfo(String name, DerbyDbTableTriggerListInfo owner) {
    super(name, owner);
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String[] getMemberNames() {
    return new String[] {"Utworzony", "Stan", "Zda¿enie", "Kiedy", "Typ"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getCreated()),
      new Variant(getState()),
      new Variant(getEvent()),
      new Variant(getFiringTime()),
      new Variant(getType())
    };
  }

  public void refresh() throws Exception {
  }
  
}
