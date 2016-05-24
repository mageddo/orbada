package pl.mpak.orbada.imports.csv.engine;

import pl.mpak.util.variant.VariantType;

public class CsvImportColumn {
  
  private String csvColumnName;
  private String tableColumnName;
  /**
   * <p>SQL command to convert value with tableColumnName parameter 
   * <p>eg. <code>to_char(:COLUMN_NAME, "yyyy-mm-dd")</code>
   * <p>If filled, tableColumnType is ignored and type set as VariantType.varString
   * <p>If action hase %s then will be replaced by parameter name
   */
  private String sqlColumnAction;
  /**
   * VariantType
   */
  private int columnType; 
  
  /**
   * <p>Column length/precision getted from CsvImport.analyze
   */
  private int columnLength;
  /**
   * <p>Scale of numeric values
   */
  private int dataScale;
  /**
   * <p>not null flag getted from CsvImport.analyze
   */
  private boolean notNull;
  private int index;

  public CsvImportColumn() {
  }

  public CsvImportColumn(int index) {
    this.index = index;
    this.columnType = VariantType.varUnassigned;
  }

  public CsvImportColumn(String csvColumnName) {
    this(csvColumnName, null, null, VariantType.varUnassigned);
  }

  public CsvImportColumn(String csvColumnName, String tableColumnName, int columnType) {
    this(csvColumnName, tableColumnName, null, columnType);
  }

  public CsvImportColumn(String csvColumnName, String tableColumnName, String sqlColumnAction, int columnType) {
    this.csvColumnName = csvColumnName;
    this.tableColumnName = tableColumnName;
    this.sqlColumnAction = sqlColumnAction;
    this.columnType = columnType;
  }

  public String getCsvColumnName() {
    return csvColumnName;
  }

  public void setCsvColumnName(String csvColumnName) {
    this.csvColumnName = csvColumnName;
  }

  public String getTableColumnName() {
    return tableColumnName;
  }

  public String getColumnName() {
    return tableColumnName == null ? csvColumnName : tableColumnName;
  }

  public void setTableColumnName(String tableColumnName) {
    this.tableColumnName = tableColumnName;
  }

  public String getSqlColumnAction() {
    return sqlColumnAction;
  }

  public void setSqlColumnAction(String sqlColumnAction) {
    this.sqlColumnAction = sqlColumnAction;
  }

  public int getColumnType() {
    return columnType;
  }

  public void setColumnType(int columnType) {
    this.columnType = columnType;
  }

  public int getColumnLength() {
    return columnLength;
  }

  public void setColumnLength(int columnLength) {
    this.columnLength = columnLength;
  }
  
  protected boolean isNotNull() {
    return notNull;
  }

  protected void setNotNull(boolean notNull) {
    this.notNull = notNull;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  protected int getDataScale() {
    return dataScale;
  }

  protected void setDataScale(int dataPrecision) {
    this.dataScale = dataPrecision;
  }

  @Override
  public String toString() {
    return 
      "[csvColumnName:" +csvColumnName +
      ", tableColumnName:" +tableColumnName +
      ", sqlColumnAction:" +sqlColumnAction +
      ", columnType:" +columnType +
      ", columnLength:" +columnLength +
      ", dataPrecision:" +dataScale +
      ", notNull:" +(notNull ? "true" : "false") +
      ", index:" +index +"]";
  }

}
