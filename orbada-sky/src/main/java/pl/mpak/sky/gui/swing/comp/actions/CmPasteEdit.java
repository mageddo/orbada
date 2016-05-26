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

public class CmPasteEdit extends Action {
  private static final long serialVersionUID = 1L;

  private static ImageIcon icon = null;
  
  private JTextComponent textComponent;
  
  public CmPasteEdit(JTextComponent textComponent) {
    super();
    this.textComponent = textComponent;
    setText(Messages.getString("CmPasteEdit.text")); //$NON-NLS-1$
    if (icon == null) {
      icon = pl.mpak.sky.gui.swing.ImageManager.getImage("/res/paste.gif"); //$NON-NLS-1$
    }
    setSmallIcon(icon);
    setShortCut(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
    setActionCommandKey(DefaultEditorKit.pasteAction);
    addActionListener(createActionListener());
    this.textComponent.addCaretListener(createCaretListener());
  } 

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textComponent.paste();
      }
    };
  }

  private CaretListener createCaretListener() {
    return new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        setEnabled(textComponent.isEditable() && textComponent.isEnabled());
      }
    };
  }

}
