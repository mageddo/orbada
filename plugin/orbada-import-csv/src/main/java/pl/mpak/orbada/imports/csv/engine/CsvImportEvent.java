package pl.mpak.orbada.imports.csv.engine;

import java.util.EventObject;

public class CsvImportEvent extends EventObject {
  
  private static final long serialVersionUID = -8922979598333916315L;
  
  /**
   * <p>Continue import, default for Event
   */
  public static final int CONTINUE_IMPORT = 0;
  /**
   * <p>Skip import record for beforeImportRecord() event
   */
  public static final int SKIP_RECORD = 1;
  /**
   * <p>Abort import, raise an CsvImportAbortedException exception
   */
  public static final int ABORT_IMORT = 2;
  /**
   * <p>Stop import without exception, eg. for stop analyze
   */
  public static final int STOP_IMORT = 3;
  
  private boolean analyzing;
  private String[] values;
  private long recNo;
  private int action = CONTINUE_IMPORT;

  public CsvImportEvent(Object source, boolean analyzing, String[] values, long recNo) {
    super(source);
    this.analyzing = analyzing;
    this.values = values;
    this.recNo = recNo;
  }

  /**
   * <p>For process() false, for analyze() true
   * @return
   */
  public boolean isAnalyzing() {
    return analyzing;
  }

  /**
   * <p>Headers on beforeImport or Values on beforeImportRecord and afterImportRecord
   * @return
   */
  public String[] getValues() {
    return values;
  }

  /**
   * <p>Current importing record or -1 if before/after import event, start at 1
   * @return
   */
  public long getRecNo() {
    return recNo;
  }

  /**
   * <p>Return current cation state for next import move
   * @return
   */
  public int getAction() {
    return action;
  }

  /**
   * <p>Action for next import move
   * <p>SKIP_RECORD works only for CsvImportListener.beforeImportRecord() event
   * @param action one of CONTINUE_IMPORT, SKIP_RECORD or ABORT_IMORT
   */
  public void setAction(int action) {
    this.action = action;
  }

}
