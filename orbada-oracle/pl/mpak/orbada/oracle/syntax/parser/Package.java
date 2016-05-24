package pl.mpak.orbada.oracle.syntax.parser;

import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;

public class Package extends BlockElement {

  private String name;
  
  public Package(CodeElement owner) {
    super(owner, "Package");
  }

  public Package(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
