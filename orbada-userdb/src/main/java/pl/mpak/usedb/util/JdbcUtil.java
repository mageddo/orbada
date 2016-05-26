package pl.mpak.usedb.util;

import java.util.ArrayList;

import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;

public class JdbcUtil {

  public static String[] getPrimaryKeyFields(Database database, String catalogName, String schemaName, String tableName) {
    ArrayList<String> primaryKeyColumns = new ArrayList<String>();
    Query query = database.createQuery();
    try {
      if (schemaName == null) {
        query.setResultSet(database.getMetaData().getPrimaryKeys(catalogName, database.getUserName(), tableName));
      }
      if (!query.isActive() || query.eof()) {
        query.close();
        query.setResultSet(database.getMetaData().getPrimaryKeys(catalogName, schemaName, tableName));
      }
      if (!query.eof()) {
        while (!query.eof()) {
          primaryKeyColumns.add(query.fieldByName("COLUMN_NAME").getString());
          query.next();
        }
      } else {
        return null;
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    } finally {
      query.close();
    }
    return primaryKeyColumns.toArray(new String[primaryKeyColumns.size()]);
  }
  
}
