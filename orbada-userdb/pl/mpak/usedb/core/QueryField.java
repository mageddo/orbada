/*
 * Created on 2005-08-03
 *
 */
package pl.mpak.usedb.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.Languages;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;

/**
 * @author Andrzej Ka³u¿a
 *
 */
public class QueryField {

  private static Languages language = new Languages(QueryField.class);

  private ResultSet resultSet = null;
  private Query query;
  private ResultSetMetaData rsmd = null;
  private int index = -1;
  private String fieldName = null;
  private boolean readOnly = true;
  private boolean writable = false;
  private int dataType = Types.NULL;
  private int variantType = VariantType.varUnassigned;
  private String dataTypeName = "NULL";
  private String displayName = null;
  private int precision = 0;
  private int scale = 0;
  private int width = 0;
  private int displaySize = 0;
  private int optimalSize = 0;
  private boolean nullable;
  private Class<?> fieldClass = Object.class;
  private InputStream stream = null;
  private long rowNum = 0;
  private String tableName = null;
  private String schemaName = null;
  
  public QueryField(Query query) throws SQLException {
    super();
    setQuery(query);
    //checkResultSet();
  }
  
  public QueryField(Query query, int index) throws SQLException, UseDBException {
    this(query);
    setIndex(index);
    prepareProperties();
  }
  
//  private void checkResultSet() throws UseDBException {
//    if (resultSet == null) {
//      throw new UseDBException(language.getString("err_db_NoResultSetField", new Object[] {fieldName}));
//    }
//  }
  
  private void throwCachedField(String typeName) throws UseDBException {
    throw new UseDBException(language.getString("err_db_FieldTypeNotSupported", new Object[] {typeName}));
  }
  
  private void prepareProperties() throws SQLException {
    fieldName = rsmd.getColumnLabel(this.index);
    if (query.findFieldByName(fieldName) != null) {
      fieldName = fieldName +"_" +this.index;
    }
    readOnly = rsmd.isReadOnly(this.index);
    writable = rsmd.isWritable(this.index);
    dataType = rsmd.getColumnType(this.index);
    nullable = rsmd.isNullable(this.index) == 1;
    dataTypeName = rsmd.getColumnTypeName(this.index);
    if ("NULL".equalsIgnoreCase(dataTypeName)) {
      dataType = Types.VARCHAR;
    }     
    variantType = SQLUtil.sqlTypeToVariant(dataType);
    displayName = fieldName; //rsmd.getColumnLabel(this.index);
    // to jest zakomentowane poniewa¿ jest problem przy odczycie danych
    // binarnych. Gdy poni¿sze jest wywo³ane, nie mo¿na pobraæ getBinaryStream
    // bo siê burzy o to, ¿e jest pobrane dwa razy !!!
//    try {
//      precision = rsmd.getPrecision(this.index);
//    }
//    catch(Throwable e) {
//      ;
//    }
    scale = rsmd.getScale(this.index);
    displaySize = rsmd.getColumnDisplaySize(this.index);
    // on PostgreSQL displaySize may be a big number like 1234567 for numeric or varchar
    if ((dataType == Types.NUMERIC || dataType == Types.DECIMAL) && displaySize > 32) {
      optimalSize = 32;
    }
    else if (dataType == Types.VARCHAR && displaySize > 1000) {
      optimalSize = 1000;
    }
    else {
      optimalSize = displaySize;
    }
    width = displaySize *6 +5;
    if (width > 500) {
      width = 500;
    }
    if (width < 20) {
      width = 20;
    }
    tableName = rsmd.getTableName(this.index);
    schemaName = rsmd.getSchemaName(this.index);
  }
  
  void write(RandomAccessFile raf) throws IOException, VariantException {
    raf.writeInt(index);
    new Variant(fieldName).write(raf);
    raf.writeBoolean(readOnly);
    raf.writeBoolean(writable);
    raf.writeInt(dataType);
    raf.writeInt(variantType);
    raf.writeBoolean(nullable);
    new Variant(dataTypeName).write(raf);
    new Variant(displayName).write(raf);
    raf.writeInt(scale);
    raf.writeInt(displaySize);
    raf.writeInt(optimalSize);
    raf.writeInt(width);
    new Variant(tableName).write(raf);
    new Variant(schemaName).write(raf);
  }
  
