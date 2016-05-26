/*
 * OrbadaLafSubstancePlugin.java
 *
 * Created on 2010-10-31, 16:26:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.substance;

import java.util.ArrayList;
import java.util.List;
import orbada.Consts;
import pl.mpak.orbada.laf.substance.services.SubstanceAutumnLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceBusinessBlackSteelLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceBusinessBlueSteelLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceBusinessLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceChallengerDeepLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceCremeCoffeeLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceCremeLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceDustCoffeeLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceDustLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceEmeraldDuskLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceGeminiLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceGraphiteAquaLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceGraphiteGlassLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceGraphiteLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceMagellanLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceMarinerLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceMistAquaLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceMistSilverLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceModerateLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceNebulaBrickWallLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceNebulaLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceOfficeBlack2007LookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceOfficeBlue2007LookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceOfficeSilver2007LookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceRavenLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceSaharaLookAndFeelService;
import pl.mpak.orbada.laf.substance.services.SubstanceTwilightLookAndFeelService;
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
public class OrbadaLafSubstancePlugin extends OrbadaPlugin {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLafSubstancePlugin.class);

  public final static String pluginName = "Substance";
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
    
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  @Override
  public String getInternalName() {
    return "OrbadaLafSubstancePlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  @Override
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaLafSubstancePlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "IDE,LAF";
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
    return new VersionID(1, 0, 0, 4).toString();
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
    return Consts.orbadaLafSubstancePluginId;
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
    classList.add(SubstanceAutumnLookAndFeelService.class);
    classList.add(SubstanceBusinessLookAndFeelService.class);
    classList.add(SubstanceBusinessBlackSteelLookAndFeelService.class);
    classList.add(SubstanceBusinessBlueSteelLookAndFeelService.class);
    classList.add(SubstanceChallengerDeepLookAndFeelService.class);
    classList.add(SubstanceCremeLookAndFeelService.class);
    classList.add(SubstanceCremeCoffeeLookAndFeelService.class);
    classList.add(SubstanceDustCoffeeLookAndFeelService.class);
    classList.add(SubstanceDustLookAndFeelService.class);
    classList.add(SubstanceEmeraldDuskLookAndFeelService.class);
    classList.add(SubstanceGeminiLookAndFeelService.class);
    classList.add(SubstanceGraphiteLookAndFeelService.class);
    classList.add(SubstanceGraphiteAquaLookAndFeelService.class);
    classList.add(SubstanceGraphiteGlassLookAndFeelService.class);
    classList.add(SubstanceMagellanLookAndFeelService.class);
    classList.add(SubstanceMarinerLookAndFeelService.class);
    classList.add(SubstanceMistAquaLookAndFeelService.class);
    classList.add(SubstanceMistSilverLookAndFeelService.class);
    classList.add(SubstanceModerateLookAndFeelService.class);
    classList.add(SubstanceNebulaBrickWallLookAndFeelService.class);
    classList.add(SubstanceNebulaLookAndFeelService.class);
    classList.add(SubstanceOfficeBlack2007LookAndFeelService.class);
    classList.add(SubstanceOfficeBlue2007LookAndFeelService.class);
    classList.add(SubstanceOfficeSilver2007LookAndFeelService.class);
    classList.add(SubstanceRavenLookAndFeelService.class);
    classList.add(SubstanceSaharaLookAndFeelService.class);
    classList.add(SubstanceTwilightLookAndFeelService.class);
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
  
}
