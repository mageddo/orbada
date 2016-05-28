/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.tinylaf.starters;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.ThemeDescription;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class TinyLookAndFeelStarter implements ILookAndFeelStarter {

  protected IApplication application;
  protected Properties properties;

  public static final String tinyLaFConfigFileName = "laf-tinylaf.properties";

  public static final String set_CurrentTheme = "current-theme";

  @Override
  public void setApplication(IApplication application) {
    this.application = application;
    properties = new Properties();
    File file = new File(application.getConfigPath() + "/" +tinyLaFConfigFileName);
    if (file.exists()) {
      try {
        properties.load(new FileInputStream(file));
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  @Override
  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    Toolkit.getDefaultToolkit().setDynamicLayout(true);
    System.setProperty("sun.awt.noerasebackground", "true");
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);

    String currentTheme = properties.getProperty(set_CurrentTheme);
    ThemeDescription[] themes = Theme.getAvailableThemes();
    if (themes != null) {
      for (ThemeDescription theme : themes) {
        if (theme.getName().equals(currentTheme)) {
          Theme.loadTheme(theme);
          break;
        }
      }
    }

    UIManager.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
  }

}
