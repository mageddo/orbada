/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.services;

import java.awt.Component;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.HashMap;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbDatabaseInfo;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseListener;
import pl.mpak.usedb.core.ExecutableListener;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class SQLiteDbInfoProvider extends DatabaseInfoProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public static SQLiteDbInfoProvider instance;

  private HashMap<String, DbDatabaseInfo> databaseInfoList;
  private HashMap<String, HashMap<String, String[]>> databaseStringList;

  public SQLiteDbInfoProvider() {
    instance = this;
    if (databaseInfoList == null) {
      databaseInfoList = new HashMap<String, DbDatabaseInfo>();
    }
    if (databaseStringList == null) {
      databaseStringList = new HashMap<String, HashMap<String, String[]>>();
    }
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaSQLitePlugin.driverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("SQLiteDbInfoProvider-description");
  }

  @Override
  public String getBanner(Database database) {
    return String.format(stringManager.getString("SQLiteDbInfoProvider-banner"), new Object[] {getVersion(database)});
  }

  private String getUniqueName(Database database) {
    return database.getUniqueID();
  }

  public void resetDatabaseInfo(Database database) {
    databaseInfoList.remove(getUniqueName(database));
    databaseStringList.remove(getUniqueName(database));
  }

  private String[] getStrings(Database database, String type) {
    HashMap<String, String[]> result = databaseStringList.get(getUniqueName(database));
    if (result == null) {
      return null;
    }
    return result.get(type);
  }

  public String[] putStrings(Database database, String type, String[] array) {
    HashMap<String, String[]> list = databaseStringList.get(getUniqueName(database));
    if (list == null) {
      list = new HashMap<String, String[]>();
      databaseStringList.put(getUniqueName(database), list);
    }
    list.put(type, array);
    return array;
  }

  public String[] getKeywords(Database database) {
    String[] list = {
      "ABORT", "ADD", "AFTER", "ALTER", "ANALYZE", "AS", "ASC", "ATTACH", "AND",
      "AUTOINCREMENT", "BEFORE", "BEGIN", "BY", "CASCADE", "CASE", "CAST", "CHECK",
      "COLLATE", "COLUMN", "COMMIT", "CONFLICT", "CONSTRAINT", "CREATE", "CROSS",
      "DATABASE", "DEFAULT", "SELECT", "UPDATE", "WHERE", "UNION",
      "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DETACH", "DISTINCT", "DROP",
      "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUSIVE", "EXPLAIN",
      "FAIL", "FOR", "FOREIGN", "FROM", "FULL", "GLOB", "GROUP", "HAVING", "IF",
      "IGNORE", "IMMEDIATE", "INDEX", "INDEXED", "INITIALLY", "INNER",
      "INSERT", "INSTEAD", "INTERSECT", "INTO", "ISNULL", "JOIN", "KEY",
      "LEFT", "LIMIT", "MATCH", "NATURAL", "NOTNULL", "NULL",
      "OF", "OFFSET", "ON", "OR", "ORDER", "OUTER", "PLAN", "PRAGMA", "PRIMARY",
      "QUERY", "RAISE", "REFERENCES", "REGEXP", "REINDEX", "RELEASE", "RENAME", "REPLACE", "RESTRICT",
      "TRUNCATE", "PERSIST", "MEMORY", "OFF", "NORMAL", "EXCLUDE", "TABLE", "VIEW", "TRIGGER",
      "TRANSACTION", "VALUES", "ROLLBACK", "VACUUM", "ROW"
    };
    return list;
  }

  public String[] getOperators(Database database) {
    String[] list = {"ALL", "ANY", "BETWEEN", "IN", "IS", "LIKE", "NOT", "EXISTS"};
    return list;
  }

  public String[] getUserTables(Database database) {
    String[] array = getStrings(database, "USER_TABLES");
    if (array == null) {
      array = putStrings(database, "USER_TABLES", QueryUtil.queryToArray(database, "select name from sqlite_master where type = 'table'"));
    }
    return array;
  }

  public String[] getExceptions(Database database) {
    String[] array = getStrings(database, "EXCEPTIONS");
    return array;
  }

  public String[] getSqlFunctions(Database database) {
    String[] list = {
      "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "strftime", "DATE", "TIME", "DATETIME",
      "avg", "count", "group_concat", "max", "min", "total", "abs", "changes", "coalesce",
      "glob", "ifnull", "hex", "last_insert_rowid", "length", "load_extension", "lower",
      "ltrim", "nullif", "quote", "random", "randomblob", "replace", "round", "rtrim",
      "soundex", "sqlite_version", "substr", "total_changes", "trim", "typeof", "upper",
      "zeroblob"
    };
    return list;
  }

  public String[] getUserFunctions(final Database database) {
    String[] array = getStrings(database, "USER_FUNCTIONS");
    return array;
  }

  public String[] getPublicTables(final Database database) {
    String[] array = {
      "sqlite_master", "sqlite_temp_master", "auto_vacuum", "cache_size", "case_sensitive_like",
      "count_changes", "default_cache_size", "empty_result_callbacks", "encoding",
      "full_column_names", "fullfsync", "incremental_vacuum", "journal_mode", "journal_size_limit",
      "legacy_file_format", "locking_mode", "page_size", "max_page_count", "read_uncommitted",
      "reverse_unordered_selects", "short_column_names", "synchronous", "temp_store",
      "temp_store_directory", "collation_list", "database_list", "foreign_key_list",
      "freelist_count", "index_info", "index_list", "page_count", "table_info",
      "schema_version", "user_version", "integrity_check", "quick_check", "parser_trace",
      "vdbe_trace", "vdbe_listing"
    };
    array = StringUtil.unionList(array, getCatalogs(database));
    return array;
  }

  public String[] getDataTypes(final Database database) {
    String[] types = {
      "TEXT", "NUMBER", "BLOB", "INTEGER", "NUMERIC", "REAL", "NONE", "AUTOINC", "VARCHAR", "BOOLEAN"
    };
    return types;
  }

  public String[] getSchemas(final Database database) {
    return getCatalogs(database);
  }

  public String[] getCatalogs(final Database database) {
    String[] array = getStrings(database, "CATALOGS");
    if (array == null) {
      array = putStrings(database, "CATALOGS", QueryUtil.queryToArray(database, "{name}", "PRAGMA database_list"));
    }
    return array;
  }

  public String getCurrentCatalog() {
    return "main";
  }

  public String[] getTableTypes(final Database database) {
    String[] tableTypes = {"TABLE", "VIEW"};
    return tableTypes;
  }

  public Component[] getExtendedPanelInfo(Database database) {
    return null;
  }

  @Override
  public String getVersion(Database database) {
    Query query = database.createQuery();
    try {
      query.open("select sqlite_version() v");
      return new VersionID(query.fieldByName("v").getString()).toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
    finally {
      query.close();
    }
  }

  public DbDatabaseInfo getDatabaseInfo(final Database database) {
    synchronized (this) {
      DbDatabaseInfo info = databaseInfoList.get(getUniqueName(database));
      if (info == null) {
        databaseInfoList.put(getUniqueName(database), info = new JdbcDbDatabaseInfo(database));
        database.addDatabaseListener(new DatabaseListener() {
          public void beforeConnect(EventObject e) {
          }
          public void afterConnect(EventObject e) {
          }
          public void beforeDisconnect(EventObject e) {
          }
          public void afterDisconnect(EventObject e) {
            databaseInfoList.remove(getUniqueName(database));
            databaseStringList.remove(getUniqueName(database));
          }
        });
        database.addExecutableListener(new ExecutableListener() {
          public boolean canExecute(EventObject e) throws SQLException {
            return parseCommand((ParametrizedCommand)e.getSource());
          }
        });
      }
      return info;
    }
  }

  private boolean parseCommand(ParametrizedCommand command) throws SQLException {
//    if (settings.getValue(FirebirdGeneralSettingsProvider.SET_ConnectionTransaction, false)) {
//      if (pattCommit.matcher(command.getSqlText()).find()) {
//        command.getDatabase().commit();
//        return false;
//      }
//      else if (pattRollback.matcher(command.getSqlText()).find()) {
//        command.getDatabase().rollback();
//        return false;
//      }
//    }
    return true;
  }

  @Override
  public String[] getUniqueIdentFields(Database database, String catalogName, String schemaName, String tableName) {
    String[] result = super.getUniqueIdentFields(database, catalogName, schemaName, tableName);
    if (result == null || result.length == 0) {
      return new String[] {"ROWID"};
    }
    return result;
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }

}
