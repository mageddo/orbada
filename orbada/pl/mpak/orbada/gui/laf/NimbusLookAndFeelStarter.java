/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.laf;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;

/**
 *
 * @author akaluza
 */
public class NimbusLookAndFeelStarter implements ILookAndFeelStarter {

  private IApplication application;

  @Override
  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
  }

  @Override
  public void setApplication(IApplication application) {
    this.application = application;
  }

}
