/*
 * ISettingsGroup.java
 * 
 * Created on 2007-10-12, 23:20:53
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import pl.mpak.util.variant.Variant;

/**
 * <p>Pozwala odczytaæ lub zapisaæ wartoœæ z listy ustawieñ.
 * @author akaluza
 */
public interface ISettings {
  
  public void setValue(String name, Variant value);
  
  public void setValue(String name, String value);
  
  public void setValue(String name, Long value);
  
  public void setValue(String name, Boolean value);
  
  public void setValue(String name, Color value);
  
  public void setValue(String name, Date value);
  
  public void setValue(String name, Font value);
  
  public Variant getValue(String name);

  public Variant getValue(String name, Variant defaultValue);
  
  public String getValue(String name, String defaultValue);
  
  public Long getValue(String name, Long defaultValue);
  
  public Boolean getValue(String name, Boolean defaultValue);
  
  public Color getValue(String name, Color defaultValue);
  
  public Date getValue(String name, Date defaultValue);
  
  public Font getValue(String name, Font defaultValue);
  
  public void store();

}
