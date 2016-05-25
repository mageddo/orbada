package pl.mpak.sky.gui.swing.syntax;

public class CommentSpec {

  private String commentBegin;
  private String commentEnd;

  public CommentSpec(String commentBegin, String commentEnd) {
    this.commentBegin = commentBegin;
    this.commentEnd = commentEnd;
  }

  public String getCommentBegin() {
    return commentBegin;
  }

  public String getCommentEnd() {
    return commentEnd;
  }

  public boolean isLineComment() {
    return "\n".equals(commentEnd);
  }
  
  public boolean isBlockComment() {
    return !isLineComment();
  }
  
  public static CommentSpec getLineComment(CommentSpec[] comments) {
    if (comments != null) {
      for (CommentSpec c : comments) {
        if (c.isLineComment()) {
          return c;
        }
      }
    }
    return null;
  }

  public static CommentSpec getBlockComment(CommentSpec[] comments) {
    if (comments != null) {
      for (CommentSpec c : comments) {
        if (c.isBlockComment()) {
          return c;
        }
      }
    }
    return null;
  }

}
