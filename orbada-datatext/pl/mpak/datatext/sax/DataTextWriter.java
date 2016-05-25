package pl.mpak.datatext.sax;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import pl.mpak.datatext.DataTextException;
import pl.mpak.datatext.util.DataTextUtils;

public class DataTextWriter {

  private OutputStream outputStream;

  private DataTextColumn[] columns = null;

  private String charsetName;

  public DataTextWriter(OutputStream outputStream) {
    this(outputStream, null);
  }

  public DataTextWriter(OutputStream outputStream, String aCharsetName) {
    if (outputStream == null) {
      throw new IllegalArgumentException("DataTextWriter: outputStream == null");
    }
    this.outputStream = outputStream;
    this.charsetName = aCharsetName;
  }

  private byte[] toBytes(String text) {
    try {
      if (charsetName == null || charsetName.length() == 0) {
        return text.getBytes();
      } else
        return text.getBytes(charsetName);
    } catch (UnsupportedEncodingException e) {
      return text.getBytes();
    }
  }

  /**
   * <p>
   * Pozwala zapisaæ komentarz w pliku.
   * 
   * @param comment
   *          ci¹g znaków odpowiednio, zostanie dodana kropka
   * @throws IOException
   */
  public void writeComment(String comment) throws IOException {
    outputStream.write(toBytes(("." + comment)));
    outputStream.write(toBytes("\r\n"));
  }

  /**
   * <p>
   * Pozwala zapisaæ nag³ówek tabeli.
   * <p>
   * Jeœli nazwa kolumny bêdzie d³u¿sza ni¿ dane to d³ugoœæ zostanie
   * automatycznie dopasowana.
   * 
   * @param tableName
   *          nazwa tabeli, null lub pusty ci¹g znaków
   * @param columns
   * @throws IOException
   */
  public void writeHeader(String tableName, DataTextColumn[] columns)
      throws IOException {
    if (columns == null || columns.length == 0) {
      throw new IllegalArgumentException(
          "writeHeader: columns == null || columns.length = 0");
    }

    if (tableName != null && !tableName.equals("")) {
      outputStream.write(toBytes(("#" + tableName)));
      outputStream.write(toBytes("\r\n"));
    }

    this.columns = columns;
    for (int i = 0; i < columns.length; i++) {
      if (columns[i].getName().length() > columns[i].getLength()) {
        columns[i].length = columns[i].getName().length();
      }
      outputStream.write(toBytes(DataTextUtils.fillRight("-"
          + columns[i].getName(), '-', columns[i].getLength())));
    }
    outputStream.write(toBytes("-\r\n"));
  }

  /**
   * <p>
   * Pozwala zapisaæ dane zgodnie z wczeœniej zdefiniowan¹ list¹ kolumn.
   * 
   * @param datas
   * @throws DataTextException
   *           gdy liczba kolumn jest ró¿na ni¿ liczba danych lub gdy wielkoœæ
   *           danych jest niezgodna z wyspecyfikowanymi kolumnami
   * @throws IOException
   */
  public void writeData(String[] datas) throws DataTextException, IOException {
    if (columns == null || columns.length == 0) {
      throw new IllegalArgumentException(
          "writeData: columns == null || columns.length = 0");
    }
    if (datas == null || datas.length == 0) {
      throw new IllegalArgumentException(
          "writeData: datas == null || datas.length = 0");
    }
    if (columns.length != datas.length) {
      throw new DataTextException(
          "Liczba kolumn jest ró¿na od iloœci przekazanych danych!");
    }

    for (int i = 0; i < columns.length; i++) {
      if (datas[i] != null && datas[i].length() > columns[i].getLength()) {
        // throw new DataTextException(String.format("Zbyt du¿a liczba
        // danych w kolumnie \"%s\"!", new Object[]
        // {columns[i].getName()}));
        throw new DataTextException("Zbyt du¿a liczba danych w kolumnie \""
            + columns[i].getName() + "\"!");
      }
      if (columns[i].getAlignment() == DataTextColumn.RIGHT_ALIGN) {
        outputStream.write(toBytes(DataTextUtils.fillLeft(" " + (datas[i] == null ? "" : datas[i]), ' ',
            columns[i].getLength())));
      } else {
        outputStream.write(toBytes(DataTextUtils.fillRight(" " + (datas[i] == null ? "" : datas[i]), ' ',
            columns[i].getLength())));
      }
    }
    outputStream.write(toBytes(" \r\n"));
  }

  /**
   * <p>
   * Zapisuje pust¹ liniê
   * 
   * @throws IOException
   */
  public void writeEmptyLine() throws IOException {
    outputStream.write(toBytes("\r\n"));
  }

  /**
   * <p>
   * Zamyka strumieñ
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    outputStream.close();
  }

}
