package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class GroupBy extends CodeElementList<CodeElement> {

  public GroupBy(CodeElement owner) {
    super(owner, "Group by");
  }

  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
}
