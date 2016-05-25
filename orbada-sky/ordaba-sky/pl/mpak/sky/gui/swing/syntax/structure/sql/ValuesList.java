package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class ValuesList extends CodeElementList<CodeElement> {

  public ValuesList(CodeElement owner) {
    super(owner, "ValuesList");
  }

  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
}
