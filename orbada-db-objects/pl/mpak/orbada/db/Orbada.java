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
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Orbada extends DefaultBufferedRecord {
  
  public Orbada(Database database) {
    super(database, "ORBADA", "NAME");
    add("ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("USER_ID", VariantType.varString, true);
    add("NAME", VariantType.varString, true);
    add("VALUE", VariantType.varString, true);
  }
  
  public Orbada(Database database, String usrId, String name) throws UseDBException {
    this(database);
    loadRecordBy(new String[] {"USER_ID", "NAME"}, new Variant[] {new Variant(usrId), new Variant(name)});
  }
  
  public String getId() {
    try {
      return fieldByName("ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setId(String id) {
    fieldByName("ID").setString(id);
  }
  
  public String getUsrId() {
    try {
      return fieldByName("USER_ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUsrId(String usrId) {
    fieldByName("USER_ID").setString(usrId);
  }
  
  public String getName() {
    try {
      return fieldByName("NAME").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setName(String name) {
    fieldByName("NAME").setString(name);
  }
  
  public String getValue() {
    try {
      return fieldByName("VALUE").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setValue(String value) {
    fieldByName("VALUE").setString(value);
  }
  
}
