package pl.mpak.sky.gui.swing.syntax.structure;


public class BooleanElement extends ValueElement {

  public BooleanElement(CodeElement owner) {
    super(owner, "Boolean");
  }

  public BooleanElement(CodeElement owner, String value) {
    super(owner, "Boolean", value);
  }

}
