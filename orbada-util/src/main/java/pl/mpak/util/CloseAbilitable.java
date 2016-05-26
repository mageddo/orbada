package pl.mpak.util;

/**
 * @author akaluza
 * <p>Interfejs pozwalaj¹cy sprawdziæ czy "coœ" mo¿na zamkn¹æ.
 */
public interface CloseAbilitable {

  /**
   * <p>Powinna zwróciæ true jeœli mo¿na "coœ" zamkn¹æ.
   * @return
   */
  public boolean canClose();
  
}
