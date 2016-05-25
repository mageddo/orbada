package pl.mpak.sky.gui.swing.syntax.structure.sql;

import java.util.Collection;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class Having extends CodeElementList<CodeElement> {

  public Having(CodeElement owner) {
    super(owner, "Having");
  }

  public Having(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public Having(CodeElement owner, String codeName, Collection<? extends CodeElement> c) {
    super(owner, codeName, c);
  }

  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
}
