package pl.mpak.game;

import java.util.EventListener;

public interface CollisionListener extends EventListener {

  public void collisionPerformed(CollisionEvent e);
  
}
