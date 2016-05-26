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
public class OrbadaSession extends DefaultBufferedRecord {
  
  public OrbadaSession(Database database) {
    super(database, "ORBADA_SESSIONS", "OSES_ID");
    add("OSES_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("OSES_START_TIME", VariantType.varTimestamp, true);
    add("OSES_END_TIME", VariantType.varTimestamp, true);
    add("OSES_VERSION", VariantType.varString, true);
    add("OSES_USR_ID", VariantType.varString, true);
    add("OSES_TERMINAL", VariantType.varString, true);
  }
  
  public OrbadaSession(Database database, String gdg_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(gdg_id));
  }
  
  public String getId() {
    try {
      return fieldByName("OSES_ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setId(String ppsId) {
    fieldByName("OSES_ID").setString(ppsId);
  }
  
  public Long getStartTime() {
    try {
      return fieldByName("OSES_START_TIME").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setStartTime(Long startTime) {
    fieldByName("OSES_START_TIME").setLong(startTime);
  }
  
  public void setStartTime(String startTime) {
    fieldByName("OSES_START_TIME").setString(startTime);
  }
  
  public Long getEndTime() {
    try {
      return fieldByName("OSES_END_TIME").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setEndTime(Long endTime) {
    fieldByName("OSES_END_TIME").setLong(endTime);
  }
  
  public void setEndTime(String endTime) {
    fieldByName("OSES_END_TIME").setString(endTime);
  }
  
  public String getUsrId() {
    try {
      return fieldByName("OSES_USR_ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUsrId(String id) {
    fieldByName("OSES_USR_ID").setString(id);
  }
  
  public String getVersion() {
    try {
      return fieldByName("OSES_VERSION").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setVersion(String version) {
    fieldByName("OSES_VERSION").setString(version);
  }
  
  public String getTerminal() {
    try {
      return fieldByName("OSES_TERMINAL").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setTerminal(String terminal) {
    fieldByName("OSES_TERMINAL").setString(terminal);
  }
  
}
