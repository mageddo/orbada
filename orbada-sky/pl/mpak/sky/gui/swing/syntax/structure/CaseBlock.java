package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.ArrayList;


public class CaseBlock extends CodeElement {

  private Expression expression;
  private ArrayList<CaseWhen> whenList;
  private BlockElement elseBlock;
  
  public CaseBlock(CodeElement owner) {
    super(owner, "Case");
  }

  public CaseBlock(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  public ArrayList<CaseWhen> getWhenList() {
    if (whenList == null) {
      whenList = new ArrayList<CaseWhen>();
    }
    return whenList;
  }

  public BlockElement getElseBlock() {
    return elseBlock;
  }

  public void setElseBlock(BlockElement elseBlock) {
    this.elseBlock = elseBlock;
  }

  public CodeElement getElementAt(int offset) {
    CodeElement result;
    if (expression != null) {
      if ((result = expression.getElementAt(offset)) != null) {
        return result;
      }
    }
    for (CaseWhen e : getWhenList()) {
      if ((result = e.getElementAt(offset)) != null) {
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

}
