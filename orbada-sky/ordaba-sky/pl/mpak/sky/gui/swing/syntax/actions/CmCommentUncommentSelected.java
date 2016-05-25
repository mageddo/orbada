package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.syntax.CommentSpec;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.array.StringList;

public class CmCommentUncommentSelected extends CmTextArea {
  private static final long serialVersionUID = 5553115113824850880L;

  public CmCommentUncommentSelected(JTextArea textArea) {
    super(textArea, Messages.getString("CmCommentUncommentSelected.text"), KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, KeyEvent.CTRL_MASK)); //$NON-NLS-1$
    setEnabled(textArea instanceof SyntaxEditor);
    setActionCommandKey("cmCommentUncomment"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SyntaxEditor editor = (SyntaxEditor)textArea;
        CommentSpec lineComment = CommentSpec.getLineComment(editor.getComments());
        CommentSpec blockComment = CommentSpec.getBlockComment(editor.getComments());
        if (lineComment == null || blockComment == null || !editor.isEditable()) {
          return;
        }
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        String text = textArea.getSelectedText();
        if (text != null) {
          try {
            if (start > textArea.getLineStartOffset(textArea.getLineOfOffset(start)) ||
                end < textArea.getLineEndOffset(textArea.getLineOfOffset(end -1))) {
              if (text.length() >= 4 && text.startsWith(blockComment.getCommentBegin()) && text.endsWith(blockComment.getCommentEnd())) {
                textArea.replaceSelection(text.substring(blockComment.getCommentBegin().length(), text.length() -blockComment.getCommentEnd().length()));
                textArea.setSelectionStart(start);
                textArea.setSelectionEnd(end -blockComment.getCommentBegin().length() -blockComment.getCommentEnd().length());
              }
              else {
                textArea.replaceSelection(blockComment.getCommentBegin() +text +blockComment.getCommentEnd());
                textArea.setSelectionStart(start);
                textArea.setSelectionEnd(end +blockComment.getCommentBegin().length() +blockComment.getCommentEnd().length());
              }
              return;
            }
          } catch (BadLocationException e1) {
          }
          if (text.length() > 0 && text.charAt(text.length() -"\n".length()) == '\n') { //$NON-NLS-1$
            textArea.setSelectionEnd(textArea.getSelectionEnd() -"\n".length()); //$NON-NLS-1$
            text = textArea.getSelectedText();
          }
          StringList sl = new StringList(false);
          sl.setText(text);
          for (int i=0; i<sl.size(); i++) {
            String line = sl.get(i);
            boolean uncomment = false;
            if (line.length() > 0) {
              int c = 0;
              while (c<line.length() && line.charAt(c) == ' ') {
                c++;
              }
              if (c<=line.length() -lineComment.getCommentBegin().length() && lineComment.getCommentBegin().equals(line.substring(c, c +lineComment.getCommentBegin().length()))) {
                line = line.substring(0, c) +line.substring(c +lineComment.getCommentBegin().length());
                uncomment = true;
              }
            }
            if (!uncomment) {
              line = lineComment.getCommentBegin() +line;
            }
            sl.set(i, line);
          }
          text = sl.getText();
          textArea.replaceSelection(text);
          textArea.setSelectionStart(start);
          textArea.setSelectionEnd(start +text.length() +1);
        }
      }
    };
  }
  
}
