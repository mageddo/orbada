package pl.mpak.orbada.tray;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.tray.gui.TraySettingsPanel;
import pl.mpak.orbada.tray.services.TraySettingsProvider;
import pl.mpak.util.id.VersionID;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.SystemUtil;

/**
 *
 * @author akaluza
 */
public class OrbadaTrayPlugin extends OrbadaPlugin {

  private final StringManager stringManager = StringManagerFactory.getStringManager("tray");

  public final static String pluginGroupName = "Orbada Tools";
  public final ArrayList<Class<? extends IPluginProvider>> classList = new ArrayList<Class<? extends IPluginProvider>>();

  private ISettings settings;
  private TrayIcon trayIcon;
  private ImageIcon mainIcon;
  private JPopupMenu menuTray;
  private boolean changingStat = false;

  public OrbadaTrayPlugin() {
    if (SystemUtil.isLinux()) {
      mainIcon = new javax.swing.ImageIcon(getClass().getResource("/res/icons/orbada24.png"));
    }
    else {
      mainIcon = new javax.swing.ImageIcon(getClass().getResource("/res/icons/orbada16.png"));
    }
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê wewnêtrzn¹ wtyczki
   * @return
   */
  public String getInternalName() {
    return "OrbadaTrayPlugin";
  }
  
  /**
   * Funkcja powinna zwracaæ nazwê opisow¹ wtyczki
   * @return
   */
  public String getDescriptiveName() {
    return String.format(stringManager.getString("OrbadaTrayPlugin-descriptive-name"), new Object[] {getVersion()});
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
    return "Tools";
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
    return new VersionID(1, 0, 1, 5).toString();
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
    return "orbada-tray-plugin";
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

  private void deiconified() {
    if (settings.getValue(TraySettingsPanel.setTrayOn, false) && !changingStat) {
      changingStat = true;
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.remove(trayIcon);
            Application.get().getMainFrame().setState(JFrame.NORMAL);
            Application.get().getMainFrame().setVisible(true);
            Application.get().getMainFrame().toFront();
          }
          finally {
            changingStat = false;
          }
        }
      });
    }
  }

  private void iconified() {
    if (settings.getValue(TraySettingsPanel.setTrayOn, false) && !changingStat) {
      changingStat = true;
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          SystemTray tray = SystemTray.getSystemTray();
          try {
            Application.get().getMainFrame().setVisible(false);
            tray.add(trayIcon);
          } catch (AWTException ex) {
            ExceptionUtil.processException(ex);
          }
          finally {
            changingStat = false;
          }
        }
      });
    }
  }

  private void initTray() {
    menuTray = new JPopupMenu();
    JMenuItem item = menuTray.add(new JMenuItem("Przywróæ"));
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deiconified();
      }
    });
    item.setFont(Application.get().getMainFrame().getFont().deriveFont(Font.BOLD));

    trayIcon = new TrayIcon(mainIcon.getImage(), Application.get().getMainFrame().getTitle(), null);
    trayIcon.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) {
          deiconified();
        }
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          menuTray.setLocation(e.getX(), e.getY());
          menuTray.setInvoker(menuTray);
          menuTray.setVisible(true);
        }
      }
    });

    Application.get().getMainFrame().addWindowListener(new WindowListener() {
      public void windowOpened(WindowEvent e) {
      }
      public void windowClosing(WindowEvent e) {
      }
      public void windowClosed(WindowEvent e) {
      }
      public void windowIconified(WindowEvent e) {
        iconified();
      }
      public void windowDeiconified(WindowEvent e) {
      }
      public void windowActivated(WindowEvent e) {
      }
      public void windowDeactivated(WindowEvent e) {
      }
    });
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
    settings = application.getSettings(TraySettingsPanel.settingsName);

    if (SystemTray.isSupported()) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          initTray();
        }
      });
    }
  }
  
  /**
   * <p>Funkcja powinna sprawdziæ listê potrzebnych innych wtyczek
   * return informacje czy mo¿na warunki s¹ spe³nione i czy mo¿na u¿ywaæ tej wtyczki
   * @param loadedPlugins 
   * @return 
   */
  public boolean requires(List<IPlugin> loadedPlugins) {
    addDepend(Consts.orbadaUniversalPluginId);
    for (IPlugin plugin : loadedPlugins) {
      if (Consts.orbadaUniversalPluginId.equals(plugin.getUniqueID())) {
        classList.add(TraySettingsProvider.class);
      }
    }
    return true;
  }

  public Class<IPluginProvider>[] getProviderArray() {
    return classList.toArray(new Class[classList.size()]);
  }

  @Override
  public void processMessage(PluginMessage message) {
  }

}
