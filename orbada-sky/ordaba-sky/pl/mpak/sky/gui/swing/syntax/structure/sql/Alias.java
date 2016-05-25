package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;

public class Alias extends CodeElement {

  private String name;
  private String info;
  private CodeElement element;
  
  public Alias(CodeElement owner) {
    super(owner, "Alias");
  }

  public Alias(CodeElement owner, String name, String info) {
    this(owner);
    this.name = name;
    this.info = info;
  }

  public Alias(CodeElement owner, CodeElement element) {
    this(owner);
    this.element = element;
    if (element instanceof SymbolElement) {
      this.name = ((SymbolElement)element).getName();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }
  
  public CodeElement getElement() {
    return element;
  }

  public void setElement(CodeElement element) {
    this.element = element;
  }

  @Override
  public String toSource(int level) {
    return name +(info != null ? " " +info : "");
  }

}
