/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jaybird.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.jaybird.gui.BackupDatabaseDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class BackupDatabaseAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("jaybird");

  public BackupDatabaseAction() {
    setText(stringManager.getString("BackupDatabaseAction.text"));
    setTooltip(stringManager.getString("BackupDatabaseAction.tooltip"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/backup_database.gif", this.getClass()));
    setActionCommandKey("BackupDatabaseAction");
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        performeAction(e);
      }
    });
  }

  private void performeAction(ActionEvent e) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        BackupDatabaseDialog.showDialog();
      }
    });
  }

}
