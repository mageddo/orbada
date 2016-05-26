/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.melddiff.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import pl.mpak.orbada.gui.PerspectivePanel;
import pl.mpak.orbada.melddiff.OrbadaMeldDiffPlugin;
import pl.mpak.orbada.melddiff.services.MeldSyntaxService;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class RightContentAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager("meld-diff");

  private SyntaxTextArea syntaxTextArea;
  
  public RightContentAction(SyntaxTextArea syntaxTextArea) {
    super();
    setText(stringManager.getString("RightContentAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/diff.gif"));
    setActionCommandKey("RightContentAction");
    addActionListener(createActionListener());
    this.syntaxTextArea = syntaxTextArea;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if ((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
          MeldSyntaxService.setRightContent(syntaxTextArea.getCurrentText(), (PerspectivePanel)getParent(e, PerspectivePanel.class));
        }
        else {
          MeldSyntaxService.setRightContent(syntaxTextArea.getText(), (PerspectivePanel)getParent(e, PerspectivePanel.class));
        }
      }
    };
  }

}
