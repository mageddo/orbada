package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import java.util.EventObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import pl.mpak.orbada.gui.comps.table.QueryTablePanel;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.dbinfo.OracleDatabaseInfo;
import pl.mpak.orbada.oracle.util.OracleUtil;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.sky.gui.swing.AutoCompleteItem;
import pl.mpak.sky.gui.swing.ParametrizedAutoCompleteItem;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.CommandListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseListener;
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
public class OracleDbInfoProvider extends DatabaseInfoProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  public static OracleDbInfoProvider instance;
  
  private HashMap<String, DbDatabaseInfo> databaseInfoList;
  private HashMap<String, HashMap<String, String[]>> databaseStringList;
  private HashMap<String, HashMap<String, ArrayList<AutoCompleteItem>>> databaseSyntaxList;
  
  private long lastSchemaListTime;
  
  public OracleDbInfoProvider() {
    instance = this;
    databaseInfoList = new HashMap<String, DbDatabaseInfo>();
    databaseStringList = new HashMap<String, HashMap<String, String[]>>();
    databaseSyntaxList = new HashMap<String, HashMap<String, ArrayList<AutoCompleteItem>>>();
  }
  
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }
  
  public String getDescription() {
    return stringManager.getString("OracleDbInfoProvider-description");
  }

  private String getUniqueName(Database database) {
    return database.getUniqueID();
  }
  
  public static String getCurrentSchema(Database database) {
    return database.getUserProperties().getProperty("schema-name", database.getUserName().toUpperCase());
  }

  private String getWordId(Database database, String[] words) {
    String result = "";
    for (int i=0; i<words.length -1; i++) {
      if (result.length() > 0) {
        result = result +".";
      }
      result = result +words[i];
    }
    return "".equals(result) ? getCurrentSchema(database) : result;
  }

  public ArrayList<AutoCompleteItem> getAutoCompleteList(Database database, String[] words, boolean bracketMode, int commaCount) {
    synchronized (database) {
      HashMap<String, ArrayList<AutoCompleteItem>> map = databaseSyntaxList.get(getUniqueName(database));
      if (map == null) {
        map = new HashMap<String, ArrayList<AutoCompleteItem>>();
        databaseSyntaxList.put(getUniqueName(database), map);
      }
      ArrayList<AutoCompleteItem> items = map.get(getWordId(database, words));
      if (items == null) {
        items = prepareInfo(database, words);
        map.put(getWordId(database, words), items);
      }
      return items;
    }
  }

  private void prepareSchemaObjectsInfo(Database database, String schemaName, ArrayList<AutoCompleteItem> items) throws Exception {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.autocompleteSchemaObjects());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new AutoCompleteItem(
          query.fieldByName("OBJECT_NAME").getString(),
          query.fieldByName("OBJECT_TYPE").getString(),
          "<html>" +
          "<b>" +query.fieldByName("OBJECT_NAME").getString() +"</b>" +
          ParametrizedAutoCompleteItem.commentString(query.fieldByName("DESCRIPTION").getString())
        );
        item.setIcon(OracleUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
      query.setSqlText(Sql.autocompleteSchemaProcedures());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.open();
      while (!query.eof()) {
        ParametrizedAutoCompleteItem item = new ParametrizedAutoCompleteItem(query.fieldByName("OBJECT_NAME").getString(), query.fieldByName("OBJECT_TYPE").getString());
        while (!query.eof()) {
          if (query.fieldByName("ARGUMENT_NAME").isNull()) {
            item.setReturnDataType(query.fieldByName("DATA_TYPE").getString());
          }
          else {
            item.add(query.fieldByName("ARGUMENT_NAME").getString(), query.fieldByName("DATA_TYPE").getString());
          }
          query.next();
          if (!query.eof() && !query.fieldByName("OBJECT_NAME").getString().equals(item.getWord())) {
            break;
          }
        }
        item.setIcon(OracleUtil.getObjectIcon(item.getType()));
        items.add(item);
      }
      query.setSqlText(Sql.autocompleteSchemaProceduresNoParams());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.open();
      while (!query.eof()) {
        ParametrizedAutoCompleteItem item = new ParametrizedAutoCompleteItem(query.fieldByName("OBJECT_NAME").getString(), "PROCEDURE");
        item.setIcon(OracleUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
    }
    finally {
      query.close();
    }
  }

  private void prepareSchemaObjectPackageInfo(Database database, String schemaName, String objectName, ArrayList<AutoCompleteItem> items) throws Exception {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.autocompleteSchemaPackageProcedures());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        String result = null;
        ParametrizedAutoCompleteItem item = new ParametrizedAutoCompleteItem(query.fieldByName("OBJECT_NAME").getString(), query.fieldByName("OBJECT_TYPE").getString());
        String overload = query.fieldByName("OVERLOAD").getString();
        while (!query.eof()) {
          if (query.fieldByName("ARGUMENT_NAME").isNull()) {
            item.setReturnDataType(query.fieldByName("DATA_TYPE").getString());
          }
          else {
            item.add(query.fieldByName("ARGUMENT_NAME").getString(), query.fieldByName("DATA_TYPE").getString());
          }
          query.next();
          if (!query.eof() && 
              (!StringUtil.equals(query.fieldByName("OBJECT_NAME").getString(), item.getWord()) ||
               !StringUtil.equals(query.fieldByName("OVERLOAD").getString(), overload))) {
            break;
          }
        }
        item.setIcon(OracleUtil.getObjectIcon(item.getType()));
        items.add(item);
      }
      query.setSqlText(Sql.autocompleteSchemaPackageProceduresNoParams());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new ParametrizedAutoCompleteItem(query.fieldByName("OBJECT_NAME").getString(), "PROCEDURE");
        item.setIcon(OracleUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
    }
    finally {
      query.close();
    }
  }

  private void prepareSchemaObjectTypeInfo(Database database, String schemaName, String objectName, ArrayList<AutoCompleteItem> items) throws Exception {
    Query query = database.createQuery();
    try {
      StringBuffer sb = new StringBuffer();
      query.setSqlText(Sql.autocompleteSchemaTypeAttributes());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new AutoCompleteItem(query.fieldByName("OBJECT_NAME").getString(), "ATTRIBUTE", (String)null);
        sb.setLength(0);
        sb.append("<html>");
        sb.append("<b>" +item.getWord() +"</b>");
        sb.append(ParametrizedAutoCompleteItem.dataTypeString(query.fieldByName("DATA_TYPE").getString()));
        item.setDisplayText(sb.toString());
        item.setIcon(OracleUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
    }
    finally {
      query.close();
    }
  }

  private void prepareColumnsInfo(Database database, String schemaName, String objectName, ArrayList<AutoCompleteItem> items) throws Exception {
    Query query = database.createQuery();
    try {
      StringBuffer sb = new StringBuffer();
      query.setSqlText(Sql.autocompleteTableColumns());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("OBJECT_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new AutoCompleteItem(query.fieldByName("COLUMN_NAME").getString(), "COLUMN", (String)null);
        sb.setLength(0);
        sb.append("<html>");
        sb.append("<b>" +item.getWord() +"</b>");
        sb.append(ParametrizedAutoCompleteItem.dataTypeString(query.fieldByName("DISPLAY_TYPE").getString()));
        sb.append(ParametrizedAutoCompleteItem.commentString(query.fieldByName("COMMENTS").getString()));
        item.setReturnDataType(query.fieldByName("DATA_TYPE").getString());
        item.setDisplayText(sb.toString());
        item.setIcon(OracleUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
    }
    finally {
      query.close();
    }
  }

  private ArrayList<AutoCompleteItem> prepareInfo(Database database, String[] words) {
    Query resolver = database.createQuery();
    ArrayList<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
    try {
      resolver.setSqlText(Sql.resolveObject());
      String schemaName = null;
      String objectName = null;
      String objectType = null;
      if (words.length <= 1)  {
        schemaName = getCurrentSchema(database);
      }
      else {
        for (int i=0; i<words.length -1; i++) {
          resolver.close();
          resolver.paramByName("SCHEMA_NAME").setString(schemaName);
          resolver.paramByName("OBJECT_NAME").setString(words[i]);
          resolver.open();
          if (resolver.eof()) {
            schemaName = null;
            objectName = null;
            objectType = null;
            break;
          }
          if ("SCHEMA".equals(resolver.fieldByName("OBJECT_TYPE").getString())) {
            schemaName = resolver.fieldByName("OBJECT_NAME").getString();
          }
          else {
            schemaName = resolver.fieldByName("SCHEMA_NAME").getString();
            objectName = resolver.fieldByName("OBJECT_NAME").getString();
            objectType = resolver.fieldByName("OBJECT_TYPE").getString();
          }
        }
      }
      if (schemaName != null) {
        if (objectType == null) {
          prepareSchemaObjectsInfo(database, schemaName, items);
        }
        else if ("PACKAGE".equals(objectType)) {
          prepareSchemaObjectPackageInfo(database, schemaName, objectName, items);
        }
        else if ("TYPE".equals(objectType)) {
          prepareSchemaObjectPackageInfo(database, schemaName, objectName, items);
          prepareSchemaObjectTypeInfo(database, schemaName, objectName, items);
        }
        else if (StringUtil.anyOfString(objectType, new String[] {"TABLE", "VIEW", "MATERIALIZED VIEW"}) != -1) {
          prepareColumnsInfo(database, schemaName, objectName, items);
        }
        else if ("SEQUENCE".equals(objectType)) {
          items.add(new AutoCompleteItem("NEXTVAL", "FUNCTION", (String)null));
          items.add(new AutoCompleteItem("CURRVAL", "FUNCTION", (String)null));
        }
      }
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      resolver.close();
    }
    Collections.sort(items);
    return items;
  }
  
  public static String getBannerInfo(Database database) {
    Query query = database.createQuery();
    try {
      StringBuffer sb = new StringBuffer();
      query.open(Sql.getBannerList());
      while (!query.eof()) {
        if (sb.length() != 0) {
          sb.append('\n');
        }
        sb.append(query.fieldByName("banner").getString());
        query.next();
      }
      return sb.toString();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return "";
    }
    finally {
      query.close();
    }
  }
  
  public static String getVersionInfo(Database database) {
    Query query = database.createQuery();
    try {
      query.open(Sql.getBannerList());
      String banner = "";
      while (!query.eof()) {
        banner = query.fieldByName("banner").getString();
        if (banner.toUpperCase().startsWith("ORACLE")) {
          banner = banner.substring(0, banner.indexOf('-') -1).trim();
          banner = banner.substring(banner.lastIndexOf(' ') +1, banner.length()).trim();
          break;
        }
        query.next();
      }
      return banner;
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return "";
    }
    finally {
      query.close();
    }
  }
  
  public int getMajorVersion(Database database) {
    return new VersionID(database.getUserProperties().getProperty("version", "0")).getMajor();
  }
  
  public boolean isDebugClauseNeeded(Database database) {
    return 
      getMajorVersion(database) <= 9 && 
      "TRUE".equalsIgnoreCase(database.getUserProperties().getProperty("plsql_debug", "FALSE"));
  }
  
  public void resetDatabaseInfo(Database database) {
    databaseInfoList.remove(getUniqueName(database));
    databaseStringList.remove(getUniqueName(database));
    databaseSyntaxList.remove(getUniqueName(database));
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
      "ABORT", "ACCEPT", "ACCESS", "ADD", "AFTER", "ALTER", 
      "ARRAY", "ARRAYLEN", "ASC", "ASSERT", "ASSIGN", "AT", 
      "AUDIT", "AUTHORIZATION", "BASE_TABLE", "BEFORE", "BEGIN", 
      "BODY", "BY", "CACHE", "CASCADE", "CASE", "cast", 
      "CHAR_BASE", "CHARSET", "CHECK", "CLOSE", "CLUSTER", "CLUSTERS", 
      "COLAUTH", "COLUMN", "COMMENT", "COMMIT", "COMPRESS", "CONNECT", 
      "CONSTANT", "CONSTRAINT", "CRASH", "CREATE", "CURRENT", "CURRENT_USER", 
      "CURRVAL", "CURSOR", "CYCLE", "DATA_BASE", "DATABASE", "DBA", 
      "DEBUGOFF", "DEBUGON", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFINITION", 
      "DELAY", "DELETE", "DELETING", "DELTA", "DESC", "DIGITS", "DISABLE", 
      "DISPOSE", "DISTINCT", "DO", "DROP", "EACH", "ELSE", "ELSIF", "END", 
      "ENTRY", "ESCAPE", "EXCEPTION", "EXCEPTION_INIT", "EXCLUSIVE", 
      "EXIT", "FALSE", "FETCH", "FILE", "first", "FOR", "FOREIGN", 
      "FORM", "FROM", "FULL", "FUNCTION", "GENERIC", "GOTO", "GRANT", "GROUP", 
      "HAVING", "IDENTIFIED", "IF", "IMMEDIATE", "INCREMENT", "INDEX", 
      "INDEXES", "INDICATOR", "INITIAL", "INITIALLY", "INNER", "INSERT", 
      "INSERTING", "INTERFACE", "INTERSECT", "INTO", "join", "KEY", 
      "left", "LEVEL", "LIMITED", "LINK", "LOCK", "LOOP", 
      "materialized", "MAXEXTENTS", "MINUS", "MINVALUE", "MODE", "MODIFY", 
      "NATURAL", "NATURALN", "NEW", "NEXTVAL", "NOAUDIT", "NOCACHE", "NOCOMPRESS", 
      "NOCYCLE", "NOMAXVALUE", "NOORDER", "NOWAIT", "NULL", "nulls", 
      "NUMBER_BASE", "OF", "OFFLINE", "OLD", "ON", "ONLINE", "only", "OPEN", 
      "OPTION", "ORDER", "OUT", "outer", "PACKAGE", "PARTITION", 
      "PCTFREE", "PLS_INTEGER", "POSITIVE", "POSITIVEN", "PRAGMA", "PRECISION", 
      "PRIMARY", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURE", "PUBLIC", 
      "purge", "RAISE", "RANGE", "read", "REAL", "RECORD", "REF", "REFERENCES", 
      "REFERENCING", "RELEASE", "REMR", "RENAME", "REPLACE", "RESOURCE", 
      "RESTRICT_REFERENCES", "RETURN", "RETURNING", "REVERSE", "REVOKE", 
      "right", "ROLLBACK", "ROW", "ROWLABEL", "ROWNUM", "ROWS", "ROWTYPE", 
      "RUN", "SAVEPOINT", "SCHEMA", "SELECT", "SEPARATE", "SEQUENCE", 
      "SESSION", "SET", "SHARE", "SIZE", "SPACE", "SQL", "SQLCODE", 
      "SQLERRM", "START", "STATEMENT", "STDDEV", "SUBTYPE", "SUCCESSFUL", 
      "SYNONYM", "TABAUTH", "TABLE", "TABLES", "TABLESPACE", "TASK", 
      "TERMINATE", "THEN", "TO", "TRIGGER", "TRUE", "TRUNCATE", "TYPE", 
      "UNION", "UNIQUE", "UPDATE", "UPDATING", "USE", "USING", 
      "VALIDATE", "VALUES", "VIEW", "VIEWS", "WHEN", "WHENEVER", "WHERE", 
      "WHILE", "WITH", "WORK", "WRITE", "XOR", "PURGE", "BULK", "COLLECT",
      "EXECUTE", "ENABLE", "STORAGE", "INITRANS", "MAXTRANS", "COMPUTE",
      "STATISTICS", "NEXT", "MINEXTENTS", "PCTINCREASE", "FREELISTS", "FREELIST",
      "GROUPS", "PCTUSED", "LOGGING", "MOVEMENT", "AS", "AND", "OR",
      "CONSTRUCTOR", "MEMBER", "STATIC", "WAIT", "FORALL"
    };
    return list;
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

  private boolean collectSynonyms(Database database) {
    ISettings settings = application.getSettings(database.getUserProperties().getProperty("schemaId"), OracleSettingsProvider.settingsName);
    if (settings.getValue(OracleSettingsProvider.setUseGlobalSettings, true)) {
      settings = application.getSettings(OracleSettingsProvider.settingsName);
    }
    return settings.getValue(OracleSettingsProvider.setEditorHighlightSynonyms, false);
  }
  
  public String[] getUserTables(final Database database) {
    synchronized (database) {
      String[] array = getStrings(database, "USER_TABLES");
      if (array == null) {
        array = QueryUtil.queryToArray(database, Sql.getHighlightTableList());
        if (collectSynonyms(database)) {
          array = StringUtil.unionList(array, QueryUtil.queryToArray(database, Sql.getHighlightTableFromSynonymsList()));
        }
        putStrings(database, "USER_TABLES", array);
      }
      return array;
    }
  }
  
  public String[] getExceptions(Database database) {
    String[] list = {
      "ACCESS_INTO_NULL", "COLLECTION_IS_NULL", "CURSOR_ALREADY_OPEN",
      "DUP_VAL_ON_INDEX", "INVALID_CURSOR", "INVALID_NUMBER",
      "LOGIN_DENIED", "NO_DATA_FOUND", "NOT_LOGGED_ON",
      "OTHERS", "PROGRAM_ERRORROWTYPE_MISMATCH", "STORAGE_ERROR",
      "SUBSCRIPT_BEYOND_COUNT", "SUBSCRIPT_OUTSIDE_LIMIT", "TIMEOUT_ON_RESOURCE",
      "TOO_MANY_ROWS", "VALUE_ERROR", "ZERO_DIVIDE", "CASE_NOT_FOUND",
      "PROGRAM_ERROR", "ROWTYPE_MISMATCH", "SELF_IS_NULL", "SYS_INVALID_ROWID"
    };
    return list;
  }
  
  public String[] getSqlFunctions(Database database) {
    try {
      String[] list = {
        "ABS", "ACOS", "ASCII", "ASIN", "ATAN", "ATAN2", "AVG", "CEIL", "CHR", 
        "CONCAT", "COS", "COSH", "COUNT", "DECODE", "EXP", "FLOOR", "GREATEST", 
        "INITCAP", "INSTR", "INSTRB", "LEAST", "LENGTH", "LENGTHB", "LN", 
        "LOG", "LOWER", "LPAD", "LTRIM", "MAX", "MIN", "MOD", "NLS_INITCAP", 
        "NLS_LOWER", "NLS_UPPER", "NLSSORT", "NVL", "POWER", "REPLACE", 
        "ROUND", "RPAD", "RTRIM", "SIGN", "SIN", "SINH", "SOUNDEX", "SQRT", 
        "STDDEV", "SUBSTR", "SUBSTRB", "SUM", "SYSDATE", "TAN", "TANH", 
        "TO_CHAR", "TO_DATE", "TO_NUMBER", "TRANSLATE", "TRUNC", "UID", 
        "UPPER", "USER", "USERENV", "VARIANCE", "VSIZE",
        "ASCIISTR", "DECOMPOSE", "DUMP", "COMPOSE", "BIN_TO_NUM", "NUMTODSINTERVAL", "NUMTOYMINTERVAL", 
        "TO_DSINTERVAL", "TO_SINGLE_BYTE", "CHARTOROWID", "RAWTOHEX", "TO_LOB", "TO_TIMESTAMP", 
        "FROM_TZ", "TO_MULTI_BYTE", "TO_TIMESTAMP_TZ", "HEXTORAW", "TO_CLOB", "TO_NCLOB", "TO_YMINTERVAL", 
        "BFILENAME", "GROUP_ID", "NULLIF", "CARDINALITY", "LAG", "LEAD", "NVL2", "COALESCE", 
        "LNNVL", "SYS_CONTEXT", "NANVL", "COVAR_POP", "COVAR_SAMP", "MEDIAN", "CUME_DIST", "DENSE_RANK", 
        "RANK", "BIN_TO_NUM", "EXTRACT", "REMAINDER", "BITAND", "VAR_POP", "VAR_SAMP", "CORR", "ADD_MONTHS", 
        "LAST_DAY", "CURRENT_DATE", "LOCALTIMESTAMP", "SESSIONTIMEZONE", "CURRENT_TIMESTAMP", "MONTHS_BETWEEN", 
        "TZ_OFFSET", "DBTIMEZONE", "NEW_TIME", "SYSTIMESTAMP", "FROM_TZ", "NEXT_DAY"};
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
    synchronized (database) {
      String[] array = getStrings(database, "USER_FUNCTIONS");
      if (array == null) {
        array = QueryUtil.queryToArray(database, Sql.getHighlightUserFunctionList());
        if (collectSynonyms(database)) {
          array = StringUtil.unionList(array, QueryUtil.queryToArray(database, Sql.getHighlightUserFunctionFromSynonymsList()));
        }
        putStrings(database, "USER_FUNCTIONS", array);
      }
      return array;
    }
  }
  
  public String[] getPublicTables(final Database database) {
    synchronized (database) {
      String[] array = getStrings(database, "DICTIONARY");
      if (array == null) {
        array = putStrings(database, "DICTIONARY", QueryUtil.queryToArray(database, Sql.getHighlightPublicTableList()));
      }
      return array;
    }
  }
  
  public String[] getDataTypes(final Database database) {
    synchronized (database) {
      String[] list = getStrings(database, "TYPES");
      if (list == null) {
        list = new String[] {"BINARY_INTEGER", "VARCHAR", "BOOLEAN", "INTEGER", "VARRAY"};
        list = StringUtil.unionList(list, getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/DATA TYPES")));
        list = StringUtil.unionList(list, QueryUtil.queryToArray(database, Sql.getHighlightTypeList()));
        if (collectSynonyms(database)) {
          list = StringUtil.unionList(list, QueryUtil.queryToArray(database, Sql.getHighlightTypeFromSynonymsList()));
        }
        putStrings(database, "TYPES", list);
      }
      return list;
    }
  }
  
  public String[] getSchemas(final Database database) {
    String[] list = getStrings(database, "SCHEMAS");
    if (list == null || System.currentTimeMillis() -lastSchemaListTime > 1000) {
      list = QueryUtil.queryToArray(database, Sql.getFullSchemaList());
      lastSchemaListTime = System.currentTimeMillis();
      putStrings(database, "SCHEMAS", list);
      return list;
    }
    return list;
  }
  
  public String[] getTableTypes(final Database database) {
    String[] tableTypes = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TABLE TYPES"));
    return tableTypes;
  }
  
  private void addExtendedPanel(ArrayList<Component> list, String sqlText, Database database, String title) {
    try {
      QueryTablePanel panel = new QueryTablePanel(sqlText, database, title);
      list.add(panel);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public Component[] getExtendedPanelInfo(Database database) {
    ArrayList<Component> list = new ArrayList<Component>();
    addExtendedPanel(list, "select * from all_users order by username", database, "U¿ytkownicy");
    if ("true".equalsIgnoreCase(database.getUserProperties().getProperty("dba-role", "false"))) {
      addExtendedPanel(list, "select * from dba_tablespaces order by tablespace_name", database, "Przestrzenie");
    }
    else {
      addExtendedPanel(list, "select * from user_tablespaces order by tablespace_name", database, "Przestrzenie");
    }
    addExtendedPanel(list, "select * from dict order by table_name", database, "Tablice s³ownikowe");
    addExtendedPanel(list, "select * from NLS_SESSION_PARAMETERS order by parameter", database, "NLS Sesji");
    return list.toArray(new Component[list.size()]);
  }
  
  public DbDatabaseInfo getDatabaseInfo(final Database database) {
    synchronized (this) {
      DbDatabaseInfo info = databaseInfoList.get(getUniqueName(database));
      if (info == null) {
        databaseInfoList.put(getUniqueName(database), info = new OracleDatabaseInfo(database));
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
            databaseSyntaxList.remove(getUniqueName(database));
          }
        });
        database.addCommandListener(new CommandListener() {
          public void beforeExecute(EventObject e) {
          }
          public void afterExecute(EventObject e) {
            try {
              parseCommand((Command)e.getSource());
            }
            catch (Throwable ex) {
            }
          }
          public void errorPerformed(EventObject e) {
          }
        });
      }
      return info;
    }
  }

  @Override
  public String[] getUniqueIdentFields(Database database, String catalogName, String schemaName, String tableName) {
    ArrayList<String> result = new ArrayList<String>();
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getPrimaryKeyColumns());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("TABLE_NAME").setString(tableName);
      query.open();
      while (!query.eof()) {
        result.add(query.fieldByName("COLUMN_NAME").getString());
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    if (result.size() == 0) {
      result.add("ROWID");
    }
    return result.toArray(new String[result.size()]);
  }
  
  private void parseCommand(Command command) {
    StringTokenizer st = new StringTokenizer(command.getSqlText());
    String token;
    if (st.hasMoreTokens()) {
      token = st.nextToken();
      if ("ALTER".equalsIgnoreCase(token) && st.hasMoreTokens()) {
        token = st.nextToken();
        if (("SESSION".equalsIgnoreCase(token) || "SYSTEM".equalsIgnoreCase(token)) && st.hasMoreTokens()) {
          token = st.nextToken();
          if ("SET".equalsIgnoreCase(token) && st.hasMoreTokens()) {
            String paramName = st.nextToken().toLowerCase();
            st.nextToken(); // =
            if (paramName.equals("plsql_debug")) {
              String paramValue = st.nextToken().toUpperCase();
              command.getDatabase().getUserProperties().setProperty(paramName, paramValue);
            }
            else if (paramName.equals("current_schema")) {
              String paramValue = st.nextToken().toUpperCase();
              command.getDatabase().getUserProperties().setProperty("schema-name", paramValue);
            }
          }
        }
      }
      else if (("CREATE".equalsIgnoreCase(token) || "DROP".equalsIgnoreCase(token) || "ALTER".equalsIgnoreCase(token)) && st.hasMoreTokens()) {
        boolean refresh = true;
        token = st.nextToken();
        if ("OR".equalsIgnoreCase(token) && st.hasMoreTokens()) {
          token = st.nextToken();
          if ("REPLACE".equalsIgnoreCase(token) && st.hasMoreTokens()) {
            refresh = false;
          }
        }
        if (refresh) {
          try {
            getDatabaseInfo(command.getDatabase()).refresh();
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    }
  }
  
  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}
