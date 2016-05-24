/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.laf.jtatoo.starters.abs;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ClassUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public abstract class JTatooLookAndFeelStarter implements ILookAndFeelStarter {
  
  protected IApplication application;
  protected Properties properties;

  public static final String configFileName = "laf-jtatoo.properties";

  public static final String set_CurrentTheme = "-current-theme";
  public static final String set_LicenseKey = "license-key";
  public static final String set_CompanyName = "company-name";

  protected String licenseKey;
  protected String companyName;
  protected String themeName;
  
  @Override
  public void setApplication(IApplication application) {
    this.application = application;
    properties = new Properties();
    File file = new File(application.getConfigPath() + "/" +configFileName);
    if (file.exists()) {
      try {
        properties.load(new FileInputStream(file));
        licenseKey = properties.getProperty(set_LicenseKey);
        companyName = properties.getProperty(set_CompanyName);
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  protected void applySettings(String className, String lafId) {
    Toolkit.getDefaultToolkit().setDynamicLayout(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);
    
    themeName = properties.getProperty(lafId +set_CurrentTheme);
    final Class<?> clazz;
    try {
      clazz = this.getClass().getClassLoader().loadClass(className);
    } catch (ClassNotFoundException ex) {
      ExceptionUtil.processException(ex);
      return;
    }
    final Method m = ClassUtil.getStaticMethod(clazz, "setTheme", new Class<?>[] {String.class});
    final Method mex = ClassUtil.getStaticMethod(clazz, "setTheme", new Class<?>[] {String.class, String.class, String.class});
    if (m != null && mex != null) {
      SwingUtil.invokeAndWait(new Callable() {
        @Override
        public Object call() throws Exception {
          if (!StringUtil.isEmpty(themeName)) {
            if (!StringUtil.isEmpty(companyName)) {
              mex.invoke(null, new Object[] {themeName, licenseKey, companyName});
            }
            else {
              m.invoke(null, new Object[] {themeName});
            }
          }
          UIManager.setLookAndFeel((LookAndFeel)clazz.newInstance());
          return null;
        }
      });
    }
  }

}
