package pl.mpak.orbada.oracle.syntax.parser;

import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;

public class TypeObject extends BlockElement {

  private String name;
  
  public TypeObject(CodeElement owner) {
    super(owner, "Type");
  }

  public TypeObject(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
