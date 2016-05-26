/*
 * DerbyDbInfoProvider.java
 *
 * Created on 2007-10-27, 16:28:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.services;

import java.awt.Component;
import java.util.EventObject;
import java.sql.SQLException;
import java.util.HashMap;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.derbydb.dbinfo.DerbyDbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseListener;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class DerbyDbInfoProvider extends DatabaseInfoProvider {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  public static DerbyDbInfoProvider instance;
  
  private HashMap<String, DbDatabaseInfo> databaseInfoList;
  
  public DerbyDbInfoProvider() {
    instance = this;
  }
  
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType.equals(database.getDriverType());
  }
  
  private String getUniqueName(Database database) {
    return database.getUniqueID();
  }
  
  public void resetDatabaseInfo(Database database) {
    databaseInfoList.remove(getUniqueName(database));
  }
  
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
        "STYLE", "READS", "NAME", "RESULT", "SETS", "DYNAMIC", "AS", "AND", "OR"};
      StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getSQLKeywords(), ", "));
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
  
  public String[] getUserTables(final Database database) {
    String[] tableList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/" +database.getUserName().toUpperCase() +"/TABLES"));
    String[] viewList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/" +database.getUserName().toUpperCase() +"/VIEWS"));
    return StringUtil.unionList(tableList, viewList);
  }
  
  public String[] getExceptions(Database database) {
    return null;
  }
  
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
      StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getStringFunctions(), ", "));
      StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getTimeDateFunctions(), ", "));
      StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getNumericFunctions(), ", "));
      StringUtil.unionList(list, StringUtil.tokenizeList(database.getMetaData().getSystemFunctions(), ", "));
      return list;
    } catch (SQLException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public String[] getUserFunctions(final Database database) {
    String[] procList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/" +database.getUserName().toUpperCase() +"/PROCEDURES"));
    String[] funcList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/" +database.getUserName().toUpperCase() +"/FUNCTIONS"));
    return StringUtil.unionList(procList, funcList);
  }
  
  public String[] getPublicTables(final Database database) {
    String[] tableList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/SYS/TABLES"));
    String[] viewList = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS/SYS/VIEWS"));
    return StringUtil.unionList(tableList, viewList);
  }
  
  public String[] getDataTypes(final Database database) {
    String[] types = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TYPES"));
    return types;
  }
  
  public String[] getSchemas(final Database database) {
    String[] schemas = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/SCHEMAS"));
    return schemas;
  }
  
  public String[] getTableTypes(final Database database) {
    String[] tableTypes = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TABLE TYPES"));
    return tableTypes;
  }
  
  public Component[] getExtendedPanelInfo(Database database) {
    return null;
  }
  
  public DbDatabaseInfo getDatabaseInfo(final Database database) {
    synchronized (this) {
      if (databaseInfoList == null) {
        databaseInfoList = new HashMap<String, DbDatabaseInfo>();
      }
      DbDatabaseInfo info = databaseInfoList.get(getUniqueName(database));
      if (info == null) {
        databaseInfoList.put(getUniqueName(database), info = new DerbyDbDatabaseInfo(database));
        database.addDatabaseListener(new DatabaseListener() {
          public void beforeConnect(EventObject e) {
          }
          public void afterConnect(EventObject e) {
          }
          public void beforeDisconnect(EventObject e) {
          }
          public void afterDisconnect(EventObject e) {
            databaseInfoList.remove(getUniqueName(database));
          }
        });
      }
      return info;
    }
  }
  
  public String getDescription() {
    return stringManager.getString("DerbyDbInfoProvider-description");
  }

  public String getGroupName() {
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType;
  }

}
