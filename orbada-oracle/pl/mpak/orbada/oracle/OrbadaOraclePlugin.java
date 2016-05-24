package pl.mpak.orbada.oracle;

import java.util.ArrayList;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Template;
import pl.mpak.orbada.oracle.cm.CallObjectFromEditorAction;
import pl.mpak.orbada.oracle.cm.FindObjectFromEditorAction;
import pl.mpak.orbada.oracle.services.OracleAutoCompleteService;
import pl.mpak.orbada.oracle.services.OracleCompileErrorSettingsProvider;
import pl.mpak.orbada.oracle.services.OracleConnectionSettingsProvider;
import pl.mpak.orbada.oracle.services.OracleCopySqlFromSourceActionProvider;
import pl.mpak.orbada.oracle.services.OracleDatabaseProvider;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.oracle.services.OracleDbLinksView;
import pl.mpak.orbada.oracle.services.OracleDbmsOutputConnectionSettingsProvider;
import pl.mpak.orbada.oracle.services.OracleDbmsOutputSettingsProvider;
import pl.mpak.orbada.oracle.services.OracleDbmsOutputView;
import pl.mpak.orbada.oracle.services.OracleDictionaryView;
import pl.mpak.orbada.oracle.services.OracleDirectoriesView;
import pl.mpak.orbada.oracle.services.OracleExplainPlanProvider;
import pl.mpak.orbada.oracle.services.OracleExplainPlanSourceActionProvider;
import pl.mpak.orbada.oracle.services.OracleFocusProvider;
import pl.mpak.orbada.oracle.services.OracleFunctionsView;
import pl.mpak.orbada.oracle.services.OracleIndexesView;
import pl.mpak.orbada.oracle.services.OracleJavaClassesView;
import pl.mpak.orbada.oracle.services.OracleJavaResourcesView;
import pl.mpak.orbada.oracle.services.OracleJavaSourcesView;
import pl.mpak.orbada.oracle.services.OracleJobsView;
import pl.mpak.orbada.oracle.services.OracleMViewsView;
import pl.mpak.orbada.oracle.services.OraclePackagesView;
import pl.mpak.orbada.oracle.services.OraclePerspectiveProvider;
import pl.mpak.orbada.oracle.services.OracleProceduresView;
import pl.mpak.orbada.oracle.services.OracleQuickSearchObject;
import pl.mpak.orbada.oracle.services.OracleRecyclebinView;
import pl.mpak.orbada.oracle.services.OracleSearchObjectView;
import pl.mpak.orbada.oracle.services.OracleSearchSourceView;
import pl.mpak.orbada.oracle.services.OracleSequencesView;
import pl.mpak.orbada.oracle.services.OracleSettingsProvider;
import pl.mpak.orbada.oracle.services.OracleSourceCreatorSettingsProvider;
import pl.mpak.orbada.oracle.services.OracleSynonymsView;
import pl.mpak.orbada.oracle.services.OracleTablesView;
import pl.mpak.orbada.oracle.services.OracleTemplatesSettingsProvider;
import pl.mpak.orbada.oracle.services.OracleTriggersView;
import pl.mpak.orbada.oracle.services.OracleTypesView;
import pl.mpak.orbada.oracle.services.OracleViewsView;
import pl.mpak.orbada.oracle.services.UniversalColumnProvider;
import pl.mpak.orbada.oracle.services.UniversalErrorPosition;
import pl.mpak.orbada.oracle.services.UniversalResultDbmsOutputProvider;
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

/**
 *
 * @author akaluza
 */
public class OrbadaOraclePlugin extends OrbadaPlugin {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public final static String versionId = new VersionID(1, 0, 1, 60).toString();

  public final static String oracleDriverType = "Oracle";
  public final static String javaGroup = stringManager.getString("OrbadaOraclePlugin-java-objects");
  public final static String dataGroup = stringManager.getString("OrbadaOraclePlugin-data-objects");
  public final static String editableGroup = stringManager.getString("OrbadaOraclePlugin-editable-objects");
  public final static String advancedGroup = stringManager.getString("OrbadaOraclePlugin-advanced");
  public final static String adminGroup = stringManager.getString("OrbadaOraclePlugin-oracle-dba");

  public final static String specjalOracleWizardsActions = "oracle-wizards-actions";
  public final static String specjalOracleActions = "oracle-actions";
  
  private static pl.mpak.util.timer.TimerQueue refreshQueue;
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
  
