package pl.mpak.sky.gui.swing.syntax.structure.sql;

import java.util.Collection;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;

public class Into extends CodeElementList<CodeElement> {

  public Into(CodeElement owner) {
    super(owner, "Into");
  }

  public Into(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public Into(CodeElement owner, String codeName, Collection<? extends CodeElement> c) {
    super(owner, codeName, c);
  }

  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
}
