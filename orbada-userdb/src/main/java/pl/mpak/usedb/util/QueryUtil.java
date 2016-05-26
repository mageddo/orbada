package pl.mpak.usedb.util;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.core.QueryFieldList;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

public class QueryUtil {
  
  public static interface Column {
    public final static int TITLE = 1; 
    public final static int FIELD_NAME = 2;
    public final static int DATA = 3;
    public final static int FIELD_TYPE = 4;
  }

  /**
   * Pobiera wiersz z otwartego query i tworzy z niego w zale¿noœci od parametru 
   * columns listê wierszy w uk³adzie pionowym czyli
   * [[DATA,TITLE,FIELD_NAME,FIELD_TYPE],[DATA,TITLE,FIELD_NAME,FIELD_TYPE],[...]]
   * Query musi byæ ustawione na odpowiednim wierszu
   * 
   * @param query otwarty obiekt Query
   * @param columns kolumny które maj¹ byæ umieszczone na liœcie
   * @return lista Vector&lt;Vector&gt;
   */
  public static Vector<Vector<String>> rotateRow(Query query, int[] columns) {
    Vector<Vector<String>> list = new Vector<Vector<String>>();
    
    QueryFieldList tfl = query.getFieldList();
    for (int i=0; i<tfl.getFieldCount(); i++) {
      Vector<String> line = new Vector<String>();
      for (int c=0; c<columns.length; c++) {
        switch (columns[c]) {
          case Column.DATA:
            try {
              if (!query.eof()) {
                line.add(tfl.getField(i).getString());
              }
              else {
                line.add("");
              }
            }
            catch (Exception e) {
              line.add(e.getMessage());
            }
            break;
          case Column.TITLE:
            line.add(tfl.getField(i).getDisplayName());
            break;
          case Column.FIELD_NAME:
            line.add(tfl.getField(i).getFieldName());
            break;
          case Column.FIELD_TYPE:
            line.add(tfl.getField(i).getDataTypeName());
            break;
        }
      }
      list.add(line);
    }
    
    return list;
  }
  
  /**
   * <p>Pobiera wiersz z otwartego query i tworzy z niego w zale¿noœci od parametru 
   * columns listê wierszy w uk³adzie pionowym czyli
   * [[TITLE,DATA],[TITLE,DATA],[...]]
   * <p>TITLE bêdzie do³aczone tylko w pierszej kolumnie wynikowej
   * 
   * @param query otwarty obiekt Query
   * @param columns kolumny które maj¹ byæ umieszczone na liœcie
   * @return lista Vector&lt;Vector&gt;
   * @throws Exception 
   */
  public static Vector<Vector<String>> rotateRows(Query query, int[] columns) throws Exception {
    Vector<Vector<String>> list = new Vector<Vector<String>>();
    
    QueryFieldList tfl = query.getFieldList();
    for (int i=0; i<tfl.getFieldCount(); i++) {
      list.add(new Vector<String>());
    }
    boolean firstTime = true;
    while (!query.eof()) {
      for (int i=0; i<tfl.getFieldCount(); i++) {
        Vector<String> line = list.get(i);
        for (int c=0; c<columns.length; c++) {
          switch (columns[c]) {
            case Column.DATA:
              try {
                if (!query.eof()) {
                  line.add(tfl.getField(i).getString());
                }
                else {
                  line.add("");
                }
              }
              catch (Exception e) {
                line.add(e.getMessage());
              }
              break;
            case Column.TITLE:
              if (firstTime) {
                line.add(tfl.getField(i).getDisplayName());
              }
              break;
          }
        }
      }
      firstTime = false;
      query.next();
    }
    
    return list;
  }
  
  /**
   * Na podstawie danych z query tworzy statyczn¹ listê kolumn i wierszy
   * 
   * @param query otwarte query
   * @param title czy maj¹ byæ do listy do³aczone tytu³y kolumn
   * @return lista Vector&lt;Vector&gt;
   * @throws Exception
   */
  public static Vector<Vector<String>> staticData(Query query) throws Exception {
    Vector<Vector<String>> list = new Vector<Vector<String>>();

    QueryFieldList tfl = query.getFieldList();

    while (!query.eof()) {
      Vector<String> line = new Vector<String>();
      for (int i=0; i<tfl.getFieldCount(); i++) {
        line.add(tfl.getField(i).getString());
      }
      list.add(line);
      query.next();
    }
    
    return list;
  }
  
  /**
   * <p>Na podstawie danych z query tworzy statyczn¹ listê kolumn i wierszy
   * 
   * @param format 
   * @see QueryUtil.format
   * @param query
   * @return
   * @throws Exception
   */
  public static Vector<String> staticData(String format, Query query) throws Exception {
    Vector<String> list = new Vector<String>();

    while (!query.eof()) {
      list.add(format(format, query));
      query.next();
    }
    
    return list;
  }
  
  public static ArrayList<String> staticValues(String format, Query query) throws Exception {
    ArrayList<String> list = new ArrayList<String>();

    while (!query.eof()) {
      list.add(format(format, query));
      query.next();
    }
    
    return list;
  }
  
  /**
   * Tworzy listê nazw pobranych z query
   * 
   * @param query
   * @return
   */
  public static Vector<String> staticTitle(Query query) {
    Vector<String> line = new Vector<String>();
    QueryFieldList tfl = query.getFieldList();

    for (int i=0; i<tfl.getFieldCount(); i++) {
      line.add(tfl.getField(i).getDisplayName());
    }
    
    return line;
  }
  
