package pl.mpak.sky.gui.swing.syntax.structure;


public class HintElement extends CodeElement {

  private String hint;
  
  public HintElement(CodeElement owner) {
    super(owner, "Hint");
  }

  public HintElement(CodeElement owner, String hint) {
    this(owner);
    this.hint = hint;
  }

  public void setHint(String hint) {
    this.hint = hint;
  }

  public String getHint() {
    return hint;
  }
  
  public String toString() {
    return super.toString() +",hint=" +hint;
  }

  public String toSource(int level) {
    return hint;
  }
  
}
