package pl.mpak.datatext.sax;

import pl.mpak.datatext.DataTextException;

public interface DataTextHandler {

  /**
   * <p>Linia rozpoczynaj¹ca siê od DataTextFormat.COMMENT_CHAR '.'
   * @param comment
   */
  public void commentReaded(String comment) throws DataTextException;
  
  /**
   * <p>Linia rozpoczynaj¹ca siê od DataTextFormat.TABLE_CHAR '#'
   * <p>Wywo³ywane w przypadku wyst¹pienia powy¿szego oraz gdy nie wyst¹pi³o powy¿sze,
   * a wyst¹pi³ DataTextFormat.HEADER_CHAR '-'. Wtedy tableName = "".
   * @param tableName
   */
  public void tableNameReaded(String tableName) throws DataTextException;
  
  /**
   * <p>Linia rozpoczynaj¹ca siê od DataTextFormat.HEADER_CHAR '-'
   * @param field
   */
  public void headerReaded(DataTextColumn[] columns) throws DataTextException;
  
  /**
   * <p>Linia rozpoczynaj¹ca siê od DataTextFormat.DATA_CHAR ' '
   * @param data
   */
  public void dataReaded(String[] datas) throws DataTextException;
  
  public void emptyLineReaded() throws DataTextException;
  
}
