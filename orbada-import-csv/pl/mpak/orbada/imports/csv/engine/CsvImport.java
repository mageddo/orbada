package pl.mpak.orbada.imports.csv.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.event.EventListenerList;

import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.VariantType;

import com.csvreader.CsvReader;

public class CsvImport {
  
  private CsvImportConfiguration configuration;
  private final EventListenerList importListenerList = new EventListenerList();
  private volatile long recordCount;

  public enum Event {
    BEFORE_IMPORT, AFTER_IMPORT, BEFORE_RECORD, AFTER_RECORD
  }

  public CsvImport() {
  }

  public CsvImport(CsvImportConfiguration configuration) {
    this.configuration = configuration;
  }

  public void addImportListener(CsvImportListener listener) {
    synchronized (importListenerList) {
      importListenerList.add(CsvImportListener.class, listener);
    }
  }

  public void removeImportListener(CsvImportListener listener) {
    synchronized (importListenerList) {
      importListenerList.remove(CsvImportListener.class, listener);
    }
  }

  public int fireQueryListener(Event event, String[] strings, long recNo, boolean analyzing) {
    synchronized (importListenerList) {
      int result = CsvImportEvent.CONTINUE_IMPORT;
      CsvImportEvent eo = new CsvImportEvent(this, analyzing, strings, recNo);
      CsvImportListener[] listeners = importListenerList.getListeners(CsvImportListener.class);
      for (int i = 0; i < listeners.length; i++) {
        switch (event) {
          case BEFORE_IMPORT:
            listeners[i].beforeImport(eo);
            switch (eo.getAction()) {
              case CsvImportEvent.ABORT_IMORT: 
                result = CsvImportEvent.ABORT_IMORT;
                break;
            }
            break;
          case AFTER_IMPORT:
            listeners[i].afterImport(eo);
            break;
          case BEFORE_RECORD:
            listeners[i].beforeImportRecord(eo);
            switch (eo.getAction()) {
              case CsvImportEvent.ABORT_IMORT: 
                result = CsvImportEvent.ABORT_IMORT;
                break;
              case CsvImportEvent.SKIP_RECORD: 
                result = CsvImportEvent.SKIP_RECORD;
                break;
            }
            break;
          case AFTER_RECORD:
            listeners[i].afterImportRecord(eo);
            break;
        }
      }
      return result;
    }
  }

  public CsvImportConfiguration getConfiguration() {
    return configuration;
  }
  
