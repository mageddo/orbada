package pl.mpak.orbada.util.settings;

import java.util.Properties;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class BlackHoleSettings extends OrbadaSettings {
  
  private Properties properties;
  
  public BlackHoleSettings() {
    properties = new Properties();
  }
  
  @Override
  public void load() {
  }
  
  @Override
  public void setValue(String name, Variant value) {
    properties.setProperty(name, value.toString());
  }
  
  @Override
  public Variant getValue(String name) {
    return new Variant(properties.getProperty(name));
  }
  
  @Override
  public Variant getValue(String name, Variant defaultValue) {
    return new Variant(properties.getProperty(name, defaultValue.toString()));
  }
  
  @Override
  public void store() {
  }
  
}
