/*
 * Created on 2005-08-02
 *
 */
package pl.mpak.usedb.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ParameterMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

/**
 * @author Andrze Ka³u¿a
 *  
 */
public class Parameter {
  private String paramName = "";
  private ArrayList<Integer> paramIndexs = null;
  private Variant value = null;
  private int paramDataType = Types.NULL;
  private int paramMode = ParameterMetaData.parameterModeIn;
  private boolean bindable = true;
  
  public final static String[] paramTypesStr = SQLUtil.typeToString(new int[] {
      Types.NULL, Types.INTEGER, Types.BOOLEAN, Types.DATE, Types.TIMESTAMP, Types.NUMERIC, Types.DOUBLE, 
      Types.VARCHAR, Types.BLOB, Types.CLOB
    });
  public final static String[] paramModesStr = SQLUtil.paramModeToString(new int[] {
      ParameterMetaData.parameterModeIn, ParameterMetaData.parameterModeInOut, ParameterMetaData.parameterModeOut
    });

  public Parameter() {
    super();
    paramIndexs = new ArrayList<Integer>();
  }

  public Parameter(String paramName) {
    this();
    this.paramName = paramName.toUpperCase();
  }

  void setParamName(String paramName) {
    this.paramName = paramName;
  }

  public String getParamName() {
    return this.paramName;
  }

  /**
   * @param paramDataType any of java.sql.Types
   */
  public void setParamDataType(int paramDataType) {
    this.paramDataType = paramDataType;
  }

  public int getParamDataType() {
    return this.paramDataType;
  }
  
  /**
   * @param paramMode any of ParameterMetaData.parameterMode...
   */
  public void setParamMode(int paramMode) {
    this.paramMode = paramMode;
  }
  
  /**
   * @param paramMode any of ParameterMetaData.parameterMode...
   * @param paramDataType any of java.sql.Types
   */
  public void setParamMode(int paramMode, int paramDataType) {
    this.paramMode = paramMode;
    this.paramDataType = paramDataType;
  }
  
  public int getParamMode() {
    return this.paramMode;
  }

  public boolean isBindable() {
    return bindable;
  }

  public void setBindable(boolean bindable) {
    this.bindable = bindable;
  }

  private int resolveValueType(Object value) {
    if (value instanceof Integer || value instanceof Short || 
        value instanceof Byte) {
      return Types.INTEGER;
    }
    if (value instanceof Long || value instanceof BigInteger) {
      return Types.BIGINT;
    }
    else if (value instanceof Boolean) {
      return Types.BOOLEAN;
    }
    else if (value instanceof Date || value instanceof Calendar) {
      return Types.DATE;
    }
    else if (value instanceof Time) {
      return Types.TIME;
    }
    else if (value instanceof Timestamp) {
      return Types.TIMESTAMP;
    }
    else if (value instanceof BigDecimal) {
      return Types.NUMERIC;
    }
    else if (value instanceof Double || value instanceof Float) {
      return Types.DOUBLE;
    }
    else if (value instanceof InputStream) {
      return Types.LONGVARBINARY;
    }
    else if (value instanceof Variant) {
      return resolveValueType(((Variant)value).getValue());
    }
    else if (value == null) {
      return Types.NULL;
    }
    else {
      return Types.VARCHAR;
    }
  }

  public void clearValue() {
    this.paramDataType = Types.NULL;
    this.value = null;
  }

  /**
   * Funkcja nie pozwala ustawiæ wartoœci typu BLOB, aby ustawiæ tego typu parametr nale¿y
   * pos³u¿yæ siê funkcj¹ asBinaryStream gdzie trzeba okreœliæ rozmiar
   * 
   * @param value obiekt prostego typu String, Integer, Numeric, Boolean, Date
   */
  public void setValue(Object value) {
    setValue(value, resolveValueType(value));
  }

  public void setValue(Object value, int type) {
    if (value instanceof Variant) {
      this.value = (Variant)value;
      this.paramDataType = type;
    }
    else if (value == null) {
      this.paramDataType = type;
      this.value = new Variant();
    }
    else {
      try {
        switch (type) {
          case Types.CHAR:
          case Types.LONGVARCHAR:
          case Types.VARCHAR:
            setString(value.toString());
            break;
          case Types.BIGINT: 
            setLong(new Long(value.toString()).longValue());
            break;
          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.INTEGER: 
            setInteger((new Integer(value.toString())).intValue());
            break;
          case Types.DECIMAL:
          case Types.NUMERIC:
            setBigDecimal(new BigDecimal(value.toString()));
            break;
          case Types.REAL:
          case Types.DOUBLE:
          case Types.FLOAT:
            setDouble((new Double(value.toString())).doubleValue());
            break;
          case Types.BOOLEAN:
            setBoolean((Boolean.valueOf(value.toString())).booleanValue());
            break;
          case Types.DATE:
            if (value instanceof Variant) {
              setDate(((Variant)value).getDate());
            }
            else {
              setDate((new Long(value.toString())).longValue());
            }
            break;
          case Types.TIME:
            if (value instanceof Variant) {
              setTime(((Variant)value).getLong());
            }
            else {
              setTime((new Long(value.toString())).longValue());
            }
            break;
          case Types.TIMESTAMP:
            if (value instanceof Variant) {
              setTimestamp(((Variant)value).getLong());
            }
            else {
              setTimestamp((new Long(value.toString())).longValue());
            }
            break;
          case Types.BLOB:
          case Types.BINARY:
          case Types.VARBINARY:
          case Types.LONGVARBINARY:
            setBinaryStream(value.toString());
            break;
          case Types.CLOB:
            setAsciiStream(value.toString());
            break;
          case Types.NULL:
            clearValue();
            break;
          default: 
            setString(value.toString());
        }
      }
      catch(Exception e) {
        setString(value.toString());
        setParamDataType(type);
      }
    }
  }
  
