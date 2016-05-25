package pl.mpak.sky.gui.swing.syntax.structure;


public class Function extends CallableElement {

  public Function(CodeElement owner) {
    super(owner, "Function");
  }
  
  public Function(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

}
