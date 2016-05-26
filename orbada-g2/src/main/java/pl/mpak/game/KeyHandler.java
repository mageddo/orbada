package pl.mpak.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

public class KeyHandler {

  private Set<Integer> keyPressed;
  private KeyListener keyListener;
  private JComponent component;
  
  public KeyHandler(JComponent component) {
    keyPressed = new HashSet<Integer>();
    keyListener = new KeyListener() {
      public void keyPressed(KeyEvent e) {
        keyPressed.add(e.getKeyCode());
      }
      public void keyReleased(KeyEvent e) {
        keyPressed.remove(e.getKeyCode());
      }
      public void keyTyped(KeyEvent e) {
      }
    };
    setComponent(component);
  }
  
  public void setComponent(JComponent component) {
    if (this.component != component) {
      if (this.component != null) {
        this.component.removeKeyListener(keyListener);
      }
      this.component = component;
      if (this.component != null) {
        this.component.addKeyListener(keyListener);
      }
    }
  }
  
  public boolean contains(int keyCode) {
    return keyPressed.contains(keyCode);
  }
  
  public Set<Integer> getKeyPressed() {
    return keyPressed;
  }

}
