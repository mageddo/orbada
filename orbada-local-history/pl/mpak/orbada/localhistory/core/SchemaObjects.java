/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.core;

import java.util.HashMap;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.Sql;
import pl.mpak.orbada.localhistory.db.OlhObjectRecord;
import pl.mpak.orbada.localhistory.services.LocalHistorySettingsService;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.TaskUtil;
import pl.mpak.util.task.Task;
import pl.mpak.util.timer.Timer;

/**
 *
 * @author akaluza
 */
public class SchemaObjects {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLocalHistoryPlugin.class);

  private IApplication application;
  private volatile Database database;
  private final HashMap<String, OlhObjectRecord> objectList;
  private volatile boolean loading;
  private volatile boolean canceled;
  private volatile boolean writted = true;
  private Timer writeTimer;
  private ISettings settings;
  private boolean tunedOn;
  
  public SchemaObjects(IApplication application, Database database) {
    this.objectList = new HashMap<String, OlhObjectRecord>();
    this.application = application;
    this.database = database;
    this.writeTimer = getWriteTimer();
    this.settings = application.getSettings(database.getUserProperties().getProperty("schemaId"), LocalHistorySettingsService.settingsName);
    this.tunedOn = 
      (!getSettings().getValue(LocalHistorySettingsService.setGlobalSettings, true) && 
       getSettings().getValue(LocalHistorySettingsService.setTurnedOn, true)) ||
      (getSettings().getValue(LocalHistorySettingsService.setGlobalSettings, true) && 
       OrbadaLocalHistoryPlugin.getSettings().getValue(LocalHistorySettingsService.setTurnedOn, true));
    OrbadaLocalHistoryPlugin.getRefreshQueue().add(this.writeTimer);
  }
  
  private Timer getWriteTimer() {
    return new Timer(10000) {
      public void run() {
        writeHistory();
      }
    };
  }
  
  private void writeHistory() {
    if (!writted) {
      synchronized (objectList) {
        for (OlhObjectRecord olho : objectList.values()) {
          if (olho.isChanged()) {
            try {
              olho.applyInsert();
            } catch (Exception ex) {
              ExceptionUtil.processException(ex);
            }
          }
        }
        writted = true;
      }
    }
  }
  
  private String getKey(String schemaName, String objectType, String objectName) {
    return objectType +" " +StringUtil.nvl(schemaName, "") +"." +objectName;
  }
  
  public void loadLastObjects() {
    loading = true;
    application.getOrbadaDatabase().getTaskPool().addTask(new Task(stringManager.getString("SchemaObjects-loading-connection-history")) {
      public void run() {
        synchronized (objectList) {
          long count;
          Query query = application.getOrbadaDatabase().createQuery();
          try {
            query.setSqlText(Sql.getLastLhObjectCount(null));
            query.paramByName("sch_id").setString(database.getUserProperties().getProperty("schemaId"));
            query.open();
            count = query.fieldByName("cnt").getLong();
            query.setSqlText(Sql.getLastLhObjectList(null));
            query.paramByName("sch_id").setString(database.getUserProperties().getProperty("schemaId"));
            query.open();
            long i = 0;
            while (!query.eof()) {
              OlhObjectRecord olho = new OlhObjectRecord(application.getOrbadaDatabase());
              olho.updateFrom(query);
              objectList.put(getKey(olho.getObjectSchema(), olho.getObjectType(), olho.getObjectName()), olho);
              if (canceled) {
                break;
              }
              if (++i % 10 == 0) {
                setPercenExecution((int)((double)i /count *100));
              }
              query.next();
            }
          }
          catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
          finally {
            loading = false;
            query.close();
          }
        }
      }
    });
  }
  
  public void cancel() {
    canceled = true;
    writeTimer.cancel();
    if (!loading) {
      writeHistory();
    }
    while (loading || !writted) {
      TaskUtil.sleep(10);
    }
  }

  public OlhObjectRecord getObject(String schemaName, String objectType, String objectName) {
    synchronized (objectList) {
      return objectList.get(getKey(schemaName, objectType, objectName));
    }
  }
  
  public void putObject(String schemaName, String objectType, String objectName, String source) {
    synchronized (objectList) {
      writted = false;
      OlhObjectRecord record = new OlhObjectRecord(application.getOrbadaDatabase());
      record.setObjectSchema(schemaName);
      record.setObjectType(objectType);
      record.setObjectName(objectName);
      record.setSchId(database.getUserProperties().getProperty("schemaId"));
      record.setSource(source);
      objectList.put(getKey(schemaName, objectType, objectName), record);
    }
  }

  public ISettings getSettings() {
    return settings;
  }
  
  public boolean isTurnedOn() {
    return tunedOn;
  }
  
}
