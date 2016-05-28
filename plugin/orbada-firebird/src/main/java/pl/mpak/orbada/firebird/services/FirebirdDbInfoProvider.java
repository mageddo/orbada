package pl.mpak.orbada.firebird.services;

import java.awt.Component;
import java.util.EventObject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbDatabaseInfo;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseListener;
import pl.mpak.usedb.core.ExecutableListener;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class FirebirdDbInfoProvider extends DatabaseInfoProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  public static FirebirdDbInfoProvider instance;
  
  private HashMap<String, DbDatabaseInfo> databaseInfoList;
  private HashMap<String, HashMap<String, String[]>> databaseStringList;

  private ISettings settings;
  private static Pattern pattCommit = Pattern.compile("(^COMMIT(\\s+|;)|^COMMIT$)", Pattern.CASE_INSENSITIVE);
  private static Pattern pattRollback = Pattern.compile("(^ROLLBACK(\\s+|;)|^ROLLBACK)", Pattern.CASE_INSENSITIVE);

  public FirebirdDbInfoProvider() {
    instance = this;
    databaseInfoList = new HashMap<String, DbDatabaseInfo>();
    databaseStringList = new HashMap<String, HashMap<String, String[]>>();
  }
  
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaFirebirdPlugin.firebirdDriverType.equals(database.getDriverType());
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
        "ALLOCATE", "ALTER", "ANY", "ARE", "ASC", "ASSERTION", "ADD", "ACTION", "ACTIVE", "ADMIN",
        "AT", "AUTHORIZATION", "BEGIN", "BIT", "BOTH", "ASCENDING", "AUTO", "AUTODDL", "BASED",
        "BY", "CALL", "CASCADE", "CASCADED", "CASE", "CAST", "CHECK", "BASENAME", "BUFFER", "CACHE",
        "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "CONNECT", "CONNECTION",
        "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING", "CREATE", "CURRENT",
        "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "COMMITTED", 
        "DEALLOCATE", "DEC", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "CONTAINING",
        "DESC", "DESCRIBE", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DROP", "ELSE", "RETURNS",
        "END", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "IF", "VARIABLE", "RETURNING_VALUES",
        "EXPLAIN", "EXTERNAL", "FALSE", "FETCH", "FIRST", "FOR", "FOREIGN", "FOUND", "DESCENDING",
        "FROM", "FULL", "FUNCTION", "GET", "GETCURRENTCONNECTION", "GLOBAL", "GO", "GOTO", "FREE_IT",
        "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE", "INDICATOR", "DOMAIN", "EVENT",
        "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INT", "INDEX", "INACTIVE", 
        "INTERSECT", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LAST", "LEFT", "GENERATOR", "INIT",
        "MATCH", "NATIONAL", "NATURAL", "NEXT", "NO", "NULL", "OF", "ON", "ONLY", "OPEN", "OPTION",
        "ORDER", "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "PREPARE", "PRESERVE", "PAGE",
        "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL", "REFERENCES",
        "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "RTRIM", "SCHEMA", "SCROLL",
        "SECOND", "SELECT", "SESSION_USER", "SET", "SOME", "SPACE", "SQL", "SQLCODE", "PLAN", "QUIT",
        "SQLERROR", "SQLSTATE", "SUBSTR", "SUBSTRING", "SYSTEM_USER", "TABLE", "TEMPORARY", "STATIC",
        "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TYPE",
        "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "USING", "VALUES", "RETURN", "ROLE", "VALUE",
        "VARYING", "VIEW", "WHENEVER", "WHERE", "WITH", "WHEN", "WORK", "WRITE", "XML", "WAIT",
        "XMLEXISTS", "XMLPARSE", "XMLQUERY", "XMLSERIALIZE", "TRIGGER", "AFTER" ,"BEFORE",
        "REFERENCING", "EACH", "ROW", "MODE", "DB2SQL", "THEN", "OUT", "LANGUAGE", "JAVA", "PARAMETER",
        "STYLE", "READS", "NAME", "RESULT", "SETS", "DYNAMIC", "SUSPEND", "DO", "PAGES", "PASSWORD",
        "POSITION", "PROTECTED", "SEGMENT", "OR", "AS", "AND", "CHARACTER"};
      list = StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getSQLKeywords(), ", "));
      return list;
    } catch (SQLException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public String[] getOperators(Database database) {
    String[] list = {"ALL", "ANY", "BETWEEN", "IN", "IS", "LIKE", "NOT", "EXISTS"};
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
      array = putStrings(database, "USER_TABLES", QueryUtil.queryToArray(database, "SELECT TRIM(RDB$RELATION_NAME) RELATION_NAME FROM RDB$RELATIONS WHERE COALESCE(RDB$SYSTEM_FLAG, 0) = 0"));
    }
    return array;
  }
  
  public String[] getExceptions(Database database) {
    String[] array = getStrings(database, "EXCEPTIONS");
    if (array == null) {
      array = putStrings(database, "EXCEPTIONS", QueryUtil.queryToArray(database, "SELECT TRIM(RDB$EXCEPTION_NAME) EXCEPTION_NAME FROM RDB$EXCEPTIONS WHERE COALESCE(RDB$SYSTEM_FLAG, 0) = 0"));
    }
    return array;
  }
  
  public String[] getSqlFunctions(Database database) {
    try {
      String[] list = {
        "AVG", "CAST", "COUNT", "LENGTH", "LOWER", "LTRIM", "RTRIM", "SUBSTR", "UPPER", "USER", 
        "TRIM", "MAX", "MIN", "SUM", "GEN_ID", "WEEKDAY", "YEAR", "YEARDAY", "MINUTE", "MONTH"
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
      array = putStrings(database, "USER_FUNCTIONS", QueryUtil.queryToArray(database, "SELECT TRIM(RDB$PROCEDURE_NAME) NAZWA FROM RDB$PROCEDURES WHERE COALESCE(RDB$SYSTEM_FLAG, 0) = 0 UNION ALL SELECT TRIM(RDB$FUNCTION_NAME) NAZWA FROM RDB$FUNCTIONS WHERE COALESCE(RDB$SYSTEM_FLAG, 0) = 0"));
    }
    return array;
  }
  
  public String[] getPublicTables(final Database database) {
    String[] array = getStrings(database, "PUBLIC_TABLES");
    if (array == null) {
      array = putStrings(database, "PUBLIC_TABLES", QueryUtil.queryToArray(database, "SELECT TRIM(RDB$RELATION_NAME) RELATION_NAME FROM RDB$RELATIONS WHERE COALESCE(RDB$SYSTEM_FLAG, 0) = 1"));
    }
    return array;
  }
  
  public String[] getDataTypes(final Database database) {
    String[] types = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TYPES"));
    return types;
  }
  
  public String[] getSchemas(final Database database) {
    return new String[] {};
  }
  
  public String[] getTableTypes(final Database database) {
    String[] tableTypes = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TABLE TYPES"));
    return tableTypes;
  }
  
  public Component[] getExtendedPanelInfo(Database database) {
    return null;
  }
  
  public DbDatabaseInfo getDatabaseInfo(final Database database) {
    if (settings == null) {
      settings = application.getSettings(FirebirdGeneralSettingsProvider.settingsName);
    }
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
    if (settings.getValue(FirebirdGeneralSettingsProvider.SET_ConnectionTransaction, false)) {
      if (pattCommit.matcher(command.getSqlText()).find()) {
        command.getDatabase().commit();
        return false;
      }
      else if (pattRollback.matcher(command.getSqlText()).find()) {
        command.getDatabase().rollback();
        return false;
      }
    }
    return true;
  }
  
  public String getDescription() {
    return stringManager.getString("FirebirdDbInfoProvider-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }

}
