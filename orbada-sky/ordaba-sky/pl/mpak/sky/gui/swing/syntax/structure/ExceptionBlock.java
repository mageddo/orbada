package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.ArrayList;


public class ExceptionBlock extends CodeElement {

  ArrayList<CatchException> catchList;
  
  public ExceptionBlock(CodeElement owner) {
    super(owner, "Exceptions");
  }

  public ExceptionBlock(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public ArrayList<CatchException> getCatchList() {
    if (catchList == null) {
      catchList = new ArrayList<CatchException>();
    }
    return catchList;
  }

  public CodeElement getElementAt(int offset) {
    CodeElement result;
    for (CatchException e : getCatchList()) {
      if ((result = e.getElementAt(offset)) != null) {
        return result;
      }
    }
    return super.getElementAt(offset);
  }

}
