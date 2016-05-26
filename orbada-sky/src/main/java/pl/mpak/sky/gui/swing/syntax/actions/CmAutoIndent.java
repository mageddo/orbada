package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;

public class CmAutoIndent extends CmTextArea {
  private static final long serialVersionUID = -7829859405804367189L;

  public CmAutoIndent(JTextArea textArea) {
    super(textArea, Messages.getString("CmAutoIndent.text"), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)); //$NON-NLS-1$
    setActionCommandKey("cmAutoIndent"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Document doc = textArea.getDocument();
    
        if (!textArea.isEditable())
          return;
        try {
          int line = textArea.getLineOfOffset(textArea.getCaretPosition());
          int start = textArea.getLineStartOffset(line);
          int end = textArea.getLineEndOffset(line);
          String str = doc.getText(start, end - start);
          String whiteSpace = getLeadingWhiteSpace(str);
          if (doc instanceof SQLSyntaxDocument && textArea instanceof SyntaxEditor) {
            List<TokenRef> tokens = ((SyntaxEditor)textArea).getTokens(start, textArea.getCaretPosition());
            if (tokens != null && tokens.size() > 0) {
              if (tokens.get(tokens.size() -1).ref.anyOfStyle(SQLSyntaxDocument.documentationStyles)) {
                if (!tokens.get(0).ref.anyOfStyle(SQLSyntaxDocument.documentationStyles)) {
                  whiteSpace = whiteSpace +" * ";
                }
                else {
                  whiteSpace = whiteSpace +"* ";
                }
              }
            }
          }
          if (str.trim().length() == 0) {
            doc.remove(start, end -start -1);
            textArea.setCaretPosition(start);
            doc.insertString(textArea.getCaretPosition(), "\n", null); //$NON-NLS-1$
          }
          else {
            doc.insertString(textArea.getCaretPosition(), '\n' + whiteSpace, null);
          }
        } catch (BadLocationException ex) {
          try {
            doc.insertString(textArea.getCaretPosition(), "\n", null); //$NON-NLS-1$
          } catch (BadLocationException ignore) {
            // ignore
          }
        }
      }
    };
  }

  /**
   * Returns leading white space characters in the specified string.
   */
  private String getLeadingWhiteSpace(String str) {
    return str.substring(0, getLeadingWhiteSpaceWidth(str));
  }

  /**
   * Returns the number of leading white space characters in the specified
   * string.
   */
  private int getLeadingWhiteSpaceWidth(String str) {
    int whitespace = 0;
    while (whitespace < str.length()) {
      char ch = str.charAt(whitespace);
      if (ch == ' ' || ch == '\t')
        whitespace++;
      else
        break;
    }
    return whitespace;
  }

}
