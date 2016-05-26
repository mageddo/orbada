/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.util.Utils;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class HelpAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public HelpAction() {
    super();
    setText(stringManager.getString("HelpAction-text"));
    setShortCut(KeyEvent.VK_F1, 0);
    setActionCommandKey("cmHelp");
    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        HelpAction.this.actionPerformed();
      }
    });
  }

  private void actionPerformed() {
    Utils.gotoHelp();
  }

}
