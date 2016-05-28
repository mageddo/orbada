/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.nimrod.starters;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.sf.nimrod.NimRODLookAndFeel;
import net.sf.nimrod.NimRODTheme;
import pl.mpak.orbada.laf.nimrod.OrbadaLafNimRODPlugin;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class NimRODLookAndFeelStarter implements ILookAndFeelStarter {

  protected IApplication application;
  protected Properties properties;

  public static final String nimRODConfigFileName = "laf-nimrod.properties";
  public static final String nimRODThemeUrl = "/pl/mpak/orbada/laf/nimrod/res/";

  public static final String set_CurrentTheme = "current-theme";

  @Override
  public void setApplication(IApplication application) {
    this.application = application;
    properties = new Properties();
    File file = new File(application.getConfigPath() + "/" +nimRODConfigFileName);
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
    NimRODLookAndFeel laf = new NimRODLookAndFeel();
    String theme = properties.getProperty(set_CurrentTheme);
    if (!StringUtil.isEmpty(theme)) {
      NimRODTheme nt = new NimRODTheme(OrbadaLafNimRODPlugin.class.getResource(nimRODThemeUrl +theme +".theme"));
      NimRODLookAndFeel.setCurrentTheme(nt);
    }

    UIManager.setLookAndFeel(laf);
  }

}
