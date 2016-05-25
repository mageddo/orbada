package pl.mpak.sky.gui.swing.syntax.structure;

import pl.mpak.sky.gui.swing.syntax.structure.sql.ExpressionArray;
import pl.mpak.sky.gui.swing.syntax.structure.sql.ExpressionList;
import pl.mpak.sky.gui.swing.syntax.structure.sql.Identifier;
import pl.mpak.sky.gui.swing.syntax.structure.sql.Select;
import pl.mpak.sky.gui.swing.syntax.structure.sql.Table;
import pl.mpak.sky.gui.swing.syntax.structure.sql.Where;

public class Expression extends CodeElementList<CodeElement> {

  public Expression(CodeElement owner) {
    super(owner, "Expression");
  }

  public Expression(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public String toSource(int level) {
    StringBuilder sb = new StringBuilder();
    boolean between = false;
    for (int i=0; i<size(); i++) {
      CodeElement e = get(i);
      if (e instanceof KeywordElement && "between".equalsIgnoreCase(((KeywordElement)e).getKeyword())) {
        between = true;
      }
      if (sb.length() != 0) {
        if (e instanceof KeywordElement && 
            "and".equalsIgnoreCase(((KeywordElement)e).getKeyword()) && 
            !between && 
            !(e.getOwner() instanceof Table) && 
            e.getOwner(ExpressionList.class, Where.class) == null) {
          sb.append("\n   ");
        }
        else if (e instanceof KeywordElement && 
            "connect".equalsIgnoreCase(((KeywordElement)e).getKeyword()) && 
            !(e.getOwner() instanceof Table) && 
            e.getOwner(ExpressionList.class, Where.class) == null) {
          sb.append("\n   ");
        }
        else
        if (!(e instanceof OperatorElement && "::".equals(((OperatorElement)e).getOperator())) &&
            !(i > 0 && get(i -1) instanceof OperatorElement && "::".equals(((OperatorElement)get(i -1)).getOperator())) &&
            !(e instanceof OperatorElement && "||".equals(((OperatorElement)e).getOperator())) &&
            !(i > 0 && get(i -1) instanceof OperatorElement && "||".equals(((OperatorElement)get(i -1)).getOperator())) &&
            !(e instanceof OperatorElement && "(+)".equals(((OperatorElement)e).getOperator())) &&
            !((e instanceof ExpressionList || e instanceof ExpressionArray) && i > 0 && get(i -1) instanceof Identifier) &&
            !(e instanceof StringElement && i > 0 && get(i -1) instanceof Identifier)) {
          sb.append(" ");
        }
      }
      if (e instanceof Select) {
        sb.append(e.toSource(level +1));
      }
      else if (e != null) {
        sb.append(e.toSource(level));
      }
      if (e instanceof KeywordElement && "and".equalsIgnoreCase(((KeywordElement)e).getKeyword())) {
        between = false;
      }
    }
    return sb.toString();
  }
  
}
