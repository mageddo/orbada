/*
 * SettingsFactory.java
 *
 * Created on 2007-10-13, 12:53:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.settings;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.util.ExceptionUtil;

/**
 * <p>Fabryka tworzenia klasy grupy ustawieñ
 * @author akaluza
 */
public class SettingsFactory {

  /**
   * <p>Domyœlna klasa ustawieñ
   */
  public static final String defaultSettingsClass = DatabaseSettings.class.getCanonicalName();
  
  public static ISettings createInstance(String groupName) {
    return createInstance(null, groupName);
  }
  
  public static ISettings createInstance(String schemaId, String groupName) {
    String className = Application.get().getProperty("settings.class");
    if (className == null || "".equals(className)) {
      className = defaultSettingsClass;
    }
    try {
      OrbadaSettings os = (OrbadaSettings)Application.get().getClass().getClassLoader().loadClass(className).newInstance();
      os.setGroupName(groupName);
      os.setSchemaId(schemaId);
      os.load();
      return os;
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      System.exit(-1);
    }
    return null;
  }
  
}
