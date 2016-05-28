/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import java.math.BigInteger;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleSequenceInfo extends DbObjectIdentified implements DbObjectInfo {
  
  private BigInteger minimumValue;
  private BigInteger maximumValue;
  private BigInteger increment;
  private BigInteger lastValue;
  private BigInteger bufferSize;
  private String cycle;
  private String ordered;
  
  public OracleSequenceInfo(String name, OracleSequenceListInfo owner) {
    super(name, owner);
  }

  public BigInteger getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(BigInteger bufferSize) {
    this.bufferSize = bufferSize;
  }

  public String getCycle() {
    return cycle;
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public String getOrdered() {
    return ordered;
  }

  public void setOrdered(String ordered) {
    this.ordered = ordered;
  }

  public BigInteger getIncrement() {
    return increment;
  }

  public void setIncrement(BigInteger increment) {
    this.increment = increment;
  }

  public BigInteger getMaximumValue() {
    return maximumValue;
  }

  public void setMaximumValue(BigInteger maximumValue) {
    this.maximumValue = maximumValue;
  }

  public BigInteger getMinimumValue() {
    return minimumValue;
  }

  public void setMinimumValue(BigInteger minimumValue) {
    this.minimumValue = minimumValue;
  }

  public BigInteger getLastValue() {
    return lastValue;
  }

  public void setLastValue(BigInteger lastValue) {
    this.lastValue = lastValue;
  }
  
  public String[] getMemberNames() {
    return new String[] {"Wartoœæ", "Wartoœæ min.", "Wartoœæ max.", "Inkr.", "Cykl.", "Porz¹d.", "Bufor"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(lastValue),
      new Variant(minimumValue), 
      new Variant(maximumValue), 
      new Variant(increment),
      new Variant(cycle),
      new Variant(ordered),
      new Variant(bufferSize)
    };
  }

  public void refresh() throws Exception {
  }

  public String getObjectType() {
    return "SEQUENCE";
  }
  
}
