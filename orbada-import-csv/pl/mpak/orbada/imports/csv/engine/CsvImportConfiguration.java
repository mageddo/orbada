package pl.mpak.orbada.imports.csv.engine;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.csvreader.CsvReader;
import java.io.File;
import java.util.Arrays;
import pl.mpak.util.StringUtil;


public class CsvImportConfiguration {
  
  public enum ImportMode {
    /**
     * <p>Create table before import and insert all records
     */
    CREATE_TABLE_INSERT,
    /**
     * <p>Truncate table before import and insert all records
     */
    TRUNCATE_AND_INSERT,
    /**
     * <p>Insert records only if they are not exists
     */
    INSERT_NEW,
    /**
     * <p>Update records if they exists, otherwise insert
     */
    UPDATE_OR_INSERT,
    /**
     * <p>Delete records that exists
     */
    DELETE_ONLY,
    /**
     * <p>Update records that exists
     */
    UPDATE_ONLY,
    /**
     * <p>Insert all records
     */
    INSERT_ALL
  }
  
  private String fileName;
  private char delimiter = ',';
  private int escapeMode = CsvReader.ESCAPE_MODE_BACKSLASH;
  private char recordDelimiter = '\n';
  private boolean skipEmptyRecords = true;
  private char textQualifier = '"';
  private boolean trimWhitespace = true;
  private boolean useTextQualifier = true;
  private boolean ignoreErrors = false;
  private String encoding = "UTF-8";
  private boolean headerPresent = true;
  private int precisionRound = 10;
  
  private ImportMode importMode = ImportMode.INSERT_ALL;

  private boolean tableAutomatic = true;
  private boolean addNullCheck = true;
  private boolean addPrimaryKey = false;
  private String tableName;
  private String[] tablePrimaryKey;
  private String tableCreationCommand;
  
  private ArrayList<CsvImportColumn> columnList;
  
  public CsvImportConfiguration() {
    columnList = new ArrayList<CsvImportColumn>();
  }
  
  public CsvReader configureReader(CsvReader csv) {
    csv.setDelimiter(delimiter);
    csv.setEscapeMode(escapeMode);
    csv.setRecordDelimiter(recordDelimiter);
    csv.setSkipEmptyRecords(skipEmptyRecords);
    csv.setTextQualifier(textQualifier);
    csv.setTrimWhitespace(trimWhitespace);
    csv.setUseTextQualifier(useTextQualifier);
    return csv;
  }
  
  public CsvReader createReader() throws FileNotFoundException {
    return configureReader(new CsvReader(fileName, delimiter, Charset.forName(encoding)));
  }

  public char getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(char delimiter) {
    this.delimiter = delimiter;
  }

  public void setDelimiter(String delimiter) {
    if (!StringUtil.isEmpty(delimiter)) {
      this.delimiter = delimiter.charAt(0);
    }
  }

  public int getEscapeMode() {
    return escapeMode;
  }

  public void setEscapeMode(int escapeMode) {
    this.escapeMode = escapeMode;
  }

  public char getRecordDelimiter() {
    return recordDelimiter;
  }

  public void setRecordDelimiter(char recordDelimiter) {
    this.recordDelimiter = recordDelimiter;
  }

  public void setRecordDelimiter(String recordDelimiter) {
    if (!StringUtil.isEmpty(recordDelimiter)) {
      this.recordDelimiter = recordDelimiter.charAt(0);
    }
  }

  public boolean isSkipEmptyRecords() {
    return skipEmptyRecords;
  }

  public void setSkipEmptyRecords(boolean skipEmptyRecords) {
    this.skipEmptyRecords = skipEmptyRecords;
  }

  public char getTextQualifier() {
    return textQualifier;
  }

  public void setTextQualifier(char textQualifier) {
    this.textQualifier = textQualifier;
  }

  public void setTextQualifier(String textQualifier) {
    if (!StringUtil.isEmpty(textQualifier)) {
      this.textQualifier = textQualifier.charAt(0);
    }
  }

  public boolean isTrimWhitespace() {
    return trimWhitespace;
  }

  public void setTrimWhitespace(boolean trimWhitespace) {
    this.trimWhitespace = trimWhitespace;
  }

