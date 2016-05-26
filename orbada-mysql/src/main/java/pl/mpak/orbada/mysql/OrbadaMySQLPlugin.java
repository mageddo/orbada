package pl.mpak.orbada.mysql;

import java.util.ArrayList;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Template;
import pl.mpak.orbada.mysql.cm.FindObjectFromEditorAction;
import pl.mpak.orbada.mysql.services.ExplainPlanProvider;
import pl.mpak.orbada.mysql.services.MySQLAutotraceProvider;
import pl.mpak.orbada.mysql.services.MySQLDatabaseProvider;
import pl.mpak.orbada.mysql.services.MySQLDbInfoProvider;
import pl.mpak.orbada.mysql.services.MySQLFocusProvider;
import pl.mpak.orbada.mysql.services.MySQLFunctionsView;
import pl.mpak.orbada.mysql.services.MySQLHelpView;
import pl.mpak.orbada.mysql.services.MySQLPerspectiveProvider;
import pl.mpak.orbada.mysql.services.MySQLProceduresView;
import pl.mpak.orbada.mysql.services.MySQLSearchObjectView;
import pl.mpak.orbada.mysql.services.MySQLSessionsView;
import pl.mpak.orbada.mysql.services.MySQLTablesView;
import pl.mpak.orbada.mysql.services.MySQLTemplatesSettingsProvider;
import pl.mpak.orbada.mysql.services.MySQLTriggersView;
import pl.mpak.orbada.mysql.services.MySQLViewsView;
import pl.mpak.orbada.mysql.services.UniversalColumnProvider;
import pl.mpak.orbada.mysql.services.actions.FunctionActionsService;
import pl.mpak.orbada.mysql.services.actions.ObjectSearchActionsService;
import pl.mpak.orbada.mysql.services.actions.ProcedureActionsService;
import pl.mpak.orbada.mysql.services.actions.SessionViewActionsService;
import pl.mpak.orbada.mysql.services.actions.TableActionsService;
import pl.mpak.orbada.mysql.services.actions.TableColumnActionsService;
import pl.mpak.orbada.mysql.services.actions.TableConstraintActionsService;
import pl.mpak.orbada.mysql.services.actions.TableIndexActionsService;
import pl.mpak.orbada.mysql.services.actions.TableTriggerActionsService;
import pl.mpak.orbada.mysql.services.actions.TableUtilsActionsService;
import pl.mpak.orbada.mysql.services.actions.TriggerActionsService;
import pl.mpak.orbada.mysql.services.actions.ViewActionsService;
import pl.mpak.orbada.mysql.services.actions.ViewColumnActionsService;
import pl.mpak.orbada.mysql.services.actions.ViewUtilsActionsService;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class OrbadaMySQLPlugin extends OrbadaPlugin {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public final static String driverType = "MySQL";
  public final static String adminGroup = stringManager.getString("OrbadaMySQLPlugin-admin-group");
  public final static String specjalMySQLActions = "mysql-actions";

  private static pl.mpak.util.timer.TimerQueue refreshQueue;
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();

  public static FindObjectFromEditorAction findObjectAction;

  public static pl.mpak.util.timer.TimerQueue getRefreshQueue() {
    if (refreshQueue == null) {
      refreshQueue = pl.mpak.util.timer.TimerManager.getTimer("orbada-mysql-refresh");
    }
    return refreshQueue;
  }

  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaMySQLPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaMySQLPlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,Database,MySQL";
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
    return new VersionID(1, 1, 0, 37).toString();
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
    return Consts.orbadaMySQLPluginId;
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
    findObjectAction = new FindObjectFromEditorAction();
    
    classList.add(MySQLDatabaseProvider.class);
    classList.add(MySQLDbInfoProvider.class);
    classList.add(MySQLHelpView.class);
    classList.add(MySQLTablesView.class);
    classList.add(MySQLViewsView.class);
    classList.add(MySQLProceduresView.class);
    classList.add(MySQLFunctionsView.class);
    classList.add(TableActionsService.class);
    classList.add(TableColumnActionsService.class);
    classList.add(TableIndexActionsService.class);
    classList.add(TableConstraintActionsService.class);
    classList.add(TableTriggerActionsService.class);
    classList.add(ViewActionsService.class);
    classList.add(ViewColumnActionsService.class);
    classList.add(ProcedureActionsService.class);
    classList.add(FunctionActionsService.class);
    classList.add(UniversalColumnProvider.class);
    classList.add(MySQLPerspectiveProvider.class);
    classList.add(MySQLTriggersView.class);
    classList.add(TriggerActionsService.class);
    classList.add(MySQLTemplatesSettingsProvider.class);
    classList.add(MySQLAutotraceProvider.class);
    classList.add(MySQLSessionsView.class);
    classList.add(MySQLSearchObjectView.class);
    classList.add(MySQLFocusProvider.class);
    classList.add(SessionViewActionsService.class);
    classList.add(TableUtilsActionsService.class);
    classList.add(ViewUtilsActionsService.class);
    classList.add(ObjectSearchActionsService.class);
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
    Query query = InternalDatabase.get().createQuery();
    try {
      query.open("select count( 0 ) cnt from templates where tpl_name like 'mysql-%'");
      if (query.fieldByName("cnt").getInteger() == 0) {
        Template template = new Template(application.getOrbadaDatabase());
        template.setName("mysql-function");
        template.setDescription("MySQL Function Template");
        template.setUsrId("");
        template.setBody(
          "CREATE FUNCTION $(&name)($(&parameters)) RETURNS $(&return)\n" +
          "  $(pr&operties)\n" +
          "BEGIN\n" +
          "  -- ############################################################################\n" +
          "  -- # ORBADA   : To change this template, choose Tools | Templates\n" +
          "  -- # Author   : $(orbada.user.name)\n" +
          "  -- # Created  : $(orbada.current.date) $(orbada.current.time)\n" +
          "  -- ############################################################################\n" +
          "  --\n" +
          "  /* TODO function implementation */\n" +
          "$(&body)\n" +
          "END");
        template.applyInsert();

        template = new Template(application.getOrbadaDatabase());
        template.setName("mysql-procedure");
        template.setDescription("MySQL Procedure Template");
        template.setUsrId("");
        template.setBody(
          "CREATE PROCEDURE $(&name)($(&parameters))\n" +
          "  $(pr&operties)\n" +
          "BEGIN\n" +
          "  -- ############################################################################\n" +
          "  -- # ORBADA   : To change this template, choose Tools | Templates\n" +
          "  -- # Author   : $(orbada.user.name)\n" +
          "  -- # Created  : $(orbada.current.date) $(orbada.current.time)\n" +
          "  -- ############################################################################\n" +
          "  --\n" +
          "  /* TODO procedure implementation */\n" +
          "$(&body)\n" +
          "END");
        template.applyInsert();

        template = new Template(application.getOrbadaDatabase());
        template.setName("mysql-trigger");
        template.setDescription("MySQL Trigger Template");
        template.setUsrId("");
        template.setBody(
          "CREATE TRIGGER $(&name)\n" +
          "$(&type)\n" +
          "BEGIN\n" +
          "  -- ############################################################################\n" +
          "  -- # ORBADA   : To change this template, choose Tools | Templates\n" +
          "  -- # Author   : $(orbada.user.name)\n" +
          "  -- # Created  : $(orbada.current.date) $(orbada.current.time)\n" +
          "  -- ############################################################################\n" +
          "  --\n" +
          "  /* TODO trigger implementation */\n" +
          "$(&body)\n" +
          "END");
        template.applyInsert();
      }
      
      ISettings settings = Application.get().getSettings(MySQLTemplatesSettingsProvider.settingsName);
      if (settings.getValue(MySQLTemplatesSettingsProvider.setFunction).isNull()) {
        settings.setValue(MySQLTemplatesSettingsProvider.setFunction, "mysql-function");
      }
      if (settings.getValue(MySQLTemplatesSettingsProvider.setProcedure).isNull()) {
        settings.setValue(MySQLTemplatesSettingsProvider.setProcedure, "mysql-procedure");
      }
      if (settings.getValue(MySQLTemplatesSettingsProvider.setTrigger).isNull()) {
        settings.setValue(MySQLTemplatesSettingsProvider.setTrigger, "mysql-trigger");
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
