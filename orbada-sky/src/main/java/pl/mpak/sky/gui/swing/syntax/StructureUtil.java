package pl.mpak.sky.gui.swing.syntax;

import javax.swing.text.BadLocationException;

import pl.mpak.sky.gui.swing.syntax.structure.CallableElement;
import pl.mpak.sky.gui.swing.syntax.structure.CaseBlock;
import pl.mpak.sky.gui.swing.syntax.structure.CaseWhen;
import pl.mpak.sky.gui.swing.syntax.structure.CatchException;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.DeclaredElement;
import pl.mpak.sky.gui.swing.syntax.structure.If;
import pl.mpak.util.StringUtil;

public class StructureUtil {
  
  public static String getText(CodeElement item) {
    if (item instanceof CallableElement) {
      CallableElement block = (CallableElement)item;
      StringBuilder sb = new StringBuilder();
      for (DeclaredElement e : block.getParameterList()) {
        if (e.getName() != null) {
          if (sb.length() > 0) {
            sb.append(", ");
          }
          sb.append(e.getName());
        }
      }
      return 
        "<html><b>" +block.getName() +"</b>" +
        (sb.length() > 0 ? ("(" +sb.toString() +")") : "") +
        " <span style=\"color:gray;\">" +item.getCodeName() +"</span>";
    }
    else if (item instanceof DeclaredElement) {
      String name = "<html>";
      if (((DeclaredElement)item).getName() != null) {
        name = name +"<b>" +((DeclaredElement)item).getName() +"</b>";
      }
      if (((DeclaredElement)item).getType() != null) {
        name = name +" <span style=\"color:#FF1010;\">" +((DeclaredElement)item).getType() +"</span>";
      }
      name = name +" <span style=\"color:gray;\">" +item.getCodeName() +"</span>";
      return name;
    }
    return item.getCodeName();
  }
  
  private static String getTextElement(CodeElement item, SyntaxEditor editor) {
    String text;
    try {
      text = editor.getText(item.getStartOffset(), item.getEndOffset() -item.getStartOffset());
      text = StringUtil.replaceString(text, "\n", " ");
      text = text.substring(0, Math.min(150, text.length() -1));
      return text;
    } catch (BadLocationException e1) {
    }
    return "";
  }
  
  public static String getTooltip(CodeElement item, SyntaxEditor editor) {
    if (item instanceof CallableElement) {
      return item.getCodeName() +" " +((CallableElement)item).getName();
    }
    else if (item instanceof DeclaredElement) {
      String type = ((DeclaredElement)item).getType();
      return item.getCodeName() +" " +((DeclaredElement)item).getName() +(type != null ? " " +type : "");
    }
    else if (item instanceof If) {
      if (((If)item).getCondition() != null) {
        return item.getCodeName() +" " +getTextElement(((If)item).getCondition(), editor);
      }
    }
    else if (item instanceof CatchException) {
      if (((CatchException)item).getExpression() != null) {
        return item.getCodeName() +" " +getTextElement(((CatchException)item).getExpression(), editor);
      }
    }
    else if (item instanceof CaseWhen) {
      if (((CaseWhen)item).getExpression() != null) {
        return item.getCodeName() +" " +getTextElement(((CaseWhen)item).getExpression(), editor);
      }
    }
    else if (item instanceof CaseBlock) {
      if (((CaseBlock)item).getExpression() != null) {
        return item.getCodeName() +" " +getTextElement(((CaseBlock)item).getExpression(), editor);
      }
    }
    return item.getCodeName();
  }
  
}
