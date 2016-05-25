package pl.mpak.sky.gui.swing.syntax.structure;

import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;

public abstract class SqlStructureParser extends StructureParser {

  public SqlStructureParser() {
    super(SQLSyntaxDocument.blankAllStyles);
  }

  protected boolean updateStyle(int orygStyleId) {
    return 
      orygStyleId == SyntaxDocument.NONE ||
      orygStyleId == SQLSyntaxDocument.IDENTIFIER ||
      orygStyleId == SQLSyntaxDocument.LOCAL_IDENTIFIER;
  }
  
}
