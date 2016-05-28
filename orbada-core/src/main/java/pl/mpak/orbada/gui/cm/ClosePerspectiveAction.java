/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.core.Application;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ClosePerspectiveAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  public ClosePerspectiveAction() {
    super();
    setText(stringManager.getString("ClosePerspectiveAction-text"));
    setActionCommandKey("cmClosePerspective");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (Application.get().getMainFrame().closePerspective(Application.get().getMainFrame().getActivePerspective())) {
          Application.get().getMainFrame().setActivePerspective(null);
        }
      }
    };
  }
  
}
