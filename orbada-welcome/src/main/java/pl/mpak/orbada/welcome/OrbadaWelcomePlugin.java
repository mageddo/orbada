package pl.mpak.orbada.welcome;

import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.MainFrame;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.welcome.serives.WelcomeView;
import pl.mpak.util.id.VersionID;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OrbadaWelcomePlugin extends OrbadaPlugin {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("welcome");

  public final static String todoGroupName = "Orbada Welcome";
  
  /**
   * Funkcja powinna zwracaï¿½ nazwï¿½ wewnï¿½trznï¿½ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaWelcomePlugin";
  }
  
  /**
   * Funkcja powinna zwracaï¿½ nazwï¿½ opisowï¿½ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaWelcomePlugin-description-name"), new Object[] {getVersion()});
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
    return "IDE";
  }
  
  /**
   * Funkcja powinna zwracaï¿½ informacje o autorach wtyczki
   * @return
   */
  public String getAuthor() {
    return "Andrzej Ka³u¿a";
  }
  
  public String getCopyrights() {
    return "";
  }
  
  /**
   * Funkcja powinna zwracaï¿½ adres swtrony www
   * @return
   */
  public String getWebSite() {
    return null;
  }
  
  /**
   * Funkcja powinna zwracaï¿½ adres swtrony aktualizacji
   * @return
   */
  public String getUpdateSite() {
    return null;
  }
  
  /**
   * Funckja powinna zwrï¿½ciï¿½ wersjï¿½ najlepiej w postaci:
   * major.minor.release.build
   * @return
   */
  public String getVersion() {
    return new VersionID(1, 0, 0, 15).toString();
  }
  
  /**
   * Moï¿½e zwrï¿½ciï¿½ treï¿½ï¿½ licencji
   * @return
   */
  public String getLicence() {
    return null;
  }
  
  /**
   * <p>Funkcja musi zwracaï¿½ unikalny identyfikator wtyczki
   * <p>W tym miejscu moï¿½na skoï¿½ystaï¿½ z kasy pl.mpak.sky.utils.UniqueID
   * Identyfikator identyfikuje jednoznacznie zaï¿½adowanï¿½ wtyczkï¿½.
   * <p>Moï¿½e teï¿½ byï¿½ to unikalna nazwa wtyczki.
   * @return
   */
  public String getUniqueID() {
    return Consts.orbadaWelcomePluginId;
  }
  
  /**
   * Funkcja wywoï¿½ywana jest zaraz po zaï¿½adowaniu wtyczki.
   * ManOra jest juï¿½ utworzona, konfiguracja programu zaï¿½adowana 
   */
  public void load() {
    
  }
  
  /**
   * Funkcja wywoï¿½ywana jest zaraz przed zamkniï¿½ciem programu
   */
  public void unload() {
  }
  
  /**
   * Funkcja wywoï¿½ywana jest po zaï¿½adowaniu wszystkich wtyczek i pokazaniu okna gï¿½ï¿½wnego.
   * W tym miejscu moï¿½e byï¿½ sprawdzone czy sï¿½ wszystkie wtyczki potrzebne
   * do prawidï¿½owego dziaï¿½ania tej wtyczki.
   * Rï¿½wnieï¿½ w tym miejscu moï¿½na podpiï¿½ï¿½ listenery gdzie tylko siï¿½ chce.
   * Moï¿½e podpiï¿½ï¿½ siï¿½ w odpowiednie miejsca menu, toolbar-a, listï¿½ poï¿½ï¿½czeï¿½
   * skonfigurowanych i nawiï¿½zanych. Moï¿½e uruchomiï¿½ jakieï¿½ zadania (Task), wpisaï¿½
   * coï¿½ do log-a (pl.mpak.sky.utils.logging.Logger), etc
   */
  public void initialize() {
  }
  
  /**
   * <p>Funkcja powinna sprawdziï¿½ listï¿½ potrzebnych innych wtyczek
   * return informacje czy moï¿½na warunki sï¿½ speï¿½nione i czy moï¿½na uï¿½ywaï¿½ tej wtyczki
   * @param loadedPlugins 
   * @return 
   */
  public boolean requires(List<IPlugin> loadedPlugins) {
    return true;
  }

  public Class<IPluginProvider>[] getProviderArray() {
    return new Class[] {
      WelcomeView.class
    };
  }

  @Override
  public void processMessage(PluginMessage message) {
    if (message.getDestinationId() == null && message.isMessageId(Consts.globalMessageOrbadaStarted)) {
      ISettings settings = application.getSettings("orbada-welcome");
      if (settings.getValue("show", true)) {
        final MainFrame main = Application.get().getMainFrame();
        java.awt.EventQueue.invokeLater(new Runnable() {
          @Override
          public void run() {
            if (main.getToolsPerspective() == null) {
              main.openPerspectiveFor(null, null);
            }
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                if (main.getToolsPerspective() != null && !main.getToolsPerspective().isViewCreated(WelcomeView.class)) {
                  main.setActivePerspective(main.getToolsPerspective());
                  main.getToolsPerspective().createView(new WelcomeView(), false, false);
                }
              }
            });
          }
        });
      }
    }
  }
  
}
