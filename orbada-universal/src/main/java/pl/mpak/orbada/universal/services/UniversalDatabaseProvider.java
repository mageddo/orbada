/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.universal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalDatabaseProvider extends DatabaseProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  public static UniversalDatabaseProvider instance;

  public UniversalDatabaseProvider() {
    instance = this;
  }

  public String getFilePath() {
    return application.getConfigPath() + "/universal";
  }

  public String getScriptFile(Database database, boolean connect) {
    return getFilePath() + "/" + database.getUserProperties().getProperty("schemaId") + "-" + (connect ? "connect" : "disconnect") + ".sql";
  }

  private void runScript(Database database, String scriptText) {
    try {
      database.executeScript(scriptText);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public String readScript(Database database, boolean connect) {
    String fileName = getScriptFile(database, connect);
    String script = null;
    try {
      File file = new File(fileName);
      if (file.exists()) {
        script = StreamUtil.stream2String(new FileInputStream(fileName));
      }
    } catch (IOException ex) {
    }
    return script;
  }

  @Override
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  @Override
  public void afterConnection(Database database) {
    String script = readScript(database, true);
    if (script != null) {
      runScript(database, script);
    }
  }

  @Override
  public void beforeDisconnect(Database database) {
    String script = readScript(database, false);
    if (script != null) {
      runScript(database, script);
    }
  }

  public String getDescription() {
    return stringManager.getString("UniversalDatabaseProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }
}
