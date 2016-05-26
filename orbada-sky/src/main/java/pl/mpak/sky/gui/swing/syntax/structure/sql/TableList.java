package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;
import pl.mpak.sky.gui.swing.syntax.structure.StarElement;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;

public class TableList extends CodeElementList<Table> {

  public TableList(CodeElement owner) {
    super(owner, "Table list");
  }

  public TableList(CodeElement owner, String codeName) {
    super(owner, codeName);
  }
  
  public CodeElement indexOf(Identifier i) {
    if (i.size() == 1 && i.get(0) instanceof StarElement) {
      return this;
    }
    for (CodeElement e : this) {
      if (e instanceof Table) {
        Table t = (Table)e;
        if (t.getAlias() != null && t.getAlias().getName() != null && i.size() > 1 && i.get(0) instanceof SymbolElement && ((SymbolElement)i.get(0)).getName() != null) {
          if (t.getAlias().getName().equalsIgnoreCase(((SymbolElement)i.get(0)).getName())) {
            return t;
          }
        }
      }
    }
//    if (i.size() == 1 && i.get(0) instanceof SymbolElement && ((SymbolElement)i.get(0)).getName() != null) {
//      for (CodeElement e : this) {
//        if (e instanceof Table) {
//          Table t = (Table)e;
//            for (CodeElement c : t.getColumnList()) {
//              Alias a = (Alias)c;
//              if (a != null && a.getName() != null && a.getName().equalsIgnoreCase(((SymbolElement)i.get(0)).getName())) {
//                return t;
//              }
//            }
//          }
//      }
//    }
    return null;
  }

  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
  public String toSource(int level) {
    boolean brk = 
        getOwner() == null ||
        getOwner().getOwner(ColumnList.class) == null;
    StringBuilder sb = new StringBuilder();
    StringBuilder sbe = new StringBuilder();
    for (Table e : this) {
      String table = e.toSource(level);
      if (sb.length() != 0) {
        sbe.append(table);
        if (sbe.length() > 80 || table.length() > 30) {
          sb.append("\n      ");
          sbe.setLength(0);
        }
        table = " " +e.keywordsToSource(level, ",") +" " +table;
      }
      sb.append(table);
    }
    return keywordsToSource(level, "") +" " +sb.toString();
  }
  
}
