package pl.mpak.orbada.sqlmacro;

import java.util.ArrayList;
import java.util.List;
import orbada.Consts;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.sqlmacro.services.MacroSqlTextTransform;
import pl.mpak.orbada.sqlmacro.services.SqlMacrosSettingsProvider;
import orbada.util.ScriptUtil;
import pl.mpak.util.id.VersionID;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OrbadaSqlMacrosPlugin extends OrbadaPlugin {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSqlMacrosPlugin.class);

  public final static String pluginGroupName = "Orbada Tools";
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
  
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaSqlMacrosPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaSqlMacrosPlugin-descriptive-name"), new Object[] {getVersion()});
  }
  
  /**
   * <p>Funkcja powinna zwracaæ rozszerzone informacje opisowe dotycz¹ce wtyczki.
   * @return
   */
  public String getDescription() {
    return "";
  }
  
  /**
   * <p>Kategorie wtyczki, np:
   * <li>Database, HSQLDB</li>
   * <li>Developers</li>
   * @return
   */
  public String getCategory() {
    return "Tools";
  }
  
  /**
   * Funkcja powinna zwracaæ informacje o autorach wtyczki
   * @return
   */
  public String getAuthor() {
    return "Andrzej Ka³u¿a";
  }
  
  public String getCopyrights() {
    return "";
  }
  
  /**
   * Funkcja powinna zwracaæ adres swtrony www
   * @return
   */
  public String getWebSite() {
    return null;
  }
  
  /**
   * Funkcja powinna zwracaæ adres swtrony aktualizacji
   * @return
   */
  public String getUpdateSite() {
    return null;
  }
  
  /**
   * Funckja powinna zwróciæ wersjê najlepiej w postaci:
   * major.minor.release.build
   * @return
   */
  public String getVersion() {
    return new VersionID(1, 0, 1, 10).toString();
  }
  
  /**
   * Mo¿e zwróciæ treœæ licencji
   * @return
   */
  public String getLicence() {
    return null;
  }
  
  /**
   * <p>Funkcja musi zwracaæ unikalny identyfikator wtyczki
   * <p>W tym miejscu mo¿na sko¿ystaæ z kasy pl.mpak.sky.utils.UniqueID
   * Identyfikator identyfikuje jednoznacznie za³adowan¹ wtyczkê.
   * <p>Mo¿e te¿ byæ to unikalna nazwa wtyczki.
   * @return
   */
  public String getUniqueID() {
    return Consts.orbadaSqlMacrosPluginId;
  }
  
  /**
   * Funkcja wywo³ywana jest zaraz po za³adowaniu wtyczki.
   * ManOra jest ju¿ utworzona, konfiguracja programu za³adowana 
   */
  public void load() {
    
  }
  
  /**
   * Funkcja wywo³ywana jest zaraz przed zamkniêciem programu
   */
  public void unload() {
  }
  
  /**
   * Funkcja wywo³ywana jest po za³adowaniu wszystkich wtyczek i pokazaniu okna g³ównego.
   * W tym miejscu mo¿e byæ sprawdzone czy s¹ wszystkie wtyczki potrzebne
   * do prawid³owego dzia³ania tej wtyczki.
   * Równie¿ w tym miejscu mo¿na podpi¹æ listenery gdzie tylko siê chce.
   * Mo¿e podpi¹æ siê w odpowiednie miejsca menu, toolbar-a, listê po³¹czeñ
   * skonfigurowanych i nawi¹zanych. Mo¿e uruchomiæ jakieœ zadania (Task), wpisaæ
   * coœ do log-a (pl.mpak.sky.utils.logging.Logger), etc
   */
  public void initialize() {
    if (application.isUserAdmin()) {
      if (getLastVersion() == null) {
        ScriptUtil.executeInternalScript(getClass().getResourceAsStream("/pl/mpak/orbada/sqlmacro/sql/creation.sql"));
      }
      if (getLastVersion() == null || new VersionID(getLastVersion()).getBuild() < 1) {
        try {
          application.getOrbadaDatabase().executeCommand("update osqlmacros set osm_regexp = '^*((\\(*\\s*\\w+\\s*\\)*)(\\s*([+-/\\*]|(\\|\\|))+\\s*(\\(*\\s*\\w+\\s*\\)*))+)^*' where osm_regexp = '^*((\\(*\\s*(\\d+|[a-zA-Z_0-9]+)\\s*\\)*)(\\s*([+-/\\*]|(\\|\\|))\\s*(\\(*\\s*(\\d+|[a-zA-Z_0-9]+)+\\s*\\)*))+)^*'");
        }
        catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
      if (getLastVersion() == null || new VersionID(getLastVersion()).getBuild() < 3) {
        try {
          application.getOrbadaDatabase().executeCommand("update osqlmacros set osm_regexp = '^*@\\s*((\\(*\\s*\\w+\\s*\\)*)(\\s*([+-/\\*]|(\\|\\|))+\\s*(\\(*\\s*\\w+\\s*\\)*))+)^*', osm_name = 'Simple Math Expression (@expression)' where osm_regexp = '^*((\\(*\\s*\\w+\\s*\\)*)(\\s*([+-/\\*]|(\\|\\|))+\\s*(\\(*\\s*\\w+\\s*\\)*))+)^*'");
        }
        catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
      if (getLastVersion() == null || new VersionID(getLastVersion()).getBuild() < 6) {
        ScriptUtil.executeInternalScript(getClass().getResourceAsStream("/pl/mpak/orbada/sqlmacro/sql/1.0.1.6.sql"));
      }
    }
  }
  
  /**
   * <p>Funkcja powinna sprawdziæ listê potrzebnych innych wtyczek
   * return informacje czy mo¿na warunki s¹ spe³nione i czy mo¿na u¿ywaæ tej wtyczki
   * @param loadedPlugins 
   * @return 
   */
  public boolean requires(List<IPlugin> loadedPlugins) {
    addDepend(Consts.orbadaUniversalPluginId);
    addDepend(Consts.orbadaBeanShellPluginId);
    for (IPlugin plugin : loadedPlugins) {
      if (Consts.orbadaUniversalPluginId.equals(plugin.getUniqueID())) {
        classList.add(MacroSqlTextTransform.class);
        classList.add(SqlMacrosSettingsProvider.class);
      }
    }
    return true;
  }

  public Class<IPluginProvider>[] getProviderArray() {
    return classList.toArray(new Class[classList.size()]);
  }

  @Override
  public void processMessage(PluginMessage message) {
  }

}
