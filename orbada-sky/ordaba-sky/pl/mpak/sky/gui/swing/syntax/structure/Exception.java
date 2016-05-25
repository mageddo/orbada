package pl.mpak.sky.gui.swing.syntax.structure;


public class Exception extends DeclaredElement {

  public Exception(CodeElement owner) {
    super(owner, "Exception");
  }
  
  public Exception(CodeElement owner, String codeName) {
    super(owner, codeName);
  }
  
}
