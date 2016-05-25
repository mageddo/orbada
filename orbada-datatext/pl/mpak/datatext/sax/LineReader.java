package pl.mpak.datatext.sax;

import java.io.IOException;
import java.io.InputStream;

import pl.mpak.datatext.DataTextException;
import pl.mpak.datatext.util.DataTextUtils;

public class LineReader {

  private LineHandler lineHandler;
  private String charsetName;
  private byte[] buffer = null;
  private int bufferCount = 0;
  
  /**
   * <p>Po do³¹czeniu doscharset.jar do class_path do dyspozycji bêd¹ charsetName<br>
   * "CP-896" dla MAZOVIA i<br>
   * "CP-852" dla LATIN2
   * @param inputStream
   * @param lineHandler
   * @param charsetName
   * @throws IOException 
   */
  public LineReader(LineHandler lineHandler, String charsetName) {
    if (lineHandler == null) {
      throw new IllegalArgumentException("lineHandler == null");
    }
    this.lineHandler = lineHandler;
    this.charsetName = charsetName;
  }
  
  private void appendByte(int ch) {
    if (buffer == null) {
      buffer = new byte[1024];
    }
    else if (bufferCount == buffer.length) {
      buffer = DataTextUtils.copyOf(buffer, buffer.length +1024);
    }
    buffer[bufferCount++] = (byte)ch;
  }
  
  private void clearBuffer() {
    bufferCount = 0;
  }
  
  public void read(InputStream inputStream) throws IOException, DataTextException {
    int ch;
    
    while ((ch = inputStream.read()) != -1) {
      if ((ch != (int)'\n') && (ch != (int)'\r')) {
        appendByte(ch);
      }
      else if (bufferCount > 0) {
        boolean stop;
        if (charsetName != null) {
          stop = lineHandler.lineReaded(new String(buffer, 0, bufferCount, charsetName));
        }
        else {
          stop = lineHandler.lineReaded(new String(buffer, 0, bufferCount));
        }
        clearBuffer();
        if (stop) {
          break;
        }
      }
    }
    if (bufferCount > 0) {
      if (charsetName != null) {
        lineHandler.lineReaded(new String(buffer, 0, bufferCount, charsetName));
      }
      else {
        lineHandler.lineReaded(new String(buffer, 0, bufferCount));
      }
    }
  }
  
}
