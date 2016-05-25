package pl.mpak.sky.gui.swing.syntax.structure;


public class Constant extends DeclaredElement {

  public Constant(CodeElement owner) {
    super(owner, "Constant");
  }
  
  public Constant(CodeElement owner, String codeName) {
    super(owner, codeName);
  }
  
}
