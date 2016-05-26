package pl.mpak.sky.gui.swing.syntax.structure;


public class BindElement extends CodeElement {

  private String bind;
  
  public BindElement(CodeElement owner) {
    super(owner, "Bind");
  }

  public BindElement(CodeElement owner, String bind) {
    this(owner);
    this.bind = bind;
  }

  public void setBind(String bind) {
    this.bind = bind;
  }

  public String getBind() {
    return bind;
  }
  
  public String toString() {
    return super.toString() +",bind=" +bind;
  }

  public String toSource(int level) {
    return bind;
  }
  
}
