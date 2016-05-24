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
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Perspective extends DefaultBufferedRecord {
  
  public Perspective(Database database) {
    super(database, "PERSPECTIVES", "PPS_ID");
    add("PPS_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("PPS_USR_ID", VariantType.varString, true);
    add("PPS_SCH_ID", VariantType.varString, true);
    add("PPS_NAME", VariantType.varString, true);
    add("PPS_DESCRIPTION", VariantType.varString, true);
    add("PPS_DEFAULT", VariantType.varString, true);
    add("PPS_VIEW_COUNT", new Variant(0), VariantType.varInteger, true);
    add("PPS_GADGET_COUNT", new Variant(0), VariantType.varInteger, true);
    add("PPS_GADGETS_WIDTH", new Variant(150), VariantType.varInteger, true);
  }
  
  public Perspective(Database database, String ppr_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(ppr_id));
  }
  
  public String getPpsId() {
    try {
      return fieldByName("pps_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setPpsId(String usrId) {
    fieldByName("pps_id").setString(usrId);
  }
  
  public String getUsrId() {
    try {
      return fieldByName("pps_usr_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUsrId(String usrId) {
    fieldByName("pps_usr_id").setString(usrId);
  }
  
  public String getSchId() {
    try {
      return fieldByName("pps_sch_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setSchId(String schId) {
    fieldByName("pps_sch_id").setString(schId);
  }
  
  public String getName() {
    try {
      return fieldByName("pps_name").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return "???";
  }
  
  public void setName(String name) {
    fieldByName("pps_name").setString(name);
  }
  
  public String getDisplayName(Database database) {
    String name = getName();
    if (database != null) {
      name = StringUtil.replaceString(name, "$(public-name)", database.getPublicName());
      name = StringUtil.replaceString(name, "$(user-name)", database.getUserName());
      name = StringUtil.replaceString(name, "$(server-name)", database.getServerName());
      name = StringUtil.replaceString(name, "$(driver-name)", database.getDriverType());
    }
    return name;
  }
  
  public String getDescription() {
    try {
      return fieldByName("pps_description").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setDescription(String description) {
    fieldByName("pps_description").setString(description);
  }
  
  public boolean isDefault() {
    try {
      return StringUtil.toBoolean(fieldByName("pps_default").getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return false;
  }
  
  public void setDefault(boolean defaultValue) {
    fieldByName("pps_default").setString(defaultValue ? "T" : "F");
  }
  
  public int getViewCount() {
    try {
      return fieldByName("pps_view_count").getInteger();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return 0;
  }
  
  public void setViewCount(int viewCount) {
    fieldByName("pps_view_count").setInteger(viewCount);
  }
  
  public int getGadgetCount() {
    try {
      return fieldByName("pps_gadget_count").getInteger();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return 0;
  }
  
  public void setGadgetCount(int gadgetCount) {
    fieldByName("pps_gadget_count").setInteger(gadgetCount);
  }
  
  public int getGadgetsWidth() {
    try {
      return fieldByName("pps_gadgets_width").getInteger();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return 0;
  }
  
  public void setGadgetsWidth(int gadgetsWidth) {
    fieldByName("pps_gadgets_width").setInteger(gadgetsWidth);
  }
  
}
