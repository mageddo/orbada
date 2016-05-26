/*
 * Created on 2005-08-02
 *
 */
package pl.mpak.usedb.core;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import pl.mpak.usedb.UseDBException;
import pl.mpak.util.Languages;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.VariantException;

/**
 * @author Andrzej Ka³u¿a
 *  
 */
public class ParameterList {
  private ArrayList<Parameter> params;
  private static Languages language = new Languages(ParameterList.class);

  public ParameterList() {
    super();
    params = new ArrayList<Parameter>();
  }

  public Parameter add() {
    Parameter result = new Parameter();
    add(result);
    return result;
  }

  public void add(String paramName) {
    add().setParamName(paramName);
  }

  public void add(Parameter param) {
    params.add(param);
  }

  public void remove(int index) {
    params.remove(index);
  }

  public void remove(String name) throws UseDBException {
    params.remove(paramByName(name));
  }

  public Parameter findParamByName(String name) {
    for (Parameter p : params) {
      if (p.getParamName().equalsIgnoreCase(name)) {
        return p;
      }
    }
    return null;
  }

  public Parameter paramByName(String name) throws UseDBException {
    Parameter p = findParamByName(name);
    if (p != null) {
      return p;
    }
    else {
      throw new UseDBException(language.getString("par_not_found", new Object[] {name}));
    }
  }

  public void clear() {
    params.clear();
  }

  public int parameterCount() {
    return params.size();
  }

  public Parameter getParameter(int index) {
    return params.get(index);
  }

  private void checkLength(int i, int length) throws UseDBException {
    if (i >= length) {
      throw new UseDBException(language.getString("un_end_of_str"));
    }
  }
  
  String replaceParameters(String sqlText) {
    for(int i=0; i<parameterCount(); i++) {
      Parameter p = getParameter(i);
      if (p.isBindable()) {
        continue;
      }
      sqlText = StringUtil.replaceString(sqlText, p.getParamName(), p.getString());
    }
    return sqlText;
  }
  
  void assigOutParameters(CallableStatement stmt) throws SQLException {
    for(int i=0; i<parameterCount(); i++) {
      Parameter p = getParameter(i);
      if (!p.isBindable()) {
        continue;
      }
      if (p.getParamMode() == ParameterMetaData.parameterModeInOut || p.getParamMode() == ParameterMetaData.parameterModeOut) {
        for(int j=0; j<p.getPositionCount(); j++) {
          int index = p.getPositionAtIndex(j); 
          switch(p.getParamDataType()) {
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
            case Types.VARCHAR:
              p.setString(stmt.getString(index));
              break;
            case Types.BIGINT:
              p.setLong(stmt.getLong(index));
              break;
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.TINYINT:
              p.setInteger(stmt.getInt(index));
              break;
            case Types.DECIMAL:
            case Types.NUMERIC:
              p.setBigDecimal(stmt.getBigDecimal(index));
              break;
            case Types.DOUBLE:
            case Types.FLOAT:
              p.setDouble(stmt.getDouble(index));
              break;
            case Types.BOOLEAN:
              p.setBoolean(stmt.getBoolean(index));
              break;
            case Types.DATE:
              p.setDate(stmt.getDate(index));
              break;
            case Types.TIME:
              p.setTime(stmt.getTime(index));
              break;
            case Types.TIMESTAMP:
              p.setTimestamp(stmt.getTimestamp(index));
              break;
            case Types.BLOB:
            case Types.BINARY:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
              p.setBinary(stmt.getBytes(index));
              break;
            case Types.NULL:
              p.clearValue();
              break;
          }
        }
      }
    }
  }
  
  public void bindParameters(PreparedStatement stmt) throws SQLException, VariantException, ParseException, IOException {
    for(int i=0; i<parameterCount(); i++) {
      Parameter p = getParameter(i);
      if (!p.isBindable()) {
        continue;
      }
      for(int j=0; j<p.getPositionCount(); j++) {
        if ((p.getParamMode() == ParameterMetaData.parameterModeOut || p.getParamMode() == ParameterMetaData.parameterModeInOut) && stmt instanceof CallableStatement) {
          ((CallableStatement)stmt).registerOutParameter(p.getPositionAtIndex(j), p.getParamDataType());
        }
        if (p.getParamMode() != ParameterMetaData.parameterModeOut) {
          if (p.getValue() == null) {
            stmt.setNull(p.getPositionAtIndex(j), p.getParamDataType());
          }
          else if (p.getValue().isNullValue() || StringUtil.isEmpty(p.getValue().getString())) {
            stmt.setNull(p.getPositionAtIndex(j), p.getParamDataType());
          }
          else {
            switch(p.getParamDataType()) {
              case Types.CHAR:
              case Types.VARCHAR:
              case Types.LONGVARCHAR:
                stmt.setString(p.getPositionAtIndex(j), p.getString());
                break;
              case Types.BIGINT:
                stmt.setLong(p.getPositionAtIndex(j), p.getLong());
                break;
              case Types.SMALLINT:
              case Types.INTEGER:
              case Types.TINYINT:
                stmt.setInt(p.getPositionAtIndex(j), p.getInteger());
                break;
              case Types.DECIMAL:
              case Types.NUMERIC:
                stmt.setBigDecimal(p.getPositionAtIndex(j), p.getBigDecimal());
                break;
              case Types.REAL:
              case Types.DOUBLE:
              case Types.FLOAT:
                stmt.setDouble(p.getPositionAtIndex(j), p.getDouble());
                break;
              case Types.BOOLEAN:
                stmt.setBoolean(p.getPositionAtIndex(j), p.getBoolean());
                break;
              case Types.DATE:
                stmt.setDate(p.getPositionAtIndex(j), new java.sql.Date(p.getDate().getTime()));
                break;
              case Types.TIME:
                stmt.setTime(p.getPositionAtIndex(j), new java.sql.Time(p.getTime().getTime()));
                break;
              case Types.TIMESTAMP:
                stmt.setTimestamp(p.getPositionAtIndex(j), new java.sql.Timestamp(p.getTimestamp().getTime()));
                break;
              case Types.BINARY:
              case Types.VARBINARY:
                try {
                  stmt.setBytes(p.getPositionAtIndex(j), p.getBinary());
                } catch (IOException e) {
                  throw new VariantException(e);
                }
                break;
              case Types.BLOB:
              case Types.LONGVARBINARY:
                InputStream is = p.getInputStream();
                try {
                  stmt.setBinaryStream(p.getPositionAtIndex(j), is);
                }
                catch (java.lang.AbstractMethodError e) {
                  try {
                    stmt.setBytes(p.getPositionAtIndex(j), StreamUtil.stream2Array(is));
                  } catch (IOException ex) {
                    throw new VariantException(ex);
                  }
                }
                break;
              case Types.CLOB:
                stmt.setAsciiStream(p.getPositionAtIndex(j), p.getInputStream());
                break;
              case Types.NULL:
                stmt.setNull(p.getPositionAtIndex(j), Types.NULL);
                break;
            }
          }
        }
      }
    }
  }
  
