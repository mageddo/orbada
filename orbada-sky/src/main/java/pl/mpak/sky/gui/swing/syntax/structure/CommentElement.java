package pl.mpak.sky.gui.swing.syntax.structure;


public class CommentElement extends CodeElement {

  private String comment;
  
  public CommentElement(CodeElement owner) {
    super(owner, "Comment");
  }

  public CommentElement(CodeElement owner, String comment) {
    this(owner);
    this.comment = comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getComment() {
    return comment;
  }
  
  public String toString() {
    return super.toString() +",comment=" +comment;
  }

  public String toSource(int level) {
    return comment;
  }
  
}
