package pl.mpak.usedb.util;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.StringTokenizer;

import pl.mpak.usedb.core.Database;
import pl.mpak.util.HtmlUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.array.StringList;
import pl.mpak.util.variant.VariantType;

public class SQLUtil {
  
  public final static String sqlNameCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz$#_";
  
  public static class SQLColumnType {
    public int type;
    public String name;
    public int variantType;
    public SQLColumnType(int type, String name, int variantType) {
      this.type = type;
      this.name = name;
      this.variantType = variantType;
    }
  }
  
  private static SQLColumnType[] sqlColumnTypes;
  
  public static class SQLParameterMode {
    public int mode;
    public String name;
    public SQLParameterMode(int mode, String name) {
      this.mode = mode;
      this.name = name;
    }
  }
    
  private static SQLParameterMode[] sqlParameterModes;

  static {
    sqlColumnTypes = new SQLColumnType[] {
        new SQLColumnType(Types.TINYINT, "TINYINT", VariantType.varShort),
        new SQLColumnType(Types.SMALLINT, "SMALLINT", VariantType.varShort),
        new SQLColumnType(Types.INTEGER, "INTEGER", VariantType.varInteger),
        new SQLColumnType(Types.BIGINT, "BIGINT", VariantType.varLong),
        new SQLColumnType(Types.BIGINT, "BIGINT", VariantType.varBigInteger),
        new SQLColumnType(Types.FLOAT, "FLOAT", VariantType.varFloat),
        new SQLColumnType(Types.REAL, "REAL", VariantType.varFloat),
        new SQLColumnType(Types.DOUBLE, "DOUBLE", VariantType.varDouble),
        new SQLColumnType(Types.NUMERIC, "NUMERIC", VariantType.varBigDecimal),
        new SQLColumnType(Types.DECIMAL, "DECIMAL", VariantType.varBigDecimal),
        new SQLColumnType(Types.VARCHAR, "VARCHAR", VariantType.varString),
        new SQLColumnType(Types.CHAR, "CHAR", VariantType.varString),
        new SQLColumnType(Types.NVARCHAR, "NVARCHAR", VariantType.varString),
        new SQLColumnType(Types.NCHAR, "NCHAR", VariantType.varString),
        new SQLColumnType(Types.LONGVARCHAR, "LONGVARCHAR", VariantType.varString),
        new SQLColumnType(Types.DATE, "DATE", VariantType.varDate),
        new SQLColumnType(Types.TIME, "TIME", VariantType.varTime),
        new SQLColumnType(Types.TIMESTAMP, "TIMESTAMP", VariantType.varTimestamp),
        new SQLColumnType(Types.BINARY, "BINARY", VariantType.varBinary),
        new SQLColumnType(Types.VARBINARY, "VARBINARY", VariantType.varBinary),
        new SQLColumnType(Types.LONGVARBINARY, "LONGVARBINARY", VariantType.varBinary),
        new SQLColumnType(Types.NULL, "NULL", VariantType.varNull),
        new SQLColumnType(Types.OTHER, "OTHER", VariantType.varJavaObject),
        new SQLColumnType(Types.JAVA_OBJECT, "JAVA_OBJECT", VariantType.varJavaObject),
        new SQLColumnType(Types.DISTINCT, "DISTINCT", VariantType.varNull),
        new SQLColumnType(Types.STRUCT, "STRUCT", VariantType.varVariant),
        new SQLColumnType(Types.ARRAY, "ARRAY", VariantType.varList),
        new SQLColumnType(Types.BLOB, "BLOB", VariantType.varBinary),
        new SQLColumnType(Types.CLOB, "CLOB", VariantType.varString),
        new SQLColumnType(Types.REF, "REF", VariantType.varJavaObject),
        new SQLColumnType(Types.DATALINK, "DATALINK", VariantType.varBinary),
        new SQLColumnType(Types.BOOLEAN, "BOOLEAN", VariantType.varBoolean),
        new SQLColumnType(Types.BIT, "BIT", VariantType.varBoolean)
    };
    sqlParameterModes = new SQLParameterMode[] {
        new SQLParameterMode(ParameterMetaData.parameterModeUnknown, "UNKNOWN"),
        new SQLParameterMode(ParameterMetaData.parameterModeIn, "IN"),
        new SQLParameterMode(ParameterMetaData.parameterModeInOut, "IN/OUT"),
        new SQLParameterMode(ParameterMetaData.parameterModeOut, "OUT")
    };
  }

