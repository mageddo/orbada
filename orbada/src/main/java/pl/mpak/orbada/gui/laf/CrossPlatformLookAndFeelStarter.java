/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui.laf;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class CrossPlatformLookAndFeelStarter implements ILookAndFeelStarter {

  private IApplication application;

  @Override
  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    File propFile = new File(application.getConfigPath() + "/laf-cross-platform.properties");
    if (propFile.exists()) {
      Properties props = new Properties();
      try {
        props.load(new FileInputStream(propFile));
        String theme = props.getProperty("current-theme");
        if (!StringUtil.isEmpty(theme)) {
          MetalLookAndFeel.setCurrentTheme((MetalTheme)Class.forName(theme).newInstance());
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
  }

  @Override
  public void setApplication(IApplication application) {
    this.application = application;
  }

}
