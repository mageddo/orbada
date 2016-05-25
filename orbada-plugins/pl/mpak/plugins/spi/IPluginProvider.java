package pl.mpak.plugins.spi;

/**
 * <p>Interfejs prowidera ogólnego zastosowania.
 * <p>Klasy tego interfejsu s¹ ³adowane automatycznie podczas wyszukiwania plugin-ów
 * dla ka¿dego z PluginManager-a
 * <p>Niezale¿nie od tego czy Plugin jako taki zostanie za³adowany to ten interfejs
 * znajdzie siê na ogólnej liœcie serwisów PluginManager-a
 * @see PluginManager.getServices
 * @author akaluza
 * 
 */
public interface IPluginProvider {
  
  /**
   * <p>Czy klasa tego interfejsu mo¿e byæ wspó³dzielona. Czy ma powstaæ jedna
   * instancja tego interfejsu
   * @return
   */
  public boolean isSharedProvider();
  
  /**
   * <p>Nazwa grupy dostawcy, w niektórych miejscach bêdzie to potrzebne do zgrupwania
   * np w Menu. W ustawieniach grupy takie równie¿ mog¹ siê pojawiæ.
   * @return
   */
  public String getGroupName();

}
