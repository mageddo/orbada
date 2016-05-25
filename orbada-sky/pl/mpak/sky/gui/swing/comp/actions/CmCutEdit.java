package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;

public class CmCutEdit extends Action {
  private static final long serialVersionUID = 1L;

  private static ImageIcon icon = null;
  
  private JTextComponent textComponent;
  
  public CmCutEdit(JTextComponent textComponent) {
    super();
    this.textComponent = textComponent;
    setText(Messages.getString("CmCutEdit.text")); //$NON-NLS-1$
    if (icon == null) {
      icon = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/sky/res/cut.gif"); //$NON-NLS-1$
    }
    setSmallIcon(icon);
    setShortCut(KeyEvent.VK_X, KeyEvent.CTRL_MASK);
    setActionCommandKey(DefaultEditorKit.cutAction);
    addActionListener(createActionListener());
    this.textComponent.addCaretListener(createCaretListener());
  } 

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textComponent.cut();
      }
    };
  }
  
  private CaretListener createCaretListener() {
    return new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        setEnabled(textComponent.getSelectionEnd() != textComponent.getSelectionStart());
      }
    };
  }

}
