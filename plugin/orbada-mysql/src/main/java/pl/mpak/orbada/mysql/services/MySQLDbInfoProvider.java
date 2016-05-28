package pl.mpak.orbada.mysql.services;

import java.awt.Component;
import java.util.EventObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import pl.mpak.orbada.gui.comps.table.QueryTablePanel;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbDatabaseInfo;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
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
public class MySQLDbInfoProvider extends DatabaseInfoProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public static MySQLDbInfoProvider instance;

  private HashMap<String, DbDatabaseInfo> databaseInfoList;
  private HashMap<String, HashMap<String, String[]>> databaseStringList;

  private ISettings settings;

  public MySQLDbInfoProvider() {
    instance = this;
  }

  public String getDescription() {
    return stringManager.getString("MySQLDbInfoProvider-description");
  }

  public Component[] getExtendedPanelInfo(Database database) {
    ArrayList<Component> list = new ArrayList<Component>();
    addExtendedPanel(list, "SHOW AUTHORS", database, stringManager.getString("authors"));
    addExtendedPanel(list, "SHOW CONTRIBUTORS", database, stringManager.getString("Contributors"));
    addExtendedPanel(list, "SHOW STATUS", database, stringManager.getString("status"));
    return list.toArray(new Component[list.size()]);
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
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
    try {
      String[] list = {
        "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE",
        "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE",
        "CASE", "CHANGE", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CLOSE",
        "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE",
        "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES",
        "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", 
        "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE",
        "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "EACH",
        "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH",
        "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GOTO",
        "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND",
        "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INTERVAL", "INTO", "IS",
        "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LABEL", "LEADING", "LEAVE", "LEFT", "LIKE",
        "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LOOP",
        "LOW_PRIORITY", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH",
        "MINUTE_MICROSECOND", "MINUTE_SECOND", "HANDLER", "FOUND",
        "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "ON",
        "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION",
        "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "READ_ONLY", "READ_WRITE",
        "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE",
        "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND",
        "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SPATIAL", "SPECIFIC",
        "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS",
        "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN",
        "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION",
        "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "UPGRADE", "USAGE", "USE", "USING", "UTC_DATE",
        "UTC_TIME", "UTC_TIMESTAMP", "VALUES",
        "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL", "FUNCTION",
        "INDEXES", "RETURNS", "BEGIN", "END", "GLOBAL", "SESSION", "GRANTS", "STATUS", "FULL",
        "IDENTIFIED", "WARNINGS", "ERRORS", "SHEDULE", "AT", "DEFINER", "CHARSET", "DO", "HELP",
        "CODE", "VIEW", "ALGORITHM", "SECURITY", "MODIFY", "CHECKSUM", "UNTIL", "OPEN"
      };
      return StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getSQLKeywords(), ","));
    } catch (SQLException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }

  public String[] getOperators(Database database) {
    String[] list = {"ALL", "ANY", "BETWEEN", "IN", "IS", "LIKE", "NOT", "EXISTS", "RLIKE"};
    return list;
  }

  public String[] getContainerList(DbObjectContainer list) {
    if (list != null) {
      return list.namesArray();
    }
    return new String[] {};
  }

  public String[] getUserTables(Database database) {
    String[] array = getStrings(database, "USER_TABLES");
    if (array == null) {
      array = putStrings(database, "USER_TABLES", QueryUtil.queryToArray(database, "SHOW TABLES"));
    }
    return array;
  }

  public String[] getExceptions(Database database) {
    String[] array = getStrings(database, "EXCEPTIONS");
//    if (array == null) {
//      array = putStrings(database, "EXCEPTIONS", getFromQuery(database, "SELECT TRIM(RDB$EXCEPTION_NAME) EXCEPTION_NAME FROM RDB$EXCEPTIONS WHERE COALESCE(RDB$SYSTEM_FLAG, 0) = 0"));
//    }
    return array;
  }

  public String[] getSqlFunctions(Database database) {
    try {
      String[] list = {
        "GROUP_CONCAT", "UUID", "UUID_SHORT", "COUNT", "SUM", "AVG", "MIN", "MAX", "ISNULL", "SUBSTR",
        "GREATEST", "LEAST", "IFNULL", "CAST", "CAST", "EXTRACTVALUE", "FORMAT", "UNHEX", "UPDATEXML",
        "ADDDATE", "ADDTIME", "CONVERT_TZ", "DATEDIFF", "DATE_ADD", "DATE_SUB", "DAY", "EXTRACT", "GET_FORMAT",
        "LAST_DAY", "MAKEDATE", "MAKETIME", "MICROSECOND", "STR_TO_DATE", "SUBDATE", "SUBTIME", "TIMEDIFF",
        "TIMESTAMPADD", "TIMESTAMPDIFF", "WEEKOFYEAR", "YEARWEEK", "BENCHMARK", "COERCIBILITY", "COLLATION",
        "CONNECTION_ID", "FOUND_ROWS", "ROW_COUNT", "CEIL", "CRC32", "LN", "LOG2", "SIGN"
      };
      list = StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getStringFunctions(), ", "));
      list = StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getTimeDateFunctions(), ", "));
      list = StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getNumericFunctions(), ", "));
      list = StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getSystemFunctions(), ", "));
      return list;
    } catch (SQLException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }

  public String[] getUserFunctions(final Database database) {
    String[] array = getStrings(database, "USER_FUNCTIONS");
    if (array == null) {
      array = putStrings(database, "USER_FUNCTIONS", QueryUtil.queryToArray(database, "select routine_name from information_schema.routines where routine_schema = database()"));
    }
    return array;
  }

  public String[] getPublicTables(final Database database) {
    String[] array = getStrings(database, "PUBLIC_TABLES");
    if (array == null) {
      array = putStrings(database, "PUBLIC_TABLES", QueryUtil.queryToArray(database, "select distinct table_name from information_schema.tables where table_schema in ('information_schema', 'mysql')"));
    }
    return array;
  }

  public String[] getDataTypes(final Database database) {
    String[] types = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TYPES"));
    return types;
  }

  public String[] getSchemas(final Database database) {
    return QueryUtil.queryToArray(database, "select schema_name from information_schema.schemata order by schema_name");
  }

  public String[] getTableTypes(final Database database) {
    String[] list = {"BOOLEAN"};
    String[] tableTypes = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TABLE TYPES"));
    list = StringUtil.unionList(list, tableTypes);
    return list;
  }

  private void addExtendedPanel(ArrayList<Component> list, String sqlText, Database database, String title) {
    try {
      QueryTablePanel panel = new QueryTablePanel(sqlText, database, title);
      list.add(panel);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  @Override
  public String getVersion(Database database) {
    Query query = database.createQuery();
    try {
      query.open("SHOW SESSION VARIABLES LIKE 'version'");
      return new VersionID(query.fieldByName("value").getString()).toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
    finally {
      query.close();
    }
  }

  @Override
  public String getBanner(Database database) {
    Query query = database.createQuery();
    try {
      String version = "";
      String comment = "";
      String compile = "";
      query.open("SHOW SESSION VARIABLES LIKE 'version%'");
      String fieldName = "variable_value";
      if (query.findFieldByName(fieldName) == null) {
        fieldName = "value";
      }
      while (!query.eof()) {
        if ("version".equalsIgnoreCase(query.fieldByName("variable_name").getString())) {
          version = query.fieldByName(fieldName).getString();
        }
        else if ("version_comment".equalsIgnoreCase(query.fieldByName("variable_name").getString())) {
          comment = query.fieldByName(fieldName).getString();
        }
        else if ("version_compile_machine".equalsIgnoreCase(query.fieldByName("variable_name").getString())) {
          compile = compile +query.fieldByName(fieldName).getString();
        }
        else if ("version_compile_os".equalsIgnoreCase(query.fieldByName("variable_name").getString())) {
          compile = compile +" " +query.fieldByName(fieldName).getString();
        }
        query.next();
      }
      return String.format(
        "Baza danych\nWersja: %s\nOpis: %s\nKompilacja na: %s\n\nSterownik\nNazwa: %s\nWersja: %s",
        new Object[] {version, comment, compile, database.getMetaData().getDriverName(), database.getMetaData().getDriverVersion()});
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
//    if (settings == null) {
//      settings = application.getSettings(FirebirdGeneralSettingsProvider.settingsName);
//    }
    synchronized (this) {
      if (databaseInfoList == null) {
        databaseInfoList = new HashMap<String, DbDatabaseInfo>();
      }
      if (databaseStringList == null) {
        databaseStringList = new HashMap<String, HashMap<String, String[]>>();
      }
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

  public static String getCurrentDatabase(Database database) {
    return database.getUserProperties().getProperty("database-name", database.getUserName().toUpperCase());
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

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}
