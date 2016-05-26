package pl.mpak.orbada.sqlmacro.services;

import java.util.ArrayList;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IProcessMessagable;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.sqlmacro.Consts;
import pl.mpak.orbada.sqlmacro.OrbadaSqlMacrosPlugin;
import pl.mpak.orbada.sqlmacro.db.SqlMacroRecord;
import pl.mpak.orbada.universal.providers.UniversalSqlTextTransformProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.task.Task;

/**
 *
 * @author akaluza
 */
public class MacroSqlTextTransform extends UniversalSqlTextTransformProvider implements IProcessMessagable {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sql-macro");

  private final ArrayList<SqlMacroRecordForTransform> macroList;
  private boolean inited = false;
  
  public MacroSqlTextTransform() {
    macroList = new ArrayList<SqlMacroRecordForTransform>();
  }
  
  @Override
  public void setApplication(IApplication application) {
    this.application = application;
    this.application.registerRequestMessager(this);
  }
  
  private void reloadMacros() {
    synchronized (macroList) {
      macroList.clear();
      Query query = application.getOrbadaDatabase().createQuery();
      try {
        query.setSqlText("select * from osqlmacros left outer join driver_types on (dtp_id = osm_dtp_id) where osm_usr_id is null or osm_usr_id = :USR_ID order by osm_order, dtp_id");
        query.paramByName("USR_ID").setString(application.getUserId());
        query.open();
        while (!query.eof()) {
          SqlMacroRecordForTransform macro = new SqlMacroRecordForTransform(application.getOrbadaDatabase());
          macro.updateFrom(query);
          if (!query.fieldByName("dtp_name").isNull()) {
            macro.setDriverType(query.fieldByName("dtp_name").getString());
          }
          macroList.add(macro);
          query.next();
        }
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
    }
  }
  
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  public String transformSqlText(Database database, String sqlText) {
    if (!inited) {
      reloadMacros();
      inited = true;
    }
    synchronized (macroList) {
      for (SqlMacroRecordForTransform macro : macroList) {
        if (macro.getDriverType() == null || StringUtil.equals(macro.getDriverType(), database.getDriverType())) {
          String resolve = macro.resolve(sqlText);
          if (resolve != null) {
            return resolve;
          }
        }
      }
    }
    return null;
  }

  public String getDescription() {
    return stringManager.getString("MacroSqlTextTransform-description");
  }

  public String getGroupName() {
    return OrbadaSqlMacrosPlugin.pluginGroupName;
  }

  public void processMessage(PluginMessage message) {
    if (message.isMessageId(Consts.sqlMacrosReloadMsg)) {
      application.getOrbadaDatabase().getTaskPool().addTask(new Task() {
        public void run() {
          reloadMacros();
        }
      });
    }
  }

  private class SqlMacroRecordForTransform extends SqlMacroRecord {
    private String driverType;
    
    public SqlMacroRecordForTransform(Database database) {
      super(database);
    }

    public String getDriverType() {
      return driverType;
    }

    public void setDriverType(String driverType) {
      this.driverType = driverType;
    }

  }

}
