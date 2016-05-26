/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.laf.jgoodies.starters;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pl.mpak.orbada.laf.jgoodies.starters.abs.JGoodiesPlasticLookAndFeelStarter;

/**
 *
 * @author akaluza
 */
public class PlasticXPLookAndFeelStarter extends JGoodiesPlasticLookAndFeelStarter {

  @Override
  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    applyOptions();
    applyPlasticOptions();
    UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
  }

}