  public boolean isUseTextQualifier() {
    return useTextQualifier;
  }

  public void setUseTextQualifier(boolean useTextQualifier) {
    this.useTextQualifier = useTextQualifier;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
  
  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public CsvImportColumn add(String csvColumnName, String tableColumnName, String sqlColumnAction, int tableColumnType) {
    CsvImportColumn c = new CsvImportColumn(csvColumnName, tableColumnName, sqlColumnAction, tableColumnType);
    return add(c);
  }

  public CsvImportColumn add(String csvColumnName, String tableColumnName, int tableColumnType) {
    return add(csvColumnName, tableColumnName, null, tableColumnType);
  }
  
  public CsvImportColumn add(CsvImportColumn column) {
    column.setIndex(columnList.size());
    columnList.add(column);
    return column;
  }

  public void add(CsvImportColumn[] columns) {
    if (columns == null) {
      throw new IllegalArgumentException("column is null");
    }
    columnList.addAll(Arrays.asList(columns));
  }

  public ArrayList<CsvImportColumn> getColumnList() {
    return columnList;
  }

  public boolean isIgnoreErrors() {
    return ignoreErrors;
  }

  /**
   * <p>Ignore all errors on import file
   * @param ignoreErrors
   */
  public void setIgnoreErrors(boolean ignoreErrors) {
    this.ignoreErrors = ignoreErrors;
  }

  public String[] getTablePrimaryKey() {
    return tablePrimaryKey;
  }

  /**
   * <p>Set primary key fields for created table primary key
   * @param tablePrimaryKey
   * @see addPrimaryKey
   * @see importMode = ImportMode.CREATE_TABLE_INSERT
   */
  public void setTablePrimaryKey(String[] tablePrimaryKey) {
    this.tablePrimaryKey = tablePrimaryKey;
  }

  public boolean isHeaderPresent() {
    return headerPresent;
  }

  public void setHeaderPresent(boolean headerPresent) {
    this.headerPresent = headerPresent;
  }

  public int getPrecisionRound() {
    return precisionRound;
  }

  /**
   * <p>Number, varchar round ength for create table
   * @param precisionRound
   * @see importMode = ImportMode.CREATE_TABLE_INSERT
   */
  public void setPrecisionRound(int precisionRound) {
    this.precisionRound = precisionRound;
  }

  public ImportMode getImportMode() {
    return importMode;
  }

  public void setImportMode(ImportMode importMode) {
    this.importMode = importMode;
  }

  public boolean isAddNullCheck() {
    return addNullCheck;
  }

  /**
   * <p>Set to true if you want add null check for creating table
   * @param addNullCheck
   * @see importMode = ImportMode.CREATE_TABLE_INSERT
   */
  public void setAddNullCheck(boolean addNullCheck) {
    this.addNullCheck = addNullCheck;
  }

  public boolean isAddPrimaryKey() {
    return addPrimaryKey;
  }

  /**
   * <p>Set to true if you want add primary key for creating table
   * @param addPrimaryKey
   * @see importMode = ImportMode.CREATE_TABLE_INSERT
   */
  public void setAddPrimaryKey(boolean addPrimaryKey) {
    this.addPrimaryKey = addPrimaryKey;
  }

  /**
   * set or get user table creation command (not generated automatically)
   * @return 
   */
  public String getTableCreationCommand() {
    return tableCreationCommand;
  }

  public void setTableCreationCommand(String tableCreationCommand) {
    this.tableCreationCommand = tableCreationCommand;
  }

  /**
   * <p>All table settings are automatic
   * @return 
   */
  public boolean isTableAutomatic() {
    return tableAutomatic;
  }

  public void setTableAutomatic(boolean tableAutomatic) {
    this.tableAutomatic = tableAutomatic;
  }
  
  public String getImportTableName() {
    if (tableAutomatic) {
      return new File(fileName).getName().replaceFirst("[.][^.]+$", "");
    }
    return tableName;
  }
  
  public String[] getImportTablePrimaryKey() {
    if (tableAutomatic && columnList != null && !columnList.isEmpty()) {
      return new String[] {columnList.get(0).getColumnName()};
    }
    return tablePrimaryKey;
  }
  
}
