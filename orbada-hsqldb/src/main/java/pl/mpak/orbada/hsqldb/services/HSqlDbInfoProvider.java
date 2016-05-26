package pl.mpak.orbada.hsqldb.services;

import java.awt.Component;
import java.util.EventObject;
import java.sql.SQLException;
import java.util.HashMap;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.hsqldb.dbinfo.HSqlDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class HSqlDbInfoProvider extends DatabaseInfoProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  public static HSqlDbInfoProvider instance;
  
  private HashMap<String, DbDatabaseInfo> databaseInfoList;

  public final static int hsqlDbVerMultipier = 100;
  public final static int hsqlDb18 = 1 *hsqlDbVerMultipier +8;
  public final static int hsqlDb20 = 2 *hsqlDbVerMultipier +0;
  
  public HSqlDbInfoProvider() {
    instance = this;
  }
  
  @Override
  public String getDescription() {
    return stringManager.getString("HSqlDbInfoProvider-description");
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaHSqlDbPlugin.hsqlDbDriverType.equals(database.getDriverType());
  }
  
  private String getUniqueName(Database database) {
    return database.getUniqueID();
  }
  
  public static String getCurrentSchema(Database database) {
    Query query = database.createQuery();
    try {
      query.open(Sql.getCurrentSchema(HSqlDbInfoProvider.getVersionTest(database)));
      return query.fieldByName("schema_name").getString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return "";
    }
    finally {
      query.close();
    }
  }

  public static int getVersionTest(Database database) {
    VersionID vid = new VersionID(database.getUserProperties().getProperty("version", "0"));
    return vid.getMajor() *100 +vid.getMinor();
  }
  
  @Override
  public void resetDatabaseInfo(Database database) {
    databaseInfoList.remove(getUniqueName(database));
  }
  
  @Override
  public String[] getKeywords(Database database) {
    try {
      String[] list = {
        "ALLOCATE", "ALTER", "ANY", "ARE", "ASC", "ASSERTION", "ADD",
        "AT", "AUTHORIZATION", "BEGIN", "BIT", "BOTH",
        "BY", "CALL", "CASCADE", "CASCADED", "CASE", "CAST", "CHECK",
        "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "CONNECT", "CONNECTION",
        "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING", "CREATE", "CURRENT",
        "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR",
        "DEALLOCATE", "DEC", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE",
        "DESC", "DESCRIBE", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DROP", "ELSE",
        "END", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", 
        "EXPLAIN", "EXTERNAL", "FALSE", "FETCH", "FIRST", "FOR", "FOREIGN", "FOUND",
        "FROM", "FULL", "FUNCTION", "GET", "GETCURRENTCONNECTION", "GLOBAL", "GO", "GOTO",
        "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE", "INDICATOR",
        "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INT", "INDEX",
        "INTERSECT", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LAST", "LEFT", 
        "MATCH", "NATIONAL", "NATURAL", "NEXT", "NO", "NULL", "OF", "ON", "ONLY", "OPEN", "OPTION",
        "ORDER", "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "PREPARE", "PRESERVE",
        "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL", "REFERENCES",
        "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "RTRIM", "SCHEMA", "SCROLL",
        "SECOND", "SELECT", "SESSION_USER", "SET", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE",
        "SQLERROR", "SQLSTATE", "SUBSTR", "SUBSTRING", "SYSTEM_USER", "TABLE", "TEMPORARY",
        "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRANSACTION", "TRANSLATE", "TRANSLATION",
        "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "USING", "VALUES",
        "VARYING", "VIEW", "WHENEVER", "WHERE", "WITH", "WHEN", "WORK", "WRITE", "XML",
        "XMLEXISTS", "XMLPARSE", "XMLQUERY", "XMLSERIALIZE", "TRIGGER", "AFTER" ,"BEFORE",
        "REFERENCING", "EACH", "ROW", "MODE", "DB2SQL", "THEN", "OUT", "LANGUAGE", "JAVA", "PARAMETER",
        "STYLE", "READS", "NAME", "RESULT", "SETS", "DYNAMIC", "ROLE", "SHUTDOWN", "COMPACT",
        "CLASS", "SEQUENCE", "SCRIPT", "AS", "AND", "OR"};
      StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getSQLKeywords(), ", "));
      return list;
    } catch (SQLException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  @Override
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
  
  @Override
  public String[] getUserTables(final Database database) {
    String[] tableList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/" +getCurrentSchema(database) +"/TABLES"));
    String[] viewList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/" +getCurrentSchema(database) +"/VIEWS"));
    return StringUtil.unionList(tableList, viewList);
  }
  
  @Override
  public String[] getExceptions(Database database) {
    return null;
  }
  
  @Override
  public String[] getSqlFunctions(Database database) {
    try {
      String[] list = {
        "AVG", "ABS", "ABSVAL", "ACOS", "ASIN", "ATAN", "CAST", "CEIL", "CEILING", "COUNT",
        "COS", "NULLIF", "CASE", "CURRENT_DATE", "CURRENT", "CURRENT_TIME", "CURRENT_TIMESTAMP",
        "CURRENT_USER", "DATE", "DAY", "DEGREES", "DOUBLE", "EXP", "FLOOR", "HOUR",
        "IDENTITY_VAL_LOCAL", "LENGTH", "LN", "LOG", "LOG10", "LOCATE", "LCASE",
        "LOWER", "LTRIM", "MINUTE", "MOD", "MONTH", "PI", "RADIANS", "RTRIM", "SECOND",
        "SESSION_USER", "SIN", "SMALLINT", "SQRT", "SUBSTR", "TAN",
        "UCASE", "UPPER", "USER", "YEAR", "MAX", "MIN"};
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
  
  @Override
  public String[] getUserFunctions(final Database database) {
    String[] funcList = null;
    if (HSqlDbInfoProvider.getVersionTest(database) == HSqlDbInfoProvider.hsqlDb18) {
      funcList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/" +getCurrentSchema(database) +"/ALIASES"));
    }
    return funcList;
  }
  
  @Override
  public String[] getPublicTables(final Database database) {
    String[] tableList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/INFORMATION_SCHEMA/TABLES"));
    String[] viewList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/INFORMATION_SCHEMA/VIEWS"));
    return StringUtil.unionList(tableList, viewList);
  }
  
  @Override
  public String[] getDataTypes(final Database database) {
    String[] types = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TYPES"));
    return types;
  }
  
  @Override
  public String[] getSchemas(final Database database) {
    String[] schemas = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS"));
    return schemas;
  }
  
  @Override
  public String[] getTableTypes(final Database database) {
    String[] tableTypes = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TABLE TYPES"));
    return tableTypes;
  }
  
  @Override
  public Component[] getExtendedPanelInfo(Database database) {
    return null;
  }
  
  @Override
  public DbDatabaseInfo getDatabaseInfo(final Database database) {
    synchronized (this) {
      if (databaseInfoList == null) {
        databaseInfoList = new HashMap<String, DbDatabaseInfo>();
      }
      DbDatabaseInfo info = databaseInfoList.get(getUniqueName(database));
      if (info == null) {
        databaseInfoList.put(getUniqueName(database), info = new HSqlDatabaseInfo(database));
        database.addDatabaseListener(new DatabaseListener() {
          @Override
          public void beforeConnect(EventObject e) {
          }
          @Override
          public void afterConnect(EventObject e) {
          }
          @Override
          public void beforeDisconnect(EventObject e) {
          }
          @Override
          public void afterDisconnect(EventObject e) {
            databaseInfoList.remove(getUniqueName(database));
          }
        });
      }
      return info;
    }
  }
  
  @Override
  public String getGroupName() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType;
  }

}
