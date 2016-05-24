/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.db;

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
public class ReportUserRecord extends DefaultBufferedRecord {

  public ReportUserRecord(Database database) {
    super(database, "OREP_USERS", "OREPU_ID");
    add("OREPU_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("OREPU_OREP_ID", VariantType.varString, true);
    add("OREPU_USR_ID", VariantType.varString, true);
  }
  
  public ReportUserRecord(Database database, String orepu_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(orepu_id));
  }
  
  public String getId() {
    return fieldByName("OREPU_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("OREPU_ID").setString(id);
  }
  
  public String getUsrId() {
    return fieldByName("OREPU_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("OREPU_USR_ID").setString(id);
  }
  
  public String getOrepId() {
    return fieldByName("OREPU_OREP_ID").getValue().toString();
  }
  
  public void setOrepId(String id) {
    fieldByName("OREPU_OREP_ID").setString(id);
  }
  
}
