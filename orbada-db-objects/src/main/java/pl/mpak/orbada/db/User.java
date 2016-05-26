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
public class User extends DefaultBufferedRecord {
  
  public User(Database database) {
    super(database, "USERS", "USR_ID");
    add("USR_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("USR_CREATED", VariantType.varTimestamp, true);
    add("USR_UPDATED", VariantType.varTimestamp, true);
    add("USR_NAME", VariantType.varString, true);
    add("USR_PASSWORD", VariantType.varString, true);
    add("USR_DESCRIPTION", VariantType.varString, true);
    add("USR_ADMIN", VariantType.varString, true);
    add("USR_ORBADA", VariantType.varString, true);
    add("USR_ACTIVE", new Variant("T"), VariantType.varString, true);
    add("USR_REGISTERED", VariantType.varString, true);
  }
  
  public User(Database database, String usr_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(usr_id));
  }
  
  public String getUserName() {
    return fieldByName("usr_name").getValue().toString();
  }
  
  public void setUserName(String userName) {
    fieldByName("usr_name").setValue(new Variant(userName.toLowerCase()));
  }
  
  public String getUserId() {
    return fieldByName("usr_id").getValue().toString();
  }
  
  public void setUserId(String id) {
    fieldByName("usr_id").setString(id);
  }

  public String getPassword() {
    return fieldByName("usr_password").getValue().toString();
  }
  
  public void setPassword(String password) {
    fieldByName("usr_password").setValue(new Variant(password));
  }
  
  public boolean isUserAdmin() {
    return "Y".equals(fieldByName("usr_admin").getValue().toString());
  }
  
  public void setActive(String value) {
    fieldByName("usr_active").setValue(new Variant(value));
  }
  
  public boolean isActive() {
    return "T".equals(fieldByName("usr_active").getValue().toString());
  }
  
  public void setRegistered(String value) {
    fieldByName("usr_registered").setString(value);
  }
  
  public void setRegistered(boolean value) {
    fieldByName("usr_registered").setString(value ? "T" : "F");
  }
  
  public boolean isRegistered() {
    return "T".equals(fieldByName("usr_registered").getValue().toString());
  }
  
  @Override
  public void applyInsert() throws Exception {
    if (!fieldByName("USR_CREATED").isChanged()) {
      fieldByName("USR_CREATED").setValue(new Variant(System.currentTimeMillis()));
    }
    if (!fieldByName("USR_UPDATED").isChanged()) {
      fieldByName("USR_UPDATED").setLong(System.currentTimeMillis());
    }
    super.applyInsert();
    getDatabase().executeCommand(
      "insert into tools (to_usr_id, to_id, to_command, to_source, to_arguments, to_title, to_menu)\n" +
      "values ('" +fieldByName("usr_id").getValue().toString() +"', '" +UniqueID.next() +"', 'notepad-run', 'javaw -jar tools/jfc-notepad-1.0.0.jar', null, 'Notatnik', 'Y')");
    getDatabase().executeCommand(
      "insert into tools (to_usr_id, to_id, to_command, to_source, to_arguments, to_title, to_menu)\n" +
      "values ('" +fieldByName("usr_id").getValue().toString() +"', '" +UniqueID.next() +"', 'notepad', 'javaw -jar tools/jfc-notepad-1.0.0.jar', '\"-open:%s\"', 'Notatnik', 'N')");
  }
  
  @Override
  public void applyUpdate() throws Exception {
    if (!fieldByName("USR_UPDATED").isChanged()) {
      fieldByName("USR_UPDATED").setLong(System.currentTimeMillis());
    }
    super.applyUpdate();
  }
  
}
