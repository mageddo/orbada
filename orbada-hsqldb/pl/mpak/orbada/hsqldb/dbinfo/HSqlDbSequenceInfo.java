/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
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
public class HSqlDbSequenceInfo extends DbObjectIdentified {
  
  private Long minimumValue;
  private Long maximumValue;
  private Long increment;
  private Long startWith;
  
  public HSqlDbSequenceInfo(String name, HSqlDbSequenceListInfo owner) {
    super(name, owner);
  }

  public Long getIncrement() {
    return increment;
  }

  public void setIncrement(Long increment) {
    this.increment = increment;
  }

  public Long getMaximumValue() {
    return maximumValue;
  }

  public void setMaximumValue(Long maximumValue) {
    this.maximumValue = maximumValue;
  }

  public Long getMinimumValue() {
    return minimumValue;
  }

  public void setMinimumValue(Long minimumValue) {
    this.minimumValue = minimumValue;
  }

  public Long getStartWith() {
    return startWith;
  }

  public void setStartWith(Long startWith) {
    this.startWith = startWith;
  }
  
  public String[] getMemberNames() {
    return new String[] {"Wartoœæ min.", "Wartoœæ max.", "Inc", "Start od"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(minimumValue), 
      new Variant(maximumValue), 
      new Variant(increment), 
      new Variant(startWith)};
  }

  public void refresh() throws Exception {
  }
  
}
