/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.beanshell.services.cm;

import bsh.EvalError;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.beanshell.BeanShellInterpreter;
import pl.mpak.orbada.beanshell.OrbadaBeanshellPlugin;
import pl.mpak.orbada.beanshell.db.BshActionRecord;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class BshComponentAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("beanshell");

  private BshActionRecord action;
  
  public BshComponentAction(BshActionRecord action) {
    super(action.getTitle());
    this.action = action;
    if (!StringUtil.isEmpty(action.getTooltip())) {
      setTooltip(action.getTooltip());
    }
    int code = (action.getShortcutCode() != null ? action.getShortcutCode() : 0);
    int modf = (action.getShortcutModifiers() != null ? action.getShortcutModifiers() : 0);
    if (code > 0 || modf > 0) {
      setShortCut(code, modf);
    }
    setActionCommandKey("cmBshComponent");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        BeanShellInterpreter interpreter = new BeanShellInterpreter(database);
        try {
          interpreter.set("component", getComponent());
          interpreter.eval(action.getScript());
        } catch (EvalError ex) {
          MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
        }
      }
    };
  }

}
