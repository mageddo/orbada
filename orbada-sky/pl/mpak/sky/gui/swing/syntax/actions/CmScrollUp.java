package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;

public class CmScrollUp extends CmTextArea {
  private static final long serialVersionUID = -176545153421794106L;

  public CmScrollUp(JTextArea textArea) {
    super(textArea, Messages.getString("CmScrollUp.text"), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK)); //$NON-NLS-1$
    setActionCommandKey("CmScrollUp"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JScrollPane scroll = (JScrollPane)SwingUtil.getOwnerComponent(JScrollPane.class, textArea);
        if (scroll != null) {
          JScrollBar vBar = scroll.getVerticalScrollBar();
          vBar.setValue(vBar.getValue() + vBar.getUnitIncrement(-1));
        }
      }
    };
  }
  
}
