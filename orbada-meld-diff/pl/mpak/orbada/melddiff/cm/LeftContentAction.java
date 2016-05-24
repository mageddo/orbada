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
public class LeftContentAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMeldDiffPlugin.class);

  private SyntaxTextArea syntaxTextArea;
  
  public LeftContentAction(SyntaxTextArea syntaxTextArea) {
    super();
    setText(stringManager.getString("LeftContentAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/diff.gif"));
    setActionCommandKey("LeftContentAction");
    addActionListener(createActionListener());
    this.syntaxTextArea = syntaxTextArea;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if ((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
          MeldSyntaxService.setLeftContent(syntaxTextArea.getCurrentText(), (PerspectivePanel)getParent(e, PerspectivePanel.class));
        }
        else {
          MeldSyntaxService.setLeftContent(syntaxTextArea.getText(), (PerspectivePanel)getParent(e, PerspectivePanel.class));
        }
      }
    };
  }

}
