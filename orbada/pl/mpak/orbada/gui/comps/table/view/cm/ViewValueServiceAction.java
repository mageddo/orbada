/*
 * ViewValueAction.java
 *
 * Created on 2007-11-26, 18:22:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.view.cm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.gui.comps.ViewValueDialog;
import pl.mpak.orbada.plugins.providers.ViewValueProvider;
import pl.mpak.sky.gui.swing.Action;

/**
 *
 * @author akaluza
 */
public class ViewValueServiceAction extends Action {
  
  private ViewValueProvider provider;
  private Object value;
  private ViewValueDialog dialog;

  public ViewValueServiceAction(ViewValueDialog dialog, ViewValueProvider provider, Object value, int vk) {
    this.provider = provider;
    this.value = value;
    this.dialog = dialog;
    setText(provider.getDescription());
    setShortCut(vk, KeyEvent.ALT_MASK);
    setActionCommandKey("cmViewValueService");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Component component = provider.createComponent(value);
        if (component != null) {
          dialog.setCurrentPanel(component);
        }
      }
    };
  }
  
}
