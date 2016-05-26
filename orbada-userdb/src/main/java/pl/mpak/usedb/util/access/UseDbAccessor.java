package pl.mpak.usedb.util.access;

import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseManager;
import pl.mpak.usedb.core.Query;

public class UseDbAccessor implements IConnectionAccessor {

  private Database database;
  private HashMap<String, ResultColumnList> tableCacheMap;
  
  public UseDbAccessor() {
    tableCacheMap = new HashMap<String, ResultColumnList>();
  }
  
  public UseDbAccessor(Database database) {
    this();
    this.database = database;
  }

  @Override
  public ResultColumnList tableColumnList(String[] tableName) throws AccessorException {
    if (database == null) {
      DatabaseManager dm = DatabaseManager.getManager();
      List<Database> dbl = dm.getDatabaseList(DatabaseManager.LK_PUBLIC_NAME, "internal");
      if (dbl.size() == 1) {
        database = dbl.get(0);
      }
      else {
        throw new AccessorException("Not selected database for UseDb library");
      }
    }
    ResultColumnList rcl = tableCacheMap.get(Arrays.toString(tableName));
    if (rcl == null) {
      rcl = new ResultColumnList();
      boolean eof = false;
      Query query = database.createQuery();
      if (tableName.length == 1) {
        try {
          query.setResultSet(database.getMetaData().getColumns(null, null, database.normalizeName(tableName[0]), null));
          eof = query.eof();
        } catch (Exception e) {
          throw new AccessorException(e);
        }
      }
      else if (tableName.length == 2) {
        try {
          query.setResultSet(database.getMetaData().getColumns(null, database.normalizeName(tableName[0]), database.normalizeName(tableName[1]), null));
          eof = query.eof();
        } catch (Exception e) {
          throw new AccessorException(e);
        }
      }
//      else {
//        throw new AccessorException("Table " +tableName.toString() +" not found");
//      }
      if (eof) {
        throw new AccessorException("Table " +Arrays.toString(tableName) +" not found");
      }
      
      if (query.isActive()) {
        try {
          while (!query.eof()) {
            String columnName = database.normalizeName(query.fieldByName("COLUMN_NAME").getString());
            ResultColumn rc = new ResultColumn(columnName);
            rc.setType(query.fieldByName("DATA_TYPE").getInteger());
            if (query.fieldByName("DECIMAL_DIGITS").isNull()) {
              rc.setLength(query.fieldByName("COLUMN_SIZE").getInteger());
            }
            else if (rc.getType() == Types.NUMERIC || rc.getType() == Types.DECIMAL) {
              rc.setPrecision(query.fieldByName("COLUMN_SIZE").getInteger());
              rc.setScale(query.fieldByName("DECIMAL_DIGITS").getInteger());
            }
            rcl.add(rc);
            query.next();
          }
        }
        catch (Exception e) {
          throw new AccessorException(e);
        }
        finally {
          query.close();
        }
        tableCacheMap.put(Arrays.toString(tableName), rcl);
      }
    }
    return rcl;
  }

  @Override
  public void reset() {
    tableCacheMap.clear();
  }

}
