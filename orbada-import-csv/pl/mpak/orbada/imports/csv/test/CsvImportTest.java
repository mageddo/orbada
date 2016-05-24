package pl.mpak.orbada.imports.csv.test;

import java.util.Arrays;

import pl.mpak.orbada.imports.csv.engine.CsvImport;
import pl.mpak.orbada.imports.csv.engine.CsvImportColumn;
import pl.mpak.orbada.imports.csv.engine.CsvImportConfiguration;
import pl.mpak.orbada.imports.csv.engine.CsvImportEvent;
import pl.mpak.orbada.imports.csv.engine.CsvImportListener;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseManager;

public class CsvImportTest {

  /**
   * @param args
   * @throws UseDBException 
   */
  public static void main(String[] args) throws UseDBException {
    CsvImportConfiguration config = new CsvImportConfiguration();
    //config.setHeaderPresent(true);
    //config.setDelimiter(';');
    config.setTableName("vmg");
    config.setTablePrimaryKey(new String[] {"ivmg"});
    config.setFileName("d:/x/vmg.csv");
    
//    config.add("schemaname", "schemaname", VariantType.varLong);
//    config.add("tablename", "tablename", VariantType.varString);
//    config.add("tablespace", "tablespace", VariantType.varString);
//    config.add("hasindexes", "hasindexes", VariantType.varBigDecimal);
//    config.add("hasrules", "hasrules", VariantType.varString);
//    config.add("hastriggers", "hastriggers", VariantType.varString);
//    config.setTablePrimaryKey(new String[] {"schemaname", "tablename"});
    
    Database database = DatabaseManager.createDatabase(
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/postgres",
        "postgres", "postgres");
    try {
      CsvImport csv = new CsvImport(config);
      csv.addImportListener(new CsvImportListener() {
        @Override
        public void beforeImportRecord(CsvImportEvent event) {
          if (event.isAnalyzing()) {
          }
        }
        @Override
        public void beforeImport(CsvImportEvent event) {
        }
        @Override
        public void afterImportRecord(CsvImportEvent event) {
        }
        @Override
        public void afterImport(CsvImportEvent event) {
        }
      });
//      for (String[] strs : csv.readFirstRows(10)) {
//        System.out.println(Arrays.toString(strs));
//      }
      //System.out.println(csv.readFirstLines(10));
      CsvImportColumn[] columns = csv.analyze();
      System.out.println("Records: " +csv.getRecordCount());
      config.getColumnList().addAll(Arrays.asList(columns));
      System.out.println(Arrays.toString(columns));
      csv.process(database);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      database.close();
    }
  }

}
