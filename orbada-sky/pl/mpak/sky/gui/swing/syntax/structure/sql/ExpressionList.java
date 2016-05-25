package pl.mpak.sky.gui.swing.syntax.structure.sql;

import java.util.Collection;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class ExpressionList extends CodeElementList<CodeElement> {

  public ExpressionList(CodeElement owner) {
    super(owner, "List");
  }

  public ExpressionList(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public ExpressionList(CodeElement owner, String codeName, Collection<? extends CodeElement> c) {
    super(owner, codeName, c);
  }

  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
  public String toSource(int level) {
    StringBuilder sb = new StringBuilder();
    for (CodeElement e : this) {
      if (sb.length() != 0) {
        sb.append(", ");
      }
      sb.append(e.toSource(level));
    }
    return "(" +sb.toString() +")";
  }
  
}
