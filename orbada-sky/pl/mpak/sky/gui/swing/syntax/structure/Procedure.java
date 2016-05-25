package pl.mpak.sky.gui.swing.syntax.structure;


public class Procedure extends CallableElement {

  public Procedure(CodeElement owner) {
    super(owner, "Procedure");
  }
  
  public Procedure(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

}
