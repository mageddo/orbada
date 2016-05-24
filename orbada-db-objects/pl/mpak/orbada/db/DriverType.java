/*
 * Driver.java
 *
 * Created on 2007-10-11, 22:50:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class DriverType extends DefaultBufferedRecord {
  
  public DriverType(Database database) {
    super(database, "DRIVER_TYPES", "DTP_ID");
    add("DTP_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("DTP_NAME", VariantType.varString, true);
  }
  
  public DriverType(Database database, String dtp_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(dtp_id));
  }
  
  public String getId() {
    return fieldByName("DTP_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("DTP_ID").setString(id);
  }
  
  public String getName() {
    return fieldByName("DTP_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("DTP_NAME").setString(name);
  }
  
  public String replacePatts(String templateUrl) {
    String text = templateUrl;
    text = Utils.replace(text, "drivertype", fieldByName("DTP_NAME").getValue().toString());
    return text;
  }

}
