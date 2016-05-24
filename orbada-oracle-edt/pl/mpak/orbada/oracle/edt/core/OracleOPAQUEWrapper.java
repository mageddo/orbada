package pl.mpak.orbada.oracle.edt.core;

import java.lang.reflect.Method;
import java.sql.SQLException;

import pl.mpak.usedb.core.QueryColumnTypeWrapper;
import pl.mpak.util.ExceptionUtil;

public class OracleOPAQUEWrapper implements QueryColumnTypeWrapper {

  private Class clazz;

  public OracleOPAQUEWrapper(Class clazz) {
    this.clazz = clazz;
  }

  @Override
  public Object convert(Object o) throws SQLException {
    if (o == null) {
      return null;
    }
    if (clazz != null && clazz.isInstance(o)) {
      try {
        Method m = clazz.getMethod("getStringVal", (Class<?>[])null);
        return m.invoke(o, (Object[])null);
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      return ((oracle.xdb.XMLType)o).getStringVal();
    }
    return ((oracle.sql.OPAQUE)o).getBytes();
  }

}
