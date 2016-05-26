/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pl.mpak.orbada.core.Application;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MovePerspectiveLeftAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  public MovePerspectiveLeftAction() {
    super();
    setText(stringManager.getString("MovePerspectiveLeftAction-text"));
    setActionCommandKey("MovePerspectiveLeftAction");
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/left10.gif"));
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Application.get().getMainFrame().moveTabLeft();
      }
    };
  }
  
}
