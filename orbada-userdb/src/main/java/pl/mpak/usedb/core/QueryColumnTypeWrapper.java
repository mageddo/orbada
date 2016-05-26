package pl.mpak.usedb.core;

import java.sql.SQLException;
import java.util.HashMap;

public interface QueryColumnTypeWrapper {
  
  public static HashMap<String, QueryColumnTypeWrapper> wrapperMap = new HashMap<String, QueryColumnTypeWrapper>();

  /**
   * <p>Powinna konwertowaæ obiekt "o" bazy danych do typu który klasa Variant bêdzie w stanie zapisaæ i odczytaæ.
   * Mo¿e to byæ obiekt serializowany lub zgodny (i zarejestrowany) z VariantConnectable.
   * <p>W najprostrzej postaci mo¿e byæ konwertowany do typu String. 
   * @param o
   * @return
   */
  public Object convert(Object o) throws SQLException;
  
}
