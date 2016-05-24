/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlscripts.db;

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
public class SqlScriptRecord extends DefaultBufferedRecord {

  public SqlScriptRecord(Database database) {
    super(database, "OSQLSCRIPTS", "OSS_ID");
    add("OSS_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("OSS_USR_ID", VariantType.varString, true);
    add("OSS_NAME", VariantType.varString, true);
    add("OSS_DTP_ID", VariantType.varString, true);
    add("OSS_SCRIPT", VariantType.varString, true);
  }
  
  public SqlScriptRecord(Database database, String osm_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(osm_id));
  }
  
  public String getId() {
    return fieldByName("OSS_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("OSS_ID").setString(id);
  }
  
  public String getUsrId() {
    return fieldByName("OSS_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("OSS_USR_ID").setString(id);
  }
  
  public String getName() {
    return fieldByName("OSS_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("OSS_NAME").setString(name);
  }
  
  public String getDtpId() {
    return fieldByName("OSS_DTP_ID").getValue().toString();
  }
  
  public void setDtpId(String id) {
    fieldByName("OSS_DTP_ID").setString(id);
  }
  
  public String getScript() {
    return fieldByName("OSS_SCRIPT").getValue().toString();
  }
  
  public void setScript(String script) {
    fieldByName("OSS_SCRIPT").setString(script);
  }
  
}
