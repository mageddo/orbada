package pl.mpak.usedb.core;

import java.sql.SQLException;
import java.util.EventListener;
import java.util.EventObject;

/**
 * @author akaluza
 * <p>Listener w którym zwraca siê informacjê o tym czy polecenie e.getSource() mo¿e byæ wykonane.
 * <p>Listener wywo³ywany jest w Command.execeute() lub Query.open() jeszcze przed jakimkolwiek innym sprawdzeniem lub zda¿eniem.
 */
public interface ExecutableListener extends EventListener {

  /**
   * <p>Powinno zwróciæ informacjê czy polecenie mo¿e byæ wykonane czy nie.
   * @param e
   * @return
   * @throws SQLException
   */
  public boolean canExecute(EventObject e) throws SQLException;

}
