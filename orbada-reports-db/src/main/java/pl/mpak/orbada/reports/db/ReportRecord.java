/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author akaluza
 */
public class ReportRecord extends DefaultBufferedRecord {

  public ReportRecord(Database database) {
    super(database, "OREP_REPORTS", "OREP_ID");
    add("OREP_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("OREP_CREATED", VariantType.varTimestamp, true);
    add("OREP_UPDATED", VariantType.varTimestamp, true);
    add("OREP_OREPG_ID", VariantType.varString, true);
    add("OREP_OREP_ID", VariantType.varString, true);
    add("OREP_NAME", VariantType.varString, true);
    add("OREP_DESCRIPTION", VariantType.varString, true);
    add("OREP_TOOLTIP", VariantType.varString, true);
    add("OREP_TYPE", new Variant("T"), VariantType.varString, true);
    add("OREP_ARRANGE", new Variant("H"), VariantType.varString, true);
    add("OREP_SQL", VariantType.varString, true);
  }
  
  public ReportRecord(Database database, String orep_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(orep_id));
  }
  
  public String getId() {
    return fieldByName("OREP_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("OREP_ID").setString(id);
  }
  
  public Long getCreated() {
    try {
      return fieldByName("OREP_CREATED").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setCreated(Long created) {
    fieldByName("OREP_CREATED").setLong(created);
  }
  
  public void setCreated(String created) {
    fieldByName("OREP_CREATED").setString(created);
  }
  
  public Long getUpdated() {
    try {
      return fieldByName("OREP_UPDATED").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUpdated(Long created) {
    fieldByName("OREP_UPDATED").setLong(created);
  }
  
  public void setUpdated(String created) {
    fieldByName("OREP_UPDATED").setString(created);
  }
  
  public String getOrepgId() {
    if (fieldByName("OREP_OREPG_ID").isNull()) {
      return null;
    }
    return fieldByName("OREP_OREPG_ID").getValue().toString();
  }
  
  public void setOrepgId(String id) {
    fieldByName("OREP_OREPG_ID").setString(id);
  }
  
  public String getOrepId() {
    if (fieldByName("OREP_OREP_ID").isNull()) {
      return null;
    }
    return fieldByName("OREP_OREP_ID").getValue().toString();
  }
  
  public void setOrepId(String id) {
    fieldByName("OREP_OREP_ID").setString(id);
  }
  
  public String getName() {
    return fieldByName("OREP_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("OREP_NAME").setString(name);
  }
  
  public String getDescription() {
    return fieldByName("OREP_DESCRIPTION").getValue().toString();
  }
  
  public void setDescription(String description) {
    fieldByName("OREP_DESCRIPTION").setString(description);
  }
  
  public String getTooltip() {
    return fieldByName("OREP_TOOLTIP").getValue().toString();
  }
  
  public void setTooltip(String tooltip) {
    fieldByName("OREP_TOOLTIP").setString(tooltip);
  }
  
  public String getType() {
    return fieldByName("OREP_TYPE").getValue().toString();
  }
  
  public void setType(String type) {
    fieldByName("OREP_TYPE").setString(type);
  }
  
  public String getArrange() {
    return fieldByName("OREP_ARRANGE").getValue().toString();
  }
  
  public void setArrange(String arrange) {
    fieldByName("OREP_ARRANGE").setString(arrange);
  }
  
  public String getSql() {
    return fieldByName("OREP_SQL").getValue().toString();
  }
  
  public void setSql(String sql) {
    fieldByName("OREP_SQL").setString(sql);
  }
  
  @Override
  public void applyInsert() throws Exception {
    if (!fieldByName("OREP_CREATED").isChanged()) {
      fieldByName("OREP_CREATED").setValue(new Variant(System.currentTimeMillis()));
    }
    if (!fieldByName("OREP_UPDATED").isChanged()) {
      fieldByName("OREP_UPDATED").setLong(System.currentTimeMillis());
    }
    super.applyInsert();
  }
  
  @Override
  public void applyUpdate() throws Exception {
    if (!fieldByName("OREP_UPDATED").isChanged()) {
      fieldByName("OREP_UPDATED").setLong(System.currentTimeMillis());
    }
    super.applyUpdate();
  }
  
}