  public Variant getValue() {
    return this.value;
  }
  
  public void setNull(int type) {
    this.paramDataType = type;
    this.value = null;
  }

  public void setString(String value) {
    this.paramDataType = Types.VARCHAR;
    this.value = new Variant(value);
  }

  public String getString() {
    try {
      return (this.value == null ? "" : this.value.getString());
    } catch (Exception e) {
      return "";
    }
  }

  public void setDouble(double value) {
    this.paramDataType = Types.DOUBLE;
    this.value = new Variant(value);
  }

  public double getDouble() throws VariantException {
    return this.value.getDouble();
  }

  public void setBigDecimal(BigDecimal value) {
    this.paramDataType = Types.NUMERIC;
    this.value = new Variant(value);
  }

  public BigDecimal getBigDecimal() throws VariantException {
    return this.value.getBigDecimal();
  }

  public void setBoolean(boolean value) {
    this.paramDataType = Types.BOOLEAN;
    this.value = new Variant(value);
  }

  public boolean getBoolean() throws VariantException {
    return this.value.getBoolean();
  }

  public void setDate(long value) {
    this.paramDataType = Types.DATE;
    this.value = new Variant(value);
  }

  public void setDate(Date value) {
    this.paramDataType = Types.DATE;
    this.value = new Variant(value);
  }

  public Date getDate() throws VariantException, ParseException {
    return this.value.getDate();
  }

  public void setTime(long value) {
    this.paramDataType = Types.TIME;
    this.value = new Variant(value);
  }

  public void setTime(Time value) {
    this.paramDataType = Types.TIME;
    this.value = new Variant(value);
  }

  public Time getTime() throws VariantException, ParseException {
    return this.value.getTime();
  }

  public void setTimestamp(long value) {
    this.paramDataType = Types.TIMESTAMP;
    this.value = new Variant(value);
  }

  public void setTimestamp(Timestamp value) {
    this.paramDataType = Types.TIMESTAMP;
    this.value = new Variant(value);
  }

  public Timestamp getTimestamp() throws VariantException, ParseException {
    return this.value.getTimestamp();
  }

  public void setInputStream(InputStream value) {
    setInputStream(value, -1);
  }

  public void setInputStream(InputStream value, int size) {
    this.paramDataType = Types.LONGVARBINARY;
    this.value = new Variant(value, size);
  }

  public void setBinaryStream(String fileName) {
    this.paramDataType = Types.BLOB;
    this.value = new Variant(fileName);
  }

  public void setAsciiStream(String fileName) {
    this.paramDataType = Types.CLOB;
    this.value = new Variant(fileName);
  }

  public void setBinary(byte[] value) {
    this.paramDataType = Types.LONGVARBINARY;
    this.value = new Variant(value);
  }
  
  public boolean isFile() throws VariantException {
    if (value != null && value.getObject() instanceof String) {
      try {
        return new File(this.value.getString()).exists();
      } catch (IOException e) {
        throw new VariantException(e);
      }
    }
    return false;
  }

  public InputStream getInputStream() throws VariantException {
    if (value != null && value.getObject() instanceof String) {
      File file;
      try {
        file = new File(this.value.getString());
        if (file.exists()) {
          try {
            return new FileInputStream(file);
          } catch (FileNotFoundException e) {
            throw new VariantException(e);
          }
        }
      } catch (IOException ex) {
        throw new VariantException(ex);
      }
    }
    return this.value.getInputStream();
  }
  
  public byte[] getBinary() throws VariantException, IOException {
    if (value != null && value.getObject() instanceof String) {
      File file = new File(this.value.getString());
      if (file.exists()) {
        try {
          byte[] v = StreamUtil.stream2Array(new FileInputStream(file));
          this.value.setBinary(v);
          return v;
        } catch (FileNotFoundException e) {
          throw new VariantException(e);
        }
      }
    }
    return this.value.getBinary();
  }
  
  public int getInputStreamSize() {
    return this.value.getSize();
  }

  public void setInteger(int value) {
    this.paramDataType = Types.INTEGER;
    this.value = new Variant(value);
  }

  public int getInteger() throws VariantException {
    return this.value.getInteger();
  }

  public void setLong(long value) {
    this.paramDataType = Types.BIGINT;
    this.value = new Variant(value);
  }

  public long getLong() throws VariantException {
    return this.value.getLong();
  }

//  public void setBigInteger(BigInteger value) {
//    this.paramType = Types.BIGINT;
//    this.value = new Variant(value);
//  }
//
//  public void setBigInteger(long value) {
//    this.paramType = Types.BIGINT;
//    this.value = new Variant(value);
//  }
//
//  public BigInteger getBigInteger() throws VariantException {
//    return this.value.getBigInteger();
//  }

  void clearPositions() {
    paramIndexs.clear();
  }

  int getPositionCount() {
    return paramIndexs.size();
  }

  int getPositionAtIndex(int inx) {
    return ((Integer) paramIndexs.get(inx)).intValue();
  }

  void addPosition(int pos) {
    if (paramIndexs.indexOf(Integer.valueOf(pos)) == -1) {
      paramIndexs.add(Integer.valueOf(pos));
    }
  }

  void removePosition(int pos) {
    if (paramIndexs.indexOf(Integer.valueOf(pos)) != -1) {
      paramIndexs.remove(Integer.valueOf(pos));
    }
  }
  
  public String toString() {
    return "[" +getParamName() +"(" +SQLUtil.typeToString(getParamDataType()) +") = " +getValue() +"]";
  }
}
