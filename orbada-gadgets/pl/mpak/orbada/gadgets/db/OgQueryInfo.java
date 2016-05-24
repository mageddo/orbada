/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gadgets.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;
import pl.mpak.util.id.UniqueID;

/**
 *
 * @author akaluza
 */
public class OgQueryInfo extends DefaultBufferedRecord {

  public OgQueryInfo(Database database) {
    super(database, "OG_QUERYINFOS", "GQI_ID");
    add("GQI_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("GQI_USR_ID", VariantType.varString, true);
    add("GQI_DTP_ID", VariantType.varString, true);
    add("GQI_NAME", VariantType.varString, true);
    add("GQI_SQL", VariantType.varString, true);
  }
  
  public OgQueryInfo(Database database, String gqi_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(gqi_id));
  }
  
  public String getId() {
    return fieldByName("GQI_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("GQI_ID").setString(id);
  }
  
  public String getUsrId() {
    if (fieldByName("GQI_USR_ID").isNull()) {
      return null;
    }
    return fieldByName("GQI_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("GQI_USR_ID").setString(id);
  }
  
  public String getDtpId() {
    if (fieldByName("GQI_DTP_ID").isNull()) {
      return null;
    }
    return fieldByName("GQI_DTP_ID").getValue().toString();
  }
  
  public void setDtpId(String id) {
    fieldByName("GQI_DTP_ID").setString(id);
  }
  
  public String getName() {
    return fieldByName("GQI_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("GQI_NAME").setString(name);
  }
  
  public String getSql() {
    return fieldByName("GQI_SQL").getValue().toString();
  }
  
  public void setSql(String description) {
    fieldByName("GQI_SQL").setString(description);
  }
  
}
