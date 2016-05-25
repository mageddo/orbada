package pl.mpak.util.stream;

import java.io.IOException;
import java.io.OutputStream;

public class BufferedOutputStream extends OutputStream {

  protected byte buf[];

  protected int count;

  public BufferedOutputStream() {
    this(32);
  }

  public BufferedOutputStream(int size) {
    if (size < 0) {
      throw new IllegalArgumentException("Negative initial size: " + size);
    }
    buf = new byte[size];
  }

  public synchronized void write(int b) {
    int newcount = count + 1;
    if (newcount > buf.length) {
      byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
      System.arraycopy(buf, 0, newbuf, 0, count);
      buf = newbuf;
    }
    buf[count] = (byte) b;
    count = newcount;
  }

  public synchronized void write(byte b[], int off, int len) {
    if ((off < 0) || (off > b.length) || (len < 0)
        || ((off + len) > b.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    }
    else if (len == 0) {
      return;
    }
    int newcount = count + len;
    if (newcount > buf.length) {
      byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
      System.arraycopy(buf, 0, newbuf, 0, count);
      buf = newbuf;
    }
    System.arraycopy(b, off, buf, count, len);
    count = newcount;
  }

  public byte[] getBuffer() {
    return buf;
  }

  public int size() {
    return count;
  }
  
  public synchronized void reset() {
    count = 0;
  }

  public String toString() {
    return new String(buf, 0, count);
  }

  public void close() throws IOException {
  }

}
