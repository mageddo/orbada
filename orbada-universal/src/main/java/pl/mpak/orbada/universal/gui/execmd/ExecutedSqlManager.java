/*
 * ExecutedSqlManager.java
 *
 * Created on 2007-10-21, 17:46:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.execmd;

import java.sql.Timestamp;
import java.util.Date;
import java.util.zip.CRC32;
import orbada.core.Application;
import orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.universal.services.UniversalSettingsProvider;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class ExecutedSqlManager {
  
  public static int deleteAfterDays = 365;
  private static ExecutedSqlManager manager = new ExecutedSqlManager();
  
  public ExecutedSqlManager() {
    ISettings settings = Application.get().getSettings(UniversalSettingsProvider.settingsName);
    deleteAfterDays = settings.getValue(UniversalSettingsProvider.setDeleteAfterDays, (long)deleteAfterDays).intValue();
    checkTable();
  }
  
  public static ExecutedSqlManager get() {
    return manager;
  }
  
  private void checkTable() {
    try {
      Variant v = new Variant(new Date()).subtract(new Variant(deleteAfterDays));
      Command command = getDatabase().createCommand("delete from execmds where cmd_selected < :cmd_selected and cmd_usr_id = :cmd_usr_id", false);
      command.paramByName("cmd_selected").setTimestamp(v.getTimestamp());
      command.paramByName("cmd_usr_id").setString(Application.get().getUserId());
      command.execute();
    } 
    catch(Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public Database getDatabase() {
    return InternalDatabase.get();
  }
  
  public void updateSelected(String fileId) {
    try {
      Command command = getDatabase().createCommand("update execmds set cmd_selected = :cmd_selected where cmd_file_id = :cmd_file_id", false);
      command.paramByName("cmd_selected").setTimestamp(new Timestamp(System.currentTimeMillis()));
      command.paramByName("cmd_file_id").setString(fileId);
      command.execute();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void addSqlText(String sqlText, String schemaId) {
    CRC32 crc32 = new CRC32();
    crc32.update(SQLUtil.removeWhiteSpaces(sqlText).getBytes());
    Query query = getDatabase().createQuery();
    try {
      query.setSqlText("select cmd_file_id from execmds where cmd_usr_id = :cmd_usr_id and cmd_crc32 = :cmd_crc32 and (cmd_sch_id = :cmd_sch_id or cmd_sch_id is null)");
      query.paramByName("cmd_usr_id").setString(Application.get().getUserId());
      query.paramByName("cmd_crc32").setDouble(crc32.getValue());
      query.paramByName("cmd_sch_id").setString(schemaId);
      query.open();
      if (!query.eof()) {
        Command command = getDatabase().createCommand("update execmds set cmd_selected = :cmd_selected, cmd_source = :cmd_source, cmd_executed_count = cmd_executed_count +1 where cmd_usr_id = :cmd_usr_id and cmd_crc32 = :cmd_crc32 and (cmd_sch_id = :cmd_sch_id or cmd_sch_id is null)", false);
        command.paramByName("cmd_usr_id").setString(Application.get().getUserId());
        command.paramByName("cmd_selected").setTimestamp(new Timestamp(System.currentTimeMillis()));
        command.paramByName("cmd_crc32").setDouble(crc32.getValue());
        command.paramByName("cmd_source").setString(sqlText);
        command.paramByName("cmd_sch_id").setString(schemaId);
        command.execute();
      }
      else {
        String id = new UniqueID().toString();
        Command command = getDatabase().createCommand("insert into execmds (cmd_usr_id, cmd_crc32, cmd_executed, cmd_selected, cmd_file_id, cmd_source, cmd_sch_id, cmd_executed_count) values (:cmd_usr_id, :cmd_crc32, :cmd_executed, :cmd_executed, :cmd_file_id, :cmd_source, :cmd_sch_id, 1)", false);
        command.paramByName("cmd_usr_id").setString(Application.get().getUserId());
        command.paramByName("cmd_crc32").setLong(crc32.getValue());
        command.paramByName("cmd_executed").setTimestamp(new Timestamp(System.currentTimeMillis()));
        command.paramByName("cmd_file_id").setString(id);
        command.paramByName("cmd_source").setString(sqlText);
        command.paramByName("cmd_sch_id").setString(schemaId);
        command.execute();
      }
    } catch(Exception ex) {
      ExceptionUtil.processException(ex);
    } finally {
      query.close();
    }
  }
  
}
