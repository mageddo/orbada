package pl.mpak.usedb.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author akaluza
 * <p>Adnotacja s³urz¹ca do definiowania w³aœciwoœci tabeli dla bean-a 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

  /**
   * <p>Nazwa tabeli
   * @return
   */
  String name();
  
  /**
   * <p>Kolumny klucza g³ównego
   * @return
   */
  String[] primaryKey() default {};
  
}
