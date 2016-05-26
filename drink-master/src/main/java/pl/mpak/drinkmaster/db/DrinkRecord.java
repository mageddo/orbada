/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.drinkmaster.db;

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
public class DrinkRecord extends DefaultBufferedRecord {

  public DrinkRecord(Database database) {
    super(database, "DRINKS", "DNK_ID");
    add("DNK_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("DNK_NAME", VariantType.varString, true);
    add("DNK_MAKE_UP", VariantType.varString, true);
    add("DNK_DESCRIPTION", VariantType.varString, true);
  }
  
  public DrinkRecord(Database database, String id) throws UseDBException {
    this(database);
    loadRecord(new Variant(id));
  }
  
  public String getId() {
    return fieldByName("DNK_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("DNK_ID").setString(id);
  }
  
  public String getName() {
    return fieldByName("DNK_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("DNK_NAME").setString(name);
  }
  
  public String getMakeUp() {
    return fieldByName("DNK_MAKE_UP").getValue().toString();
  }
  
  public void setMakeUp(String makeUp) {
    fieldByName("DNK_MAKE_UP").setString(makeUp);
  }
  
  public String getDescription() {
    return fieldByName("DNK_DESCRIPTION").getValue().toString();
  }
  
  public void setDescription(String description) {
    fieldByName("DNK_DESCRIPTION").setString(description);
  }
  
}
