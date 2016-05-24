package pl.mpak.orbada.postgresql;

import java.util.ArrayList;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.postgresql.services.PostgreSQLAggregateListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLDatabaseProvider;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.postgresql.services.PostgreSQLFunctionListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLPerspectiveProvider;
import pl.mpak.orbada.postgresql.services.PostgreSQLRoleListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLSchemaListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLSearchObjectView;
import pl.mpak.orbada.postgresql.services.PostgreSQLSearchSourceView;
import pl.mpak.orbada.postgresql.services.PostgreSQLSequenceListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLSessionsView;
import pl.mpak.orbada.postgresql.services.PostgreSQLTableListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLTableSizeView;
import pl.mpak.orbada.postgresql.services.PostgreSQLTablespaceListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLTriggerFunctionListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLTriggerListView;
import pl.mpak.orbada.postgresql.services.PostgreSQLTypesView;
import pl.mpak.orbada.postgresql.services.PostgreSQLViewListView;
import pl.mpak.orbada.postgresql.services.actions.ExplainPlanService;
import pl.mpak.orbada.postgresql.services.actions.FunctionActionsService;
import pl.mpak.orbada.postgresql.services.actions.PostgreSQLCancelQueryAction;
import pl.mpak.orbada.postgresql.services.actions.PostgreSQLSQLWarningsService;
import pl.mpak.orbada.postgresql.services.actions.PostgreSQLTerminateSessionAction;
import pl.mpak.orbada.postgresql.services.actions.TableActionsService;
import pl.mpak.orbada.postgresql.services.actions.TriggerActionsService;
import pl.mpak.orbada.postgresql.services.actions.TypeActionsService;
import pl.mpak.orbada.postgresql.services.actions.ViewActionsService;
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
public class OrbadaPostgreSQLPlugin extends OrbadaPlugin {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public final static String driverType = "PostgreSQL";
  public final static VersionID version = new VersionID(1, 0, 2, 23);
  public final static String adminGroup = stringManager.getString("OrbadaPostgreSQLPlugin-admin-group");

  private static pl.mpak.util.timer.TimerQueue refreshQueue;
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();

  public static pl.mpak.util.timer.TimerQueue getRefreshQueue() {
    if (refreshQueue == null) {
      refreshQueue = pl.mpak.util.timer.TimerManager.getTimer("orbada-postgresql-refresh");
    }
    return refreshQueue;
  }

  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  @Override
  public String getInternalName() {
    return "OrbadaPostgreSQLPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  @Override
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaPostgreSQLPlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,Database,PostgreSQL";
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
    return Consts.orbadaPostgreSQLPluginId;
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
    application.registerDriverType(driverType);
    classList.add(PostgreSQLDbInfoProvider.class);
    classList.add(PostgreSQLDatabaseProvider.class);
    classList.add(PostgreSQLPerspectiveProvider.class);
    classList.add(PostgreSQLTableListView.class);
    classList.add(PostgreSQLViewListView.class);
    classList.add(PostgreSQLSearchObjectView.class);
    classList.add(PostgreSQLTableSizeView.class);
    classList.add(PostgreSQLFunctionListView.class);
    classList.add(PostgreSQLTriggerFunctionListView.class);
    classList.add(PostgreSQLSequenceListView.class);
    classList.add(PostgreSQLTablespaceListView.class);
    classList.add(PostgreSQLSchemaListView.class);
    classList.add(PostgreSQLSearchSourceView.class);
    classList.add(PostgreSQLTriggerListView.class);
    classList.add(PostgreSQLSQLWarningsService.class);
    classList.add(PostgreSQLSessionsView.class);
    classList.add(PostgreSQLCancelQueryAction.class);
    classList.add(PostgreSQLTerminateSessionAction.class);
    classList.add(PostgreSQLTypesView.class);
    classList.add(TableActionsService.class);
    classList.add(ViewActionsService.class);
    classList.add(TypeActionsService.class);
    classList.add(FunctionActionsService.class);
    classList.add(TriggerActionsService.class);
    classList.add(PostgreSQLAggregateListView.class);
    classList.add(ExplainPlanService.class);
    classList.add(PostgreSQLRoleListView.class);
    initTemplates();
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
        //classList.add(ExplainPlanProvider.class);
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
