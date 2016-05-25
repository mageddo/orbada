package pl.mpak.util.parser;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Token implements Cloneable {
  
  private TokenIterator tokenIterator;
  private int line;
  private int column;
  private int tokenType;
  private String tokenString;
  protected int index;
  private int offset;

  public Token(TokenIterator tokenIterator, int tokenType, String token, int offset, int line, int column) {
    super();
    this.tokenIterator = tokenIterator;
    this.tokenType = tokenType;
    this.tokenString = token;
    this.line = line;
    this.column = column;
    this.index = this.tokenIterator.getTokenList().size();
    this.offset = offset;
  }
  
  public int getIndex() {
    return index;
  }
  
  public boolean isFirst() {
    return index == 0;
  }
  
  public boolean isLast() {
    return index == this.tokenIterator.getTokenList().size() -1;
  }
  
  public Token getLeft() {
    return tokenIterator.getLeft();
  }
  
  public Token getRight() {
    return tokenIterator.getRight();
  }
  
  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public int getType() {
    return tokenType;
  }
  
  public boolean isType(int type) {
    return tokenType == type;
  }
  
  /**
   * Zwraca pobrany token
   * @return
   */
  public String getString() {
    return tokenString;
  }
  
  /**
   * Zwraca token w postaci numerycznej
   * @return
   */
  public double getNumber() {
    return new Double(getString());
  }
  
  /**
   * Zwraca token w postaci numerycznej BigDecimal
   * @return
   */
  public BigDecimal getDecimal() {
    return new BigDecimal(getString());
  }
  
  /**
   * Zwraca token w postaci liczby ca³kowitej
   * @return
   */
  public int getInteger() {
    return new Integer(getString());
  }
  
  /**
   * Zwraca token w postaci liczby ca³kowitej du¿ej
   * @return
   */
  public BigInteger getBigInteger() {
    return new BigInteger(getString());
  }
  
  /**
   * Zwraca token w postaci liczby rzeczywistej du¿ej
   * @return
   */
  public BigDecimal getBigDecimal() {
    return new BigDecimal(getString());
  }
  
  /**
   * Testuje token na równoœæ z getTokenString()
   * Jeœli s¹ równe funkcja zwraca true
   * @param token
   * @param caseInsensitive
   * @return
   */
  public boolean isString(String token, boolean caseInsensitive) {
    return caseInsensitive ? token.equalsIgnoreCase(getString()) : token.equals(getString()); 
  }
  
  public boolean isString(String token) {
    return isString(token, true); 
  }
  
  /**
   * Testuje ci¹gi znaków w tokens na równoœæ z getTokenString()
   * Jeœli którykolwiek jest równy funkcja zwraca true
   * @param tokens
   * @param caseInsensitive
   * @return
   */
  public boolean areString(String[] tokens, boolean caseInsensitive) {
    for (int i=0; i<tokens.length; i++) {
      if (isString(tokens[i], caseInsensitive)) {
        return true;
      }
    }
    return false;
  }
  
  public TokenIterator getIterator() {
    return tokenIterator;
  }
  
  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getOffset() {
    return offset;
  }

  public String toString() {
    return "[" +
      "Line:" +line +", " +
      "Column:" +column +", " +
      "Type:" +tokenType +", " +
      "String:\"" +tokenString +"\", " +
      "Offset:" +offset +"]";
  }
  
  public Object clone() {
    Token token = new Token(tokenIterator, tokenType, new String(tokenString), offset, line, column);
    token.index = index;
    return token;
  }
  
}
