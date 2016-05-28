/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.orbada.programmers.gui.ResolversDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ResolversAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager("programmers");

  public ResolversAction() {
    super();
    setText(stringManager.getString("ResolversAction-text"));
    setActionCommandKey("ResolversAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ResolversDialog.showDialog();
      }
    };
  }
  
}
