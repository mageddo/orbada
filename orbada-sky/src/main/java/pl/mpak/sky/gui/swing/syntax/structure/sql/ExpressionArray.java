package pl.mpak.sky.gui.swing.syntax.structure.sql;

import java.util.Collection;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class ExpressionArray extends CodeElementList<CodeElement> {

  public ExpressionArray(CodeElement owner) {
    super(owner, "Array");
  }

  public ExpressionArray(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public ExpressionArray(CodeElement owner, String codeName, Collection<? extends CodeElement> c) {
    super(owner, codeName, c);
  }

  public String toString() {
    return 
        "[" +
        super.toString() +
        "]";
  }
  
  public String toSource(int level) {
    StringBuilder sb = new StringBuilder();
    for (CodeElement e : this) {
      if (sb.length() != 0) {
        sb.append(", ");
      }
      sb.append(e.toSource(level));
    }
    return "[" +sb.toString() +"]";
  }
  
}
