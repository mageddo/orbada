package pl.mpak.sky.gui.swing.syntax.structure;


public class If extends BlockElement {

  private Condition condition;
  private Else elseBlock;
  
  public If(CodeElement owner) {
    super(owner, "If");
  }

  public If(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public Condition getCondition() {
    return condition;
  }

  public void setCondition(Condition condition) {
    this.condition = condition;
  }

  public Else getElseBlock() {
    return elseBlock;
  }

  public void setElseBlock(Else elseBlock) {
    this.elseBlock = elseBlock;
  }

  public CodeElement getElementAt(int offset) {
    CodeElement result;
    if (condition != null) {
      if ((result = condition.getElementAt(offset)) != null) {
        return result;
      }
    }
    if (elseBlock != null) {
      if ((result = elseBlock.getElementAt(offset)) != null) {
        return result;
      }
    }
    return super.getElementAt(offset);
  }

  public String toString() {
    return 
      super.toString() +
      ",\ncondition=" +condition +
      ",\nelseBlock=" +elseBlock;
  }
  
}