  public static String typeToString(int type) {
    for (int i=0; i<sqlColumnTypes.length; i++) {
      if (sqlColumnTypes[i].type == type) {
        return sqlColumnTypes[i].name;
      }
    }
    return (Integer.valueOf(type)).toString(); 
  }
  
  public static String[] typeToString(int[] types) {
    String[] result = new String[types.length];
    for (int i=0; i<types.length; i++) {
      result[i] = typeToString(types[i]);
    }
    return result; 
  }
  
  public static int stringToType(String name) {
    if (name == null) {
      return Types.NULL;
    }
    for (int i=0; i<sqlColumnTypes.length; i++) {
      if (sqlColumnTypes[i].name.equalsIgnoreCase(name)) {
        return sqlColumnTypes[i].type;
      }
    }
    return Integer.parseInt(name); 
  }
  
  public static int variantToSqlType(int variantType) {
    for (int i=0; i<sqlColumnTypes.length; i++) {
      if (sqlColumnTypes[i].variantType == variantType) {
        return sqlColumnTypes[i].type;
      }
    }
    return Types.NULL;
  }
  
  public static int sqlTypeToVariant(int sqlType) {
    for (int i=0; i<sqlColumnTypes.length; i++) {
      if (sqlColumnTypes[i].type == sqlType) {
        return sqlColumnTypes[i].variantType;
      }
    }
    return VariantType.varNull;
  }
  
  public static String paramModeToString(int paramMode) {
    for (int i=0; i<sqlParameterModes.length; i++) {
      if (sqlParameterModes[i].mode == paramMode) {
        return sqlParameterModes[i].name;
      }
    }
    return (Integer.valueOf(paramMode)).toString(); 
  }
  
  public static int stringToParamMode(String name) {
    if (name == null) {
      return ParameterMetaData.parameterModeUnknown;
    }
    for (int i=0; i<sqlParameterModes.length; i++) {
      if (sqlParameterModes[i].name.equalsIgnoreCase(name)) {
        return sqlParameterModes[i].mode;
      }
    }
    return Integer.parseInt(name);
  }
  
  public static String[] paramModeToString(int[] paramModes) {
    String[] result = new String[paramModes.length];
    for (int i=0; i<paramModes.length; i++) {
      result[i] = paramModeToString(paramModes[i]);
    }
    return result; 
  }
  
  public static String removeComments(String text) {
    StringBuilder sb = new StringBuilder();
    
    int i = 0;
    while (i<text.length()) {
      if (text.charAt(i) == '/' && i +1 < text.length() && text.charAt(i +1) == '*') {
        i+=2;
        while (i<text.length()) {
          if (text.charAt(i) == '*' && i +1 < text.length() && text.charAt(i +1) == '/') {
            i+=2;
            break;
          }
          i++;
        }
      }
      else {
        sb.append(text.charAt(i));
        i++;
      }
    }
    
    return sb.toString();
  }
  
  
  public static String removeLineComment(String text) {
    StringBuilder sb = new StringBuilder();
    
    int i = 0;
    while (i<text.length()) {
      if (text.charAt(i) == '"' || text.charAt(i) == '\'' || text.charAt(i) == '`') {
        char ch = text.charAt(i);
        sb.append(ch);
        i++;
        while (i<text.length()) {
          sb.append(text.charAt(i));
          if (text.charAt(i) == ch) {
            if (i +1 >= text.length() || text.charAt(i +1) != ch) {
              i++;
              break;
            }
          }
          i++;
        }
      }
      else if (text.charAt(i) == '/' && i +1 < text.length() && text.charAt(i +1) == '*') {
        sb.append("/*");
        i+=2;
        while (i<text.length()) {
          if (text.charAt(i) == '*' && i +1 < text.length() && text.charAt(i +1) == '/') {
            sb.append("*/");
            i+=2;
            break;
          }
          else {
            sb.append(text.charAt(i));
          }
          i++;
        }
      }
      else if (text.charAt(i) == '-' && i +1 < text.length() && text.charAt(i +1) == '-') {
        i+=2;
        while (i<text.length()) {
          if (text.charAt(i) == '\n') {
            i++;
            break;
          }
          i++;
        }
      }
      else {
        sb.append(text.charAt(i));
        i++;
      }
    }
    
    return sb.toString();
  }
  
