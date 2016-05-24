/*
 * DerbyDbTriggerInfo.java
 *
 * Created on 2007-11-15, 20:26:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class HSqlDbTableTriggerInfo extends DbObjectIdentified {
  
  private String state;
  private String event;
  private String type;
  
  public HSqlDbTableTriggerInfo(String name, HSqlDbTableTriggerListInfo owner) {
    super(name, owner);
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
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
    return new String[] {"Stan", "Zda¿enie", "Typ"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getState()),
      new Variant(getEvent()),
      new Variant(getType())
    };
  }

  public void refresh() throws Exception {
  }
  
}
