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
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author akaluza
 */
public class OgQueryInfoPerspective extends DefaultBufferedRecord {

  public OgQueryInfoPerspective(Database database) {
    super(database, "OG_QUERYINFO_PERSPECTIVES", "QIP_ID");
    add("QIP_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("QIP_GQI_ID", VariantType.varString, true);
    add("QIP_PPS_ID", VariantType.varString, true);
    add("QIP_INTERVAL_S", VariantType.varInteger, true);
    add("QIP_ORDER", VariantType.varInteger, true);
  }
  
  public OgQueryInfoPerspective(Database database, String qip_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(qip_id));
  }
  
  public String getId() {
    return fieldByName("QIP_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("QIP_ID").setString(id);
  }
  
  public String getGqiId() {
    return fieldByName("QIP_GQI_ID").getValue().toString();
  }
  
  public void setGqiId(String id) {
    fieldByName("QIP_GQI_ID").setString(id);
  }
  
  public String getPpsId() {
    return fieldByName("QIP_PPS_ID").getValue().toString();
  }
  
  public void setPpsId(String id) {
    fieldByName("QIP_PPS_ID").setString(id);
  }
  
  public Integer getIntervalSec() {
    try {
      return fieldByName("QIP_INTERVAL_S").getInteger();
    } catch (VariantException ex) {
      return null;
    }
  }
  
  public void setIntervalSec(Integer intervalSec) {
    fieldByName("QIP_INTERVAL_S").setInteger(intervalSec);
  }
  
  public Integer getOrder() {
    try {
      return fieldByName("QIP_ORDER").getInteger();
    } catch (VariantException ex) {
      return null;
    }
  }
  
  public void setOrder(Integer order) {
    fieldByName("QIP_ORDER").setInteger(order);
  }
  
}
