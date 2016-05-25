package pl.mpak.sky.gui.swing.syntax.structure;


public class KeywordElement extends CodeElement {

  private String keyword;
  
  public KeywordElement(CodeElement owner) {
    super(owner, "Keyword");
  }

  public KeywordElement(CodeElement owner, String keyword) {
    this(owner);
    this.keyword = keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public String getKeyword() {
    return keyword;
  }
  
  public String toString() {
    return super.toString() +",keyword=" +keyword;
  }

  public String toSource(int level) {
    return keyword;
  }
  
}
