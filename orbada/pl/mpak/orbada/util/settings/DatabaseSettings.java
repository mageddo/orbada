package pl.mpak.orbada.util.settings;

import java.util.HashMap;
import java.util.Iterator;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.OrbadaDatabase;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DatabaseSettings extends OrbadaSettings {
  
  private HashMap<String, SettingElement> map;
  private boolean loaded;
  
  public DatabaseSettings() {
    map = new HashMap<String, SettingElement>();
  }
  
  @Override
  public void setValue(String name, Variant value) {
    if (!loaded) {
      loadInternal();
    }
    SettingElement se = map.get(name.toLowerCase());
    if (se == null) {
      se = new SettingElement(name, value);
      se.setNewest(true);
      map.put(name.toLowerCase(), se);
    }
    else {
      se.setValue(value);
    }
  }
  
  @Override
  public Variant getValue(String name) {
    return getValue(name, new Variant());
  }
  
  @Override
  public Variant getValue(String name, Variant defaultValue) {
    if (!loaded) {
      loadInternal();
    }
    SettingElement se = map.get(name.toLowerCase());
    if (se == null || se.getValue().isNullValue()  || se.getValue().isNull()) {
      return defaultValue;
    }
    return se.getValue();
  }
  
  @Override
  public void store() {
    if (InternalDatabase.get() == null) {
      return;
    }
    Iterator<SettingElement> i = map.values().iterator();
    Command command = InternalDatabase.get().createCommand();
    while (i.hasNext()) {
      SettingElement se = i.next();
      try {
        if (se.isNewest()) {
          command.setSqlText("insert into settings (set_usr_id, set_sch_id, set_group, set_name, set_value) values (:set_usr_id, :set_sch_id, :set_group, :set_name, :set_value)");
          if (getSchemaId() != null) {
            command.paramByName("set_sch_id").setString(getSchemaId());
          }
          else {
            command.paramByName("set_sch_id").setString(null);
          }
        }
        else if(se.isChanged()) {
          if (getSchemaId() != null) {
            command.setSqlText("update settings set set_value = :set_value where set_usr_id = :set_usr_id and set_sch_id = :set_sch_id and set_group = :set_group and set_name = :set_name");
            command.paramByName("set_sch_id").setString(getSchemaId());
          }
          else {
            command.setSqlText("update settings set set_value = :set_value where set_usr_id = :set_usr_id and set_sch_id is null and set_group = :set_group and set_name = :set_name");
          }
        }
        if (se.isNewest() || se.isChanged()) {
          command.paramByName("set_usr_id").setString(Application.get().getUserId());
          command.paramByName("set_group").setString(getGroupName().toLowerCase());
          command.paramByName("set_name").setString(se.getName());
          command.paramByName("set_value").setString(se.getValue().toString());
          command.execute();
        }
        se.setChanged(false);
        se.setNewest(false);
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  @Override
  public void load() {
    loaded = false;
  }
  
  public void loadInternal() {
    loaded = true;
    map.clear();
    if (InternalDatabase.get() == null) {
      return;
    }
    try {
      Query query = InternalDatabase.get().createQuery();
      try {
        if (getSchemaId() != null) {
          query.setSqlText("select set_name, set_value from settings where set_usr_id = :set_usr_id and set_sch_id = :set_sch_id and set_group = :set_group");
          query.paramByName("set_sch_id").setString(getSchemaId());
        }
        else {
          query.setSqlText("select set_name, set_value from settings where set_usr_id = :set_usr_id and set_sch_id is null and set_group = :set_group");
        }
        query.paramByName("set_usr_id").setString(Application.get().getUserId());
        query.paramByName("set_group").setString(getGroupName().toLowerCase());
        try {
          query.open();
        }
        catch(Exception ex) {
          ((OrbadaDatabase)InternalDatabase.get()).executeCommand("CREATE TABLE SETTINGS(SET_USR_ID VARCHAR(40) NOT NULL, SET_SCH_ID VARCHAR(40), SET_GROUP VARCHAR(100) NOT NULL,SET_NAME VARCHAR(200) NOT NULL, SET_VALUE VARCHAR(4000))");
          ((OrbadaDatabase)InternalDatabase.get()).executeCommandNoException("ALTER TABLE SETTINGS ADD CONSTRAINT SETTINGS_SCHEMA_FK FOREIGN KEY (SET_SCH_ID) REFERENCES SCHEMAS(SCH_ID) ON DELETE CASCADE");
          ((OrbadaDatabase)InternalDatabase.get()).executeCommandNoException("alter table settings add constraint setting_user_fk foreign key (set_usr_id) references users (usr_id) on delete cascade");
          ((OrbadaDatabase)InternalDatabase.get()).executeCommand("CREATE INDEX SETTINGS_UI ON SETTINGS (SET_USR_ID, SET_GROUP, SET_NAME)");
          ((OrbadaDatabase)InternalDatabase.get()).executeCommand("CREATE UNIQUE INDEX SETTINGS_SCHEMA_GROUP_I ON SETTINGS (SET_USR_ID, SET_SCH_ID, SET_GROUP, SET_NAME)");
          ((OrbadaDatabase)InternalDatabase.get()).executeCommand("CREATE INDEX SETTINGS_SCHEMA_I ON SETTINGS (SET_SCH_ID)");
          query.open();
        }
        while (!query.eof()) {
          map.put(
            query.fieldByName("set_name").getString().toLowerCase(), 
            new SettingElement(query.fieldByName("set_name").getString(), new Variant(query.fieldByName("set_value").getValue())));
          query.next();
        }
      }
      finally {
        query.close();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
}
