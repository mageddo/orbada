package pl.mpak.sky.gui.swing.syntax.structure;


public class CaseWhen extends BlockElement {

  private Expression expression;

  public CaseWhen(CodeElement owner) {
    super(owner, "When");
  }

  public CaseWhen(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  public CodeElement getElementAt(int offset) {
    CodeElement result;
    if (expression != null) {
      if ((result = expression.getElementAt(offset)) != null) {
        return result;
      }
    }
    return super.getElementAt(offset);
  }

}
