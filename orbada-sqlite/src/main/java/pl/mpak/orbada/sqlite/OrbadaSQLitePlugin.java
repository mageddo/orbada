package pl.mpak.orbada.sqlite;

import java.util.ArrayList;
import java.util.List;
import orbada.Consts;
import orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.sqlite.services.ExplainPlanProvider;
import pl.mpak.orbada.sqlite.services.SQLiteDatabaseProvider;
import pl.mpak.orbada.sqlite.services.SQLiteDatabasesView;
import pl.mpak.orbada.sqlite.services.SQLiteDbInfoProvider;
import pl.mpak.orbada.sqlite.services.SQLitePerspectiveProvider;
import pl.mpak.orbada.sqlite.services.SQLiteTablesView;
import pl.mpak.orbada.sqlite.services.SQLiteTriggersView;
import pl.mpak.orbada.sqlite.services.SQLiteViewsView;
import pl.mpak.orbada.sqlite.services.actions.AttachDatabaseAction;
import pl.mpak.orbada.sqlite.services.actions.DetachDatabaseAction;
import pl.mpak.orbada.sqlite.services.actions.DropTableAction;
import pl.mpak.orbada.sqlite.services.actions.DropTriggerAction;
import pl.mpak.orbada.sqlite.services.actions.DropViewAction;
import pl.mpak.orbada.sqlite.services.actions.FreezeAction;
import pl.mpak.orbada.sqlite.services.actions.PragmaDatabaseCacheSizeAction;
import pl.mpak.orbada.sqlite.services.actions.PragmaDatabaseIntegrityCheckAction;
import pl.mpak.orbada.sqlite.services.actions.PragmaDatabaseLockingModeAction;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class OrbadaSQLitePlugin extends OrbadaPlugin {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public final static String driverType = "SQLite";
  public final static VersionID version = new VersionID(2, 0, 0, 15);

  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();

  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaSQLitePlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaSQLitePlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,Database,SQLite";
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
    return version.toString();
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
    return Consts.orbadaSQLitePluginId;
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
    application.registerDriverType(driverType);
    classList.add(SQLiteDbInfoProvider.class);
    classList.add(SQLiteDatabaseProvider.class);
    classList.add(SQLiteTablesView.class);
    classList.add(SQLiteViewsView.class);
    classList.add(SQLiteTriggersView.class);
    classList.add(SQLiteDatabasesView.class);
    classList.add(DropTableAction.class);
    classList.add(DropViewAction.class);
    classList.add(DropTriggerAction.class);
    classList.add(FreezeAction.class);
    classList.add(AttachDatabaseAction.class);
    classList.add(DetachDatabaseAction.class);
    classList.add(SQLitePerspectiveProvider.class);
    classList.add(PragmaDatabaseLockingModeAction.class);
    classList.add(PragmaDatabaseCacheSizeAction.class);
    classList.add(PragmaDatabaseIntegrityCheckAction.class);
    initTemplates();
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
    for (IPlugin plugin : loadedPlugins) {
      if (Consts.orbadaUniversalPluginId.equals(plugin.getUniqueID())) {
        classList.add(ExplainPlanProvider.class);
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

  private void initTemplates() {
    if (InternalDatabase.get() == null) {
      return;
    }
    Database database = InternalDatabase.get();
    Query query = database.createQuery();
    try {
//      query.open("select count( 0 ) cnt from templates where tpl_name like 'oracle-%'");
//      if (query.fieldByName("cnt").getInteger() == 0) {
//        Template template = new Template(application.getOrbadaDatabase());
//        template.setName("oracle-function");
//        template.setDescription("Oracle Function Template");
//        template.setUsrId("");
//        template.setBody(
//          "CREATE FUNCTION $(&name) $(&parameters) RETURN $(&return) IS\n" +
//          "--############################################################################\n" +
//          "--# ORBADA   : To change this template, choose Tools | Templates\n" +
//          "--# Autor    : $(orbada.user.name)\n" +
//          "--# Stworzono: $(orbada.current.date) $(orbada.current.time)\n" +
//          "--# Opis     : $(&description)\n" +
//          "--# Zmiany   :\n" +
//          "--# Data:      Autor:         Wersja:\n" +
//          "--# ---------- -------------- ------------------------------------------------\n" +
//          "--############################################################################\n" +
//          "BEGIN\n" +
//          "  /* TODO function implementation */\n" +
//          "$(&body)\n" +
//          "END;");
//        template.applyInsert();
//      }
      
//      ISettings settings = Application.get().getSettings(OracleTemplatesSettingsProvider.settingsName);
//      if (settings.getValue(OracleTemplatesSettingsProvider.setFunction).isNull()) {
//        settings.setValue(OracleTemplatesSettingsProvider.setFunction, "oracle-function");
//      }
//      settings.store();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }
  
}
