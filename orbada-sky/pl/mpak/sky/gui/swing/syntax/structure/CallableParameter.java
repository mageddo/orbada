package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.Arrays;


public class CallableParameter extends DeclaredElement {

  private String[] flags;
  
  public CallableParameter(CodeElement owner) {
    super(owner, "Parameter");
  }

  public CallableParameter(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public String[] getFlags() {
    return flags;
  }
  
  public String toString() {
    return super.toString() +",flags=" +Arrays.toString(flags);
  }

}
