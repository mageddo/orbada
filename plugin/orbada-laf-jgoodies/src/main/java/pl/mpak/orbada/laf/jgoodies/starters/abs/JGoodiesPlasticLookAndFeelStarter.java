/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jgoodies.starters.abs;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public abstract class JGoodiesPlasticLookAndFeelStarter extends JGoodiesLookAndFeelStarter {

  protected void applyPlasticOptions() {
    String theme = properties.getProperty(JGoodiesLookAndFeelStarter.set_CurrentTheme, "");
    if (!StringUtil.isEmpty(theme)) {
      try {
        PlasticLookAndFeel.setCurrentTheme((MetalTheme) Class.forName(theme).newInstance());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    PlasticLookAndFeel.setTabStyle(properties.getProperty(JGoodiesLookAndFeelStarter.set_TabStyle, "Metal"));
  }

}
