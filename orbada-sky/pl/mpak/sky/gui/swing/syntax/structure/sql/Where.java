package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.Expression;

public class Where extends Expression {

  public Where(CodeElement owner) {
    super(owner, "Where");
  }
  
  public String toSource(int level) {
    return keywordsToSource(level, "") +" " +super.toSource(level);
  }

}
