package pl.mpak.orbada.imports.csv.engine;

public class CsvImportAbortedException extends CsvImportException {

  private static final long serialVersionUID = 3180048447365450013L;

  public CsvImportAbortedException() {
    super();
  }

  public CsvImportAbortedException(String message) {
    super(message);
  }

  public CsvImportAbortedException(Throwable cause) {
    super(cause);
  }

  public CsvImportAbortedException(String message, Throwable cause) {
    super(message, cause);
  }

}
