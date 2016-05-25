/*
 * Created on 2005-07-13
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pl.mpak.sky.gui.swing.themes;

import java.awt.Font;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

/**
 * @author Administrator
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class DefaultOceanTheme extends OceanTheme {

  private FontUIResource monospacedFont;
  private FontUIResource controlFont;

  public static void setCurrentTheme() {
    MetalLookAndFeel.setCurrentTheme(new DefaultOceanTheme());
  }

  // private static final ColorUIResource primary1 = new ColorUIResource(
  // 102, 102, 153);
  // private static final ColorUIResource primary2 = new ColorUIResource(153,
  // 153, 204);
  // private static final ColorUIResource primary3 = new ColorUIResource(
  // 204, 204, 255);
  // private static final ColorUIResource secondary1 = new ColorUIResource(
  // 102, 102, 102);
  // private static final ColorUIResource secondary2 = new ColorUIResource(
  // 153, 153, 153);
  // private static final ColorUIResource secondary3 = new ColorUIResource(
  // 204, 204, 204);
  //
  public String getName() {
    return "Sky.Ocean";
  }

  public DefaultOceanTheme() {
    super();
    monospacedFont = new FontUIResource("Monospaced", Font.PLAIN, 11);
    controlFont = new FontUIResource("Tahoma",Font.PLAIN, 11);
  }

  // these are blue in Metal Default Theme
  // protected ColorUIResource getPrimary1() { return primary1; }
  // protected ColorUIResource getPrimary2() { return primary2; }
  // protected ColorUIResource getPrimary3() { return primary3; }

  // these are gray in Metal Default Theme
  // protected ColorUIResource getSecondary1() { return secondary1; }
  // protected ColorUIResource getSecondary2() { return secondary2; }
  // protected ColorUIResource getSecondary3() { return secondary3; }

  public FontUIResource getControlTextFont() {
    return getDefaultFont();
  }

  public FontUIResource getSystemTextFont() {
    return getDefaultFont();
  }

  public FontUIResource getUserTextFont() {
    return getDefaultFont();
  }

  public FontUIResource getMenuTextFont() {
    return getDefaultFont();
  }

  public FontUIResource getWindowTitleFont() {
    return getDefaultFont();
  }

  public FontUIResource getSubTextFont() {
    return getDefaultFont();
  }

  private FontUIResource getDefaultFont() {
    return controlFont;
  }

  public void addCustomEntriesToTable(UIDefaults table)
  {
      super.addCustomEntriesToTable(table);
      UIManager.getDefaults().put("PasswordField.font", monospacedFont);
      UIManager.getDefaults().put("TextArea.font", monospacedFont);
      UIManager.getDefaults().put("TextPane.font", monospacedFont);
      UIManager.getDefaults().put("EditorPane.font", monospacedFont);
}

}
