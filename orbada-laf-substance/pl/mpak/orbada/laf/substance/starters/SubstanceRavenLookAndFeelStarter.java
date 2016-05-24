/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.substance.starters;

import java.util.concurrent.Callable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pl.mpak.orbada.laf.substance.starters.abs.SubstanceLookAndFeelStarter;
import pl.mpak.sky.gui.swing.SwingUtil;

/**
 *
 * @author akaluza
 */
public class SubstanceRavenLookAndFeelStarter extends SubstanceLookAndFeelStarter {

  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    applySettings();
    SwingUtil.invokeAndWait(new Callable() {
      public Object call() throws Exception {
        UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel");
        return null;
      }
    });
  }

}
