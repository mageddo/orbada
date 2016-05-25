package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.ArrayList;


public class BlockElement extends CodeElement {

  private int beginBlockOffset;
  private ArrayList<DeclaredElement> declaredList;
  private ArrayList<CallableElement> callableList;
  private ArrayList<CodeElement> codeList;
  private ExceptionBlock exceptionBlock;
  private Finally finallyBlock;
  
  public BlockElement(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public int getBeginBlockOffset() {
    return beginBlockOffset;
  }

  public void setBeginBlockOffset(int beginBlockOffset) {
    this.beginBlockOffset = beginBlockOffset;
  }

  public void putDeclare(DeclaredElement element) {
    element.setIndex(getCallableList().size());
    getDeclaredList().add(element);
  }

  public ArrayList<DeclaredElement> getDeclaredList() {
    if (declaredList == null) {
      declaredList = new ArrayList<DeclaredElement>();
    }
    return declaredList;
  }
  
  public void putCallable(CallableElement element) {
    element.setIndex(getCallableList().size());
    getCallableList().add(element);
  }

  public ArrayList<CallableElement> getCallableList() {
    if (callableList == null) {
      callableList = new ArrayList<CallableElement>();
    }
    return callableList;
  }

  public void putCode(CodeElement element) {
    element.setIndex(getCodeList().size());
    getCodeList().add(element);
  }

  public ArrayList<CodeElement> getCodeList() {
    if (codeList == null) {
      codeList = new ArrayList<CodeElement>();
    }
    return codeList;
  }

  public Finally getFinallyBlock() {
    return finallyBlock;
  }

  public void setFinallyBlock(Finally finallyBlock) {
    this.finallyBlock = finallyBlock;
  }
  
  public ExceptionBlock getExceptionBlock() {
    return exceptionBlock;
  }

  public void setExceptionBlock(ExceptionBlock exceptionBlock) {
    this.exceptionBlock = exceptionBlock;
  }
  
  public CodeElement getElementAt(int offset) {
    CodeElement result;
    for (DeclaredElement e : getDeclaredList()) {
      if ((result = e.getElementAt(offset)) != null) {
        return result;
      }
    }
    for (CallableElement e : getCallableList()) {
      if ((result = e.getElementAt(offset)) != null) {
        return result;
      }
    }
    for (CodeElement e : getCodeList()) {
      if (e != null && (result = e.getElementAt(offset)) != null) {
        return result;
      }
    }
    if (exceptionBlock != null) {
      if ((result = exceptionBlock.getElementAt(offset)) != null) {
        return result;
      }
    }
    if (finallyBlock != null) {
      if ((result = finallyBlock.getElementAt(offset)) != null) {
        return result;
      }
    }
    return super.getElementAt(offset);
  }
  
}
