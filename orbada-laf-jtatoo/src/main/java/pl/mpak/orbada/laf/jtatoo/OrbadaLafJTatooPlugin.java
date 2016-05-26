/*
 * OrbadaLafSubstancePlugin.java
 *
 * Created on 2010-10-31, 16:26:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jtatoo;

import java.util.ArrayList;
import java.util.List;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.laf.jtatoo.services.JTatooAcrylLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooAeroLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooAluminiumLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooBernsteinLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooFastLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooGlobalOptionsLookAndFeelSettingsService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooGraphiteLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooHiFiLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooLunaLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooMcWinLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooMintLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooNoireLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.services.JTatooSmartLookAndFeelService;
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
public class OrbadaLafJTatooPlugin extends OrbadaPlugin {
  
  private StringManager i18n = StringManagerFactory.getStringManager(OrbadaLafJTatooPlugin.class);

  public final static String pluginName = "JTatoo";
  public final static ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();
    
  /**
   * Funkcja powinna zwracaï¿½ nazwï¿½ wewnï¿½trznï¿½ wtyczki
   * @return
   */
  @Override
  public String getInternalName() {
    return "OrbadaLafJTatooPlugin";
  }
  
  /**
   * Funkcja powinna zwracaï¿½ nazwï¿½ opisowï¿½ wtyczki
   * @return
   */
  @Override
  public String getDescriptiveName() {
    return String.format(i18n.getString("OrbadaLafJTatooPlugin-descriptive-name"), new Object[] {getVersion()});
  }
  
  /**
   * <p>Funkcja powinna zwracaï¿½ rozszerzone informacje opisowe dotyczï¿½ce wtyczki.
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
   * Funkcja powinna zwracaï¿½ informacje o autorach wtyczki
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
   * Funkcja powinna zwracaï¿½ adres swtrony www
   * @return
   */
  @Override
  public String getWebSite() {
    return null;
  }
  
  /**
   * Funkcja powinna zwracaï¿½ adres swtrony aktualizacji
   * @return
   */
  @Override
  public String getUpdateSite() {
    return null;
  }
  
  /**
   * Funckja powinna zwrï¿½ciï¿½ wersjï¿½ najlepiej w postaci:
   * major.minor.release.build
   * @return
   */
  @Override
  public String getVersion() {
    return new VersionID(1, 0, 0, 1).toString();
  }
  
  /**
   * Moï¿½e zwrï¿½ciï¿½ treï¿½ï¿½ licencji
   * @return
   */
  @Override
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
  @Override
  public String getUniqueID() {
    return Consts.orbadaLafJTatooPluginId;
  }
  
  /**
   * Funkcja wywoï¿½ywana jest zaraz po zaï¿½adowaniu wtyczki.
   * ManOra jest juï¿½ utworzona, konfiguracja programu zaï¿½adowana
   */
  @Override
  public void load() {
  }
  
  /**
   * Funkcja wywoï¿½ywana jest zaraz przed zamkniï¿½ciem programu
   */
  @Override
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
  @Override
  public void initialize() {
    classList.add(JTatooAcrylLookAndFeelService.class);
    classList.add(JTatooAeroLookAndFeelService.class);
    classList.add(JTatooAluminiumLookAndFeelService.class);
    classList.add(JTatooBernsteinLookAndFeelService.class);
    classList.add(JTatooFastLookAndFeelService.class);
    classList.add(JTatooGraphiteLookAndFeelService.class);
    classList.add(JTatooHiFiLookAndFeelService.class);
    classList.add(JTatooLunaLookAndFeelService.class);
    classList.add(JTatooMcWinLookAndFeelService.class);
    classList.add(JTatooMintLookAndFeelService.class);
    classList.add(JTatooNoireLookAndFeelService.class);
    classList.add(JTatooSmartLookAndFeelService.class);
    classList.add(JTatooGlobalOptionsLookAndFeelSettingsService.class);
  }
  
  /**
   * <p>Funkcja powinna sprawdziï¿½ listï¿½ potrzebnych innych wtyczek
   * return informacje czy moï¿½na warunki sï¿½ speï¿½nione i czy moï¿½na uï¿½ywaï¿½ tej wtyczki
   * <p>Wywoï¿½ane przed initialize()
   * @param loadedPlugins
   * @return
   */
  @Override
  public boolean requires(List<IPlugin> loadedPlugins) {
    return true;
  }
  
  /**
   * <p>Wywoï¿½ywane jest po initialize()
   */
  @Override
  public Class<IPluginProvider>[] getProviderArray() {
    return classList.toArray(new Class[classList.size()]);
  }

  @Override
  public void processMessage(PluginMessage message) {
  }
  
}
