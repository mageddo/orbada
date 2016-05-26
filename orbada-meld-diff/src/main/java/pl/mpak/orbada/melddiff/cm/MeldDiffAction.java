/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.melddiff.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.melddiff.OrbadaMeldDiffPlugin;
import pl.mpak.orbada.melddiff.services.DiffViewService;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MeldDiffAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager("meld-diff");

  private IPerspectiveAccesibilities accesibilities;
  
  public MeldDiffAction(IPerspectiveAccesibilities accesibilities) {
    super();
    setText(stringManager.getString("MeldDiffAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/diff.gif"));
    setActionCommandKey("MeldDiffAction");
    addActionListener(createActionListener());
    this.accesibilities = accesibilities;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        accesibilities.createView(new DiffViewService());
      }
    };
  }

}
