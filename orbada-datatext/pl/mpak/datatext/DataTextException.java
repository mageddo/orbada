package pl.mpak.datatext;

public class DataTextException extends Exception {
  private static final long serialVersionUID = 1L;

  public DataTextException() {
  }

  public DataTextException(String message) {
    super(message);
  }

  public DataTextException(Throwable cause) {
    super(cause);
  }

  public DataTextException(String message, Throwable cause) {
    super(message, cause);
  }

}
