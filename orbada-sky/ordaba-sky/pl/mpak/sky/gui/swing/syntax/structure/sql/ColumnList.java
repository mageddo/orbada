package pl.mpak.sky.gui.swing.syntax.structure.sql;

import java.util.Collection;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class ColumnList extends CodeElementList<CodeElement> {

  public ColumnList(CodeElement owner) {
    super(owner, "Column list");
  }

  public ColumnList(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public ColumnList(CodeElement owner, String codeName, Collection<? extends CodeElement> c) {
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
    StringBuilder sbe = new StringBuilder();
    for (CodeElement e : this) {
      String column = e.toSource(level);
      if (sb.length() != 0) {
        sb.append(",");
        sbe.append(column);
        if (sbe.length() > 80 || column.length() > 40) {
          sb.append("\n       ");
          sbe.setLength(0);
        }
        else {
          sb.append(" ");
        }
      }
      sb.append(column);
    }
    return sb.toString();
  }
  
}
