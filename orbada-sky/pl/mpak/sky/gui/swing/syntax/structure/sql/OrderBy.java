package pl.mpak.sky.gui.swing.syntax.structure.sql;

import java.util.Collection;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class OrderBy extends CodeElementList<CodeElement> {

  public OrderBy(CodeElement owner) {
    super(owner, "Order by");
  }

  public OrderBy(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public OrderBy(CodeElement owner, String codeName, Collection<? extends CodeElement> c) {
    super(owner, codeName, c);
  }

  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
}
