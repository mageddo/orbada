package pl.mpak.usedb.br;

import java.io.IOException;
import java.sql.SQLException;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.util.Assert;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

public class QueryBufferedRecord extends BufferedRecord {

  private Database database;
  
  public QueryBufferedRecord(String tableName, String[] primaryKey, Query query) throws VariantException, UseDBException, SQLException, IOException {
    super(tableName, primaryKey);
    Assert.notNull(query);
    this.database = query.getDatabase();
    queryFields(query);
  }

  /**
   * <p>Od razu wczytuje rekord w wartoœciach klucza g³ównego 
   * @param tableName
   * @param primaryKey
   * @param database
   * @param keyValues
   * @throws UseDBException 
   */
  public QueryBufferedRecord(String tableName, String[] primaryKey, Database database, Variant[] keyValues) throws UseDBException {
    super(tableName, primaryKey);
    this.database = database;
    loadRecord(keyValues);
  }
  
  /**
   * <p>Aby wczytaæ rekord nale¿y pos³u¿yæ siê funkcj¹ loadRecord
   * @param tableName
   * @param primaryKey
   * @param database
   * @throws VariantException
   * @throws UseDBException
   * @throws SQLException
   * @throws IOException
   */
  public QueryBufferedRecord(String tableName, String[] primaryKey, Database database) {
    super(tableName, primaryKey);
    this.database = database;
  }
  
  /**
   * <p>Pozwala zainicjowaæ bufor oraz wczytaæ z bazy danych rekord.
   * @param keyValues
   * @throws UseDBException
   */
  public void loadRecord(Variant[] keyValues) throws UseDBException {
    cancelUpdates();
    Query query = new Query(database);
    query.setSqlText("select * from " +database.quoteName(tableName) +" where " +primaryKeyWhere(database));
    for (int i=0; i<keyValues.length; i++) {
      query.paramByName(primaryKey[i] +"_PK").setValue(keyValues[i].getValue());
    }
    try {
      query.open();
      queryFields(query);
    } catch (Exception ex) {
      throw new UseDBException(ex);
    }
    try {
      if (!query.eof()) {
        updateFrom(query);
      }
    } finally {
      query.close();
    }
  }
  
  /**
   * <p>Tworzy listê kolumn na podstawie kolumn z Query
   * @param query
   */
  public void createFields(Query query) {
    clear();
    for (int i=0; i<query.getFieldCount(); i++) {
      QueryField field = query.getField(i);
      add(field.getFieldName(), field.getVariantType(), true);
    }
  }
  
  private void queryFields(Query query) throws VariantException, UseDBException, SQLException, IOException {
    createFields(query);
    if (!query.eof()) {
      updateFrom(query);
    }
  }
  
  /**
   * <p>Aktualizuje dane w buforze danymi z Query
   * @param query
   * @throws UseDBException
   */
  public void updateValues(Query query) throws UseDBException {
    for (int i=0; i<fieldList.size(); i++) {
      BufferedRecordField bfl = fieldList.get(i);
      QueryField qf = query.findFieldByName(bfl.getFieldName());
      if (qf != null) {
        try {
          bfl.setValue(qf.getValue());
        } catch (Exception ex) {
          throw new UseDBException(ex);
        }
      }
      else {
        bfl.clear();
      }
    }
  }

  public void applyInsert() throws Exception {
    Command command = insertCommand(database);
    if (command != null) {
      command.execute();
    }
  }
  
  public void applyUpdate() throws Exception {
    Command command = updateCommand(database);
    if (command != null) {
      command.execute();
    }
  }
  
  public void applyDelete() throws Exception {
    Command command = deleteCommand(database);
    if (command != null) {
      command.execute();
    }
  }
  
}
