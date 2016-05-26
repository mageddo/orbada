package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;
import pl.mpak.sky.gui.swing.syntax.structure.Expression;
import pl.mpak.sky.gui.swing.syntax.structure.KeywordElement;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;

public class Table extends Expression {
  
  private Alias alias;
  private Identifier tableName;
  private With with;
  private CodeElementList<Alias> columnList;

  public Table(CodeElement owner) {
    super(owner, "Table");
  }

  public Alias getAlias() {
    return alias;
  }

  public void setAlias(Alias alias) {
    this.alias = alias;
  }

  public Identifier getTableName() {
    return tableName;
  }

  public void setTableName(Identifier tableName) {
    this.tableName = tableName;
  }

  public With getWith() {
    return with;
  }

  public void setWith(With with) {
    this.with = with;
  }

  public CodeElementList<Alias> getColumnList() {
    if (columnList == null) {
      columnList = new CodeElementList<Alias>(this);
    }
    return columnList;
  }

  public void setColumnList(CodeElementList<Alias> columnList) {
    this.columnList = columnList;
  }

  public void updateInfo() {
    SymbolElement alias = null;
    int aliasi = -1;
    for (int i=0; i<size(); i++) {
      CodeElement e = get(i);
      if (e instanceof KeywordElement && 
          ("on".equalsIgnoreCase(((KeywordElement)e).getKeyword()) ||
           "using".equalsIgnoreCase(((KeywordElement)e).getKeyword()) ||
           "start".equalsIgnoreCase(((KeywordElement)e).getKeyword()) ||
           "limit".equalsIgnoreCase(((KeywordElement)e).getKeyword()))) {
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
      if (aliasi == 0 && get(aliasi) instanceof Identifier) {
        setTableName((Identifier)get(0));
      }
      else if (aliasi > 0 && get(aliasi -1) instanceof Identifier) {
        setTableName((Identifier)get(aliasi -1));
      }
      else if (aliasi > 1 && get(aliasi -1) instanceof KeywordElement && get(aliasi -2) instanceof Identifier) {
        setTableName((Identifier)get(aliasi -2));
      }
      if (alias.getToken() != null) {
        alias.getToken().styleId = SQLSyntaxDocument.LOCAL_IDENTIFIER;
      }
      setAlias(new Alias(this, alias));
      if (getTableName() != null) {
        CodeElement[] cea = getOwners(Select.class);
        for (CodeElement e : cea) {
          Select s = (Select)e;
          if (s.getWithList() != null) {
            With w = s.getWithList().indexOf(getTableName());
            if (w != null) {
              setWith(w);
            }
          }
        }
        CodeElement[] wla = getOwners(WithList.class);
        for (CodeElement e : wla) {
          WithList wl = (WithList)e;
          if (wl != null) {
            With w = wl.indexOf(getTableName());
            if (w != null) {
              setWith(w);
            }
          }
        }
      }
    }
  }
  
  public String getDisplayCodeName() {
    return 
        getCodeName() +
        (tableName != null ? " " +tableName.toSource(0) +(with != null ? "(with)" : "") : "") +
        (tableName != null ? " as" : "") +
        (alias != null ? " " +alias.getName() : "")
        //+(columnList != null && !columnList.isEmpty() ? "(" +columnList.toSource(0) +")" : "")
        ;
  }

  public String toSource(int level) {
    return super.toSource(level); 
  }

}