  public static String removeStrings(String text) {
    StringBuilder sb = new StringBuilder();
    
    int i = 0;
    while (i<text.length()) {
      if (text.charAt(i) == '"' || text.charAt(i) == '\'' || text.charAt(i) == '`') {
        char ch = text.charAt(i);
        i++;
        while (i<text.length()) {
          if (text.charAt(i) == ch) {
            i++;
            if (i >= text.length() || text.charAt(i) != ch) {
              break;
            }
          }
          i++;
        }
      }
      else {
        sb.append(text.charAt(i));
        i++;
      }
    }
    
    return sb.toString();
  }
  
  public static String removeWhiteSpaces(String text) {
    StringBuilder sb = new StringBuilder();
    
    int i = 0;
    while (i<text.length()) {
      if (text.charAt(i) == '"' || text.charAt(i) == '\'' || text.charAt(i) == '`') {
        char ch = text.charAt(i);
        sb.append(ch);
        i++;
        while (i<text.length()) {
          sb.append(text.charAt(i));
          if (text.charAt(i) == ch) {
            if (i +1 >= text.length() || text.charAt(i +1) != ch) {
              break;
            }
          }
          i++;
        }
      }
      else if (text.charAt(i) == '-' && i +1 < text.length() && text.charAt(i +1) == '-') {
        sb.append("--");
        i+=2;
        while (i<text.length()) {
          sb.append(text.charAt(i));
          if (text.charAt(i) == '\n') {
            break;
          }
          i++;
        }
      }
      else if (text.charAt(i) == '/' && i +1 < text.length() && text.charAt(i +1) == '*') {
        sb.append("/*");
        i+=2;
        while (i<text.length()) {
          if (text.charAt(i) == '*' && i +1 < text.length() && text.charAt(i +1) == '/') {
            sb.append("*/ ");
            i++;
            break;
          }
          else {
            sb.append(text.charAt(i));
          }
          i++;
        }
      }
      else if (text.charAt(i) <= 32) {
        if (sb.length() > 0 && sb.charAt(sb.length() -1) != ' ' && sb.charAt(sb.length() -1) != '\n') {
          sb.append(' ');
        }
      }
      else {
        sb.append(text.charAt(i));
      }
      i++;
    }
    
    return sb.toString();
  }
  
  /**
   * <p>Zwraca informacjê czy polecenie jest poleceniem SELECT czy innym
   * @param text
   * @return
   */
  public static boolean isSelect(String sqlText) {
    sqlText = removeComments(sqlText);
    sqlText = removeLineComment(sqlText);
    sqlText = removeWhiteSpaces(sqlText).trim();
    sqlText = removeStrings(sqlText).trim();
    int rp = 0;
    while (rp < sqlText.length() && sqlText.charAt(rp) == '(') {
      rp++;
    }
    if (rp > 0) {
      sqlText = sqlText.substring(rp);
    }
    if (sqlText.length() >= 6 && "SELECT".equalsIgnoreCase(sqlText.substring(0, 6))) {
      return true;
    }
    if (sqlText.length() >= 4 && "SHOW".equalsIgnoreCase(sqlText.substring(0, 4))) {
      return true;
    }
    if (sqlText.length() >= 8 && "DESCRIBE".equalsIgnoreCase(sqlText.substring(0, 8))) {
      return true;
    }
    if (sqlText.length() >= 7 && "EXECUTE".equalsIgnoreCase(sqlText.substring(0, 7))) {
      return true;
    }
//    if (sqlText.length() >= 6 && "PRAGMA".equalsIgnoreCase(sqlText.substring(0, 6)) && sqlText.indexOf('=') == -1) {
//      return true;
//    }
    if (sqlText.length() >= 4 && "WITH".equalsIgnoreCase(sqlText.substring(0, 4)) && 
        sqlText.indexOf("UPDATE") == -1 &&
        sqlText.indexOf("DELETE") == -1 &&
        sqlText.indexOf("INSERT") == -1) {
      return true;
    }
    return false;
  }
  
