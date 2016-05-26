/*
 * CreateViewAction.java
 *
 * Created on 2007-10-18, 21:49:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.gui.cm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import orbada.Consts;
import orbada.gui.PerspectivePanel;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateViewAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private PerspectivePanel panel;
  private ViewProvider view;
  
  public CreateViewAction(ViewProvider view, PerspectivePanel panel) {
    super();
    this.view = view;
    this.panel = panel;
    try {
      setSmallIcon(view.getIcon());
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
    setText(view.getPublicName());
    setTooltip(view.getDescription());
    setActionCommandKey("cmCreateView");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Component component = panel.getViewComponent(view);
        if (component != null) {
          switch (MessageBox.show(panel, stringManager.getString("view"), stringManager.getString("CreateViewAction-select-open-view"), ModalResult.YESNOCANCEL, MessageBox.QUESTION)) {
            case ModalResult.YES: {
              panel.createView(view, false, false);
              break;
            }
            case ModalResult.NO: {
              panel.setSelectedView(component);
              break;
            }
          }
        }
        else {
          panel.createView(view, false, false);
        }
      }
    };
  }
  
}