  public static FindObjectFromEditorAction findObjectAction;
  public static CallObjectFromEditorAction callObjectAction;
    
  public static pl.mpak.util.timer.TimerQueue getRefreshQueue() {
    if (refreshQueue == null) {
      refreshQueue = pl.mpak.util.timer.TimerManager.getTimer("orbada-oracle-refresh");
    }
    return refreshQueue;
  }

  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaOraclePlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaOraclePlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,Database,Oracle";
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
    return versionId;
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
    return Consts.orbadaOraclePluginId;
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
    application.registerDriverType(oracleDriverType);
    findObjectAction = new FindObjectFromEditorAction();
    callObjectAction = new CallObjectFromEditorAction();
    classList.add(OracleDbInfoProvider.class);
    classList.add(OracleDictionaryView.class);
    classList.add(OracleDbmsOutputView.class);
    classList.add(OracleDbmsOutputSettingsProvider.class);
    classList.add(OracleDbmsOutputConnectionSettingsProvider.class);
    classList.add(OracleTablesView.class);
    classList.add(OracleDatabaseProvider.class);
    classList.add(OracleRecyclebinView.class);
    classList.add(OracleSequencesView.class);
    classList.add(OracleIndexesView.class);
    classList.add(OracleTriggersView.class);
    classList.add(OracleSynonymsView.class);
    classList.add(OracleDbLinksView.class);
    classList.add(OracleViewsView.class);
    classList.add(OracleTypesView.class);
    classList.add(OraclePackagesView.class);
    classList.add(OraclePerspectiveProvider.class);
    classList.add(OracleFunctionsView.class);
    classList.add(OracleProceduresView.class);
    classList.add(OracleMViewsView.class);
    classList.add(OracleJavaSourcesView.class);
    classList.add(OracleJavaClassesView.class);
    classList.add(OracleJavaResourcesView.class);
    classList.add(OracleDirectoriesView.class);
    classList.add(OracleTemplatesSettingsProvider.class);
    classList.add(OracleSearchObjectView.class);
    classList.add(OracleSearchSourceView.class);
    classList.add(OracleQuickSearchObject.class);
    classList.add(OracleJobsView.class);
    classList.add(OracleCompileErrorSettingsProvider.class);
    classList.add(OracleFocusProvider.class);
    classList.add(OracleSourceCreatorSettingsProvider.class);
    classList.add(OracleSettingsProvider.class);
    classList.add(OracleConnectionSettingsProvider.class);
    classList.add(OracleAutoCompleteService.class);
    classList.add(OracleExplainPlanSourceActionProvider.class);
    classList.add(OracleCopySqlFromSourceActionProvider.class);
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
        classList.add(OracleExplainPlanProvider.class);
        classList.add(UniversalResultDbmsOutputProvider.class);
        classList.add(UniversalErrorPosition.class);
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
      query.open("select count( 0 ) cnt from templates where tpl_name like 'oracle-%'");
      if (query.fieldByName("cnt").getInteger() == 0) {
        Template template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-function");
        template.setDescription("Oracle Function Template");
        template.setUsrId("");
        template.setBody(
          "CREATE FUNCTION $(&name) $(&parameters) RETURN $(&return) IS\n" +
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
          "  /* TODO function implementation */\n" +
          "$(&body)\n" +
          "END;");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-java-source");
        template.setDescription("Oracle Java Source Template");
        template.setUsrId("");
        template.setBody(
          "CREATE JAVA SOURCE NAMED \"$(&name)\" AS\n" +
          "public class $(&name) {\n" +
          "  /**\n" +
          "   * $(&name) [.java]\n" +
          "   * Created on $(orbada.current.date) $(orbada.current.time)\n" +
          "   * \n" +
          "   * <p>ORBADA: To change this template, choose Tools | Templates\n" +
          "   * <p>$(&description)\n" +
          "   * \n" +
          "   * @author $(orbada.user.name)\n" +
          "   * @author\n" +
          "   */\n" +
          "\n" +
          "  public $(&name)() {\n" +
          "  }\n" +
          "\n" +
          "}");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-object-type");
        template.setDescription("Oracle OBJECT Type Template");
        template.setUsrId("");
        template.setBody(
          "CREATE TYPE $(&name) AS OBJECT (\n" +
          "--############################################################################\n" +
          "--# ORBADA   : To change this template, choose Tools | Templates\n" +
          "--# Autor    : $(orbada.user.name)\n" +
          "--# Stworzono: $(orbada.current.date) $(orbada.current.time)\n" +
          "--# Opis     : $(&description)\n" +
          "--# Zmiany   :\n" +
          "--# Data:      Autor:         Wersja:\n" +
          "--# ---------- -------------- ------------------------------------------------\n" +
          "--############################################################################\n" +
          "  /* TODO enter attribute and method declarations here */\n" +
          ");");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-object-type-body");
        template.setDescription("Oracle OBJECT Type Body Template");
        template.setUsrId("");
        template.setBody(
          "CREATE TYPE BODY $(&name) IS\n" +
          "--############################################################################\n" +
          "--# ORBADA   : To change this template, choose Tools | Templates\n" +
          "--# Autor    : $(orbada.user.name)\n" +
          "--# Stworzono: $(orbada.current.date) $(orbada.current.time)\n" +
          "--# Opis     : $(&description)\n" +
          "--# Zmiany   :\n" +
          "--# Data:      Autor:         Wersja:\n" +
          "--# ---------- -------------- ------------------------------------------------\n" +
          "--############################################################################\n" +
          "  /* TODO enter attribute and method implementations here */\n" +
          "END;");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-package");
        template.setDescription("Oracle Pakage Template");
        template.setUsrId("");
        template.setBody(
          "CREATE PACKAGE $(&name) AS\n" +
          "--############################################################################\n" +
          "--# ORBADA   : To change this template, choose Tools | Templates\n" +
          "--# Autor    : $(orbada.user.name)\n" +
          "--# Stworzono: $(orbada.current.date) $(orbada.current.time)\n" +
          "--# Opis     : $(&description)\n" +
          "--# Zmiany   :\n" +
          "--# Data:      Autor:         Wersja:\n" +
          "--# ---------- -------------- ------------------------------------------------\n" +
          "--############################################################################\n" +
          "  /* TODO definitions */\n" +
          "END $(&name);");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-package-body");
        template.setDescription("Oracle Package Body Template");
        template.setUsrId("");
        template.setBody(
          "CREATE PACKAGE BODY $(&name) AS\n" +
          "--############################################################################\n" +
          "--# ORBADA   : To change this template, choose Tools | Templates\n" +
          "--# Autor    : $(orbada.user.name)\n" +
          "--# Stworzono: $(orbada.current.date) $(orbada.current.time)\n" +
          "--# Opis     : $(&description)\n" +
          "--# Zmiany   :\n" +
          "--# Data:      Autor:         Wersja:\n" +
          "--# ---------- -------------- ------------------------------------------------\n" +
          "--############################################################################\n" +
          "  /* TODO implementations */\n" +
          "END $(&name);");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-procedure");
        template.setDescription("Oracle Procedure Template");
        template.setUsrId("");
        template.setBody(
          "CREATE PROCEDURE $(&name) $(&parameters) IS\n" +
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
          "END;");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-table-type");
        template.setDescription("Oracle TABLE Type Template");
        template.setUsrId("");
        template.setBody("CREATE TYPE $(&name) AS TABLE OF $(&data.type);");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-trigger");
        template.setDescription("Oracle Trigger Template");
        template.setUsrId("");
        template.setBody(
          "CREATE TRIGGER $(&name)\n" +
          "$(&type)\n" +
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
          "END;");
        template.applyInsert();
        
        template = new Template(application.getOrbadaDatabase());
        template.setName("oracle-varray-type");
        template.setDescription("Oracle VARRAY Type Template");
        template.setUsrId("");
        template.setBody("CREATE TYPE $(&name) AS VARRAY($(&size)) OF $(&data.type);");
        template.applyInsert();
      }
      
      ISettings settings = Application.get().getSettings(OracleTemplatesSettingsProvider.settingsName);
      if (settings.getValue(OracleTemplatesSettingsProvider.setFunction).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setFunction, "oracle-function");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setJavaSource).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setJavaSource, "oracle-java-source");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setObjectType).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setObjectType, "oracle-object-type");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setObjectTypeBody).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setObjectTypeBody, "oracle-object-type-body");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setPackage).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setPackage, "oracle-package");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setPackageBody).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setPackageBody, "oracle-package-body");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setProcedure).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setProcedure, "oracle-procedure");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setTableType).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setTableType, "oracle-table-type");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setTrigger).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setTrigger, "oracle-trigger");
      }
      if (settings.getValue(OracleTemplatesSettingsProvider.setVarrayType).isNull()) {
        settings.setValue(OracleTemplatesSettingsProvider.setVarrayType, "oracle-varray-type");
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
