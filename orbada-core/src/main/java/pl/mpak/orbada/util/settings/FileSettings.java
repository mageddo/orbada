/*
 * FileSettings.java
 *
 * Created on 2007-10-19, 17:26:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.core.Application;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class FileSettings extends OrbadaSettings {
  
  private Properties properties;
  
  public FileSettings() {
    properties = new Properties();
  }
  
  private String getPath() {
    String pathName = Application.get().getConfigPath();
    pathName = pathName +"/fs";
    if (getSchemaId() != null) {
      pathName = pathName +"/schemas/" +getSchemaId();
    }
    else {
      pathName = pathName +"/global";
    }
    return pathName;
  }
  
  private String getFileName() {
    return getPath() +"/" + getGroupName() +".properties";
  }
  
  public void load() {
    try {
      properties.load(new FileInputStream(getFileName()));
    } catch (Exception ex) {
    }
  }
  
  public void setValue(String name, Variant value) {
    properties.setProperty(name, value.toString());
  }
  
  public Variant getValue(String name) {
    return new Variant(properties.getProperty(name));
  }
  
  public Variant getValue(String name, Variant defaultValue) {
    return new Variant(properties.getProperty(name, defaultValue.toString()));
  }
  
  public void store() {
    try {
      File file = new File(getPath());
      file.mkdirs();
      properties.store(new FileOutputStream(getFileName()), null);
    } catch (Exception ex) {
    }
  }
  
}
