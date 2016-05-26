package pl.mpak.sky.gui.swing.syntax.structure;


public class ValueElement extends CodeElement {

  private String value;
  
  public ValueElement(CodeElement owner) {
    super(owner, "Value");
  }

  public ValueElement(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public ValueElement(CodeElement owner, String codeName, String value) {
    this(owner, codeName);
    this.value = value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
  
  public String toString() {
    return super.toString() +",value=" +value;
  }

  public String toSource(int level) {
    return value;
  }
  
}
