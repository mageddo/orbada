package pl.mpak.orbada.imports.csv.engine;

public class CsvImportException extends Exception {

  private static final long serialVersionUID = -5233430781768769934L;

  public CsvImportException() {
    super();
  }

  public CsvImportException(String message) {
    super(message);
  }

  public CsvImportException(Throwable cause) {
    super(cause);
  }

  public CsvImportException(String message, Throwable cause) {
    super(message, cause);
  }

}
