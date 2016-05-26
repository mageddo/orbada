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
public class Driver extends DefaultBufferedRecord {
  
  public Driver(Database database) {
    super(database, "DRIVERS", "DRV_ID");
    add("DRV_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("DRV_USR_ID", VariantType.varString, true);
    add("DRV_NAME", VariantType.varString, true);
    add("DRV_LIBRARY_SOURCE", VariantType.varString, true);
    add("DRV_TYPE_NAME", VariantType.varString, true);
    add("DRV_CLASS_NAME", VariantType.varString, true);
    add("DRV_URL_TEMPLATE", VariantType.varString, true);
    add("DRV_EXTRA_LIBRARY", VariantType.varString, true);
  }
  
  public Driver(Database database, String drv_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(drv_id));
  }
  
  public String getId() {
    return fieldByName("DRV_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("DRV_ID").setString(id);
  }
  
  public String getUsrId() {
    if (fieldByName("DRV_USR_ID").isNull()) {
      return null;
    }
    return fieldByName("DRV_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("DRV_USR_ID").setString(id);
  }
  
  public DriverType getDriverType() throws UseDBException {
    DriverType dt = new DriverType(getDatabase());
    dt.loadRecordBy("DTP_NAME", fieldByName("DRV_TYPE_NAME").getValue());
    return dt;
  }

  public String replacePatts(String templateUrl) {
    String text = templateUrl;
    text = Utils.replace(text, "drivertype", fieldByName("DRV_TYPE_NAME").getValue().toString());
    text = Utils.replace(text, "drivername", fieldByName("DRV_NAME").getValue().toString());
    text = Utils.replace(text, "driver", fieldByName("DRV_NAME").getValue().toString());
    return text;
  }

}
