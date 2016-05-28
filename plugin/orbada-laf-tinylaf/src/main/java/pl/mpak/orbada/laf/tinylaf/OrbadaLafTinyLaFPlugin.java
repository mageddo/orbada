/*
 * OrbadaLafJGoodiesPlugin.java
 *
 * Created on 2010-10-31, 16:26:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.tinylaf;

import java.util.ArrayList;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.tinylaf.services.TinyLaFGlobalOptionsSettingsService;
import pl.mpak.orbada.laf.tinylaf.services.TinyLookAndFeelService;
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
public class OrbadaLafTinyLaFPlugin extends OrbadaPlugin {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("laf-tinylaf");

  public final static String tinyLaFName = "TinyLaF";
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
    
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  @Override
  public String getInternalName() {
    return "OrbadaLafTinyLaFPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  @Override
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaLafTinyLaFPlugin-descriptive-name"), new Object[] {getVersion()});
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
    return Consts.orbadaLafTinyLaFPluginId;
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
    classList.add(TinyLookAndFeelService.class);
    classList.add(TinyLaFGlobalOptionsSettingsService.class);
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