  public static ArrayList<String> staticValues(Query query) {
    ArrayList<String> line = new ArrayList<String>();
    QueryFieldList tfl = query.getFieldList();

    for (int i=0; i<tfl.getFieldCount(); i++) {
      line.add(tfl.getField(i).getDisplayName());
    }
    
    return line;
  }
  
  /**
   * Na razie nic nie robi
   * @param sqlText
   * @return
   */
  public static String removeNotUserChars(String sqlText) {
    return sqlText;
  }
  
  /**
   * <p>Funkcja pozwala pobraæ dane z aktualnego rekordu Query i wstawiæ je do ci¹g znaków.
   * @param format formatuj¹cy ci¹g znaków, dostêp do pól w formatowaniu odbywa siê 
   * poprzez ich wyspecyfikowanie zgodnie z {NAZWA_POLA}
   * @param query
   * @return
   */
  public static String format(String format, Query query) {
    if (query.isActive()) {
      if (format != null) {
        StringBuilder fieldName = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < format.length()) {
          char ch = format.charAt(i); 
          if (ch == '{') {
            if (i == 0 || format.charAt(i -1) != '\\') {
              i++;
              ch = format.charAt(i);
              while (i < format.length() && (ch = format.charAt(i)) != '}') {
                fieldName.append(ch);
                i++;
              }
              QueryField qf = query.findFieldByName(fieldName.toString());
              if (qf != null) {
                try {
                  sb.append(qf.getString());
                } catch (Exception e) {
                  ExceptionUtil.processException(e);
                }
              }
              fieldName.setLength(0);
              i++;
            }
            else {
              sb.append(ch);
              i++;
            }
          }
          else {
            sb.append(ch);
            i++;
          }
        }
        return sb.toString();
      }
      else {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<query.getFieldList().getFieldCount(); i++) {
          if (sb.length() > 0) {
            sb.append(' ');
          }
          try {
            sb.append(query.getFieldList().getField(i).getString());
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        return sb.toString();
      }
    }
    return null;
  }

  public static String[] queryToArray(Database database, String sqlText) {
    return queryToArray(database, null, sqlText);
  }
  
  public static String[] queryToArray(Database database, String format, String sqlText) {
    Query query = database.createQuery();
    try {
      return queryToArray(format, query.open(sqlText));
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
    finally {
      query.close();
    }
  }
  
  public static String[] queryToArray(String format, Query query) {
    if (query.isActive()) {
      ArrayList<String> list = new ArrayList<String>();
      try {
        while (!query.eof()) {
          list.add(format(format, query));
          query.next();
        }
      }
      catch(Exception e) {
        ExceptionUtil.processException(e);
      }
      return list.toArray(new String[list.size()]);
    }
    return null;
  }
  
  public static String[] queryToArray(Query query) {
    return queryToArray(null, query);
  }
  
  /**
   * <p>Tworzy polecenie CREATE TABLE na podstawie kolumn z otwartego Query
   * @param query
   * @param tableName
   * @return
   * @throws SQLException 
   */
  public static String queryToCreateTable(Query query, Database destDatabase, String tableName, String quotedChar) throws Exception {
    destDatabase = destDatabase == null ? query.getDatabase() : destDatabase;
    StringBuilder sb = new StringBuilder(); 
    HashMap<Integer, String> typeMap = new HashMap<Integer, String>();
    Query queryTypes = destDatabase.createQuery();
    try {
      queryTypes.setResultSet(destDatabase.getMetaData().getTypeInfo());
      while (!queryTypes.eof()) {
        if (typeMap.get(queryTypes.fieldByName("data_type").getInteger()) == null) {
          String params = null;
          try {
            params = queryTypes.fieldByName("create_params").getString();
          }
          catch (Throwable ex) {
            params = queryTypes.fieldByName("params").getString();
          }
          if (!StringUtil.isEmpty(params)) {
            if (params.charAt(0) == '(' && params.charAt(params.length() -1) == ')') {
              params = params.substring(1, params.length() -1);
            }
            params = "(" +params.toUpperCase() +")";
          }
          else if (StringUtil.anyOfString(queryTypes.fieldByName("type_name").getString(), new String[] {"VARCHAR", "VARCHAR2", "CHAR", "CHARACTER", "NVARCHAR"}, true) != -1) {
            params = "(LENGTH)";
          }
          else {
            params = "";
          }
          typeMap.put(queryTypes.fieldByName("data_type").getInteger(), queryTypes.fieldByName("type_name").getString() +params);
        }
        queryTypes.next();
      }
    } finally {
      queryTypes.close();
    }
    for (int i=0; i<query.getFieldCount(); i++) {
      QueryField field = query.getField(i);
      if (sb.length() > 0) {
        sb.append(",\n  ");
      }
      String type = typeMap.get(field.getDataType());
      if (type == null) {
        if (field.getDataType() == Types.VARCHAR) {
          type = "CHAR(" +field.getDisplaySize() +")";
        }
        else {
          type = field.getDataTypeName();
        }
      }
      else {
        type = StringUtil.replaceString(type, "(M)", "(" +field.getDisplaySize() +")");
        type = StringUtil.replaceString(type, "LENGTH", field.getDisplaySize());
        type = StringUtil.replaceString(type, "PRECISION", field.getDisplaySize());
        type = StringUtil.replaceString(type, "SCALE", field.getScale());
      }
      sb.append(quotedChar +field.getFieldName() +quotedChar +" " +type);
      if (!field.isNullable()) {
        sb.append(" NOT NULL");
      }
    }
    return "CREATE TABLE " +quotedChar +tableName +quotedChar +" (\n  " +sb.toString() +")";
  }
  
}
