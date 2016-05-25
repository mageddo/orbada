package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;
import pl.mpak.sky.gui.swing.syntax.structure.Expression;
import pl.mpak.sky.gui.swing.syntax.structure.KeywordElement;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;

public class With extends Expression {
  
  private Alias alias;
  private CodeElementList<Alias> columnList;

  public With(CodeElement owner) {
    super(owner, "With");
  }

  public Alias getAlias() {
    return alias;
  }

  public void setAlias(Alias alias) {
    this.alias = alias;
  }

  public CodeElementList<Alias> getColumnList() {
    if (columnList == null) {
      columnList = new CodeElementList<Alias>(this);
    }
    return columnList;
  }

  public void setColumnList(CodeElementList<Alias> aliasList) {
    this.columnList = aliasList;
  }

  public void updateInfo() {
    SymbolElement alias = null;
    int aliasi = -1;
    for (int i=0; i<size(); i++) {
      CodeElement e = get(i);
      if (e instanceof KeywordElement && 
          ("as".equalsIgnoreCase(((KeywordElement)e).getKeyword()))) {
        break;
      }
      if (e instanceof Identifier) {
        alias = ((Identifier)e).lastSymbol();
        aliasi = i;
      }
    }
    if (alias != null && alias.getName() != null) {
      if (aliasi +1 < size() && get(aliasi +1) instanceof ExpressionList) {
        getColumnList().clear();
        for (CodeElement ee : (ExpressionList)get(aliasi +1)) {
          if (ee instanceof Expression && ((Expression)ee).size() > 0) {
            if (((Expression)ee).get(0) instanceof Identifier) {
              getColumnList().add(new Alias(this, ((Identifier)((Expression)ee).get(0)).lastSymbol()));
            }
          }
        }
      }
      if (alias.getToken() != null) {
        alias.getToken().styleId = SQLSyntaxDocument.LOCAL_IDENTIFIER;
      }
      setAlias(new Alias(this, alias));
    }
  }
  
  public String getDisplayCodeName() {
    return getCodeName() +(alias != null ? " " +alias.getName() +(columnList != null && !columnList.isEmpty() ? "(" +columnList.toSource(0) +")" : "") : "");
  }

  public String toSource(int level) {
    return super.toSource(level); 
  }

}
