package pl.mpak.datatext.sax;

import pl.mpak.datatext.DataTextException;

public interface LineHandler {

  /**
   * <p>Obs³uguje jeden odczytany wiersz.
   * @param line
   * @return true jeœli reader ma przerwaæ odczytywanie Ÿród³a
   */
  public boolean lineReaded(String line) throws DataTextException;
  
}
