/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.orbada.programmers.gui.DatabaseUserPropertiesDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DatabaseUserPropertiesAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaProgrammersPlugin.class);

  public DatabaseUserPropertiesAction() {
    super();
    setText(stringManager.getString("DatabaseUserPropertiesAction-text"));
    setActionCommandKey("DatabaseUserPropertiesAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DatabaseUserPropertiesDialog.showDialog();
      }
    };
  }
  
}
