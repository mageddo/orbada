package pl.mpak.orbada.snippets;

import java.util.ArrayList;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.snippets.db.SnippetRecord;
import pl.mpak.orbada.snippets.services.OrbadaSnippetsSyntaxService;
import pl.mpak.orbada.snippets.services.SnippetsSettingsProvider;
import pl.mpak.orbada.util.ScriptUtil;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class OrbadaSnippetsPlugin extends OrbadaPlugin {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("snippets");

  public final static VersionID version = new VersionID(0, 0, 1, 3);
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
  public final static String pluginGroupName = stringManager.getString("OrbadaSnippetsPlugin-group-name");
  
  private static SnippetsManager snippetsManager;
  
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  @Override
  public String getInternalName() {
    return "OrbadaSnippetsPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  @Override
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaSnippetsPlugin-descriptive-name"), new Object[] {getVersion()});
  }
  
  /**
   * <p>Funkcja powinna zwracaæ rozszerzone informacje opisowe dotycz¹ce wtyczki.
   * @return
   */
  @Override
  public String getDescription() {
    return "";
  }
  
  /**
   * <p>Kategorie wtyczki, np:
   * <li>Database, HSQLDB</li>
   * <li>Developers</li>
   * @return
   */
  @Override
  public String getCategory() {
    return "IDE,Editor";
  }
  
  /**
   * Funkcja powinna zwracaæ informacje o autorach wtyczki
   * @return
   */
  @Override
  public String getAuthor() {
    return "Andrzej Ka³u¿a";
  }
  
  @Override
  public String getCopyrights() {
    return "";
  }
  
  /**
   * Funkcja powinna zwracaæ adres swtrony www
   * @return
   */
  @Override
  public String getWebSite() {
    return null;
  }
  
  /**
   * Funkcja powinna zwracaæ adres swtrony aktualizacji
   * @return
   */
  @Override
  public String getUpdateSite() {
    return null;
  }
  
  /**
   * Funckja powinna zwróciæ wersjê najlepiej w postaci:
   * major.minor.release.build
   * @return
   */
  @Override
  public String getVersion() {
    return version.toString();
  }
  
  /**
   * Mo¿e zwróciæ treœæ licencji
   * @return
   */
  @Override
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
  @Override
  public String getUniqueID() {
    return Consts.orbadaSnippetsPluginId;
  }
  
  /**
   * Funkcja wywo³ywana jest zaraz po za³adowaniu wtyczki.
   * ManOra jest ju¿ utworzona, konfiguracja programu za³adowana
   */
  @Override
  public void load() {
  }
  
  /**
   * Funkcja wywo³ywana jest zaraz przed zamkniêciem programu
   */
  @Override
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
  @Override
  public void initialize() {
    snippetsManager = new SnippetsManager(getApplication());
    if (application.isUserAdmin()) {
      if (getLastVersion() == null) {
        try {
          ScriptUtil.executeInternalScript(getClass().getResourceAsStream("/pl/mpak/orbada/snippets/sql/creation.sql"));
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230135-0001351E6CD0C2F2-584930A6", "sel", "select *\n  from |", "sql", false).applyInsert();
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230210-000135265B57FC19-281F8618", "selw", "select *\n  from |\n where ", "sql", false).applyInsert();
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230221-00013528F3AE930A-8ED8F74B", "selwo", "select *\n  from |\n where \n order by ", "sql", false).applyInsert();
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230229-0001352AD3BE51A7-74A30F93", "insv", "insert into | ()\nvalues ()", "sql", false).applyInsert();
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230237-0001352CC4358726-C9B280FE", "insel", "insert into | ()\nselect \n  from", "sql", false).applyInsert();
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230244-0001352E7A02389A-C9EEF8BE", "inselw", "insert into | ()\nselect \n  from \n where ", "sql", false).applyInsert();
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230251-0001353007E9B34C-98C13D8C", "del", "delete from |", "sql", false).applyInsert();
          new SnippetRecord(application.getOrbadaDatabase(), "20140627230304-000135331A018E92-B3E5E01A", "delw", "delete from |\n where", "sql", false).applyInsert();
        }
        catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
//      if (getLastVersion() == null || new VersionID(getLastVersion()).getBuild() < 1) {
//        try {
//          application.getOrbadaDatabase().executeCommand("update osqlmacros set osm_regexp = '^*((\\(*\\s*\\w+\\s*\\)*)(\\s*([+-/\\*]|(\\|\\|))+\\s*(\\(*\\s*\\w+\\s*\\)*))+)^*' where osm_regexp = '^*((\\(*\\s*(\\d+|[a-zA-Z_0-9]+)\\s*\\)*)(\\s*([+-/\\*]|(\\|\\|))\\s*(\\(*\\s*(\\d+|[a-zA-Z_0-9]+)+\\s*\\)*))+)^*'");
//        }
//        catch (Exception ex) {
//          ExceptionUtil.processException(ex);
//        }
//      }
    }
  }
  
  /**
   * <p>Funkcja powinna sprawdziæ listê potrzebnych innych wtyczek
   * return informacje czy mo¿na warunki s¹ spe³nione i czy mo¿na u¿ywaæ tej wtyczki
   * <p>Wywo³ane przed initialize()
   * @param loadedPlugins
   * @return
   */
  @Override
  public boolean requires(List<IPlugin> loadedPlugins) {
    addDepend(Consts.orbadaUniversalPluginId);
    for (IPlugin plugin : loadedPlugins) {
      if (Consts.orbadaUniversalPluginId.equals(plugin.getUniqueID())) {
        classList.add(OrbadaSnippetsSyntaxService.class);
        classList.add(SnippetsSettingsProvider.class);
      }
    }
    return true;
  }
  
  /**
   * <p>Wywo³ywane jest po initialize()
   */
  @Override
  public Class<IPluginProvider>[] getProviderArray() {
    return classList.toArray(new Class[classList.size()]);
  }

  @Override
  public void processMessage(PluginMessage message) {
  }

  public static SnippetsManager getSnippetsManager() {
    return snippetsManager;
  }

}
