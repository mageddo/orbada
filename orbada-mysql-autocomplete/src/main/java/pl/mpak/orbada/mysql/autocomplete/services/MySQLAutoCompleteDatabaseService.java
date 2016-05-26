package pl.mpak.orbada.mysql.autocomplete.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.autocomplete.OrbadaMySQLAutocompletePlugin;
import pl.mpak.orbada.mysql.services.MySQLDbInfoProvider;
import pl.mpak.orbada.mysql.util.MySQLUtil;
import pl.mpak.orbada.plugins.providers.DatabaseProvider;
import pl.mpak.sky.gui.swing.AutoCompleteItem;
import pl.mpak.sky.gui.swing.ParametrizedAutoCompleteItem;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class MySQLAutoCompleteDatabaseService extends DatabaseProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLAutocompletePlugin.class);

  public static MySQLAutoCompleteDatabaseService instance;
  private static HashMap<String, HashMap<String, ArrayList<AutoCompleteItem>>> databaseSyntaxList = new HashMap<String, HashMap<String, ArrayList<AutoCompleteItem>>>();

  private static String sqlProcedureList =
    "SELECT TRIM(P.RDB$PROCEDURE_NAME) OBJECT_NAME, A.RDB$PARAMETER_NUMBER SEQ, TRIM(A.RDB$PARAMETER_NAME) ARGUMENT,\n" +
    "       CASE WHEN A.RDB$PARAMETER_TYPE = 0 THEN 'IN' WHEN A.RDB$PARAMETER_TYPE = 1 THEN 'OUT' ELSE A.RDB$PARAMETER_TYPE END IN_OUT,\n" +
    "       (SELECT TRIM(RDB$TYPE_NAME) FROM RDB$TYPES T WHERE T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE' AND T.RDB$TYPE = F.RDB$FIELD_TYPE)||\n" +
    "       CASE\n" +
    "         WHEN F.RDB$FIELD_TYPE IN (7, 8, 2, 13, 35) THEN ''\n" +
    "         WHEN F.RDB$FIELD_TYPE IN (10, 11, 27) AND F.RDB$FIELD_SCALE <> 0 THEN '('||F.RDB$FIELD_LENGTH||','||F.RDB$FIELD_SCALE||')'\n" +
    "         ELSE '('||F.RDB$FIELD_LENGTH||')'\n" +
    "       END||\n" +
    "       COALESCE((SELECT ' ['||D.RDB$LOWER_BOUND||'..'||D.RDB$UPPER_BOUND||']' FROM RDB$FIELD_DIMENSIONS D WHERE D.RDB$FIELD_NAME = F.RDB$FIELD_NAME), '')||\n" +
    "       COALESCE(' ('||(SELECT TRIM(RDB$TYPE_NAME) FROM RDB$TYPES T WHERE T.RDB$FIELD_NAME = 'RDB$FIELD_SUB_TYPE' AND T.RDB$TYPE = F.RDB$FIELD_SUB_TYPE)||')', '') ARGUMENT_TYPE\n" +
    "  FROM RDB$PROCEDURES P, RDB$PROCEDURE_PARAMETERS A, RDB$FIELDS F\n" +
    " WHERE P.RDB$PROCEDURE_NAME = A.RDB$PROCEDURE_NAME\n" +
    "   AND A.RDB$FIELD_SOURCE = F.RDB$FIELD_NAME\n" +
    "   AND (P.RDB$SYSTEM_FLAG IS NULL OR P.RDB$SYSTEM_FLAG = 0)\n" +
    " ORDER BY OBJECT_NAME, SEQ";

  private static String sqlFunctionList =
    "SELECT TRIM(P.RDB$FUNCTION_NAME) OBJECT_NAME, F.RDB$ARGUMENT_POSITION SEQ, TRIM('P_'||F.RDB$ARGUMENT_POSITION) ARGUMENT,\n" +
    "       (SELECT TRIM(RDB$TYPE_NAME) FROM RDB$TYPES T WHERE T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE' AND T.RDB$TYPE = F.RDB$FIELD_TYPE)||\n" +
    "       CASE\n" +
    "         WHEN F.RDB$FIELD_TYPE IN (7, 8, 2, 13, 35) THEN ''\n" +
    "         WHEN F.RDB$FIELD_TYPE IN (10, 11, 27) AND F.RDB$FIELD_SCALE <> 0 THEN '('||F.RDB$FIELD_LENGTH||','||F.RDB$FIELD_SCALE||')'\n" +
    "         ELSE '('||F.RDB$FIELD_LENGTH||')'\n" +
    "       END||\n" +
    "       COALESCE(' ('||(SELECT TRIM(RDB$TYPE_NAME) FROM RDB$TYPES T WHERE T.RDB$FIELD_NAME = 'RDB$FIELD_SUB_TYPE' AND T.RDB$TYPE = F.RDB$FIELD_SUB_TYPE)||')', '') ARGUMENT_TYPE\n" +
    "  FROM RDB$FUNCTIONS P, RDB$FUNCTION_ARGUMENTS F\n" +
    " WHERE P.RDB$FUNCTION_NAME = F.RDB$FUNCTION_NAME\n" +
    "   AND (P.RDB$SYSTEM_FLAG IS NULL OR P.RDB$SYSTEM_FLAG = 0)\n" +
    "  ORDER BY OBJECT_NAME, SEQ";

  public MySQLAutoCompleteDatabaseService() {
    instance = this;
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public void afterConnection(Database database) {
  }

  private String getWordId(Database database, String[] words) {
    String result = "";
    for (int i=0; i<words.length -1; i++) {
      if (result.length() > 0) {
        result = result +".";
      }
      result = result +words[i];
    }
    return "".equals(result) ? "root" : result;
  }

  public ArrayList<AutoCompleteItem> getAutoCompleteList(Database database, String[] words, boolean bracketMode, int commaCount) {
    synchronized (database) {
      HashMap<String, ArrayList<AutoCompleteItem>> map = databaseSyntaxList.get(database.getUniqueID());
      if (map == null) {
        map = new HashMap<String, ArrayList<AutoCompleteItem>>();
        databaseSyntaxList.put(database.getUniqueID(), map);
      }
      ArrayList<AutoCompleteItem> items = map.get(getWordId(database, words));
      if (items == null) {
        items = prepareInfo(database, words);
        map.put(getWordId(database, words), items);
      }
      return items;
    }
  }

  private void prepareColumnsInfo(Database database, String databaseName, String objectName, ArrayList<AutoCompleteItem> items) throws Exception {
    Query query = database.createQuery();
    try {
      StringBuffer sb = new StringBuffer();
      query.setSqlText(Sql.getColumnList(null));
      query.paramByName("schema_name").setString(databaseName);
      query.paramByName("table_name").setString(objectName);
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new AutoCompleteItem(query.fieldByName("column_name").getString(), "COLUMN", (String)null);
        sb.setLength(0);
        sb.append("<html>");
        sb.append("<b>" +item.getWord() +"</b>");
        sb.append(ParametrizedAutoCompleteItem.dataTypeString(query.fieldByName("column_type").getString()));
        sb.append(ParametrizedAutoCompleteItem.commentString(query.fieldByName("column_comment").getString()));
        item.setReturnDataType(query.fieldByName("data_type").getString());
        item.setDisplayText(sb.toString());
        item.setIcon(MySQLUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
    }
    finally {
      query.close();
    }
  }

  private void prepareObjectsInfo(Database database, String databaseName, ArrayList<AutoCompleteItem> items) throws Exception {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getObjectsType("object_type not in ('PROCEDURE', 'FUNCTION', 'TRIGGER', 'INDEX')"));
      query.paramByName("schema_name").setString(databaseName);
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new AutoCompleteItem(
          query.fieldByName("OBJECT_NAME").getString(),
          query.fieldByName("OBJECT_TYPE").getString(),
          "<html>" +
          "<b>" +query.fieldByName("OBJECT_NAME").getString() +"</b>" +
          ParametrizedAutoCompleteItem.commentString(query.fieldByName("COMMENT").getString())
        );
        item.setIcon(MySQLUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
      query.setSqlText(Sql.getDatabaseRoutineParameterList());
      query.paramByName("schema_name").setString(databaseName);
      query.open();
      while (!query.eof()) {
        ParametrizedAutoCompleteItem item = new ParametrizedAutoCompleteItem(
          query.fieldByName("name").getString(),
          query.fieldByName("type").getString());
        while (!query.eof()) {
          if ("RETURNS".equals(query.fieldByName("param_type").getString())) {
            item.setReturnDataType(query.fieldByName("param").getString());
          }
          else {
            String paramName = "";
            String paramType = query.fieldByName("param").getString();
            if (paramType.indexOf(' ') > 0) {
              paramName = paramType.substring(0, paramType.indexOf(' '));
              paramType = paramType.substring(paramType.indexOf(' ')).trim();
            }
            item.add(paramName, paramType);
          }
          query.next();
          if (!query.eof() && !query.fieldByName("name").getString().equals(item.getWord())) {
            break;
          }
        }
        item.setIcon(MySQLUtil.getObjectIcon(item.getType()));
        items.add(item);
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
      resolver.setSqlText(Sql.getObjectsType());
      String databaseName = null;
      String objectName = null;
      String objectType = null;
      if (words.length <= 1) {
        databaseName = MySQLDbInfoProvider.getCurrentDatabase(database);
      }
      else {
        for (int i=0; i<words.length -1; i++) {
          resolver.close();
          resolver.paramByName("SCHEMA_NAME").setString(databaseName);
          resolver.paramByName("OBJECT_NAME").setString(words[i]);
          resolver.open();
          if (resolver.eof()) {
            databaseName = null;
            objectName = null;
            objectType = null;
            break;
          }
          if ("DATABASE".equals(resolver.fieldByName("OBJECT_TYPE").getString())) {
            databaseName = resolver.fieldByName("OBJECT_NAME").getString();
          }
          else {
            databaseName = resolver.fieldByName("OBJECT_SCHEMA").getString();
            objectName = resolver.fieldByName("OBJECT_NAME").getString();
            objectType = resolver.fieldByName("OBJECT_TYPE").getString();
          }
        }
      }
      if (databaseName != null) {
        if (objectType == null) {
          prepareObjectsInfo(database, databaseName, items);
        }
        else if (StringUtil.anyOfString(objectType, new String[] {"TABLE", "VIEW"}) != -1) {
          prepareColumnsInfo(database, databaseName, objectName, items);
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

  public void removeAutoCompleteInfo(Database database) {
    databaseSyntaxList.remove(database.getUniqueID());
  }

  @Override
  public void beforeDisconnect(Database database) {
    removeAutoCompleteInfo(database);
  }

  public String getDescription() {
    return stringManager.getString("MySQLAutoCompleteDatabaseService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }
}
