package pl.mpak.datatext.sax;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import pl.mpak.datatext.DataTextException;

public class DataTextReader {

  private DataTextHandler handler;
  private String charsetName;
  private ArrayList columnList;

  public DataTextReader(DataTextHandler handler) {
    this(handler, null);
  }
  
  public DataTextReader(DataTextHandler handler, String charsetName) {
    if (handler == null) {
      throw new IllegalArgumentException("handler == null");
    }
    this.handler = handler;
    this.charsetName = charsetName;
    columnList = new ArrayList();
  }
  
  public void read(InputStream inputStream) throws IOException, DataTextException {
    LineReader lineReader = new LineReader(new LineHandler() {
      private boolean tableCall;
      public boolean lineReaded(String line) throws DataTextException {
        if (line.length() > 0) {
          char firstChar = line.charAt(0);
          switch (firstChar) {
            case DataTextFormat.COMMENT_CHAR: {
              handler.commentReaded(line.substring(1).trim());
              break;
            }
            case DataTextFormat.TABLE_CHAR: {
              tableCall = true;
              handler.tableNameReaded(line.substring(1).trim());
              break;
            }
            case DataTextFormat.HEADER_CHAR: {
              if (!tableCall) {
                handler.tableNameReaded("");
              }
              tableCall = false;
              parseHeaderLine(line.substring(1).trim());
              break;
            }
            case DataTextFormat.DATA_CHAR: {
              if (columnList.size() > 0) {
                parseDataLine(line.substring(1));
              }
              break;
            }
          }
        }
        else {
          handler.emptyLineReaded();
        }
        return false;
      }
    }, charsetName);
    lineReader.read(inputStream);
  }
  
  @SuppressWarnings("unchecked")
  private void parseHeaderLine(String line) throws DataTextException {
    StringBuffer sb = new StringBuffer();
    char dataType = 'S';
    int  length = 0;

    columnList.clear();
    int i=0;
    while (true) {
      char ch = line.charAt(i);
      switch (ch) {
        case '-': {
          do {
            length++;
            if (++i >= line.length()) {
              break;
            }
          } while (line.charAt(i) == '-');
          columnList.add(new DataTextColumn(length, sb.toString(), dataType));
          dataType = 'S';
          sb.setLength(0);
          length = 0;
          break;
        }
        case '[': {
          i++;
          dataType = Character.toUpperCase(line.charAt(i));
          i+=2;
          length+=3;
          break;
        }
        default: {
          length++;
          sb.append(ch);
          i++;
        }
      }
      if (i >= line.length()) {
        break;
      }
    }
    if (sb.length() > 0) {
      columnList.add(new DataTextColumn(length, sb.toString(), dataType));
    }
    DataTextColumn[] columns = new DataTextColumn[columnList.size()];
    columnList.toArray(columns);
    handler.headerReaded(columns);
  }
  
  private void parseDataLine(String line) throws DataTextException {
    String[] datas = new String[columnList.size()];
    int offset = 0;
    for (int i=0; i<columnList.size(); i++) {
      if (i == columnList.size() -1) {
        datas[i] = line.substring(offset).trim();
      }
      else {
        datas[i] = line.substring(offset, Math.min(line.length(), offset +((DataTextColumn)columnList.get(i)).getLength())).trim();
      }
      offset += ((DataTextColumn)columnList.get(i)).getLength();
      offset = Math.min(offset, line.length());
    }
    handler.dataReaded(datas);
  }
  
}
