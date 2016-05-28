/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jaybird.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.jaybird.gui.DatabaseUsersDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DatabaseUsersAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("jaybird");

  public DatabaseUsersAction() {
    setText(stringManager.getString("DatabaseUsersAction.text"));
    setTooltip(stringManager.getString("DatabaseUsersAction.tooltip"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/users16.gif"));
    setActionCommandKey("DatabaseUsersAction");
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        performeAction(e);
      }
    });
  }

  private void performeAction(ActionEvent e) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        DatabaseUsersDialog.showDialog();
      }
    });
  }

}
