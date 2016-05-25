package pl.mpak.sky.gui.swing.syntax.structure;


public class DeclaredElement extends CodeElement {

  private int index;
  private String name;
  private String type;
  private Scope scope;

  public DeclaredElement(CodeElement owner, String codeName) {
    super(owner, codeName);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setScope(Scope scope) {
    this.scope = scope;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
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

  public String toString() {
    return 
      "[" +super.toString() +
      ",name=" +name +
      ",type=" +type +"]";
  }

}
