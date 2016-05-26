/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import orbada.Consts;
import orbada.core.Application;
import orbada.gui.PerspectivePropertiesDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PerspectivePropertiesAction extends Action {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public PerspectivePropertiesAction() {
    super();
    setText(stringManager.getString("mf-cmPerspectiveProperties-text"));
    setActionCommandKey("cmPerspectiveProperties");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        PerspectivePropertiesDialog.showDialog(Application.get().getMainFrame().getActivePerspective());
      }
    };
  }
  
}
