package pl.mpak.usedb.br;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.w3c.dom.DOMException;

import pl.mpak.usedb.Messages;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.ann.Column;
import pl.mpak.usedb.ann.Table;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

public class BufferedRecord {

  protected ArrayList<BufferedRecordField> fieldList;
  protected String tableName;
  protected String[] primaryKey;
  private String primaryKeyValueCommand;
  private Object beanObject;

  public BufferedRecord() {
    this(null, (String[]) null);
  }

  /**
   * <p>Konstruktor tworz¹cy odpowiedni¹ strukturê na podstawie przekazanego bean-a.
   * Bean musi zawieraæ odpowiednie adnotacje.
   * @param beanObject
   * @throws IOException 
   * @throws ParseException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws VariantException 
   * @throws IllegalArgumentException 
   * @see pl.mpak.usedb.ann.Table
   * @see pl.mpak.usedb.ann.Column
   */
  public BufferedRecord(Object beanObject) throws IllegalArgumentException, VariantException, IllegalAccessException, InvocationTargetException, ParseException, IOException {
    this(null, (String[]) null);
    this.beanObject = beanObject;
    prepareFromAnnotations();
  }

  public BufferedRecord(String tableName, String primaryKey) {
    this(tableName, new String[] { primaryKey });
  }

  /**
   * @param tableName
   *          mo¿e zawieraæ ca³y ci¹g dostêpu do tabeli np "SCHEMAT".TABELA
   * @param primaryKey
   */
  public BufferedRecord(String tableName, String[] primaryKey) {
    super();
    this.tableName = tableName;
    if (primaryKey != null) {
      this.primaryKey = Arrays.copyOf(primaryKey, primaryKey.length);;
    }
    this.fieldList = new ArrayList<BufferedRecordField>();
    this.primaryKeyValueCommand = "select IDENTITY_VAL_LOCAL() VALUE from dual"; //$NON-NLS-1$
  }

  public BufferedRecord(String tableName, String primaryKey, String primaryKeyValueCommand) {
    this(tableName, primaryKey);
    this.primaryKeyValueCommand = primaryKeyValueCommand;
  }
  