  QueryField read(RandomAccessFile raf) throws IOException, VariantException {
    index = raf.readInt();
    fieldName = (String)new Variant().read(raf).getValue();
    readOnly = raf.readBoolean();
    writable = raf.readBoolean();
    dataType = raf.readInt();
    variantType = raf.readInt();
    nullable = raf.readBoolean();
    dataTypeName = (String)new Variant().read(raf).getValue();
    displayName = new Variant().read(raf).getString();
    scale = raf.readInt();
    displaySize = raf.readInt();
    optimalSize = raf.readInt();
    width = raf.readInt();
    tableName = (String)new Variant().read(raf).getValue();
    schemaName = (String)new Variant().read(raf).getValue();
    return this;
  }

  private void setIndex(int index) {
    this.index = index +1;
  }

  /**
   * Zwraca indeks kolumny tabeli, zaczynaj¹c od wartoœci 1
   * @return
   */
  public int getIndex() {
    return index;
  }
  
  public boolean isReadOnly() {
    return readOnly;
  }

  public boolean isWritable() {
    return writable;
  }

  public ResultSet getResultSet() {
    return resultSet;
  }
  
  private void setResultSet(ResultSet rset) throws SQLException {
    this.resultSet = rset;
    if (rset != null) {
      rsmd = rset.getMetaData();
    }
  }
  
  private void setQuery(Query query) throws SQLException {
    this.query = query;
    setResultSet(query.getResultSet());
  }
  
  public Query getQuery() {
    return this.query;
  }

  public ResultSetMetaData getMetaData() {
    return rsmd;
  }
  
  public Class<?> getFieldClass() {
    return fieldClass;
  }
  
  public String getFieldName() {
    return fieldName;
  }

  public int getDataType() {
    return dataType;
  }

  public int getVariantType() {
    return variantType;
  }

