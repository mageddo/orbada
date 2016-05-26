package pl.mpak.game;

import java.util.List;

import pl.mpak.g2.G2Util;

public class CollisionPair {

  private Collisionable object1;
  private Collisionable object2;
  private List<? extends Collisionable> objectList;
  
  public CollisionPair(Collisionable object1, Collisionable object2) {
    this.object1 = object1;
    this.object2 = object2;
  }

  public CollisionPair(Collisionable object, List<? extends Collisionable> objectList) {
    this.object1 = object;
    this.objectList = objectList;
  }
  
  void checkCollision(CollisionManager collisionManager) {
    if (object1 != null && object2 != null) {
      if (G2Util.collidedImage(object1.getCollisionX(), object1.getCollisionY(), object1.getCollisionImage(), object2.getCollisionX(), object2.getCollisionY(), object2.getCollisionImage())) {
        object1.collidedWith(object2);
        object2.collidedWith(object1);
        collisionManager.fireCollisionListener(object1, object2);
      }
    }
    else if (object1 != null && objectList != null) {
      for (Collisionable o : objectList) {
        if (G2Util.collidedImage(object1.getCollisionX(), object1.getCollisionY(), object1.getCollisionImage(), o.getCollisionX(), o.getCollisionY(), o.getCollisionImage())) {
          object1.collidedWith(o);
          o.collidedWith(object1);
          collisionManager.fireCollisionListener(object1, o);
        }
      }
    }
  }
  
  boolean inPair(Collisionable object) {
    if (object1 == object || object2 == object) {
      return true;
    }
    if (objectList != null) {
      for (Collisionable o : objectList) {
        if (o == object) {
          return true;
        }
      }
    }
    return false;
  }

}
