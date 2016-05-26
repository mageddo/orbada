/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jaybird.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.jaybird.gui.CreateDatabaseDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateDatabaseAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJaybirdPlugin.class);

  public CreateDatabaseAction() {
    setText(stringManager.getString("CreateDatabaseAction.text"));
    setTooltip(stringManager.getString("CreateDatabaseAction.tooltip"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/jaybird/res/new_database.gif", this.getClass()));
    setActionCommandKey("CreateDatabaseAction");
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        performeAction(e);
      }
    });
  }

  private void performeAction(ActionEvent e) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        CreateDatabaseDialog.showDialog();
      }
    });
  }

}
