package pl.mpak.sky.gui.swing.syntax.structure;


public class NullElement extends ValueElement {

  public NullElement(CodeElement owner) {
    super(owner, "Null");
  }

  public NullElement(CodeElement owner, String value) {
    super(owner, "Null", value);
  }

}