  /**
   * <p>Zwraca informacjê czy polecenie jest poleceniem SELECT czy innym
   * @param text
   * @return
   */
  public static boolean isDml(String sqlText) {
    sqlText = removeComments(sqlText);
    sqlText = removeLineComment(sqlText);
    sqlText = removeWhiteSpaces(sqlText).trim();
    int rp = 0;
    while (rp < sqlText.length() && sqlText.charAt(rp) == '(') {
      rp++;
    }
    if (rp > 0) {
      sqlText = sqlText.substring(rp);
    }
    if (sqlText.length() >= 6 && "UPDATE".equalsIgnoreCase(sqlText.substring(0, 6))) {
      return true;
    }
    if (sqlText.length() >= 6 && "INSERT".equalsIgnoreCase(sqlText.substring(0, 6))) {
      return true;
    }
    if (sqlText.length() >= 6 && "DELETE".equalsIgnoreCase(sqlText.substring(0, 6))) {
      return true;
    }
    if (sqlText.length() >= 4 && "WITH".equalsIgnoreCase(sqlText.substring(0, 4)) && 
        (sqlText.indexOf("UPDATE") >= 0 || sqlText.indexOf("DELETE") >= 0 || sqlText.indexOf("INSERT") >= 0)) {
      return true;
    }
    return false;
  }
  
  /**
   * <p>Sprawdza czy jest potrzebne umieszczenie nazwy w cudzyms³owiu
   * i jesli tak to zwraca tak¹ w³aœnie nazwê
   * @param name
   * @return
   */
  public static String createSqlNameQ(String name, String quoter) {
    if (name == null) {
      return null;
    }
    boolean normal = true;
    for (int i=0; i<name.length(); i++) {
      if (sqlNameCharacters.indexOf(name.charAt(i)) == -1) {
        normal = false;
        break;
      }
    }
    if (name.equals(name.toUpperCase()) && normal) {
      return name;
    }
    if (quoter != null) {
      quoter = quoter.trim();
    }
    return StringUtil.evl(quoter, "\"") +name +StringUtil.evl(quoter, "\"");
  }
  
  public static String createSqlName(String name) {
    return createSqlNameQ(name, null);
  }
  
  public static String createSqlNameQ(String schemaName, String objectName, String quoter) {
    String result = "";
    if (!StringUtil.isEmpty(schemaName)) {
      result = result +createSqlNameQ(schemaName, quoter) +".";
    }
    return result +createSqlNameQ(objectName, quoter);
  }
  
  public static String createSqlName(String schemaName, String objectName) {
    return createSqlNameQ(schemaName, objectName, null);
  }
  
  public static String createSqlNameQ(String catalogName, String schemaName, String objectName, String quoter) {
    String result = "";
    if (!StringUtil.isEmpty(catalogName)) {
      result = result +createSqlNameQ(catalogName, quoter) +".";
    }
    if (!StringUtil.isEmpty(schemaName)) {
      result = result +createSqlNameQ(schemaName, quoter) +".";
    }
    return result +createSqlNameQ(objectName, quoter);
  }
  
