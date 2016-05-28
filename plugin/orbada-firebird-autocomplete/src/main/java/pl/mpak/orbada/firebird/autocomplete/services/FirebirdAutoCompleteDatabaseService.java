package pl.mpak.orbada.firebird.autocomplete.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.autocomplete.OrbadaFirebirdAutocompletePlugin;
import pl.mpak.orbada.firebird.util.FirebirdUtil;
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
public class FirebirdAutoCompleteDatabaseService extends DatabaseProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird-autocomplete");

  public static FirebirdAutoCompleteDatabaseService instance;
  private static HashMap<String, HashMap<String, ArrayList<AutoCompleteItem>>> databaseSyntaxList = new HashMap<String, HashMap<String, ArrayList<AutoCompleteItem>>>();

  private String getSqlSingleObjectList(boolean genDesc) {
    return
      "SELECT TRIM(R.RDB$RELATION_NAME) OBJECT_NAME, SUBSTRING(R.RDB$DESCRIPTION FROM 1 FOR 1000) DESCRIPTION,\n" +
      "       CASE WHEN R.RDB$VIEW_SOURCE IS NULL THEN 'TABLE' ELSE 'VIEW' END OBJECT_TYPE\n" +
      "  FROM RDB$RELATIONS R\n" +
      " WHERE (R.RDB$SYSTEM_FLAG IS NULL OR R.RDB$SYSTEM_FLAG = 0)\n" +
      "UNION ALL \n" +
      "SELECT TRIM(E.RDB$EXCEPTION_NAME) OBJECT_NAME, SUBSTRING(E.RDB$MESSAGE FROM 1 FOR 1000) DESCRIPTION,\n" +
      "       'EXCEPTION' OBJECT_TYPE\n" +
      "  FROM RDB$EXCEPTIONS E\n" +
      " WHERE (E.RDB$SYSTEM_FLAG IS NULL OR E.RDB$SYSTEM_FLAG = 0)\n" +
      "UNION ALL \n" +
      "SELECT TRIM(G.RDB$GENERATOR_NAME) OBJECT_NAME,\n" +
      "       " +(genDesc ? "SUBSTRING(G.RDB$DESCRIPTION FROM 1 FOR 1000)" : "CAST(NULL AS VARCHAR(100))") +" DESCRIPTION,\n" +
      "       'GENERATOR' OBJECT_TYPE\n" +
      "  FROM RDB$GENERATORS G\n" +
      " WHERE (G.RDB$SYSTEM_FLAG IS NULL OR G.RDB$SYSTEM_FLAG = 0)";
  }

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

  public FirebirdAutoCompleteDatabaseService() {
    instance = this;
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaFirebirdPlugin.firebirdDriverType.equals(database.getDriverType());
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

  private void prepareColumnsInfo(Database database, String objectName, ArrayList<AutoCompleteItem> items) throws Exception {
    Query query = database.createQuery();
    try {
      StringBuffer sb = new StringBuffer();
      query.setSqlText(Sql.getColumnList(null));
      query.paramByName("TABLE_NAME").setString(objectName);
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new AutoCompleteItem(query.fieldByName("FIELD_NAME").getString(), "COLUMN", (String)null);
        sb.setLength(0);
        sb.append("<html>");
        sb.append("<b>" +item.getWord() +"</b>");
        sb.append(ParametrizedAutoCompleteItem.dataTypeString(query.fieldByName("DISPLAY_TYPE").getString()));
        sb.append(ParametrizedAutoCompleteItem.commentString(query.fieldByName("DESCRIPTION").getString()));
        item.setReturnDataType(query.fieldByName("FIELD_TYPE").getString());
        item.setDisplayText(sb.toString());
        item.setIcon(FirebirdUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
    }
    finally {
      query.close();
    }
  }

  private void prepareObjectsInfo(Database database, ArrayList<AutoCompleteItem> items) throws Exception {
    boolean generatorDescription = StringUtil.toBoolean(database.getUserProperties().getProperty("generator-description", "false"));
    Query query = database.createQuery();
    try {
      query.setSqlText(getSqlSingleObjectList(generatorDescription));
      query.open();
      while (!query.eof()) {
        AutoCompleteItem item = new AutoCompleteItem(
          query.fieldByName("OBJECT_NAME").getString(),
          query.fieldByName("OBJECT_TYPE").getString(),
          "<html>" +
          "<b>" +query.fieldByName("OBJECT_NAME").getString() +"</b>" +
          ParametrizedAutoCompleteItem.commentString(query.fieldByName("DESCRIPTION").getString())
        );
        item.setIcon(FirebirdUtil.getObjectIcon(item.getType()));
        items.add(item);
        query.next();
      }
      query.setSqlText(sqlProcedureList);
      query.open();
      while (!query.eof()) {
        ParametrizedAutoCompleteItem item = new ParametrizedAutoCompleteItem(query.fieldByName("OBJECT_NAME").getString(), "PROCEDURE");
        while (!query.eof()) {
          if ("OUT".equals(query.fieldByName("IN_OUT").getString())) {
            String ret = item.getReturnDataType();
            if (ret != null) {
              ret = ret +", ";
            }
            ret = ret +query.fieldByName("ARGUMENT_TYPE").getString();
            item.setReturnDataType(ret);
          }
          else {
            item.add(query.fieldByName("ARGUMENT").getString(), query.fieldByName("ARGUMENT_TYPE").getString());
          }
          query.next();
          if (!query.eof() && !query.fieldByName("OBJECT_NAME").getString().equals(item.getWord())) {
            break;
          }
        }
        item.setIcon(FirebirdUtil.getObjectIcon(item.getType()));
        items.add(item);
      }
      query.setSqlText(sqlFunctionList);
      query.open();
      while (!query.eof()) {
        ParametrizedAutoCompleteItem item = new ParametrizedAutoCompleteItem(query.fieldByName("OBJECT_NAME").getString(), "FUNCTION");
        while (!query.eof()) {
          if (query.fieldByName("SEQ").getInteger() == 0) {
            item.setReturnDataType(query.fieldByName("ARGUMENT_TYPE").getString());
          }
          else {
            item.add(query.fieldByName("ARGUMENT").getString(), query.fieldByName("ARGUMENT_TYPE").getString());
          }
          query.next();
          if (!query.eof() && !query.fieldByName("OBJECT_NAME").getString().equals(item.getWord())) {
            break;
          }
        }
        item.setIcon(FirebirdUtil.getObjectIcon(item.getType()));
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
      resolver.setSqlText(
        "SELECT TRIM(R.RDB$RELATION_NAME) OBJECT_NAME, CASE WHEN R.RDB$VIEW_SOURCE IS NULL THEN 'TABLE' ELSE 'VIEW' END OBJECT_TYPE\n" +
        "  FROM RDB$RELATIONS R\n" +
        " WHERE R.RDB$RELATION_NAME = :OBJECT_NAME");
      String objectName = null;
      String objectType = null;
      if (words.length > 1)  {
        resolver.paramByName("OBJECT_NAME").setString(words[0].toUpperCase());
        resolver.open();
        if (!resolver.eof()) {
          objectName = resolver.fieldByName("OBJECT_NAME").getString();
          objectType = resolver.fieldByName("OBJECT_TYPE").getString();
        }
        if (StringUtil.anyOfString(objectType, new String[] {"TABLE", "VIEW"}) != -1) {
          prepareColumnsInfo(database, objectName, items);
        }
      }
      else {
        prepareObjectsInfo(database, items);
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
    return stringManager.getString("FirebirdAutoCompleteDatabaseService-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }
}
