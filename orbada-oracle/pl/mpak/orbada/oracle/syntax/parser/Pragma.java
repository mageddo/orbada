package pl.mpak.orbada.oracle.syntax.parser;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.DeclaredElement;

public class Pragma extends DeclaredElement {

  public Pragma(CodeElement owner) {
    super(owner, "Pragma");
  }
  
  public Pragma(CodeElement owner, String codeName) {
    super(owner, codeName);
  }
  
}
