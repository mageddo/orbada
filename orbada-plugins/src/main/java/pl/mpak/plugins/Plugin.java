package pl.mpak.plugins;

import java.util.List;

import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;

public class Plugin {

  private String source;
  private String className;
  private IPlugin plugin = null;
  private String uniqueID;
  private Class<?> clazz;
  private boolean canUsePlugin;
  private ClassLoader classLoader;

  public Plugin(String source, String className, ClassLoader classLoader) throws PluginException {
    this.className = className;
    this.source = source;
    this.classLoader = classLoader;
    loadPlugin();
  }

  public Plugin(String source, Class<?> clazz, ClassLoader classLoader) throws PluginException {
    this.className = clazz.getName();
    this.source = source;
    this.classLoader = classLoader;
    this.clazz = clazz;
    loadPlugin();
  }

  public String getSource() {
    return source;
  }
  
  public String getClassName() {
    return className;
  }

  public IPlugin getPlugin() {
    return plugin;
  }
  
  public Class<IPluginProvider>[] getProviderArray() {
    return plugin.getProviderArray();
  }
  
  void loadPlugin() throws PluginException {
    if (plugin == null) {
      try {
        if (clazz == null) {
          if (classLoader == null) {
            plugin = (IPlugin)ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
          }
          else {
            plugin = (IPlugin)classLoader.loadClass(className).newInstance();
          }
        }
        else {
          plugin = (IPlugin)clazz.newInstance();
        }
      }
      catch(Exception e) {
        throw new PluginException(e);
      }
    }
    plugin.load();
    uniqueID = plugin.getUniqueID();
  }
  
  void unloadPlugin() {
    plugin.unload();
    plugin = null;
  }

  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return plugin.getInternalName();
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return plugin.getDescriptiveName();
  }
  
  /**
   * <p>Funkcja powinna zwracaæ rozszerzone informacje opisowe dotycz¹ce wtyczki.
   * @return
   */
  public String getDescription() {
    return plugin.getDescription();
  }
  
  /**
   * <p>Kategorie wtyczki, np:
   * <li>Database, HSQLDB</li>
   * <li>Developers</li>
   * @return
   */
  public String getCategory() {
    return plugin.getCategory();
  }
  
  /**
   * Funkcja powinna zwracaæ informacje o autorach wtyczki
   * @return
   */
  public String getAuthor() {
    return plugin.getAuthor();
  }
  
  /**
   * <p>Funkcja powinna zwracaæ informacje o w³aœcicielu praw do rozpowszechniania
   * @return
   */
  public String getCopyrights() {
    return plugin.getCopyrights();
  }
  
  /**
   * Funkcja powinna zwracaæ adres swtrony www
   * @return
   */
  public String getWebSite() {
    return plugin.getWebSite();
  }
  
  /**
   * Funckja powinna zwróciæ wersjê najlepiej w postaci:
   * major.minor.release.build
   * @return
   */
  public String getVersion() {
    return plugin.getVersion() == null ? "" : plugin.getVersion();
  }
  
  /**
   * Mo¿e zwróciæ œcie¿kê do pliku licencji
   * @return
   */
  public String getLicence() {
    return plugin.getLicence();
  }
  
  /**
   * Funkcja powinna zwracaæ adres swtrony aktualizacji
   * @return
   */
  public String getUpdateSite() {
    return plugin.getUpdateSite();
  }
  
  /**
   * Funkcja musi zwracaæ unikalny identyfikator wtyczki
   * W tym miejscu mo¿na sko¿ystaæ z kasy pl.mpak.sky.utils.UniqueID
   * Identyfikator identyfikuje jednoznacznie za³adowan¹ wtyczkê.  
   * @return
   */
  public String getUniqueID() {
    return uniqueID;
  }
  
  /**
   * <p>Funkcja wywo³ywana jest po za³adowaniu wszystkich wtyczek i pokazaniu okna g³ównego.
   * <p>W tym miejscu mo¿e byæ sprawdzone czy s¹ wszystkie wtyczki potrzebne
   * do prawid³owego dzia³ania tej wtyczki.
   * <p>Równie¿ w tym miejscu mo¿na podpi¹æ listenery gdzie tylko siê chce.
   * <p>Mo¿e podpi¹æ siê w odpowiednie miejsca menu, toolbar-a, listê po³¹czeñ
   * skonfigurowanych i nawi¹zanych. Mo¿e uruchomiæ jakieœ zadania (Task), wpisaæ
   * coœ do log-a (pl.mpak.sky.utils.logging.Logger), etc
   * <p>Funkcja sprawdza czy mo¿e siê uruchomiæ, wczeœniej by³a wywo³ana funkcja requires
   */
  public void initialize() {
    if (canUsePlugin) {
      plugin.initialize();
    }
  }

  public boolean requires(List<IPlugin> loadedPlugins) {
    return canUsePlugin = plugin.requires(loadedPlugins);
  }

  public void reload() throws PluginException {
    unloadPlugin();
    loadPlugin();
    initialize();
  }

  public boolean isCanUsePlugin() {
    return canUsePlugin;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }
  
  public String[] getDepends() {
    return plugin.getDepends();
  }

}
