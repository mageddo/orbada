package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;

public class WithList extends CodeElementList<CodeElement> {

  /**
   * @param owner
   */
  public WithList(CodeElement owner) {
    super(owner, "WithList");
  }

  public With indexOf(Identifier i) {
    for (CodeElement e : this) {
      if (e instanceof With) {
        With w = (With)e;
        if (i.size() == 1 && w.getAlias() != null && w.getAlias().getName() != null && i.get(0) instanceof SymbolElement && w.getAlias().getName().equalsIgnoreCase(((SymbolElement)i.get(0)).getName())) {
          return w;
        }
      }
    }
    return null;
  }
  
  public String toString() {
    return 
        "(" +
        super.toString() +
        ")";
  }
  
}
