package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.Expression;
import pl.mpak.sky.gui.swing.syntax.structure.OperatorElement;
import pl.mpak.sky.gui.swing.syntax.structure.StarElement;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;

public class Column extends Expression {

  private Alias alias;

  public Column(CodeElement owner) {
    super(owner, "Column");
  }
  
  public Alias getAlias() {
    return alias;
  }

  public void setAlias(Alias alias) {
    this.alias = alias;
  }

  public void updateInfo() {
    SymbolElement alias = null;
    for (int i=0; i<size(); i++) {
      CodeElement e = get(i);
      if (e instanceof Identifier) {
        alias = ((Identifier)e).lastSymbol();
      }
    }
    if (alias != null && alias.getName() != null) {
      if (alias.getToken() != null) {
        alias.getToken().styleId = SQLSyntaxDocument.LOCAL_IDENTIFIER;
      }
      setAlias(new Alias(this, alias));
    }
    if (size() > 0 && get(size() -1) instanceof OperatorElement && "*".equals(((OperatorElement)get(size() -1)).getOperator())) {
      set(size() -1, new Identifier(this, new StarElement(null), get(size() -1)));
    }
  }
  
  public String getDisplayCodeName() {
    return getCodeName() +(alias != null ? " " +alias.getName() : "");
  }

}
