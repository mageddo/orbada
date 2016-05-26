/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jgoodies.starters.abs;

import com.jgoodies.looks.Options;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public abstract class JGoodiesLookAndFeelStarter implements ILookAndFeelStarter {

  public static final String jgoodiesConfigFileName = "res/laf-jgoodies.properties";
  
  public static final String set_PopupDropShadowEnabled = "popup-drop-shadow-enabled";
  public static final String set_TabIconsEnabled = "tab-icons-enabled";
  public static final String set_UseNarrowButtons = "use-narrow-buttons";
  public static final String set_UseSystemFonts = "use-system-fonts";

  public static final String set_CurrentTheme = "current-theme";
  public static final String set_TabStyle = "tab-style";

  protected IApplication application;
  protected Properties properties;

  @Override
  public void setApplication(IApplication application) {
    this.application = application;
    properties = new Properties();
    File file = new File(application.getConfigPath() + "/" +JGoodiesLookAndFeelStarter.jgoodiesConfigFileName);
    if (file.exists()) {
      try {
        properties.load(new FileInputStream(file));
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  protected void applyOptions() {
    Options.setPopupDropShadowEnabled(StringUtil.toBoolean(properties.getProperty(set_PopupDropShadowEnabled, "true")));
    Options.setTabIconsEnabled(StringUtil.toBoolean(properties.getProperty(set_TabIconsEnabled, "true")));
    Options.setUseNarrowButtons(StringUtil.toBoolean(properties.getProperty(set_UseNarrowButtons, "true")));
    Options.setUseSystemFonts(StringUtil.toBoolean(properties.getProperty(set_UseSystemFonts, "true")));
  }

}
