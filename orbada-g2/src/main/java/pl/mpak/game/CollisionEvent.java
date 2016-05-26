package pl.mpak.game;

import java.util.EventObject;

public class CollisionEvent extends EventObject {
  private static final long serialVersionUID = 1L;

  private Collisionable object1;
  private Collisionable object2;
  
  public CollisionEvent(Object source, Collisionable object1, Collisionable object2) {
    super(source);
    this.object1 = object1;
    this.object2 = object2;
  }

  public Collisionable getObject1() {
    return object1;
  }

  public Collisionable getObject2() {
    return object2;
  }

}