  private int detectType(String value, CsvImportColumn column) {
    int dataType = VariantType.varString;
    
    if (StringUtil.isEmpty(value)) {
      dataType = VariantType.varNull;
    }
    else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("on")) {
      dataType = VariantType.varBoolean;
    }
    else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("off")) {
      dataType = VariantType.varBoolean;
    }
    else {
      try {
        BigDecimal bd = new BigDecimal(value);
        if (bd.scale() > column.getDataScale()) {
          column.setDataScale(bd.scale());
        }
        dataType = VariantType.varBigDecimal;
      } catch (java.lang.NumberFormatException e) {
      }
      try {
        DateFormat.getDateTimeInstance().parse(value);
        dataType = VariantType.varTimestamp;
      } catch (ParseException e) {
        try {
          DateFormat.getDateInstance().parse(value);
          dataType = VariantType.varDate;
        } catch (ParseException ex) {
        }
      }
    }
        
    if (dataType != VariantType.varNull && column.getColumnType() != VariantType.varString) {
      if (column.getColumnType() == VariantType.varUnassigned) {
        column.setColumnType(dataType);
      }
      else {
        switch (column.getColumnType()) {
          case VariantType.varBoolean: {
            if (dataType != VariantType.varBoolean) {
              column.setColumnType(VariantType.varString);
            }
            break;
          }
          case VariantType.varBigDecimal: {
            if (dataType != VariantType.varBigDecimal && dataType != VariantType.varInteger) {
              column.setDataScale(0);
              column.setColumnType(VariantType.varString);
            }
            break;
          }
          case VariantType.varDate: {
            if (dataType == VariantType.varTimestamp) {
              column.setColumnType(VariantType.varTimestamp);
            }
            else if (dataType != VariantType.varDate) {
              column.setColumnType(VariantType.varString);
            }
            break;
          }
          case VariantType.varTimestamp: {
            if (dataType != VariantType.varDate && dataType != VariantType.varTimestamp) {
              column.setColumnType(VariantType.varString);
            }
            break;
          }
        }
      }
    }

    return column.getColumnType();
  }
  
  public String readFirstLines(int rowCount) throws FileNotFoundException {
    if (configuration == null) {
      throw new IllegalArgumentException("configuration is null");
    }
    StringBuilder sb = new StringBuilder();
    String NL = System.getProperty("line.separator");
    Scanner scanner = new Scanner(new FileInputStream(configuration.getFileName()), configuration.getEncoding());
    try {
      while (scanner.hasNextLine()) {
        if (--rowCount < 0) {
          break;
        }
        sb.append(scanner.nextLine()).append(NL);
      }
    }
    finally{
      scanner.close();
    }
    return sb.toString();
  }
  
  public ArrayList<String[]> readFirstRows(int rowCount) throws IOException {
    if (configuration == null) {
      throw new IllegalArgumentException("configuration is null");
    }

    ArrayList<String[]> rows = new ArrayList<String[]>();
    CsvReader csv = configuration.createReader();
    try {
      if (configuration.isHeaderPresent()) {
        csv.readHeaders();
      }
      rows.add(csv.getHeaders());
      while (csv.readRecord()) {
        if (--rowCount < 0) {
          break;
        }
        rows.add(csv.getValues());
      }
    }
    finally {
      csv.close();
    }
    return rows;
  }
  
  /**
   * <p>Analyzing CSV file for geting column and values properties
   * @throws IOException 
   * @throws CsvImportAbortedException 
   */
  public CsvImportColumn[] analyze() throws IOException, CsvImportAbortedException {
    if (configuration == null) {
      throw new IllegalArgumentException("configuration is null");
    }

    CsvImportColumn[] columns = null;
    CsvReader csv = configuration.createReader();
    recordCount = 0;
    try {
      if (configuration.isHeaderPresent()) {
        csv.readHeaders();
        String[] headers = csv.getHeaders();
        if (headers != null && headers.length == 1) {
          Character ch = null;
          if (StringUtil.charCount(headers[0], ';') >= 1) {
            ch = ';';
          }
          else if (StringUtil.charCount(headers[0], ',') >= 1) {
            ch = ',';
          }
          else if (StringUtil.charCount(headers[0], '|') >= 1) {
            ch = '|';
          }
          else if (StringUtil.charCount(headers[0], '\t') >= 1) {
            ch = '\t';
          }
          else if (StringUtil.charCount(headers[0], ' ') >= 1) {
            ch = ' ';
          }
          if (ch != null) {
            csv.close();
            configuration.setDelimiter(ch);
            csv = configuration.createReader();
            csv.readHeaders();
            headers = csv.getHeaders();
            if (headers != null && headers.length <= 1) {
              configuration.setDelimiter(csv.getDelimiter());
            }
          }
        }
        columns = new CsvImportColumn[headers.length];
        for (int i = 0; i < headers.length; i++) {
          columns[i] = new CsvImportColumn(headers[i]);
          columns[i].setNotNull(true);
          columns[i].setIndex(i);
        }
        int eventResult = fireQueryListener(Event.BEFORE_IMPORT, csv.getHeaders(), -1, true);
        if (eventResult == CsvImportEvent.ABORT_IMORT) {
          throw new CsvImportAbortedException();
        }
        else if (eventResult == CsvImportEvent.STOP_IMORT) {
          return columns;
        }
      }
      while (csv.readRecord()) {
        recordCount++;
        String[] values = csv.getValues();
        // create columns if no header presents
        if (columns == null) {
          columns = new CsvImportColumn[values.length];
          for (int i = 0; i < values.length; i++) {
            columns[i] = new CsvImportColumn(i);
            columns[i].setNotNull(true);
          }
        }
        int eventResult = fireQueryListener(Event.BEFORE_RECORD, csv.getValues(), csv.getCurrentRecord() +1, true);
        if (eventResult == CsvImportEvent.ABORT_IMORT) {
          throw new CsvImportAbortedException();
        }
        else if (eventResult == CsvImportEvent.CONTINUE_IMPORT) {
          for (int i = 0; i < values.length; i++) {
            CsvImportColumn column = columns[i];
            if (values[i].length() > column.getColumnLength()) {
              column.setColumnLength(values[i].length());
            }
            if (StringUtil.isEmpty(values[i])) {
              column.setNotNull(false);
            }
            detectType(values[i], column);
          }
          fireQueryListener(Event.AFTER_RECORD, csv.getValues(), csv.getCurrentRecord() +1, true);
        }
      }
      fireQueryListener(Event.AFTER_IMPORT, null, -1, true);
    }
    finally {
      csv.close();
    }
    return columns;
  }
  
  public void truncateTable(Database database) throws Exception {
    database.executeCommand("TRUNCATE TABLE " +configuration.getImportTableName());
  }
  
  private String primaryKeyScript() {
    String[] pk = configuration.getImportTablePrimaryKey();
    if (pk != null && pk.length > 0) {
      StringBuilder sb = new StringBuilder();
      for (String field : pk) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(field);
      }
      return "primary key (" +sb.toString() +")";
    }
    return "";
  }
  
  public String createTableScript(Database database) throws Exception {
    StringBuilder sb = new StringBuilder();
    for (CsvImportColumn c : configuration.getColumnList()) {
      if (sb.length() > 0) {
        sb.append(",\n");
      }
      String columnName = c.getColumnName();
      sb.append("  ").append(columnName);
      sb.append(" ").append(SQLUtil.typeToString(SQLUtil.variantToSqlType(c.getColumnType())));
      if (c.getColumnType() == VariantType.varString) {
        sb.append("(").append(configuration.getPrecisionRound() == 1 ? c.getColumnLength() : ((c.getColumnLength() /configuration.getPrecisionRound()) +1) *configuration.getPrecisionRound()).append(")");
      }
      else if (c.getColumnType() == VariantType.varBigDecimal) {
        sb.append("(").
          append(configuration.getPrecisionRound() == 1 ? c.getColumnLength() : ((c.getColumnLength() /configuration.getPrecisionRound()) +1) *configuration.getPrecisionRound()).
          append(",").
          append(c.getDataScale()).
          append(")");
      }
      if ((configuration.isAddNullCheck() && c.isNotNull()) || StringUtil.anyOfString(columnName, configuration.getImportTablePrimaryKey(), true) >= 0) {
        sb.append(" NOT NULL");
      }
    }
    return
      "CREATE TABLE " +configuration.getImportTableName() +" (\n" +
        sb.toString() +
        (configuration.isAddPrimaryKey() ? ",\n  " +primaryKeyScript() : "") +
      "\n)";
  }
  
  public void createTable(Database database) throws Exception {
    database.executeCommand(createTableScript(database));
  }
  
  public void process(final Database database) throws CsvImportSqlException, IOException, CsvImportAbortedException {
    if (configuration == null) {
      throw new IllegalArgumentException("configuration is null");
    }
    if (configuration.getColumnList() == null || configuration.getColumnList().isEmpty()) {
      throw new IllegalArgumentException("column list is empty");
    }
    
    if (configuration.getImportMode() == CsvImportConfiguration.ImportMode.CREATE_TABLE_INSERT) {
      try {
        if (!StringUtil.isEmpty(configuration.getTableCreationCommand())) {
          database.executeCommand(configuration.getTableCreationCommand());
        }
        else {
          createTable(database);
        }
      }
      catch (Exception ex) {
        if (!configuration.isIgnoreErrors()) {
          throw new CsvImportSqlException(ex);
        }
      }
    }
    if (configuration.getImportMode() == CsvImportConfiguration.ImportMode.TRUNCATE_AND_INSERT) {
      try {
        truncateTable(database);
      }
      catch (Exception ex) {
        if (!configuration.isIgnoreErrors()) {
          throw new CsvImportSqlException(ex);
        }
      }
    }
    
    DefaultBufferedRecord qbr = new DefaultBufferedRecord(database, configuration.getImportTableName(), (String[])null) {
      private String afterSqlText(String sqlText) {
        for (CsvImportColumn col : configuration.getColumnList()) {
          if (!StringUtil.isEmpty(col.getSqlColumnAction())) {
            String value = col.getSqlColumnAction();
            if (col.getSqlColumnAction().indexOf("%s") >= 0) {
              value = String.format(value, new Object[] {":" +col.getColumnName()});
            }
            sqlText = StringUtil.replaceString(sqlText, ":" +col.getColumnName(), value);
          }
        }
        return sqlText;
      }
      @Override
      protected String afterInsertSqlText(String sqlText) {
        return afterSqlText(sqlText);
      }
      @Override
      protected String afterUpdateSqlText(String sqlText) {
        return afterSqlText(sqlText);
      }
    };
    qbr.setPrimaryKeys(configuration.getImportTablePrimaryKey());
    for (CsvImportColumn col : configuration.getColumnList()) {
      qbr.add(col.getColumnName(), StringUtil.isEmpty(col.getSqlColumnAction()) ? col.getColumnType() : VariantType.varString, true);
    }
    
    recordCount = 0;
    CsvReader csv = configuration.createReader();
    try {
      if (configuration.isHeaderPresent()) {
        csv.readHeaders();
        int eventResult = fireQueryListener(Event.BEFORE_IMPORT, csv.getHeaders(), -1, false);
        if (eventResult == CsvImportEvent.ABORT_IMORT) {
          throw new CsvImportAbortedException();
        }
        else if (eventResult == CsvImportEvent.STOP_IMORT) {
          return;
        }
      }
      while (csv.readRecord()) {
        recordCount++;
        int eventResult = fireQueryListener(Event.BEFORE_RECORD, csv.getValues(), csv.getCurrentRecord() +1, false);
        if (eventResult == CsvImportEvent.ABORT_IMORT) {
          throw new CsvImportAbortedException();
        }
        else if (eventResult == CsvImportEvent.CONTINUE_IMPORT) {
          qbr.clearValues();
          for (CsvImportColumn col : configuration.getColumnList()) {
            String value;
            if (configuration.isHeaderPresent()) {
              value = csv.get(col.getCsvColumnName());
            }
            else {
              value = csv.get(col.getIndex());
            }
            qbr.fieldByName(col.getColumnName()).setString(value);
          }
          try {
            if (configuration.getImportMode() == CsvImportConfiguration.ImportMode.TRUNCATE_AND_INSERT || 
                configuration.getImportMode() == CsvImportConfiguration.ImportMode.INSERT_ALL ||
                configuration.getImportMode() == CsvImportConfiguration.ImportMode.CREATE_TABLE_INSERT) {
              qbr.applyInsert();
            }
            else if (configuration.getImportMode() == CsvImportConfiguration.ImportMode.UPDATE_OR_INSERT) {
              if (qbr.recordExists()) {
                qbr.applyUpdate();
              }
              else {
                qbr.applyInsert();
              }
            }
            else if (configuration.getImportMode() == CsvImportConfiguration.ImportMode.INSERT_NEW) {
              if (!qbr.recordExists()) {
                qbr.applyInsert();
              }
            }
            else if (configuration.getImportMode() == CsvImportConfiguration.ImportMode.DELETE_ONLY) {
              if (qbr.recordExists()) {
                qbr.applyDelete();
              }
            }
            else if (configuration.getImportMode() == CsvImportConfiguration.ImportMode.UPDATE_ONLY) {
              if (qbr.recordExists()) {
                qbr.applyUpdate();
              }
            }
          }
          catch (Exception ex) {
            if (!configuration.isIgnoreErrors()) {
              throw new CsvImportSqlException(ex);
            }
          }
          fireQueryListener(Event.AFTER_RECORD, csv.getValues(), csv.getCurrentRecord() +1, false);
        }
      }
      fireQueryListener(Event.AFTER_IMPORT, null, -1, false);
    }
    finally {
      csv.close();
    }
  }

  /**
   * <p>Returns record count of analyzed or processed rows
   * <p>It is thread save so you can use them for current imported record number
   * @return
   */
  public long getRecordCount() {
    return recordCount;
  }
  
}
