/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MovePerspectiveRightAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public MovePerspectiveRightAction() {
    super();
    setText(stringManager.getString("MovePerspectiveRightAction-text"));
    setActionCommandKey("MovePerspectiveRightAction");
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/right10.gif"));
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Application.get().getMainFrame().moveTabRight();
      }
    };
  }
  
}
