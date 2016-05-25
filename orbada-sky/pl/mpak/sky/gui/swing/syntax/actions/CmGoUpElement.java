package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CallableElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;

public class CmGoUpElement extends CmTextArea {
  private static final long serialVersionUID = -1L;

  public CmGoUpElement(SyntaxEditor textArea) {
    super(textArea, Messages.getString("CmGoUpElement.text"), KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmGoUpElement"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SyntaxEditor editor = (SyntaxEditor)textArea;
        if (editor.getStructureParser() != null) {
          BlockElement block = editor.getStructure(true);
          if (block != null) {
            int offset = editor.getCaretPosition();
            CodeElement item = block.getElementAt(offset);
            if (item == null && offset > block.getBeginBlockOffset() && block.getCallableList().size() > 0) {
              item = block.getCallableList().get(block.getCallableList().size() -1);
            }
            if (item != null) {
              block = item.getRootBlock();
              if (block != null) {
                CallableElement call = (block instanceof CallableElement ? (CallableElement)block : null);
                for (CallableElement element : block.getCallableList()) {
                  if (element.getElementAt(offset) != null) {
                    call = element;
                  }
                }
                if (call != null) {
                  if (offset > call.getBeginBlockOffset()) {
                    editor.setCaretPosition(call.getBeginBlockOffset(), true);
                  }
                  else if (offset > call.getStartOffset()) {
                    editor.setCaretPosition(call.getStartOffset(), true);
                  }
                  else if (call.getIndex() > 0) {
                    call = block.getCallableList().get(call.getIndex() -1);
                    editor.setCaretPosition(call.getEndOffset(), true);
                  }
                }
              }
            }
          }
        }
        else {
          String text = editor.getText();
          int offset = editor.getCaretPosition();
          if (offset > 0) {
            offset--;
          }
          while (offset > 0 && (text.charAt(offset) == '\n' || text.charAt(offset) == '/')) {
            offset--;
          }
          while (offset > 0) {
            if ((text.charAt(offset) == '\n' || text.charAt(offset) == '/') && offset -1 >= 0 && text.charAt(offset -1) == '\n') {
              if (text.charAt(offset) == '\n' || text.charAt(offset) == '/') {
                editor.setCaretPosition(offset +1, true);
              }
              break;
            }
            offset--;
          }
          if (offset == 0) {
            editor.setCaretPosition(0, true);
          }
        }
      }
    };
  }
  
}
