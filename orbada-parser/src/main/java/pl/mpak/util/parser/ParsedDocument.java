package pl.mpak.util.parser;

import java.io.IOException;


/**
 * @author Andrzej Ka³u¿a
 * 
 * Abstrakcyjna klasa bazowa do tworzenia struktur poddanych tokenizacji
 *
 */
public abstract class ParsedDocument {

  private Tokenizer tokenizer = null;
  
  public ParsedDocument(Tokenizer tokenizer) throws IOException {
    super();
    setTokenizer(tokenizer);
  }
  
  public ParsedDocument() {
    super();
  }
  
  public void setTokenizer(Tokenizer tokenizer) throws IOException {
    this.tokenizer = tokenizer;
    if (tokenizer == null) {
      throw new IllegalArgumentException("null Tokenizer passed");
    }
    parse();
  }

  public Tokenizer getTokenizer() {
    return tokenizer;
  }
  
  protected abstract void init();
  
  protected abstract void tokenized(int token, String text, int offset, int line, int column);
 
  private void parse() throws IOException {
    init();
    while (!tokenizer.isEof()) {
      tokenizer.getNextToken();
      tokenized(
          tokenizer.getToken(), 
          tokenizer.getString(),
          tokenizer.getTokenStartPos(),
          tokenizer.getLine(), 
          tokenizer.getColumn());
    }
  }
  
}
