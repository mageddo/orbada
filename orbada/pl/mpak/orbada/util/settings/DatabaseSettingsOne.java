package pl.mpak.orbada.util.settings;

import java.util.HashMap;
import java.util.Iterator;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.OrbadaDatabase;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class DatabaseSettingsOne extends OrbadaSettings {

  private static class NameMap extends HashMap<String, SettingElement> {}
  private static class GroupMap extends HashMap<String, NameMap> {}
  private static class SchemaMap extends HashMap<String, GroupMap> {}
  
  private static SchemaMap map = new SchemaMap();
  private static boolean loaded = false;
  
  public DatabaseSettingsOne() {
  }

  private NameMap getMap() {
    GroupMap gmap = map.get(StringUtil.nvl(getSchemaId(), ""));
    NameMap nmap = null;
    if (gmap != null) {
      nmap = gmap.get(getGroupName().toLowerCase());
    }
    else {
      gmap = new GroupMap();
      map.put(StringUtil.nvl(getSchemaId(), ""), gmap);
    }
    if (nmap == null) {
      nmap = new NameMap();
      gmap.put(getGroupName().toLowerCase(), nmap);
    }
    return nmap;
  }
  
  @Override
  public void setValue(String name, Variant value) {
    if (!loaded) {
      loadInternal();
    }
    NameMap nmap = getMap();
    SettingElement se = nmap.get(name.toLowerCase());
    if (se == null) {
      se = new SettingElement(name, value);
      se.setNewest(true);
      nmap.put(name.toLowerCase(), se);
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
    NameMap nmap = getMap();
    SettingElement se = nmap.get(name.toLowerCase());
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
    if (!loaded) {
      loadInternal();
    }
    NameMap nmap = getMap();
    Iterator<SettingElement> i = nmap.values().iterator();
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
    //loaded = false;
  }
  
  public void loadInternal() {
    loaded = true;
    if (InternalDatabase.get() == null) {
      return;
    }
    try {
      Query query = InternalDatabase.get().createQuery();
      try {
        query.setSqlText("select set_sch_id, set_group, set_name, set_value from settings where set_usr_id = :set_usr_id order by set_sch_id, set_group");
        query.paramByName("set_usr_id").setString(Application.get().getUserId());
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
        GroupMap gmap = null;
        NameMap nmap = null;
        String set_sch_id = null;
        String set_group = null;
        while (!query.eof()) {
          if (!StringUtil.equals(set_sch_id, query.fieldByName("set_sch_id").getString())) {
            set_sch_id = query.fieldByName("set_sch_id").getString();
            gmap = new GroupMap();
            map.put(set_sch_id, gmap);
          }
          if (!StringUtil.equals(set_group, query.fieldByName("set_group").getString().toLowerCase())) {
            set_group = query.fieldByName("set_group").getString().toLowerCase();
            nmap = new NameMap();
            gmap.put(set_group, nmap);
          }
          nmap.put(
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
