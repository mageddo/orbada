/*
 * Driver.java
 *
 * Created on 2007-10-11, 22:50:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.todo.db;

import orbada.Consts;
import orbada.core.Application;
import orbada.db.InternalDatabase;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.BufferedRecord;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Todo extends BufferedRecord {
  
  public Todo() {
    super("TODOS", "TD_ID");
    add("TD_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("TD_USR_ID", new Variant(Application.get().getUserId()), VariantType.varString, true);
    add("TD_SCH_ID", VariantType.varString, true);
    add("TD_PRIORITY", VariantType.varLong, true);
    add("TD_STATE", VariantType.varString, true);
    add("TD_TITLE", VariantType.varString, true);
    add("TD_DESCRIPTION", VariantType.varString, true);
    add("TD_ENABLE", VariantType.varString, true);
    add("TD_INSERTED", VariantType.varTimestamp, true);
    add("TD_UPDATED", VariantType.varTimestamp, true);
    add("TD_WORKAROUND", VariantType.varString, true);
    add("TD_PLAN_END", VariantType.varTimestamp, true);
    add("TD_ENDED", VariantType.varTimestamp, true);
    add("TD_APP_VERSION", VariantType.varString, true);
    add("TD_ORBADA", VariantType.varString, true);
    add("TD_EXPORTED", VariantType.varString, true);
  }
  
  public Todo(String td_id) throws UseDBException {
    this();
    load(td_id);
  }
  
  protected void load(String td_id) throws UseDBException {
    cancelUpdates();
    Query query = new Query(InternalDatabase.get());
    query.setSqlText("select * from todos where td_id = :td_id");
    query.paramByName("td_id").setString(td_id);
    try {
      query.open();
    } catch (Exception ex) {
      throw new UseDBException(ex);
    }
    try {
      if (!query.eof()) {
        updateFrom(query);
      }
    } finally {
      query.close();
    }
  }
  
  public void applyInsert() throws Exception {
    if (!fieldByName("TD_INSERTED").isChanged()) {
      fieldByName("TD_INSERTED").setValue(new Variant(System.currentTimeMillis()));
    }
    if (!fieldByName("TD_UPDATED").isChanged()) {
      fieldByName("TD_UPDATED").setLong(System.currentTimeMillis());
    }
    if (StringUtil.toBoolean(fieldByName("TD_ORBADA").getString()) && !Consts.orbadaUserId.equals(fieldByName("TD_USR_ID").getString())) {
      fieldByName("TD_SCH_ID").setValue(null);
      fieldByName("TD_USR_ID").setString(Consts.orbadaUserId);
    }
    Command command = insertCommand(InternalDatabase.get());
    command.execute();
  }
  
  public void applyUpdate() throws Exception {
    if (!fieldByName("TD_UPDATED").isChanged()) {
      fieldByName("TD_UPDATED").setLong(System.currentTimeMillis());
    }
    if (!StringUtil.toBoolean(fieldByName("TD_ENABLE").getString()) && 
        fieldByName("TD_ENABLE").getOldValue() != null && 
        StringUtil.toBoolean(fieldByName("TD_ENABLE").getOldValue().getString())) {
      fieldByName("TD_ENDED").setLong(System.currentTimeMillis());
    }
    else if (StringUtil.toBoolean(fieldByName("TD_ENABLE").getString())) {
      fieldByName("TD_ENDED").setValue(null);
    }
    Command command = updateCommand(InternalDatabase.get());
    command.execute();
  }
  
  public void applyDelete() throws Exception {
    Command command = deleteCommand(InternalDatabase.get());
    command.execute();
  }
  
}
