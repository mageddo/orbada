package pl.mpak.sky.gui.swing.syntax.structure;


public class StringElement extends ValueElement {

  public StringElement(CodeElement owner) {
    super(owner, "String");
  }

  public StringElement(CodeElement owner, String value) {
    super(owner, "String", value);
  }

}
