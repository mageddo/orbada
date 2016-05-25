/*
 * Created on 2005-08-03
 *
 */
package pl.mpak.usedb.core;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import pl.mpak.usedb.UseDBException;
import pl.mpak.util.Languages;
import pl.mpak.util.variant.VariantException;

/**
 * @author Andrzej Ka³u¿a
 *
 */
public class QueryFieldList {
  private static Languages language = new Languages(QueryFieldList.class);

  private ArrayList<QueryField> fields = null;
  private HashMap<String, QueryField> fieldsMap = null;
  private Query query;
  
  public QueryFieldList(Query query) {
    super();
    setQuery(query);
    fields = new ArrayList<QueryField>();
    fieldsMap = new HashMap<String, QueryField>();
  }

  private void setQuery(Query query) {
    this.query = query;
  }
  
  public Query getQuery() {
    return this.query;
  }
  
  private void add(QueryField field) {
    fields.add(field);
    fieldsMap.put(field.getFieldName().toUpperCase(), field);
  }
  
  void clear() {
    fieldsMap.clear();
    fields.clear();
  }
  
  public QueryField findFieldByName(String name) {
    QueryField result = fieldsMap.get(name.toUpperCase());
    if (result != null) {
      return result;
    }
    else {
      return null;
    }
  }
  
  public QueryField fieldByName(String name) throws UseDBException {
    QueryField f = findFieldByName(name);
    if (f != null) {
      return f;
    }
    else {
      throw new UseDBException(language.getString("err_db_FieldNotFound", new Object[] {name}));
    }
  }
  
  public int getFieldCount() {
    return fields.size();
  }
  
  public QueryField getField(int index) {
    return fields.get(index);
  }
  
  void createFields(ResultSet rset) throws SQLException, UseDBException {
    createFields(rset.getMetaData());
  }
  
  void createFields(ResultSetMetaData rsmd) throws SQLException, UseDBException {
    clear();
    for(int i=0; i < rsmd.getColumnCount(); i++ ) {
      add(new QueryField(query, i));
    }
  }
  
  void write(RandomAccessFile raf) throws IOException, VariantException {
    raf.writeInt(fields.size());
    for (QueryField field : fields) {
      field.write(raf);
    }
  }
  
  void read(RandomAccessFile raf) throws IOException, VariantException, SQLException {
    clear();
    int count = raf.readInt();
    for (int i = 0; i < count; i++) {
      add(new QueryField(query).read(raf));
    }
  }
  
}
