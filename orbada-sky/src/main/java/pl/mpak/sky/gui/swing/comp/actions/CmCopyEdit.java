package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import pl.mpak.sky.Messages;
import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.sky.gui.swing.syntax.SyntaxStyle;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;
import pl.mpak.util.HtmlTransfer;
import pl.mpak.util.HtmlUtil;
import pl.mpak.util.StringUtil;

public class CmCopyEdit extends Action {
  private static final long serialVersionUID = 1L;

  private static ImageIcon icon = null;
  
  private JTextComponent textComponent;
  
  public CmCopyEdit(JTextComponent textComponent) {
    super();
    this.textComponent = textComponent;
    setText(Messages.getString("CmCopyEdit.text")); //$NON-NLS-1$
    if (icon == null) {
      icon = pl.mpak.sky.gui.swing.ImageManager.getImage("/res/copy.gif"); //$NON-NLS-1$
    }
    setSmallIcon(icon);
    setShortCut(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
    setActionCommandKey(DefaultEditorKit.copyAction);
    addActionListener(createActionListener());
    this.textComponent.addCaretListener(createCaretListener());
  } 

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (textComponent instanceof SyntaxEditor && SkySetting.getBoolean(SkySetting.CmCopyEdit_AsHtmlToo, true)) {
          SyntaxEditor editor = (SyntaxEditor)textComponent;
          try {
            String copyText = editor.getSelectedText();
            Iterator<TokenRef> i = editor.getTokens(editor.getSelectionStart(), editor.getSelectionEnd()).iterator();
            StringBuilder sb = new StringBuilder();
            //sb.append("<html>");
            sb.append("<code>"); //$NON-NLS-1$
            boolean first = true;
            while (i.hasNext()) {
              TokenRef token = i.next();
              if (first) {
                if (token.offset < editor.getSelectionStart()) {
                  token.token = token.token.substring(editor.getSelectionStart() -token.offset);
                  token.offset += (editor.getSelectionStart() -token.offset);
                }
                first = false;
              }
              if (!i.hasNext()) {
                if (token.offset +token.length > editor.getSelectionEnd()) {
                  token.token = token.token.substring(0, editor.getSelectionEnd() -token.offset);
                }
              }
              token.token = StringUtil.replaceString(token.token, "\n", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
              token.token = StringUtil.replaceString(token.token, "\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
              token.token = StringUtil.replaceString(token.token, " ", "&#32;"); //$NON-NLS-1$ //$NON-NLS-2$
              if (token.styleId == SyntaxDocument.NONE) {
                sb.append(token.token);
              }
              else {
                SyntaxStyle style = ((SyntaxDocument)editor.getDocument()).getStyle(token.styleId);
                sb.append(
                  "<span style=\"" + //$NON-NLS-1$
                  (!style.getForeground().equals(Color.BLACK) ? "color:" +HtmlUtil.htmlColor(style.getForeground()) +";" : "") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                  (style.isBold() ? "font-weight:bold;" : "") + //$NON-NLS-1$ //$NON-NLS-2$
                  (style.isItalic() ? "font-style:italic;" : "") + //$NON-NLS-1$ //$NON-NLS-2$
                  (style.isUnderline() ? "text-decoration:underline;" : "") + //$NON-NLS-1$ //$NON-NLS-2$
                  "\">" + //$NON-NLS-1$
                  token.token +
                  "</span>"); //$NON-NLS-1$
              }
            }
            sb.append("</code>"); //$NON-NLS-1$
            Transferable t = new HtmlTransfer(sb.toString(), copyText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
          } catch (BadLocationException e1) {
            textComponent.copy();
          }
        }
        else {
          textComponent.copy();
        }
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