  private void prepareFromAnnotations() throws IllegalArgumentException, VariantException, IllegalAccessException, InvocationTargetException, ParseException, IOException {
    for (Annotation a : beanObject.getClass().getAnnotations()) {
      if (a instanceof Table) {
        Table tr = (Table)a;
        this.tableName = tr.name();
        this.primaryKey = tr.primaryKey();
      }
    }
    try {
      BeanInfo info = Introspector.getBeanInfo(beanObject.getClass(), Object.class);
      for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
        addAnnotationProperty(pd);
      }
    } catch (IntrospectionException e) {
      ExceptionUtil.processException(e);
    }
  }

  private void addAnnotationProperty(PropertyDescriptor pd) throws IllegalArgumentException, VariantException, IllegalAccessException, InvocationTargetException, ParseException, IOException {
    if (pd != null) {
      Method getter = pd.getReadMethod();
      Method setter = pd.getWriteMethod();
      String name = pd.getName();
      Column column = null;
      for (Annotation a : getter.getAnnotations()) {
        if (a instanceof Column) {
          column = (Column)a;
        }
      }
      for (Annotation a : setter.getAnnotations()) {
        if (a instanceof Column) {
          column = (Column)a;
        }
      }
      if (column != null) {
        BufferedRecordField field = new BufferedRecordField(column.name(), column.type(), column.updatable());
        field.setPropertyName(name);
        field.setSetterMethod(setter);
        field.setGetterMethod(getter);
        if (!"".equals(column.defaultValue())) { //$NON-NLS-1$
          setter.invoke(beanObject, new Variant(column.defaultValue()).cast(column.type()).getValue());
        }
        field.setFirstValue(new Variant(getter.invoke(beanObject)));
        add(field);
      }
    }
  }
  
  public ArrayList<BufferedRecordField> getFieldList() {
    return fieldList;
  }

  public void add(BufferedRecordField field) {
    fieldList.add(field);
  }

  public void add(String fieldName) {
    add(new BufferedRecordField(fieldName));
  }

  public void add(String fieldName, int valueType, boolean updatable) {
    add(new BufferedRecordField(fieldName, valueType, updatable));
  }

  public void add(String fieldName, int valueType) {
    add(new BufferedRecordField(fieldName, valueType));
  }

  public void add(String fieldName, Variant value, int valueType) {
    add(new BufferedRecordField(fieldName, value, valueType));
  }

  public void add(String fieldName, Variant value, int valueType, boolean updatable) {
    add(new BufferedRecordField(fieldName, value, valueType, updatable));
  }

  public void remove(String fieldName) {
    for (int i = 0; i < fieldList.size(); i++) {
      if (fieldName.equalsIgnoreCase(fieldList.get(i).getFieldName())) {
        fieldList.remove(i);
        break;
      }
    }
  }

  public void clear() {
    fieldList.clear();
  }

  public void clearValues() {
    for (BufferedRecordField brf : fieldList) {
      brf.clear();
    }
  }

  public boolean isChanged() {
    for (int i = 0; i < fieldList.size(); i++) {
      if (fieldList.get(i).isChanged()) {
        return true;
      }
    }
    return false;
  }

  /**
   * <p>
   * Zatwierdza zmiany dokonane w pamiêci - nie w bazie danych
   * 
   * @throws IOException
   * @throws VariantException
   */
  public void applyUpdates() {
    if (isChanged()) {
      for (int i = 0; i < fieldList.size(); i++) {
        fieldList.get(i).apply();
      }
    }
  }

  /**
   * <p>
   * Anuluje zmiany dokonane w pamiêci - nie w bazie danych
   * @throws UseDBException 
   */
  public void cancelUpdates() throws UseDBException {
    for (BufferedRecordField field : fieldList) {
      field.cancel();
      updateBeanFromField(field);
    }
  }

  public BufferedRecordField getField(int index) {
    return fieldList.get(index);
  }

  public BufferedRecordField getPrimaryKeyField() {
    if (primaryKey != null && primaryKey.length == 1) {
      return fieldByName(primaryKey[0]);
    }
    throw new IllegalArgumentException("primary key size != 1 or null"); //$NON-NLS-1$
  }

  public BufferedRecordField[] getPrimaryKeyFields() {
    if (primaryKey != null && primaryKey.length > 0) {
      BufferedRecordField[] fields = new BufferedRecordField[primaryKey.length];
      for (int i = 0; i < primaryKey.length; i++) {
        fields[i] = fieldByName(primaryKey[i]);
      }
      return fields;
    }
    throw new IllegalArgumentException("primary key list is empty"); //$NON-NLS-1$
  }

  public BufferedRecordField fieldByName(String fieldName) {
    for (BufferedRecordField brf : fieldList) {
      if (brf.getFieldName().equalsIgnoreCase(fieldName)) {
        return brf;
      }
    }
    return null;
  }

  public int fieldCount() {
    return fieldList.size();
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setPrimaryKeys(String[] primaryKey) {
    if (primaryKey != null) {
      this.primaryKey = Arrays.copyOf(primaryKey, primaryKey.length);
    }
  }

  public String[] getPrimaryKeys() {
    if (primaryKey != null) {
      return Arrays.copyOf(primaryKey, primaryKey.length);
    }
    return null;
  }

  public void setPrimaryKey(String primaryKey) {
    setPrimaryKeys(new String[] { primaryKey });
  }

  public String getPrimaryKey() {
    if (primaryKey != null && primaryKey.length == 1) {
      return primaryKey[0];
    }
    throw new IllegalArgumentException("primary key size > 1 or null"); //$NON-NLS-1$
  }

  public void setPrimaryKeyValue(Variant value) throws UseDBException {
    getPrimaryKeyField().setValue(value);
    updateBeanFromField(getPrimaryKeyField());
  }

  public void setPrimaryKeyValues(Variant[] values) throws UseDBException {
    BufferedRecordField[] fields = getPrimaryKeyFields();
    if (fields.length != values.length) {
      throw new IllegalArgumentException("primary key fields.length != values.length"); //$NON-NLS-1$
    }
    for (int i=0; i<fields.length; i++) {
      fields[i].setValue(values[i]);
      updateBeanFromField(fields[i]);
    }
  }

  /**
   * <p>
   * Aktualizuje rekord wartoœciami z Query.
   * <p>
   * Jeœli jakieœ pole w Query nie wystêpuje w BufferedRecord to zostanie ono w
   * BufferedRecord wyczyszczone - nie usuniête.
   * 
   * @param query
   * @throws UseDBException
   */
  public void updateFrom(Query query) throws UseDBException {
    for (BufferedRecordField bfl : fieldList) {
      QueryField qf = query.findFieldByName(bfl.getFieldName());
      if (qf != null) {
        try {
          bfl.setFirstValue(qf.getValue());
          bfl.setLength(qf.getDisplaySize());
          updateBeanFromField(bfl);
        } catch (Exception ex) {
          throw new UseDBException(ex);
        }
      } else {
        bfl.clear();
      }
    }
  }

  private void checkTable() throws UseDBException {
    if (tableName == null || primaryKey == null) {
      throw new UseDBException(Messages.getString("BufferedRecord.no-table-name")); //$NON-NLS-1$
    }
  }
  
  protected String primaryKeyWhere(Database database) {
    String clause = ""; //$NON-NLS-1$
    for (String field : primaryKey) {
      if (!"".equals(clause)) { //$NON-NLS-1$
        clause = clause + "\n   AND "; //$NON-NLS-1$
      }
      clause = clause + database.normalizeName(field) + " = :" + field + "_PK"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    return clause;
  }
  
  protected String getWhere(String[] columnNames, Database database) {
    String clause = ""; //$NON-NLS-1$
    for (String field : columnNames) {
      if (!"".equals(clause)) { //$NON-NLS-1$
        clause = clause + "\n   AND "; //$NON-NLS-1$
      }
      clause = clause + database.normalizeName(field) + " = :" + field; //$NON-NLS-1$
    }
    return clause;
  }
  
  protected String columnList(Database database) {
    StringBuilder sb = new StringBuilder(); 
    for (BufferedRecordField bfl : fieldList) {
      if (sb.length() > 0) {
        sb.append(", "); //$NON-NLS-1$
      }
      sb.append(database.normalizeName(bfl.getFieldName()));
    }
    return sb.toString();
  }

  private void primaryKeySetParam(Command command, boolean useOldValue) throws UseDBException {
    BufferedRecordField[] fields = getPrimaryKeyFields();
    for (BufferedRecordField field : fields) {
      updateFieldFromBean(field);
      command
          .paramByName(field.getFieldName() + "_PK") //$NON-NLS-1$
          .setValue(
              (!useOldValue || field.getOldValue() == null || field.getOldValue().isNullValue()) ? field.getValue() : field.getOldValue(),
              SQLUtil.variantToSqlType(field.getValueType()));
    }
  }

  private void primaryKeySetValues(Query query, boolean firstValue) throws VariantException, UseDBException, SQLException, IOException {
    BufferedRecordField[] fields = getPrimaryKeyFields();
    int index = 0;
    for (BufferedRecordField field : fields) {
      if (firstValue) {
        field.setFirstValue(query.getField(index).getValue());
      } else {
        field.setValue(query.getField(index).getValue());
      }
      updateBeanFromField(field);
      index++;
    }
  }
  
  private void updateBeanFromField(BufferedRecordField field) throws UseDBException {
    if (beanObject != null) {
      try {
        field.updateBean(beanObject);
      } catch (Exception ex) {
        throw new UseDBException(ex);
      }
    }
  }
  
  private void updateFieldFromBean(BufferedRecordField field) throws UseDBException {
    if (beanObject != null) {
      try {
        field.updateField(beanObject);
      } catch (Exception ex) {
        throw new UseDBException(ex);
      }
    }
  }
  
  private void updateFieldsFromBean() throws UseDBException {
    if (beanObject != null) {
      for (BufferedRecordField field : fieldList) {
        updateFieldFromBean(field);
      }
    }
  }
  
  /**
   * <p>Pozwala rozsze¿yæ polecenie DELETE
   * @param sqlText
   * @return
   */
  protected String afterDeleteSqlText(String sqlText) {
    return sqlText;
  }
  

  /**
   * <p>
   * Tworzy polecenie usuniêcia rekordu z tabeli.
   * <p>
   * Polecenie jest przygotowane tak aby usun¹æ rekord o wartoœci klucza
   * g³ównego.
   * 
   * @param database
   * @return
   * @throws UseDBException
   */
  public Command deleteCommand(Database database) throws UseDBException {
    checkTable();
    
    Command command = new Command(database);
    command.setSqlText(afterDeleteSqlText("DELETE FROM " + database.normalizeName(tableName) + "\n" + " WHERE " +primaryKeyWhere(database))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    primaryKeySetParam(command, false);

    return command;
  }

  /**
   * <p>Pozwala rozsze¿yæ polecenie INSERT
   * @param sqlText
   * @return
   */
  protected String afterInsertSqlText(String sqlText) {
    return sqlText;
  }

  /**
   * <p>
   * Tworzy polecenie wstawienia rekordu do bazy danych.
   * <p>
   * Wype³nia tylko wartoœci, które zosta³y wprowadzone.
   * 
   * @param database
   * @return
   * @throws UseDBException
   */
  public Command insertCommand(Database database) throws UseDBException {
    if (tableName == null) {
      throw new UseDBException(Messages.getString("BufferedRecord.no-table-name")); //$NON-NLS-1$
    }

    updateFieldsFromBean();
    Command command = new Command(database);
    StringBuilder fields = new StringBuilder();
    StringBuilder params = new StringBuilder();
    for (BufferedRecordField bfl : fieldList) {
      if (!bfl.isNull()) {
        if (fields.length() > 0) {
          fields.append(", "); //$NON-NLS-1$
          params.append(", "); //$NON-NLS-1$
        }
        fields.append(database.normalizeName(bfl.getFieldName()));
        params.append(":" + bfl.getFieldName()); //$NON-NLS-1$
      }
    }
    if (fields.length() == 0) {
      return null;
    }
    command.setSqlText(afterInsertSqlText("INSERT INTO " + database.normalizeName(tableName) + " (" + fields.toString() + ")\n" + "VALUES (" + params + ")" )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    for (BufferedRecordField bfl : fieldList) {
      if (!bfl.isNull()) {
        command.paramByName(bfl.getFieldName()).setValue(bfl.getValue(), SQLUtil.variantToSqlType(bfl.getValueType()));
      }
    }

    return command;
  }

  /**
   * <p>
   * Aktualizuje wartoœæ pola klucza g³ównego wykonuj¹c podane we w³aœciowoœci
   * primaryKeyValueCommand polecenie.
   * 
   * @param database
   * @throws Exception
   */
  public void updatePrimaryKeyValue(Database database) throws Exception {
    Query query = new Query(database);
    query.open(primaryKeyValueCommand);
    try {
      primaryKeySetValues(query, true);
    } finally {
      query.close();
    }
  }

  /**
   * <p>
   * Aktualizuje wartoœæ pola klucza g³ównego wykonuj¹c podane we w³aœciowoœci
   * primaryKeyValueCommand polecenie.
   * 
   * @param database
   * @throws Exception
   */
  public void newPrimaryKeyValue(Database database) throws Exception {
    Query query = new Query(database);
    query.open(primaryKeyValueCommand);
    try {
      primaryKeySetValues(query, false);
    } finally {
      query.close();
    }
  }

  /**
   * <p>Pozwala rozsze¿yæ polecenie UPDATE
   * @param sqlText
   * @return
   */
  protected String afterUpdateSqlText(String sqlText) {
    return sqlText;
  }

  /**
   * <p>
   * Tworzy polecenie aktualizacji rekordu zgodnie z wartoœci¹ klucza g³ównego.
   * 
   * @param database
   * @return
   * @throws UseDBException
   */
  public Command updateCommand(Database database) throws UseDBException {
    checkTable();

    updateFieldsFromBean();
    Command command = new Command(database);
    StringBuilder updates = new StringBuilder();
    for (BufferedRecordField bfl : fieldList) {
      if (bfl.isChanged() && bfl.isUpdatable()) {
        if (updates.length() > 0) {
          updates.append(",\n"); //$NON-NLS-1$
        }
        updates.append("  " + database.normalizeName(bfl.getFieldName()) + " = :" +bfl.getFieldName()); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
    command.setSqlText(afterUpdateSqlText("UPDATE " + database.normalizeName(tableName) + " SET\n" + updates + "\n WHERE " + primaryKeyWhere(database))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    if (updates.length() == 0) {
      return null;
    }
    for (BufferedRecordField bfl : fieldList) {
      if (bfl.isChanged() && bfl.isUpdatable()) {
        command.paramByName(bfl.getFieldName()).setValue(bfl.getValue(), SQLUtil.variantToSqlType(bfl.getValueType()));
      }
    }
    primaryKeySetParam(command, true);

    return command;
  }

  /**
   * <p>
   * Pozwala ustawiæ polecenie pobrania wartoœci wstawionego klucza g³ównego.
   * <p>
   * Domyœlnie ustawione jest na polecenie pobrania
   * "select IDENTITY_VAL_LOCAL() value from dual" (Apache Derby DB).
   * <p>
   * Polecenie musi zwracaæ jedn¹ kolumnê o nazwie "VALUE".
   * 
   * @param primaryKeyValueCommand
   */
  public void setPrimaryKeyValueCommand(String primaryKeyValueCommand) {
    this.primaryKeyValueCommand = primaryKeyValueCommand;
  }

  public String getPrimaryKeyValueCommand() {
    return primaryKeyValueCommand;
  }

  public Object getBeanObject() {
    return beanObject;
  }

  public synchronized void storeToXML(OutputStream os, String comment) throws IOException, DOMException, VariantException {
    if (os == null) {
      throw new IllegalArgumentException("os == null"); //$NON-NLS-1$
    }
    storeToXML(os, comment, "UTF-8"); //$NON-NLS-1$
  }

  public synchronized void storeToXML(OutputStream os, String comment, String encoding) throws IOException, DOMException, VariantException {
    if (os == null) {
      throw new IllegalArgumentException("os == null"); //$NON-NLS-1$
    }
    try {
      XMLUtils.save(this, os, comment, encoding);
    }
    finally {
      os.close();
    }
  }

  public synchronized void loadFromXML(InputStream in) throws IOException, UseDBException {
    if (in == null) {
      throw new IllegalArgumentException("in == null"); //$NON-NLS-1$
    }
    try {
      XMLUtils.load(this, in);
    }
    finally {
      in.close();
    }
  }
  
  public String toString() {
    return
        "tableName:" +tableName +
        ",primaryKey:" +Arrays.toString(primaryKey) +
        ",primaryKeyValueCommand:" +primaryKeyValueCommand +
        ",beanObject:" +(beanObject == null ? "" : beanObject.toString()) +
        ",fieldList:" +Arrays.toString(fieldList.toArray());
  }

}
