package pl.mpak.orbada.todo;

import java.io.File;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.todo.services.TodoPerspectiveProvider;
import pl.mpak.orbada.todo.services.TodoView;
import pl.mpak.util.id.VersionID;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author akaluza
 */
public class OrbadaTodoPlugin extends OrbadaPlugin {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaTodoPlugin.class);

  public final static String todoGroupName = "Orbada Tools";
  
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaTodoPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaTodoPlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,Developers";
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
    return new VersionID(1, 0, 2, 24).toString();
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
    return Consts.orbadaTodoPluginId;
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
    new File(Resolvers.expand("$(orbada.home)") +"/todos").mkdirs();
    if (application.isUserAdmin()) {
      Database database = InternalDatabase.get();
      if (getLastVersion() == null) {
        try {
          database.executeCommand(
            "create table todos (\n" +
            "  td_id varchar(40) not null primary key,\n" +
            "  td_usr_id varchar(40) not null,\n" +
            "  td_sch_id varchar(40),\n" +
            "  td_priority integer,\n" +
            "  td_state varchar(100),\n" +
            "  td_title varchar(200),\n" +
            "  td_description varchar(1000),\n" +
            "  td_enable varchar(1) default 'T',\n" +
            "  td_inserted timestamp,\n" +
            "  td_updated timestamp,\n" +
            "  td_workaround varchar(1000),\n" +
            "  td_plan_end timestamp,\n" +
            "  td_ended timestamp,\n" +
            "  td_app_version varchar(100),\n" +
            "  td_orbada varchar(1),\n" +
            "  td_exported varchar(1)\n" +
            ")");
          database.executeCommand("alter table todos add constraint todos_sch_id_fk foreign key (td_sch_id) references schemas (sch_id) on delete cascade");
          database.executeCommand("create index todos_sch_id_priority_i on todos (td_sch_id, td_priority)");
          database.executeCommand("alter table todos add constraint todo_user_fk foreign key (td_usr_id) references users (usr_id) on delete cascade");
        }
        catch (Exception ex2) {
          ExceptionUtil.processException(ex2);
        }
      }
      else if (new VersionID(getLastVersion()).getBuild() < 21) {
        try {
          database.executeCommand("alter table todos add td_app_version varchar(100)");
          database.executeCommand("alter table todos add td_orbada varchar(1)");
          database.executeCommand("alter table todos add td_exported varchar(1)");
        }
        catch (Exception ex2) {
          ExceptionUtil.processException(ex2);
        }
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
    return true;
  }

  public Class<IPluginProvider>[] getProviderArray() {
    return new Class[] {
      TodoView.class,
      TodoPerspectiveProvider.class
    };
  }

  @Override
  public void processMessage(PluginMessage message) {
  }
  
}
