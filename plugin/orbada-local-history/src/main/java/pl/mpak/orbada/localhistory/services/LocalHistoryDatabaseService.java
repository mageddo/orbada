package pl.mpak.orbada.localhistory.services;

import java.util.HashMap;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.core.SchemaObjects;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class LocalHistoryDatabaseService extends DatabaseProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("local-history");

  private static HashMap<String, SchemaObjects> databaseSchemaObjects = new HashMap<String, SchemaObjects>();
  
  @Override
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  @Override
  public void afterConnection(Database database) {
    synchronized (databaseSchemaObjects) {
      SchemaObjects sh = new SchemaObjects(application, database);
      if (sh.isTurnedOn()) {
        sh.loadLastObjects();
      }
      databaseSchemaObjects.put(database.getUniqueID(), sh);
    }
  }

  @Override
  public void beforeDisconnect(Database database) {
    synchronized (databaseSchemaObjects) {
      SchemaObjects sh = getSchemaObjects(database);
      if (sh != null) {
        sh.cancel();
      }
      databaseSchemaObjects.remove(database.getUniqueID());
    }
  }

  public String getDescription() {
    return stringManager.getString("LocalHistoryDatabaseService-description");
  }

  public String getGroupName() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }
  
  public static SchemaObjects getSchemaObjects(Database database) {
    synchronized (databaseSchemaObjects) {
      return databaseSchemaObjects.get(database.getUniqueID());
    }
  }
  
}
