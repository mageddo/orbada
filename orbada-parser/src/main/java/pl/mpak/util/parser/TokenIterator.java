package pl.mpak.util.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

public class TokenIterator extends ParsedDocument implements Iterator<Token> {
  
  private ArrayList<Token> tokenList = new ArrayList<Token>();
  private Integer[] skipToken;
  int cursor;
  public Token empty = new Token(this, TokenHandle.NONE, null, -1, -1, -1);

  public TokenIterator(Reader reader, TokenHandle tokenHandle) throws IOException {
    super();
    setTokenizer(new Tokenizer(reader, tokenHandle));
    reset();
  }
  
  public TokenIterator(String text, TokenHandle tokenHandle) throws IOException {
    super();
    setTokenizer(new Tokenizer(text, tokenHandle));
    reset();
  }
  
  public TokenIterator(String text, TokenHandle tokenHandle, Integer[] skipToken) throws IOException {
    super();
    this.skipToken = skipToken;
    setTokenizer(new Tokenizer(text, tokenHandle));
    reset();
  }
  
  public ArrayList<Token> getTokenList() {
    return tokenList;
  }
  
  protected void tokenized(int token, String text, int offset, int line, int column) {
    if (skipToken != null && skipToken.length > 0) {
      for (int i=0; i<skipToken.length; i++) {
        if (skipToken[i] == token) {
          return;
        }
      }
    }
    tokenList.add(new Token(this, token, text, offset, line, column));
  }

  protected void init() {
  }

  public void reset() {
    if (tokenList.size() > 0) {
      cursor = -1;
    }
    else {
      cursor = tokenList.size();
    }
  }
  
  public boolean hasNext() {
    return cursor < tokenList.size() -1;
  }
  
  public Token next() {
    if (!hasNext()) {
      cursor = tokenList.size();
      return empty;
    }
    cursor++;
    return getCurrent();
  }
  
  public Token getCurrent() {
    if (cursor >= 0 && cursor < tokenList.size()) {
      return tokenList.get(cursor);
    }
    return empty;
  }
  
  public void setCurrent(Token token) {
    if (token == null) {
      cursor = -1;
    }
    else {
      cursor = token.getIndex();
    }
  }
  
  public Token getLeft() {
    return getLeft(1);
  }

  public Token getLeft(int offset) {
    if (cursor -offset >= 0) {
      return tokenList.get(cursor -offset);
    }
    return empty;
  }

  public Token getRight() {
    return getRight(1);
  }

  public Token getRight(int offset) {
    if (cursor +offset < tokenList.size()) {
      return tokenList.get(cursor +offset);
    }
    return empty;
  }

  public void remove() {
    if (getCurrent() == null) {
      throw new IllegalArgumentException("current is null"); 
    }
    tokenList.remove(cursor);
  }
  
}
