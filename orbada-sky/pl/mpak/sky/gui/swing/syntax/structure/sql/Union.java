package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;

public class Union extends CodeElement {

  private CodeElement element;
  
  public Union(CodeElement owner) {
    super(owner, "Union");
  }

  public CodeElement getElement() {
    return element;
  }

  public void setElement(CodeElement element) {
    this.element = element;
  }

  public CodeElement getElementAt(int offset) {
    return getElementAt(new CodeElement[] {element}, offset);
  }

  public CodeElement[] find(Class<?> clazz) {
    return find(new CodeElement[] {element}, clazz);
  }

  public String toSource(int level) {
    return
      keywordsToSource(level, "") +"\n" +
      (element != null ? element.toSource(level) : "")
      ;
  }

}
