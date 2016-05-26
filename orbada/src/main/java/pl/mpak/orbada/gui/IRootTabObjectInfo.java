/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui;

import java.io.Closeable;
import pl.mpak.util.CloseAbilitable;

/**
 * <p>Interfejs jest/powinie byæ implementowany przez g³ówne listy obiektów bazy danych
 * @author akaluza
 */
public interface IRootTabObjectInfo extends CloseAbilitable, Closeable {

  /**
   * <p>Odœwierzenie natychmiastowe listy obiektów
   */
  public void refresh();

  /**
   * <p>Odœwierzenie obiektu z jego zmian¹.
   * @param objectName mo¿e to byæ identyfikator jakiegoœ obiektu (lub null) który ma byæ wyró¿niony
   */
  public void refresh(String objectName);

}