  public String parseParameters(String sqlText) throws UseDBException {
    return parseParameters(sqlText, "?");
  }

  /**
   * @param sqlText SQL command to parse parameters
   * @param paramReplaceBy named parameters will be replaced by,<br>
   *        available $(index) as sequential index number and
   *        $(name) as parameter name
   * @return
   * @throws UseDBException
   */
  public String parseParameters(String sqlText, String paramReplaceBy) throws UseDBException {
    
    if (sqlText == null) {
      return null;
    }
    
    StringBuilder result = new StringBuilder(sqlText);
    int pos = 0;

    clear();
    int i = 0;
    int index = 0;

    while (i < result.length()) {
      switch (result.charAt(i)) {
        case '\'':
        case '"': {
          char ch = result.charAt(i); 
          do {
            i++;
            checkLength(i, result.length());
            if (result.charAt(i) == ch && i +1 < result.length() && result.charAt(i +1) == ch) {
              i+=2;
            }
          } while (result.charAt(i) != ch);
          i++;
          break;
        }
        case '-':
          i++;
          checkLength(i, result.length());
          if (result.charAt(i) == '-') {
            do {
              i++;
              //checkLength(i, result.length());
            } while (i +1 < result.length() && result.charAt(i) != 10);
            i++;
          }
          break;
        case '/':
          i++;
          checkLength(i, result.length());
          if (result.charAt(i) == '*') {
            do {
              i++;
              checkLength(i + 1, result.length());
            } while (!(result.charAt(i) == '*' && result.charAt(i + 1) == '/'));
            i += 2;
          }
          else if (result.charAt(i) == '/') {
            do {
              i++;
              checkLength(i, result.length());
            } while (result.charAt(i) != 10);
            i++;
          }
          break;
        case '?': {
          Parameter p = add(); 
          p.setParamName("? (" +index +")");
          p.setBindable(true);
          p.addPosition(++pos);
          index++;
          i++;
          break;
        }
        case ':': {
          StringBuilder bf = new StringBuilder();
          int iTemp = 1;

          if (Pattern.matches("\\w", "" + result.charAt(iTemp + i)) && i > 0 && result.charAt(i -1) != ':') {
            do {
              bf.append(result.charAt(iTemp + i));
              iTemp++;
            } while ((iTemp + i) < result.length() && Pattern.matches("\\w", "" + result.charAt(iTemp + i)));
            if (bf.length() > 0) {
              Parameter p = findParamByName(bf.toString().toUpperCase());
              if (p == null) {
                p = add();
                p.setParamName(bf.toString().toUpperCase());
                p.setBindable(true);
              }
              p.addPosition(++pos);
              result.delete(i, i + iTemp);
              String text = StringUtil.replaceString(paramReplaceBy, "$(index)", String.valueOf(index));
              text = StringUtil.replaceString(text, "$(name)", p.getParamName());
              index++;
              result.insert(i, text);
            }
          }
          i++;
          break;
        }
        case '&': {
          StringBuilder bf = new StringBuilder();
          int iTemp = 1;

          if (Pattern.matches("\\w", "" + result.charAt(iTemp + i)) && i > 0 && result.charAt(i -1) != '&') {
            do {
              bf.append(result.charAt(iTemp + i));
              iTemp++;
            } while ((iTemp + i) < result.length() && Pattern.matches("\\w", "" + result.charAt(iTemp + i)));
            if (bf.length() > 0) {
              Parameter p = findParamByName("&" +bf.toString());
              if (p == null) {
                p = add();
                p.setParamName("&" +bf.toString());
                p.setBindable(false);
              }
            }
          }
          i++;
          break;
        }
        default:
          i++;
      }
    }

    return result.toString();
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<params.size(); i++) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(params.get(i).getParamName() +"=" +params.get(i).getString());
    }
    return getClass().getName() +"[" +sb.toString() +"]";
  }
}
