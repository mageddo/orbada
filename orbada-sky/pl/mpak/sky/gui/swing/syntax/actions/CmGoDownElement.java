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

public class CmGoDownElement extends CmTextArea {
  private static final long serialVersionUID = -1L;

  public CmGoDownElement(SyntaxEditor textArea) {
    super(textArea, Messages.getString("CmGoDownElement.text"), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK)); //$NON-NLS-1$
    setActionCommandKey("cmGoDownElement"); //$NON-NLS-1$
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
            if (item == null && offset < block.getStartOffset() && block.getCallableList().size() > 0) {
              item = block.getCallableList().get(0);
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
                  if (offset < call.getBeginBlockOffset()) {
                    editor.setCaretPosition(call.getBeginBlockOffset(), true);
                  }
                  else if (offset < call.getEndOffset()) {
                    editor.setCaretPosition(call.getEndOffset(), true);
                  }
                  else if (call.getIndex() < block.getCallableList().size() -1) {
                    call = block.getCallableList().get(call.getIndex() +1);
                    editor.setCaretPosition(call.getStartOffset(), true);
                  }
                }
              }
            }
          }
        }
        else {
          String text = editor.getText();
          int offset = editor.getCaretPosition();
          if (offset < text.length() && text.charAt(offset) == '\n') {
            offset++;
          }
          while (offset < text.length()) {
            if (text.charAt(offset) == '\n' && offset +1 < text.length() && (text.charAt(offset +1) == '\n' || text.charAt(offset +1) == '/')) {
              while (offset < text.length() && (text.charAt(offset) == '\n' || text.charAt(offset) == '/')) {
                offset++;
              }
              editor.setCaretPosition(offset, true);
              break;
            }
            offset++;
          }
          if (offset >= text.length() -1) {
            editor.setCaretPosition(text.length(), true);
          }
        }
      }
    };
  }
  
}
