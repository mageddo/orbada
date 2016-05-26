/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.drinkmaster.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;
import pl.mpak.util.id.UniqueID;

/**
 *
 * @author akaluza
 */
public class DrinkCompoRecord extends DefaultBufferedRecord {

  public DrinkCompoRecord(Database database) {
    super(database, "DRINK_COMPOS", "DNC_ID");
    add("DNC_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("DNC_DNK_ID", VariantType.varString, true);
    add("DNC_NO", VariantType.varLong, true);
    add("DNC_COMPONENT", VariantType.varString, true);
  }
  
  public DrinkCompoRecord(Database database, String id) throws UseDBException {
    this(database);
    loadRecord(new Variant(id));
  }
  
  public String getId() {
    return fieldByName("DNC_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("DNC_ID").setString(id);
  }
  
  public String getDnkId() {
    return fieldByName("DNC_DNK_ID").getValue().toString();
  }
  
  public void setDnkId(String id) {
    fieldByName("DNC_DNK_ID").setString(id);
  }
  
  public String getComponent() {
    return fieldByName("DNC_COMPONENT").getValue().toString();
  }
  
  public void setComponent(String component) {
    fieldByName("DNC_COMPONENT").setString(component);
  }
  
  public Long getNo() {
    try {
      return fieldByName("DNC_NO").getLong();
    } catch (VariantException ex) {
      return null;
    }
  }
  
  public void setNo(Long no) {
    fieldByName("DNC_NO").setLong(no);
  }
  
}