  public String getDataTypeName() {
    return dataTypeName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public int getPrecision() {
    return precision;
  }

  public int getScale() {
    return scale;
  }

  public int getWidth() {
    return width;
  }
  
  public boolean isNull() throws UseDBException, VariantException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().isNullValue();
    }
    else {
      return resultSet.getObject(index) == null;
    }
  }

  public int getInteger() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getInteger();
    }
    else {
      return resultSet.getInt(index);
    }
  }
  
  public byte getByte() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getByte();
    }
    else {
      return resultSet.getByte(index);
    }
  }
  
  public float getFloat() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getFloat();
    }
    else {
      return resultSet.getFloat(index);
    }
  }
  
  public double getDouble() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getDouble();
    }
    else {
      return resultSet.getDouble(index);
    }
  }
  
  public boolean getBoolean() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getBoolean();
    }
    else {
      return resultSet.getBoolean(index);
    }
  }
  
  public short getShort() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getShort();
    }
    else {
      return resultSet.getShort(index);
    }
  }
  
  public long getLong() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getLong();
    }
    else {
      return resultSet.getLong(index);
    }
  }
  
  /**
   * Funkcja pozwala pobraæ ci¹g znaków z pola lub jeœli zostanie wywo³ana dla
   * pola LONG, BLOB, CLOB to przetworzy dane ze strumienia na ci¹g znaków.
   * Funkcja w takim przypadku deklaruje bufor 1024 bajtów i sk³ada z nich obiekt
   * typu String, dla du¿ych zbiorów mo¿liwe, ¿e bêdzie to troszkê trwa³o.  
   * 
   * @return Ci¹g znaków
   * @throws SQLException 
   * @throws IOException 
   * @throws UseDBException 
   * @throws VariantException 
   * @throws SQLException
   * @throws IOException
   */
  public String getString() throws VariantException, UseDBException, IOException, SQLException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getString();
    }
    else {
      Variant value = new Variant();
      query.variantByType(dataType, index, value);
      return value.getString();
//      if (dataType == Types.LONGVARBINARY || 
//          dataType == Types.VARBINARY || 
//          dataType == Types.LONGVARCHAR ||
//          dataType == Types.CLOB ||
//          dataType == Types.BINARY) {
//        InputStream strm = getAsciiStream();
//        if (strm != null) {
//          return new String(StreamUtil.stream2Array(strm));
//        }
//        else {
//          return null;
//        }
//      }
//      else if (dataType == Types.BLOB) {
//        InputStream strm = getBinaryStream();
//        if (strm != null) {
//          return new String(StreamUtil.stream2Array(strm));
//        }
//        else {
//          return null;
//        }
//      }
//      else if (!isNull()) {
//        return resultSet.getString(index);
//      }
//      else {
//        return "";
//      }
    }
  }
  
  public Date getDate() throws VariantException, UseDBException, ParseException, SQLException, IOException {
    if (query.getCacheData()) {
      return (Date) query.getCurrentRecord().getField(index -1).getValue().getDate();
    }
    else {
      return resultSet.getDate(index);
    }
  }
  
  public Time getTime() throws VariantException, UseDBException, ParseException, SQLException, IOException {
    if (query.getCacheData()) {
      return (Time)query.getCurrentRecord().getField(index -1).getValue().getTime();
    }
    else {
      return resultSet.getTime(index);
    }
  }
  
  public Timestamp getTimestamp() throws VariantException, UseDBException, ParseException, SQLException, IOException {
    if (query.getCacheData()) {
      return (Timestamp) query.getCurrentRecord().getField(index -1).getValue().getTimestamp();
    }
    else {
      return resultSet.getTimestamp(index);
    }
  }
  
  public Array getArray() throws UseDBException, SQLException {
    if (query.getCacheData()) {
      throwCachedField("Array");
    }
    return resultSet.getArray(index);
  }
  
  public Blob getBlob() throws UseDBException, SQLException {
    if (query.getCacheData()) {
      throwCachedField("BLOB");
    }
    return resultSet.getBlob(index);
  }
  
  public BigDecimal getBigDecimal() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getBigDecimal();
    }
    else {
      return resultSet.getBigDecimal(index);
    }
  }
  
  public Clob getClob() throws UseDBException, SQLException {
    if (query.getCacheData()) {
      throwCachedField("CLOB");
    }
    return resultSet.getClob(index);
  }
  
  public InputStream getAsciiStream() throws UseDBException, SQLException {
    if (query.getCacheData()) {
      throwCachedField("AsciiStream");
    }
    if (rowNum != resultSet.getRow()) {
      rowNum = resultSet.getRow();
      return stream = resultSet.getAsciiStream(index);
    }
    return stream;
  }
  
  public InputStream getBinaryStream() throws UseDBException, SQLException {
    if (query.getCacheData()) {
      throwCachedField("BinaryStream");
    }
    if (rowNum != resultSet.getRow()) {
      rowNum = resultSet.getRow();
      return resultSet.getBinaryStream(index);
    }
    return stream;
  }
  
  public Object getObject() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getObject();
    }
    else {
      return resultSet.getObject(index);
    }
  }

  public Variant getValue() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue();
    }
    else {
      Variant value = new Variant();
      query.variantByType(dataType, index, value);
      return value;
    }
  }

  public String getValueClassName() throws VariantException, UseDBException, SQLException, IOException {
    if (query.getCacheData()) {
      return query.getCurrentRecord().getField(index -1).getValue().getValueClassName();
    }
    else {
      Object o = resultSet.getObject(index);
      return (o != null ? o.getClass().getName() : "null");
    }
  }

  public void setDisplaySize(int length) {
    this.displaySize = length;
  }

  public int getDisplaySize() {
    return displaySize;
  }

  public int getOptimalSize() {
    return optimalSize;
  }

  public void setOptimalSize(int optimalSize) {
    this.optimalSize = optimalSize;
  }

  public boolean isNullable() {
    return nullable;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public String getTableName() {
    return tableName;
  }

}
