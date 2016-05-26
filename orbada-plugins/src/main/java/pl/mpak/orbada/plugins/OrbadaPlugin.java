/*
 * OrbadaPlugin.java
 * 
 * Created on 2007-10-07, 16:31:45
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import java.util.ArrayList;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 * <p>Tej klasy nale¿y u¿yæ do oprogramowania w³asnego plugin-a
 * @author akaluza
 */
public abstract class OrbadaPlugin implements IPlugin, IProcessMessagable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPlugin.class);

  protected IApplication application;
  protected String lastVersion;
  private ArrayList<String> depends = new ArrayList<String>();
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public IApplication getApplication() {
    return application;
  }

  public String getLastVersion() {
    return lastVersion;
  }

  public void setLastVersion(String lastVersion) {
    this.lastVersion = lastVersion;
  }
  
  /**
   * <p>Dodaje do listy uniqueId wtyczek od których zale¿y ta która dodaje
   * @param pluginUniqueId
   */
  public void addDepend(String pluginUniqueId) {
    depends.add(pluginUniqueId);
  }

  /**
   * <p>Zwraca listê wtyczek od których zale¿y
   * @return
   */
  public String[] getDepends() {
    return depends.toArray(new String[depends.size()]);
  }

}
