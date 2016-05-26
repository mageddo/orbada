/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.db;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author akaluza
 */
public class ReportsGroupRecord extends DefaultBufferedRecord {

  public ReportsGroupRecord(Database database, Object beanObject) throws IllegalArgumentException, VariantException, IllegalAccessException, InvocationTargetException, ParseException, IOException {
    super(database, beanObject);
  }
  
  public ReportsGroupRecord(Database database) {
    super(database, "OREP_GROUPS", "OREPG_ID");
    add("OREPG_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("OREPG_CREATED", VariantType.varTimestamp, true);
    add("OREPG_UPDATED", VariantType.varTimestamp, true);
    add("OREPG_OREPG_ID", VariantType.varString, true);
    add("OREPG_DTP_ID", VariantType.varString, true);
    add("OREPG_USR_ID", VariantType.varString, true);
    add("OREPG_SCH_ID", VariantType.varString, true);
    add("OREPG_SHARED", VariantType.varString, true);
    add("OREPG_NAME", VariantType.varString, true);
    add("OREPG_DESCRIPTION", VariantType.varString, true);
    add("OREPG_TOOLTIP", VariantType.varString, true);
  }
  
  public ReportsGroupRecord(Database database, String orepg_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(orepg_id));
  }
  
  public String getId() {
    return fieldByName("OREPG_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("OREPG_ID").setString(id);
  }
  
  public Long getCreated() {
    try {
      return fieldByName("OREPG_CREATED").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setCreated(Long created) {
    fieldByName("OREPG_CREATED").setLong(created);
  }
  
  public void setCreated(String created) {
    fieldByName("OREPG_CREATED").setString(created);
  }
  
  public Long getUpdated() {
    try {
      return fieldByName("OREPG_UPDATED").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUpdated(Long created) {
    fieldByName("OREPG_UPDATED").setLong(created);
  }
  
  public void setUpdated(String created) {
    fieldByName("OREPG_UPDATED").setString(created);
  }
  
  public String getUsrId() {
    if (fieldByName("OREPG_USR_ID").isNull()) {
      return null;
    }
    return fieldByName("OREPG_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("OREPG_USR_ID").setString(id);
  }
  
  public String getDtpId() {
    return fieldByName("OREPG_DTP_ID").getValue().toString();
  }
  
  public void setDtpId(String id) {
    fieldByName("OREPG_DTP_ID").setString(id);
  }
  
  public String getSchId() {
    if (fieldByName("OREPG_SCH_ID").isNull()) {
      return null;
    }
    return fieldByName("OREPG_SCH_ID").getValue().toString();
  }
  
  public void setSchId(String id) {
    fieldByName("OREPG_SCH_ID").setString(id);
  }
  
  public String getOrepgId() {
    if (fieldByName("OREPG_OREPG_ID").isNull()) {
      return null;
    }
    return fieldByName("OREPG_OREPG_ID").getValue().toString();
  }
  
  public void setOrepgId(String id) {
    fieldByName("OREPG_OREPG_ID").setString(id);
  }
  
  public boolean isShared() {
    return StringUtil.toBoolean(getShared());
  }
  
  public String getShared() {
    return fieldByName("OREPG_SHARED").getValue().toString();
  }
  
  public void setShared(String shared) {
    fieldByName("OREPG_SHARED").setString(shared);
  }
  
  public void setShared(boolean shared) {
    setShared(shared ? "T" : "F");
  }
  
  public String getName() {
    return fieldByName("OREPG_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("OREPG_NAME").setString(name);
  }
  
  public String getDescription() {
    return fieldByName("OREPG_DESCRIPTION").getValue().toString();
  }
  
  public void setDescription(String description) {
    fieldByName("OREPG_DESCRIPTION").setString(description);
  }
  
  public String getTooltip() {
    return fieldByName("OREPG_TOOLTIP").getValue().toString();
  }
  
  public void setTooltip(String tooltip) {
    fieldByName("OREPG_TOOLTIP").setString(tooltip);
  }
  
  @Override
  public void applyInsert() throws Exception {
    if (!fieldByName("OREPG_CREATED").isChanged()) {
      fieldByName("OREPG_CREATED").setValue(new Variant(System.currentTimeMillis()));
    }
    if (!fieldByName("OREPG_UPDATED").isChanged()) {
      fieldByName("OREPG_UPDATED").setLong(System.currentTimeMillis());
    }
    super.applyInsert();
  }
  
  @Override
  public void applyUpdate() throws Exception {
    if (!fieldByName("OREPG_UPDATED").isChanged()) {
      fieldByName("OREPG_UPDATED").setLong(System.currentTimeMillis());
    }
    super.applyUpdate();
  }
  
}
