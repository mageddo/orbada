/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jgoodies.starters;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pl.mpak.orbada.laf.jgoodies.starters.abs.JGoodiesLookAndFeelStarter;

/**
 *
 * @author akaluza
 */
public class WindowsLookAndFeelStarter extends JGoodiesLookAndFeelStarter {

  @Override
  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    applyOptions();
    UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
  }

}
