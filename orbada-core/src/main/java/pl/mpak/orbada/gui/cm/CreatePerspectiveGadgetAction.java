/*
 * CreatePerspectiveGadgetAction.java
 *
 * Created on 2007-10-18, 21:49:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.PerspectivePanel;
import pl.mpak.orbada.gui.gadgets.GadgetPanel;
import pl.mpak.orbada.gui.PerspectivePanel;
import pl.mpak.orbada.plugins.providers.PerpectiveGadgetProvider;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class CreatePerspectiveGadgetAction extends Action {
  
  private PerspectivePanel panel;
  private PerpectiveGadgetProvider provider;
  
  public CreatePerspectiveGadgetAction(PerpectiveGadgetProvider provider, PerspectivePanel panel) {
    super();
    this.provider = provider;
    this.panel = panel;
    try {
      setSmallIcon(provider.getIcon());
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
    setText(provider.getPublicName());
    setTooltip(provider.getDescription());
    setActionCommandKey("cmCreatePerspectiveGadget");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GadgetPanel gp = panel.createGadget(provider, 0);
        gp.requestFocusInWindow();
      }

    };
  }
  
}
