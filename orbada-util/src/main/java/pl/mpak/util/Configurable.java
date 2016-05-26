package pl.mpak.util;

/**
 * @author akaluza
 * <p>Interfejs do obs³ugi konfiguracji raczej w aplikacjach okienkowych.
 */
public interface Configurable {

  /**
   * <p>Wywo³ywane na rz¹danie u¿ytkownika, powinna pokazaæ oknienko lub inny interfejs do konfiguracji.
   * @return
   */
  public boolean configure();
  
  /**
   * <p>Powinna zwróciæ informacje o tym czy mo¿na wywo³aæ konfiguracjê.
   * @return
   */
  public boolean isConfig();
  
}
