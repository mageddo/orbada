/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.substance.starters.abs;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JFrame;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public abstract class SubstanceLookAndFeelStarter implements ILookAndFeelStarter {

  protected IApplication application;
  protected Properties properties;

  public static final String configFileName = "laf-substance.properties";

  public static final String set_CurrentTheme = "current-theme";

  @Override
  public void setApplication(IApplication application) {
    this.application = application;
    properties = new Properties();
    File file = new File(application.getConfigPath() + "/" +configFileName);
    if (file.exists()) {
      try {
        properties.load(new FileInputStream(file));
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  protected void applySettings() {
    Toolkit.getDefaultToolkit().setDynamicLayout(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);
  }

}
