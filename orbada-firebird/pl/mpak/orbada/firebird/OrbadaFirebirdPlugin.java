package pl.mpak.orbada.firebird;

import java.util.ArrayList;
import java.util.List;
import orbada.Consts;
import orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Template;
import pl.mpak.orbada.firebird.cm.FindObjectFromEditorAction;
import pl.mpak.orbada.firebird.services.FirebirdDatabaseProvider;
import pl.mpak.orbada.firebird.services.FirebirdDbInfoProvider;
import pl.mpak.orbada.firebird.services.FirebirdDomainsView;
import pl.mpak.orbada.firebird.services.FirebirdExceptionsView;
import pl.mpak.orbada.firebird.services.FirebirdFocusProvider;
import pl.mpak.orbada.firebird.services.FirebirdFunctionsView;
import pl.mpak.orbada.firebird.services.FirebirdGeneralSettingsProvider;
import pl.mpak.orbada.firebird.services.FirebirdPerspectiveProvider;
import pl.mpak.orbada.firebird.services.FirebirdProceduresView;
import pl.mpak.orbada.firebird.services.FirebirdSequencesView;
import pl.mpak.orbada.firebird.services.FirebirdTablesView;
import pl.mpak.orbada.firebird.services.FirebirdTemplatesSettingsProvider;
import pl.mpak.orbada.firebird.services.FirebirdTriggersView;
import pl.mpak.orbada.firebird.services.FirebirdViewsView;
import pl.mpak.orbada.firebird.services.UniversalColumnProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.id.VersionID;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author akaluza
 */
public class OrbadaFirebirdPlugin extends OrbadaPlugin {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  public final static String firebirdDriverType = "Firebird";
  private static pl.mpak.util.timer.TimerQueue refreshQueue;
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
  public static FindObjectFromEditorAction findObjectAction;
  
  public static pl.mpak.util.timer.TimerQueue getRefreshQueue() {
    if (refreshQueue == null) {
      refreshQueue = pl.mpak.util.timer.TimerManager.getTimer("orbada-firebird-refresh");
    }
    return refreshQueue;
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaFirebirdPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaFirebirdPlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,Database,Firebird";
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
    return new VersionID(1, 0, 2, 22).toString();
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
    return Consts.orbadaFirebirdPluginId;
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
    System.setProperty("org.firebirdsql.jdbc.processName", "Orbada Firebird Plugin");
    System.setProperty("org.firebirdsql.jdbc.pid", Resolvers.expand("$(orbada.jvm.pid)"));
    findObjectAction = new FindObjectFromEditorAction();
    application.registerDriverType(firebirdDriverType);
    classList.add(FirebirdTablesView.class);
    classList.add(FirebirdTriggersView.class);
    classList.add(FirebirdViewsView.class);
    classList.add(FirebirdProceduresView.class);
    classList.add(FirebirdFunctionsView.class);
    classList.add(FirebirdSequencesView.class);
    classList.add(FirebirdExceptionsView.class);
    classList.add(FirebirdDomainsView.class);
    classList.add(FirebirdTemplatesSettingsProvider.class);
    classList.add(FirebirdPerspectiveProvider.class);
    classList.add(FirebirdFocusProvider.class);
    classList.add(FirebirdDbInfoProvider.class);
    classList.add(FirebirdDatabaseProvider.class);
    classList.add(FirebirdGeneralSettingsProvider.class);
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
        classList.add(UniversalColumnProvider.class);
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
      query.open("select count( 0 ) cnt from templates where tpl_name like 'firebird-%'");
      if (query.fieldByName("cnt").getInteger() == 0) {
        Template template;
        template = new Template(application.getOrbadaDatabase());
        template.setName("firebird-procedure");
        template.setDescription("Firebird Procedure Template");
        template.setUsrId("");
        template.setBody(
          "CREATE PROCEDURE $(&name) $(&parameters) $(&returns) AS\n" +
          "--############################################################################\n" +
          "--# ORBADA   : To change this template, choose Tools | Templates\n" +
          "--# Autor    : $(orbada.user.name)\n" +
          "--# Stworzono: $(orbada.current.date) $(orbada.current.time)\n" +
          "--# Opis     : $(&description)\n" +
          "--# Zmiany   :\n" +
          "--# Data:      Autor:         Wersja:\n" +
          "--# ---------- -------------- ------------------------------------------------\n" +
          "--############################################################################\n" +
          "BEGIN\n" +
          "  /* TODO procedure implementation */\n" +
          "$(&body)\n" +
          "END");
        template.applyInsert();

        template = new Template(application.getOrbadaDatabase());
        template.setName("firebird-trigger");
        template.setDescription("Firebird Trigger Template");
        template.setUsrId("");
        template.setBody(
          "CREATE TRIGGER $(&name) FOR $(&tablename)\n" +
          "$(&type) AS\n" +
          "--############################################################################\n" +
          "--# ORBADA   : To change this template, choose Tools | Templates\n" +
          "--# Autor    : $(orbada.user.name)\n" +
          "--# Stworzono: $(orbada.current.date) $(orbada.current.time)\n" +
          "--# Opis     : $(&description)\n" +
          "--# Zmiany   :\n" +
          "--# Data:      Autor:         Wersja:\n" +
          "--# ---------- -------------- ------------------------------------------------\n" +
          "--############################################################################\n" +
          "BEGIN\n" +
          "  /* TODO trigger implementation */\n" +
          "$(&body)\n" +
          "END");
        template.applyInsert();
      }
      
      ISettings settings = application.getSettings(FirebirdTemplatesSettingsProvider.settingsName);
      if (settings.getValue(FirebirdTemplatesSettingsProvider.setProcedure).isNull()) {
        settings.setValue(FirebirdTemplatesSettingsProvider.setProcedure, "firebird-procedure");
      }
      if (settings.getValue(FirebirdTemplatesSettingsProvider.setTrigger).isNull()) {
        settings.setValue(FirebirdTemplatesSettingsProvider.setTrigger, "firebird-trigger");
      }
      settings.store();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }
  
}
