package pl.mpak.orbada.imports.csv.engine;

import java.util.EventListener;

public interface CsvImportListener extends EventListener {
  
  /**
   * @param event
   */
  public void beforeImport(CsvImportEvent event);
  
  public void afterImport(CsvImportEvent event);
  
  /**
   * @param event
   */
  public void beforeImportRecord(CsvImportEvent event);

  public void afterImportRecord(CsvImportEvent event);

}
