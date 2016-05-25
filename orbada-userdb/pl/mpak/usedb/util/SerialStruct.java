package pl.mpak.usedb.util;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import javax.sql.rowset.serial.SQLOutputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import javax.sql.rowset.serial.SerialRef;

public class SerialStruct implements Struct, Serializable, Cloneable {

  private static final long serialVersionUID = -5227193983903945799L;

  private String SQLTypeName;

  private Object attribs[];

  public SerialStruct(Struct in, Map<String, Class<?>> map) throws SerialException {
    try {
      SQLTypeName = in.getSQLTypeName();
      attribs = in.getAttributes(map);
      mapToSerial(map);
    } catch (SQLException e) {
      throw new SerialException(e.getMessage());
    }
  }

  public SerialStruct(SQLData in, Map<String, Class<?>> map) throws SerialException {

    try {
      SQLTypeName = in.getSQLTypeName();

      Vector tmp = new Vector();
      in.writeSQL(new SQLOutputImpl(tmp, map));
      attribs = tmp.toArray();
    } catch (SQLException e) {
      throw new SerialException(e.getMessage());
    }
  }

  public String getSQLTypeName() throws SerialException {
    return SQLTypeName;
  }

  public Object[] getAttributes() throws SerialException {
    return attribs;
  }

  public Object[] getAttributes(Map<String, Class<?>> map) throws SerialException {
    return attribs;
  }

  @SuppressWarnings("unchecked")
  private void mapToSerial(Map map) throws SerialException {
    try {
      for (int i = 0; i < attribs.length; i++) {
        if (attribs[i] instanceof Struct) {
          attribs[i] = new SerialStruct((Struct) attribs[i], map);
        } else if (attribs[i] instanceof SQLData) {
          attribs[i] = new SerialStruct((SQLData) attribs[i], map);
        } else if (attribs[i] instanceof Blob) {
          attribs[i] = new SerialBlob((Blob) attribs[i]);
        } else if (attribs[i] instanceof Clob) {
          attribs[i] = new SerialClob((Clob) attribs[i]);
        } else if (attribs[i] instanceof Ref) {
          attribs[i] = new SerialRef((Ref) attribs[i]);
        } else if (attribs[i] instanceof java.sql.Array) {
          attribs[i] = new SerialArray((java.sql.Array) attribs[i], map);
        }
      }
    } catch (SQLException e) {
      throw new SerialException(e.getMessage());
    }
    return;
  }
  
  public String toString() {
    return SQLTypeName +Arrays.toString(attribs);
  }

}
