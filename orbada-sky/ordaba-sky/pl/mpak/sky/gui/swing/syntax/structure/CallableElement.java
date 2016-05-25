package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.ArrayList;
import java.util.Arrays;



public class CallableElement extends BlockElement {

  private ArrayList<DeclaredElement> parameterList;
  private int index;
  private String name;
  private Scope scope;
  
  public CallableElement(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public ArrayList<DeclaredElement> getParameterList() {
    if (parameterList == null) {
      parameterList = new ArrayList<DeclaredElement>();
    }
    return parameterList;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setScope(Scope scope) {
    this.scope = scope;
  }

  public String getName() {
    return name;
  }

  public Scope getScope() {
    return scope;
  }
  
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public CodeElement getElementAt(int offset) {
    CodeElement result;
    for (DeclaredElement e : getParameterList()) {
      if ((result = e.getElementAt(offset)) != null) {
        return result;
      }
    }
    return super.getElementAt(offset);
  }

  public String toString() {
    return 
      super.toString() +
      ",\nname=" +name +
      ",\nparameterList=" +Arrays.toString(getParameterList().toArray());
  }

}
