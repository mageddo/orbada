package pl.mpak.game;

import java.awt.image.BufferedImage;

public interface Collisionable {
  
  /**
   * <p>Wspó³¿êdne po³o¿enia X
   * @return
   */
  public int getCollisionX();
  
  /**
   * <p>Wspó³¿êdne po³o¿enia Y
   * @return
   */
  public int getCollisionY();
  
  /**
   * <p>Obrazek do sprawdzenia kolizji
   * @return
   */
  public BufferedImage getCollisionImage();
  
  /**
   * <p>Wyzwalany w momencie wyst¹pienia kolizji
   * @param object
   */
  public void collidedWith(Collisionable object);
  
}
