/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jaybird.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.jaybird.gui.RestoreDatabaseDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class RestoreDatabaseAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("jaybird");

  public RestoreDatabaseAction() {
    setText(stringManager.getString("RestoreDatabaseAction.text"));
    setTooltip(stringManager.getString("RestoreDatabaseAction.tooltip"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/jaybird/res/restore_database.gif", this.getClass()));
    setActionCommandKey("RestoreDatabaseAction");
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        performeAction(e);
      }
    });
  }

  private void performeAction(ActionEvent e) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        RestoreDatabaseDialog.showDialog();
      }
    });
  }

}