  public static String createSqlName(String catalogName, String schemaName, String objectName) {
    return createSqlNameQ(catalogName, schemaName, objectName, null);
  }
  
  public static String createSqlNameQ(String catalogName, String schemaName, String tableName, String columnName, String quoter) {
    String result = "";
    if (!StringUtil.isEmpty(catalogName)) {
      result = result +createSqlNameQ(catalogName, quoter) +".";
    }
    if (!StringUtil.isEmpty(schemaName)) {
      result = result +createSqlNameQ(schemaName, quoter) +".";
    }
    if (!StringUtil.isEmpty(tableName)) {
      result = result +createSqlNameQ(tableName, quoter) +".";
    }
    return result +createSqlNameQ(columnName, quoter);
  }
  
  public static String createSqlName(String catalogName, String schemaName, String tableName, String columnName) {
    return createSqlNameQ(catalogName, schemaName, tableName, columnName, null);
  }
  
  public static String createSqlName(String objectName, Database database) {
    String iqs = null;
    try {
      iqs = database.getMetaData().getIdentifierQuoteString();
    } catch (SQLException e) {
    }
    return createSqlNameQ(objectName, iqs);
  }
  
  public static String createSqlName(String schemaName, String objectName, Database database) {
    String iqs = null;
    try {
      iqs = database.getMetaData().getIdentifierQuoteString();
    } catch (SQLException e) {
    }
    String result = "";
    if (StringUtil.isEmpty(schemaName)) {
      schemaName = database.getUserName().toUpperCase();
    }
    if (!schemaName.equals(database.getUserName().toUpperCase())) {
      result = result +createSqlNameQ(schemaName, iqs) +".";
    }
    return result +createSqlNameQ(objectName, iqs);
  }
  
  public static String createSqlName(String catalogName, String schemaName, String objectName, Database database) {
    String iqs = null;
    try {
      iqs = database.getMetaData().getIdentifierQuoteString();
    } catch (SQLException e) {
    }
    String result = "";
    if (!StringUtil.isEmpty(catalogName)) {
      result = result +createSqlNameQ(catalogName, iqs) +".";
    }
    return result +createSqlName(schemaName, objectName, database);
  }
  
  /**
   * <p>Usuwa z nazwy cudzys³ów oraz jeœli go nie ma to wywo³uje toUpperCase
   * @param name
   * @return
   */
  public static String normalizeSqlName(String name) {
    if (name == null) {
      return null;
    }
    name = name.trim();
    if (name.length() > 2 && name.charAt(0) == '"' && name.charAt(name.length() -1) == '"') {
      name = name.substring(1, name.length() -1);
    }
    else {
      name = name.toUpperCase();
    }
    return name;
  }
  
  public static String paramNameFromColumnName(String columnName) {
    if (columnName == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<columnName.length(); i++) {
      if (sqlNameCharacters.indexOf(columnName.charAt(i)) != -1) {
        sb.append(columnName.charAt(i));
      }
    }
    return sb.toString();
  }
  
  public static SqlTableName parseSqlTableName(String fullName) {
    StringTokenizer st = new StringTokenizer(fullName, ".");
    String catalogName = null;
    String schemaName = null;
    String tableName = null;
    switch (st.countTokens()) {
      case 3:
        catalogName = normalizeSqlName(st.nextToken());
      case 2:
        schemaName = normalizeSqlName(st.nextToken());
      case 1:
        tableName = normalizeSqlName(st.nextToken());
    }
    return new SqlTableName(catalogName, schemaName, tableName);
  }
  
  public static SqlColumnName parseSqlColumnName(String fullName) {
    StringTokenizer st = new StringTokenizer(fullName, ".");
    String catalogName = null;
    String schemaName = null;
    String tableName = null;
    String columnName = null;
    switch (st.countTokens()) {
      case 4:
        catalogName = normalizeSqlName(st.nextToken());
      case 3:
        schemaName = normalizeSqlName(st.nextToken());
      case 2:
        tableName = normalizeSqlName(st.nextToken());
      case 1:
        columnName = normalizeSqlName(st.nextToken());
    }
    return new SqlColumnName(catalogName, schemaName, tableName, columnName);
  }
  
