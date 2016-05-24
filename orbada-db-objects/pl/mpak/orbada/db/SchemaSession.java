/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class SchemaSession extends DefaultBufferedRecord {
  
  public SchemaSession(Database database) {
    super(database, "SCHEMA_SESSIONS", "SSES_ID");
    add("SSES_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("SSES_OSES_ID", VariantType.varString, true);
    add("SSES_START_TIME", VariantType.varTimestamp, true);
    add("SSES_END_TIME", VariantType.varTimestamp, true);
    add("SSES_SCH_ID", VariantType.varString, true);
    add("SSES_USER", VariantType.varString, true);
    add("SSES_URL", VariantType.varString, true);
  }
  
  public SchemaSession(Database database, String gdg_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(gdg_id));
  }
  
  public String getId() {
    try {
      return fieldByName("SSES_ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setId(String ppsId) {
    fieldByName("SSES_ID").setString(ppsId);
  }
  
  public Long getStartTime() {
    try {
      return fieldByName("SSES_START_TIME").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setStartTime(Long startTime) {
    fieldByName("SSES_START_TIME").setLong(startTime);
  }
  
  public void setStartTime(String startTime) {
    fieldByName("SSES_START_TIME").setString(startTime);
  }
  
  public Long getEndTime() {
    try {
      return fieldByName("SSES_END_TIME").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setEndTime(Long endTime) {
    fieldByName("SSES_END_TIME").setLong(endTime);
  }
  
  public void setEndTime(String endTime) {
    fieldByName("SSES_END_TIME").setString(endTime);
  }
  
  public String getOsesId() {
    try {
      return fieldByName("SSES_OSES_ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setOsesId(String id) {
    fieldByName("SSES_OSES_ID").setString(id);
  }
  
  public String getSchId() {
    try {
      return fieldByName("SSES_SCH_ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setSchId(String id) {
    fieldByName("SSES_SCH_ID").setString(id);
  }
  
  public String getUser() {
    try {
      return fieldByName("SSES_USER").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUser(String user) {
    fieldByName("SSES_USER").setString(user);
  }
  
  public String getUrl() {
    try {
      return fieldByName("SSES_URL").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUrl(String terminal) {
    fieldByName("SSES_URL").setString(terminal);
  }
  
}
