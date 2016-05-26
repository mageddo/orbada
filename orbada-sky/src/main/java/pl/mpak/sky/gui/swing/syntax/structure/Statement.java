package pl.mpak.sky.gui.swing.syntax.structure;

import javax.swing.text.BadLocationException;

import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

public class Statement extends CodeElement {

  public Statement(CodeElement owner) {
    super(owner, "Statement");
  }
  
  public Statement(CodeElement owner, String codeName) {
    super(owner, codeName);
  }
  
  public String toPreCode(SyntaxEditor editor, String level) {
    try {
      return level +editor.getText(getStartOffset(), getEndOffset() -getStartOffset()) +"\n";
    } catch (BadLocationException e) {
      e.printStackTrace();
      return "";
    }
  }

}
