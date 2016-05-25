package pl.mpak.sky.gui.swing.syntax.structure;


public class OperatorElement extends CodeElement {

  private String operator;
  
  public OperatorElement(CodeElement owner) {
    super(owner, "Operator");
  }

  public OperatorElement(CodeElement owner, String operator) {
    this(owner);
    this.operator = operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getOperator() {
    return operator;
  }
  
  public String toString() {
    return super.toString() +",operator=" +operator;
  }

  public String toSource(int level) {
    return operator;
  }
  
}
