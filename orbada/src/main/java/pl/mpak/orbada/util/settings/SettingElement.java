/*
 * SettingElement.java
 *
 * Created on 2007-10-13, 13:23:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.settings;

import pl.mpak.util.variant.Variant;

/**
 * <p>Narzêdziowa klas do trzymania jednego ustawienia.
 * @see DatabaseSettings
 * @author akaluza
 */
public class SettingElement {
  
  private String name;
  private Variant value;
  private boolean changed;
  private boolean newest;
  
  public SettingElement() {
    value = new Variant();
  }
  
  public SettingElement(String name, Variant value) {
    this.name = name;
    this.value = value != null ? value : new Variant();
  }
  
  public String getName() {
    return name;
  }
  
  public boolean isChanged() {
    return changed;
  }
  
  public boolean isNewest() {
    return newest;
  }
  
  public void setNewest(boolean newest) {
    this.newest = newest;
  }
  
  public void setChanged(boolean changed) {
    this.changed = changed;
  }
  
  public Variant getValue() {
    return value;
  }
  
  public void setValue(Variant value) {
    if (value == null) {
      value = new Variant();
    }
    if (!this.value.equals(value)) {
      this.value = value;
      changed = true;
    }
  }
  
}
