/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.beanshell.db;

import java.util.logging.Level;
import java.util.logging.Logger;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;
import pl.mpak.util.id.UniqueID;

/**
 *
 * @author akaluza
 */
public class BshActionRecord extends DefaultBufferedRecord {

  public BshActionRecord(Database database) {
    super(database, "BSHACTIONS", "BSHA_ID");
    add("BSHA_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("BSHA_USR_ID", VariantType.varString, true);
    add("BSHA_DTP_ID", VariantType.varString, true);
    add("BSHA_KEY", VariantType.varString, true);
    add("BSHA_TITLE", VariantType.varString, true);
    add("BSHA_TOOLTIP", VariantType.varString, true);
    add("BSHA_SHORTCUT_CODE", VariantType.varInteger, true);
    add("BSHA_SHORTCUT_MODIFIERS", VariantType.varInteger, true);
    add("BSHA_SCRIPT", VariantType.varString, true);
  }
  
  public BshActionRecord(Database database, String gqi_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(gqi_id));
  }
  
  public String getId() {
    return fieldByName("BSHA_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("BSHA_ID").setString(id);
  }
  
  public String getUsrId() {
    if (fieldByName("BSHA_USR_ID").isNull()) {
      return null;
    }
    return fieldByName("BSHA_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("BSHA_USR_ID").setString(id);
  }
  
  public String getDtpId() {
    if (fieldByName("BSHA_DTP_ID").isNull()) {
      return null;
    }
    return fieldByName("BSHA_DTP_ID").getValue().toString();
  }
  
  public void setDtpId(String id) {
    fieldByName("BSHA_DTP_ID").setString(id);
  }
  
  public String getKey() {
    return fieldByName("BSHA_KEY").getValue().toString();
  }
  
  public void setKey(String key) {
    fieldByName("BSHA_KEY").setString(key);
  }
  
  public String getTitle() {
    return fieldByName("BSHA_TITLE").getValue().toString();
  }
  
  public void setTitle(String title) {
    fieldByName("BSHA_TITLE").setString(title);
  }
  
  public String getTooltip() {
    if (fieldByName("BSHA_TOOLTIP").isNull()) {
      return null;
    }
    return fieldByName("BSHA_TOOLTIP").getValue().toString();
  }
  
  public void setTooltip(String tooltip) {
    fieldByName("BSHA_TOOLTIP").setString(tooltip);
  }
  
  public Integer getShortcutCode() {
    if (fieldByName("BSHA_SHORTCUT_CODE").isNull()) {
      return null;
    }
    try {
      return fieldByName("BSHA_SHORTCUT_CODE").getInteger();
    } catch (VariantException ex) {
      return null;
    }
  }
  
  public void setShortcutCode(Integer shortcut) {
    if (shortcut != null) {
      fieldByName("BSHA_SHORTCUT_CODE").setInteger(shortcut);
    }
    else {
      fieldByName("BSHA_SHORTCUT_CODE").setValue(null);
    }
  }
  
  public Integer getShortcutModifiers() {
    if (fieldByName("BSHA_SHORTCUT_MODIFIERS").isNull()) {
      return null;
    }
    try {
      return fieldByName("BSHA_SHORTCUT_MODIFIERS").getInteger();
    } catch (VariantException ex) {
      return null;
    }
  }
  
  public void setShortcutModifiers(Integer modifiers) {
    if (modifiers != null) {
      fieldByName("BSHA_SHORTCUT_MODIFIERS").setInteger(modifiers);
    }
    else {
      fieldByName("BSHA_SHORTCUT_MODIFIERS").setValue(null);
    }
  }
  
  public String getScript() {
    if (fieldByName("BSHA_SCRIPT").isNull()) {
      return null;
    }
    return fieldByName("BSHA_SCRIPT").getValue().toString();
  }
  
  public void setScript(String script) {
    fieldByName("BSHA_SCRIPT").setString(script);
  }
  
}
