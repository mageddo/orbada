package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.StructureElementSelectDialog;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;

public class CmStructureElementSelect extends CmTextArea {
  private static final long serialVersionUID = 5203368814455225289L;

  public CmStructureElementSelect(SyntaxEditor textArea) {
    super(textArea, Messages.getString("CmStructureElementSelect.text"), KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmStructureElementSelect"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CodeElement root = ((SyntaxEditor)textArea).getStructure(true);
        CodeElement item = root.getElementAt(textArea.getCaretPosition());
        item = StructureElementSelectDialog.show(root, item);
        if (item != null) {
          ((SyntaxEditor)textArea).setCaretPosition(item.getStartOffset(), true);
        }
      }
    };
  }
  
}
