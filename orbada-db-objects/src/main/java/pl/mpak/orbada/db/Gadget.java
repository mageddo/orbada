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
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Gadget extends DefaultBufferedRecord {
  
  public Gadget(Database database) {
    super(database, "GADGETS", "GDG_ID");
    add("GDG_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("GDG_PPS_ID", VariantType.varString, true);
    add("GDG_ORDER", VariantType.varInteger, true);
    add("GDG_NAME", VariantType.varString, true);
    add("GDG_MINIMIZED", VariantType.varString, true);
    add("GDG_HEIGHT", VariantType.varInteger, true);
  }
  
  public Gadget(Database database, String gdg_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(gdg_id));
  }
  
  public String getPpsId() {
    try {
      return fieldByName("gdg_pps_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setPpsId(String ppsId) {
    fieldByName("gdg_pps_id").setString(ppsId);
  }
  
  public Integer getOrder() {
    if (fieldByName("gdg_order").isNull()) {
      return null;
    }
    try {
      return fieldByName("gdg_order").getInteger();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setOrder(Integer order) {
    if (order == null) {
      fieldByName("gdg_order").clear();
    }
    else {
      fieldByName("gdg_order").setInteger(order);
    }
  }
  
  public String getGdgId() {
    try {
      return fieldByName("gdg_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setGdgId(String gdgId) {
    fieldByName("gdg_id").setString(gdgId);
  }
  
  public String getName() {
    try {
      return fieldByName("gdg_name").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return "???";
  }
  
  public void setName(String name) {
    fieldByName("gdg_name").setString(name);
  }
  
  public boolean isMinimized() {
    try {
      return StringUtil.toBoolean(fieldByName("gdg_minimized").getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return false;
  }
  
  public void setMinimized(boolean value) {
    fieldByName("gdg_minimized").setString(value ? "T" : "F");
  }
  
  public Integer getHeight() {
    if (fieldByName("gdg_height").isNull()) {
      return null;
    }
    try {
      return fieldByName("gdg_height").getInteger();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setHeight(Integer height) {
    if (height == null) {
      fieldByName("gdg_height").clear();
    }
    else {
      fieldByName("gdg_height").setInteger(height);
    }
  }
  
}
