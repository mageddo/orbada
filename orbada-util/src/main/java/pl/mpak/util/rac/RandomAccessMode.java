package pl.mpak.util.rac;

public class RandomAccessMode {

  public static final RandomAccessMode READ = new RandomAccessMode(true, false);
  public static final RandomAccessMode READWRITE = new RandomAccessMode(true, true);

  private final boolean read;
  private final boolean write;

  private RandomAccessMode(final boolean read, final boolean write) {
    this.read = read;
    this.write = write;
  }

  public boolean requestRead() {
    return read;
  }

  public boolean requestWrite() {
    return write;
  }
  
  public String toString() {
    String result = "";
    if (read) {
      result = result +"r";
    }
    if (write) {
      result = result +"w";
    }
    return result;
  }
  
}
