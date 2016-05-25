package pl.mpak.usedb.br;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

public class DefaultBufferedRecord extends BufferedRecord {

  private Database database;
  private boolean exists;
  private boolean checked;

  public DefaultBufferedRecord() {
    super();
  }

  public DefaultBufferedRecord(Database database, Object beanObject) throws IllegalArgumentException, VariantException, IllegalAccessException, InvocationTargetException, ParseException, IOException {
    super(beanObject);
    this.database = database;
  }

  public DefaultBufferedRecord(Database database, String tableName, String[] primaryKey) {
    super(tableName, primaryKey);
    this.database = database;
  }

  public DefaultBufferedRecord(Database database, String tableName, String primaryKey) {
    super(tableName, new String[] {primaryKey});
    this.database = database;
  }
  
  public void loadRecord(Variant keyValue) throws UseDBException {
    loadRecord(new Variant[] {keyValue});
  }

  /**
   * <p>Pozwala zainiciowaæ bufor oraz wczytaæ z bazy danych rekord.
   * @param keyValues
   * @throws UseDBException
   */
  public void loadRecord(Variant[] keyValues) throws UseDBException {
    cancelUpdates();
    Query query = new Query(database);
    query.setSqlText("select " +columnList(database) +" from " +database.normalizeName(tableName) +" where " +primaryKeyWhere(database));
    for (int i=0; i<keyValues.length; i++) {
      query.paramByName(primaryKey[i] +"_PK").setValue(keyValues[i].getValue());
    }
    try {
      query.open();
      if (!query.eof()) {
        updateFrom(query);
        setExists(true);
      }
    } catch (Exception ex) {
      throw new UseDBException(ex);
    } finally {
      query.close();
    }
  }
  
  public void loadRecordBy(String columnName, Variant value) throws UseDBException {
    loadRecordBy(new String[] {columnName}, new Variant[] {value});
  }

  /**
   * <p>Pozwala zainiciowaæ bufor oraz wczytaæ z bazy danych rekord wg dowolnego unikalnego pola
   * @param values
   * @throws UseDBException
   */
  public void loadRecordBy(String[] columnNames, Variant[] values) throws UseDBException {
    cancelUpdates();
    Query query = new Query(database);
    query.setSqlText("select " +columnList(database) +" from " +database.normalizeName(tableName) +" where " +getWhere(columnNames, database));
    for (int i=0; i<values.length; i++) {
      query.paramByName(columnNames[i]).setValue(values[i].getValue());
    }
    try {
      query.open();
      if (!query.eof()) {
        updateFrom(query);
        setExists(true);
      }
    } catch (Exception ex) {
      throw new UseDBException(ex);
    } finally {
      query.close();
    }
  }

  /**
   * <p>Testuje czy rekord o podanych wartoœciach klucza g³ównego istnieje
   * @return
   * @throws Exception
   */
  public boolean recordExists() throws Exception {
    if (primaryKey == null || primaryKey.length == 0) {
      throw new IllegalArgumentException("primaryKeys is null");
    }
    Query query = database.createQuery();
    try {
      query.setSqlText("select 0 from " +tableName +" where " +primaryKeyWhere(database));
      for (int i=0; i<primaryKey.length; i++) {
        BufferedRecordField field = fieldByName(primaryKey[i]);
        query.paramByName(primaryKey[i] +"_PK").setValue(field.getValue(), SQLUtil.variantToSqlType(field.getValueType()));
      }
      query.open();
      return !query.eof();
    }
    finally {
      query.close();
    }
  }
  
  public void applyInsert() throws Exception {
    Command command = insertCommand(database);
    if (command != null) {
      command.execute();
    }
    applyUpdates();
  }
  
  public void applyUpdate() throws Exception {
    Command command = updateCommand(database);
    if (command != null) {
      command.execute();
    }
    applyUpdates();
  }
  
  public void applyDelete() throws Exception {
    Command command = deleteCommand(database);
    if (command != null) {
      command.execute();
    }
    setExists(false);
    applyUpdates();
  }

  public boolean isExists() {
    return exists;
  }

  /**
   * <p>Pozwala ustaliæ stan istenienia rekordu, g³ównie do celów logistycznych
   * @param exists
   * @see setChecked;
   */
  public void setExists(boolean exists) {
    this.exists = exists;
    setChecked(exists);
  }

  public boolean isChecked() {
    return checked;
  }

  /**
   * <p>Pozwala ustaliæ czy rekord jest oznaczony do zmiany, g³ównie do celów logistycznych.
   * Przy ustawianiu setExists() checked równie¿ ulega zmianie
   * @param checked
   * @see setExists
   */
  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public Database getDatabase() {
    return database;
  }

}