  public static String sqlChangeCharCase(String text, StringUtil.CharCase toCase) {
    int cmt = 0; // 0 - brak, 1 - block comment, 2 - line comment
    char txt = 0;
    char ch;
    char[] result = text.toCharArray();
    for (int i = 0; i<result.length; i++) {
      ch = result[i];
      if (ch == 39 || ch == '"' || ch == '`') {
        if (txt == 0) {
          txt = ch;
        } else if (txt == ch) {
          txt = 0;
        }
      }
      if (ch == '/' && i < result.length -1 && i < result.length && result[i +1] == '*' && cmt == 0) {
        cmt = 1;
      } else if (ch == '-' && i < result.length -1 && i < result.length && result[i +1] == '-' && cmt == 0) {
        cmt = 2;
      } else if (ch == '/' && i > 0 && result[i -1] == '*' && cmt == 1) {
        cmt = 0;
      } else if ((ch == 13 || ch == 10) && cmt == 2) {
        cmt = 0;
      } else if (cmt == 0 && txt == 0) {
        if (toCase == StringUtil.CharCase.ecUpperCase) {
          result[i] = Character.toUpperCase(result[i]);
        }
        else if (toCase == StringUtil.CharCase.ecLowerCase) {
          result[i] = Character.toLowerCase(result[i]);
        }
      }
    }
    return new String(result);
  }
  
  /**
   * <p>Szuka podanego ci¹gu znaków w poleceniu SQL pomijaj¹c ten ci¹g znajduj¹cy siê w komentarzach i ci¹gach znaków ograniczonych cudzys³owiem lub apostrofem
   * @param text
   * @param find
   * @param startAt
   * @param ignoreCase
   * @return
   */
  public static int indexOf(String text, String find, int startAt, boolean ignoreCase) {
    int cmt = 0; // 0 - brak, 1 - block comment, 2 - line comment
    char txt = 0;
    char ch;
    for (int i = startAt; i<text.length(); i++) {
      ch = text.charAt(i);
      if (ch == 39 || ch == '"' || ch == '`') {
        if (txt == 0) {
          txt = ch;
        } else if (txt == ch) {
          txt = 0;
        }
      }
      if (ch == '/' && i < text.length() -1 && i < text.length() && text.charAt(i +1) == '*' && cmt == 0) {
        cmt = 1;
      } else if (ch == '-' && i < text.length() -1 && i < text.length() && text.charAt(i +1) == '-' && cmt == 0) {
        cmt = 2;
      } else if (ch == '/' && i > 0 && text.charAt(i -1) == '*' && cmt == 1) {
        cmt = 0;
      } else if ((ch == 13 || ch == 10) && cmt == 2) {
        cmt = 0;
      } else if (cmt == 0 && txt == 0) {
        int f = 0;
        boolean found = true;
        while (f +i < text.length() && f < find.length()) {
          if (ignoreCase) {
            if (Character.toUpperCase(find.charAt(f)) != Character.toUpperCase(text.charAt(f +i))) {
              found = false;
              break;
            }
          }
          else if (find.charAt(f) != text.charAt(f +i)) {
            found = false;
            break;
          }
          f++;
        }
        if (found && f == find.length()) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public static String createTooltipFromSql(String sqlText) {
    StringBuilder sb = new StringBuilder();
    StringList sl = new StringList();
    sl.setText(sqlText);
    for (int i=0; i<sl.size(); i++) {
      if (sb.length() > 0) {
        sb.append("<br>");
      }
      String l = sl.get(i);
      if (l.length() > 80) {
        l = l.substring(0, 79) +"...";
      }
      sb.append(HtmlUtil.prepareText(l));
      if (i >= 10) {
        sb.append("<br>...");
        break;
      }
    }
    return "<html><pre>" +sb.toString() +"</pre>";
  }
  
}
