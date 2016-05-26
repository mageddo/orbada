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
public class View extends DefaultBufferedRecord {
  
  public View(Database database) {
    super(database, "VIEWS", "VWS_ID");
    add("VWS_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("VWS_PPS_ID", VariantType.varString, true);
    add("VWS_ORDER", VariantType.varInteger, true);
    add("VWS_NAME", VariantType.varString, true);
    add("VWS_HIDE_TITLE", VariantType.varString, true);
    add("VWS_HIDE_ICON", VariantType.varString, true);
  }
  
  public View(Database database, String vws_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(vws_id));
  }
  
  public String getPpsId() {
    try {
      return fieldByName("vws_pps_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setPpsId(String ppsId) {
    fieldByName("vws_pps_id").setString(ppsId);
  }
  
  public Integer getOrder() {
    if (fieldByName("vws_order").isNull()) {
      return null;
    }
    try {
      return fieldByName("vws_order").getInteger();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setOrder(Integer order) {
    if (order == null) {
      fieldByName("vws_order").clear();
    }
    else {
      fieldByName("vws_order").setInteger(order);
    }
  }
  
  public String getVwsId() {
    try {
      return fieldByName("vws_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setVwsId(String schId) {
    fieldByName("vws_id").setString(schId);
  }
  
  public String getName() {
    try {
      return fieldByName("vws_name").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return "???";
  }
  
  public void setName(String name) {
    fieldByName("vws_name").setString(name);
  }
  
  public boolean isHideTitle() {
    try {
      return StringUtil.toBoolean(fieldByName("vws_hide_title").getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return false;
  }
  
  public void setHideTitle(boolean defaultValue) {
    fieldByName("vws_hide_title").setString(defaultValue ? "T" : "F");
  }
  
  public boolean isHideIcon() {
    try {
      return StringUtil.toBoolean(fieldByName("vws_hide_icon").getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return false;
  }
  
  public void setHideIcon(boolean defaultValue) {
    fieldByName("vws_hide_icon").setString(defaultValue ? "T" : "F");
  }
  
}
