package pl.mpak.game;

import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import pl.mpak.util.Controlable;

public class CollisionManager implements Controlable {

  private EventListenerList listenerList = new EventListenerList();
  private ArrayList<CollisionPair> pairList;

  public CollisionManager() {
    pairList = new ArrayList<CollisionPair>();
  }

  public void addCollisionListener(CollisionListener listener) {
    synchronized (listenerList) {
      listenerList.add(CollisionListener.class, listener);
    }
  }
  
  public void removeCollisionListener(CollisionListener listener) {
    synchronized (listenerList) {
      listenerList.remove(CollisionListener.class, listener);
    }
  }
  
  public void fireCollisionListener(Collisionable object1, Collisionable object2) {
    synchronized (listenerList) {
      if (listenerList.getListenerCount() > 0) {
        CollisionEvent e = new CollisionEvent(this, object1, object2);
        CollisionListener[] listeners = listenerList.getListeners(CollisionListener.class);
        for (int i=0; i<listeners.length; i++) {
          listeners[i].collisionPerformed(e);
        }
      }
    }  
  }

  /**
   * <p>Sprawdza wszystkie kolizje
   */
  public void control() {
    synchronized (pairList) {
      for (CollisionPair cp : pairList) {
        cp.checkCollision(this);
      }
    }
  }
  
  public void add(Collisionable object1, Collisionable object2) {
    add(new CollisionPair(object1, object2));
  }
  
  public void add(CollisionPair cp) {
    synchronized (pairList) {
      pairList.add(cp);
    }
  }
  
  public void remove(CollisionPair cp) {
    synchronized (pairList) {
      pairList.remove(cp);
    }
  }
  
  /**
   * <p>Usuwa parê, która zawiera podany obejkt.
   * @param object
   */
  public void remove(Collisionable object) {
    synchronized (pairList) {
      for (int i = 0; i < pairList.size(); i++) {
        if (pairList.get(i).inPair(object)) {
          pairList.remove(i);
          break;
        }
      }
    }
  }
  
  public void clear() {
    synchronized (pairList) {
      pairList.clear();
    }
  }
  
}
