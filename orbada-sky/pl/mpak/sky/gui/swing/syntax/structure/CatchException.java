package pl.mpak.sky.gui.swing.syntax.structure;


public class CatchException extends BlockElement {

  private Expression expression;

  public CatchException(CodeElement owner) {
    super(owner, "Catch");
  }

  public CatchException(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

}
