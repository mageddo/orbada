package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;

public class Identifier extends CodeElementList<CodeElement> implements Comparable<Identifier> {

  private CodeElement table;
  
  public Identifier(CodeElement owner) {
    super(owner, "Identifier");
  }

  public Identifier(CodeElement owner, SymbolElement firstElement, CodeElement copyProps) {
    super(owner, "Identifier");
    if (copyProps != null) {
      setStartOffset(copyProps.getStartOffset());
      setEndOffset(copyProps.getEndOffset());
      setToken(copyProps.getToken());
      setKeywords(copyProps.getKeywords());
    }
    if (firstElement != null) {
      if (copyProps != null) {
        firstElement.setStartOffset(copyProps.getStartOffset());
        firstElement.setEndOffset(copyProps.getEndOffset());
        firstElement.setToken(copyProps.getToken());
        firstElement.setKeywords(copyProps.getKeywords());
      }
      firstElement.changeOwner(this);
      add(firstElement);
    }
  }

  public SymbolElement lastSymbol() {
    for (int i=size() -1; i>=0; i--) {
      CodeElement e = get(i);
      if (e instanceof SymbolElement && !"null".equalsIgnoreCase(((SymbolElement)e).getCodeName())) {
        return (SymbolElement)e;
      }
    }
    return null;
  }
  
  public SymbolElement firstSymbol() {
    for (int i=0; i<size(); i++) {
      CodeElement e = get(i);
      if (e instanceof SymbolElement && !"null".equalsIgnoreCase(((SymbolElement)e).getCodeName())) {
        return (SymbolElement)e;
      }
    }
    return null;
  }
  
  public CodeElement getTable() {
    return table;
  }

  public void setTable(CodeElement table) {
    this.table = table;
  }

  public String toSource(int level) {
    StringBuilder sb = new StringBuilder();
    for (CodeElement e : this) {
      if (sb.length() != 0) {
        sb.append(".");
      }
      sb.append(e.toSource(level));
    }
    return sb.toString();
  }

  public String[] toArray(String[] tab) {
    for (int i=0; i<size(); i++) {
      tab[i] = get(i).toSource(0);
    }
    return tab;
  }
  
  public void setTokenStyle(int styleId) {
    for (CodeElement e : this) {
      if (e != null) {
        e.setTokenStyle(styleId);
      }
    }
  }

  public void setTokenWaveUnderline(int waveStyleId) {
    for (CodeElement e : this) {
      if (e != null) {
        e.setTokenWaveUnderline(waveStyleId);
      }
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Identifier) {
      return compareTo((Identifier)obj) == 0;
    }
    return super.equals(obj);
  }

  @Override
  public int compareTo(Identifier o) {
    if (o == null) {
      return -1;
    }
    return toSource(0).compareToIgnoreCase(o.toSource(0));
  }
 @Override
  public String getDisplayCodeName() {
    String ts = "";
    if (table != null) {
      if (table instanceof TableList) {
        ts = " (table list)";
      }
      else if (table instanceof Table) {
        if (((Table)table).getTableName() != null) {
          ts = " (" +((Table)table).getTableName().toSource(0) +")";
        }
        else {
          ts = " (" +((Table)table).getAlias().toSource(0) +")";
        }
      }
    }
    return super.getDisplayCodeName() +ts;
  } 
}
