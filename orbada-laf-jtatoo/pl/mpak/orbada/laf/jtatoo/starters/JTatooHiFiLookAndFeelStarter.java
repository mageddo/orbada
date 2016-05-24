/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.laf.jtatoo.starters;

import javax.swing.UnsupportedLookAndFeelException;
import pl.mpak.orbada.laf.jtatoo.services.JTatooHiFiLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.starters.abs.JTatooLookAndFeelStarter;

/**
 *
 * @author akaluza
 */
public class JTatooHiFiLookAndFeelStarter extends JTatooLookAndFeelStarter {
  
  @Override
  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    applySettings("com.jtattoo.plaf.hifi.HiFiLookAndFeel", JTatooHiFiLookAndFeelService.lookAndFeelId);
  }

}
