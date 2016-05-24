/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.laf.jtatoo.starters;

import javax.swing.UnsupportedLookAndFeelException;
import pl.mpak.orbada.laf.jtatoo.services.JTatooAluminiumLookAndFeelService;
import pl.mpak.orbada.laf.jtatoo.starters.abs.JTatooLookAndFeelStarter;

/**
 *
 * @author akaluza
 */
public class JTatooAluminiumLookAndFeelStarter extends JTatooLookAndFeelStarter {
  
  @Override
  public void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    applySettings("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel", JTatooAluminiumLookAndFeelService.lookAndFeelId);
  }

}
