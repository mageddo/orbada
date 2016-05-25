package pl.mpak.sky.gui.swing.syntax.structure;


public class SymbolElement extends CodeElement {

  private String name;
  
  public SymbolElement(CodeElement owner) {
    super(owner, "Symbol");
  }

  public SymbolElement(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public String toString() {
    return super.toString() +",name=" +name;
  }

  public String toSource(int level) {
    if ("*".equals(getCodeName())) {
      return "*";
    }
    else if ("null".equalsIgnoreCase(getCodeName())) {
      return "";
    }
    return name;
  }
  
}
