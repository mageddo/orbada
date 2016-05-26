package pl.mpak.orbada.oracle.dba;

import java.util.ArrayList;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.oracle.cm.FindObjectFromEditorAction;
import pl.mpak.orbada.oracle.dba.services.OracleCacheTableAction;
import pl.mpak.orbada.oracle.dba.services.OracleCompressTableAction;
import pl.mpak.orbada.oracle.dba.services.OracleCreateDatabaseTriggerAction;
import pl.mpak.orbada.oracle.dba.services.OracleCreateSchemaTriggerAction;
import pl.mpak.orbada.oracle.dba.services.OracleDbaDatabaseProvider;
import pl.mpak.orbada.oracle.dba.services.OracleGotoLockSIDAction;
import pl.mpak.orbada.oracle.dba.services.OracleGotoSessionSIDAction;
import pl.mpak.orbada.oracle.dba.services.OracleKillSessionAction;
import pl.mpak.orbada.oracle.dba.services.OracleLocksView;
import pl.mpak.orbada.oracle.dba.services.OracleLoggingTableAction;
import pl.mpak.orbada.oracle.dba.services.OracleMoveTablespaceTableAction;
import pl.mpak.orbada.oracle.dba.services.OracleParametersView;
import pl.mpak.orbada.oracle.dba.services.OracleRebuildIndexAction;
import pl.mpak.orbada.oracle.dba.services.OracleRowMovementTableAction;
import pl.mpak.orbada.oracle.dba.services.OracleSessionsView;
import pl.mpak.orbada.oracle.dba.services.OracleSetDbParameterAction;
import pl.mpak.orbada.oracle.dba.services.OracleShrinkTableAction;
import pl.mpak.orbada.oracle.dba.services.OracleVisualDataFileView;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class OrbadaOracleDbaPlugin extends OrbadaPlugin {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);

  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
  
  public static FindObjectFromEditorAction findObjectAction;
    
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaOracleDbaPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaOracleDbaPlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,Database,Oracle,DBA";
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
    return new VersionID(1, 0, 1, 18).toString();
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
    return Consts.orbadaOracleDbaPluginId;
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
  }
  
  /**
   * <p>Funkcja powinna sprawdziæ listê potrzebnych innych wtyczek
   * return informacje czy mo¿na warunki s¹ spe³nione i czy mo¿na u¿ywaæ tej wtyczki
   * <p>Wywo³ane przed initialize()
   * @param loadedPlugins
   * @return
   */
  public boolean requires(List<IPlugin> loadedPlugins) {
    addDepend(Consts.orbadaUniversalPluginId);
    addDepend(Consts.orbadaOraclePluginId);
    for (IPlugin plugin : loadedPlugins) {
      if (Consts.orbadaUniversalPluginId.equals(plugin.getUniqueID())) {
//        classList.add(UniversalColumnProvider.class);
      }
      else if (Consts.orbadaOraclePluginId.equals(plugin.getUniqueID())) {
        classList.add(OracleDbaDatabaseProvider.class);
        classList.add(OracleSessionsView.class);
        classList.add(OracleLocksView.class);
        classList.add(OracleParametersView.class);
        classList.add(OracleCreateSchemaTriggerAction.class);
        classList.add(OracleCreateDatabaseTriggerAction.class);
        classList.add(OracleRebuildIndexAction.class);
        classList.add(OracleCompressTableAction.class);
        classList.add(OracleCacheTableAction.class);
        classList.add(OracleRowMovementTableAction.class);
        classList.add(OracleShrinkTableAction.class);
        classList.add(OracleMoveTablespaceTableAction.class);
        classList.add(OracleLoggingTableAction.class);
        classList.add(OracleVisualDataFileView.class);
        classList.add(OracleKillSessionAction.class);
        classList.add(OracleSetDbParameterAction.class);
        classList.add(OracleGotoLockSIDAction.class);
        classList.add(OracleGotoSessionSIDAction.class);
      }
    }
    return true;
  }
  
  /**
   * <p>Wywo³ywane jest po initialize()
   */
  public Class<IPluginProvider>[] getProviderArray() {
    return classList.toArray(new Class[classList.size()]);
  }

  @Override
  public void processMessage(PluginMessage message) {
  }

}
