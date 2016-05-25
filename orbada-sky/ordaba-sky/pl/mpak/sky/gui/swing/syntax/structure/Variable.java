package pl.mpak.sky.gui.swing.syntax.structure;


public class Variable extends DeclaredElement {

  public Variable(CodeElement owner) {
    super(owner, "Variable");
  }

  public Variable(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

}
