/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Generator extends DefaultBufferedRecord {

  public Generator(Database database) {
    super(database, "GENERATORS", "GEN_NAME");
    add("GEN_NAME", VariantType.varString, false);
    add("GEN_VALUE", VariantType.varString, true);
    add("GEN_MIN_VALUE", VariantType.varString, true);
    add("GEN_MAX_VALUE", VariantType.varString, true);
    add("GEN_INCREMENT", VariantType.varString, true);
    add("GEN_CYCLE", VariantType.varString, true);
    add("GEN_LOCKED", VariantType.varString, true);
  }
  
  public Generator(Database database, String gen_name) throws UseDBException {
    this(database);
    loadRecord(new Variant(gen_name));
  }
  
  public String getName() {
    try {
      return fieldByName("GEN_NAME").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setName(String name) {
    fieldByName("GEN_NAME").setString(name);
  }
  
  public String getValue() {
    try {
      return fieldByName("GEN_VALUE").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setValue(String value) {
    fieldByName("GEN_VALUE").setString(value);
  }
  
  public String getMinValue() {
    try {
      return fieldByName("GEN_MIN_VALUE").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setMinValue(String minValue) {
    fieldByName("GEN_MIN_VALUE").setString(minValue);
  }
  
  public String getMaxValue() {
    try {
      return fieldByName("GEN_MAX_VALUE").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setMaxValue(String maxValue) {
    fieldByName("GEN_MAX_VALUE").setString(maxValue);
  }
  
  public String getIncrement() {
    try {
      return fieldByName("GEN_INCREMENT").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setIncrement(String increment) {
    fieldByName("GEN_INCREMENT").setString(increment);
  }
  
  public String getCycle() {
    try {
      return fieldByName("GEN_CYCLE").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setCycle(String cycle) {
    fieldByName("GEN_CYCLE").setString(cycle);
  }
  
  public boolean isCycle() {
    return StringUtil.toBoolean(getCycle());
  }
  
  public void setCycle(boolean cycle) {
    setCycle(cycle ? "T" : "F");
  }
  
  public String getLocked() {
    try {
      return fieldByName("GEN_LOCKED").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setLocked(String locked) {
    fieldByName("GEN_LOCKED").setString(locked);
  }
  
  public boolean isLocked() {
    return StringUtil.toBoolean(getLocked());
  }
  
  public void setLocked(boolean locked) {
    setLocked(locked ? "T" : "");
  }
  
}
