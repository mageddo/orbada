package pl.mpak.sky.gui.swing.syntax.structure;


public class NumericElement extends ValueElement {

  public NumericElement(CodeElement owner) {
    super(owner, "Numeric");
  }

  public NumericElement(CodeElement owner, String value) {
    super(owner, "Numeric", value);
  }

}
