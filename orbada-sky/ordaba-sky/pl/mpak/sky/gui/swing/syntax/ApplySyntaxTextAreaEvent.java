package pl.mpak.sky.gui.swing.syntax;

/**
 * @author akaluza
 * Interface for apply "somthings" to all syntax editors
 * @see SyntaxTextArea.apply
 */
public interface ApplySyntaxTextAreaEvent {

  public void apply(SyntaxTextArea editor);
  
}
