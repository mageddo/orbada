package pl.mpak.orbada.imports.csv.engine;

public class CsvImportSqlException extends CsvImportException {

  private static final long serialVersionUID = -7907484347060911624L;

  public CsvImportSqlException() {
    super();
  }

  public CsvImportSqlException(String message) {
    super(message);
  }

  public CsvImportSqlException(Throwable cause) {
    super(cause);
  }

  public CsvImportSqlException(String message, Throwable cause) {
    super(message, cause);
  }

}
