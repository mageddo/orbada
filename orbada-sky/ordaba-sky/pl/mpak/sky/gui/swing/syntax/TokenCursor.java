package pl.mpak.sky.gui.swing.syntax;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;
import pl.mpak.util.StringUtil;

public class TokenCursor implements Cloneable {

  private List<TokenRef> list;
  protected Iterator<TokenRef> iterator;
  protected TokenRef currentToken;
  protected TokenRef lastToken;
  protected boolean ignoreCase = true;
  protected int[] skipBlanks = new int[] {SyntaxDocument.NONE};
  
  public TokenCursor() {
  }
  
  public TokenCursor(TokenCursor cursor) {
    setTokenList(cursor.list);
    setIgnoreCase(cursor.ignoreCase);
    skipBlanks = cursor.skipBlanks;
  }
  
  public TokenCursor(List<TokenRef> list) {
    setTokenList(list);
  }
  
  public TokenCursor(List<TokenRef> list, int[] skipBlanks) {
    this.skipBlanks = skipBlanks;
    setTokenList(list);
  }
  
  public void setTokenList(List<TokenRef> list) {
    if (this.list != null) {
      this.list.clear();
    }
    this.list = list;
    reset();
  }
  
  public List<TokenRef> getTokenList() {
    return list;
  }

  public void setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }
  
  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  public int[] getSkipBlanks() {
    return skipBlanks;
  }

  public void setSkipBlanks(int[] skipBlanks) {
    this.skipBlanks = skipBlanks;
  }

  private TokenRef internalNextToken() {
    if (iterator.hasNext()) {
      return currentToken = iterator.next();
    }
    return currentToken = null;
  }
  
  public void reset() {
    this.iterator = list.iterator();
    this.currentToken = null;
    this.lastToken = null;
    nextToken();
  }
  
  /**
   * <p>Zwraca nastêpny element lub null jeœli koniec i pomija wszystko co zosta³o zdefiniowane w skipBlanks
   * @return
   * @see token()
   */
  public TokenRef nextToken() {
    return nextToken(true);
  }
  
  /**
   * <p>Zwraca nastêpny element i przy okazji mo¿e pomijac wszystko co zosta³o zdefiniowane w skipBlank()
   * @param skipBlanks
   * @return null jeœli nie ma wiêcej tokenów
   * @see skipBlanks
   */
  public TokenRef nextToken(boolean skipBlanks) {
    lastToken = currentToken;
    TokenRef result = internalNextToken();
    if (skipBlanks && result != null) {
      result = skipBlank();
    }
    return result;
  }
  
  public TokenRef skipBlank() {
    if (skipBlanks != null && Arrays.binarySearch(skipBlanks, token().styleId) >= 0) {
      while (internalNextToken() != null) {
        if (Arrays.binarySearch(skipBlanks, token().styleId) < 0) {
          return token();
        }
      }
    }
    return currentToken;
  }
  
  /**
   * <p>Zwraca poprzednio uzyskany token.<br>
   * @return null jeœli nie ma tokena
   */
  public TokenRef lastToken() {
    return lastToken;
  }
  
  /**
   * <p>Zwraca offset dla okreœlenia pocz¹tku bloku
   * @return
   */
  public int getStartOffset() {
    if (currentToken != null) {
      return currentToken.offset;
    }
    return -1;
  }
  
  /**
   * <p>Zwraca offset dla okreœlenia koñca bloku
   * @return
   */
  public int getEndOffset() {
    if (lastToken != null) {
      return lastToken.offset +lastToken.token.length();
    }
    return -1;
  }
  
  /**
   * <p>Sprawdzenie czy token jest identyczny co text
   * @param text
   * @return
   */
  public boolean isToken(String text) {
    if (currentToken != null) {
      if (ignoreCase) {
        return currentToken.isTokenIgnoreCase(text);
      }
      return currentToken.isToken(text);
    }
    return false;
  }
  
  /**
   * Którykolwiek z ci¹gów znaków
   * @param list
   * @return
   */
  public boolean isToken(String[] list) {
    if (currentToken != null) {
      return StringUtil.equalAnyOfString(currentToken.token, list, ignoreCase);
    }
    return false;
  }
  
  public boolean isToken(int styleId, String text) {
    return isStyle(styleId) && isToken(text);
  }
  
  public boolean isStyle(int styleId) {
    if (currentToken != null) {
      return currentToken.styleId == styleId;
    }
    return false;
  }
  
  /**
   * Którykolwiek ze styli
   * @param styles
   * @return
   */
  public boolean isStyle(int[] styles) {
    if (currentToken != null) {
      Arrays.sort(styles);
      return Arrays.binarySearch(styles, currentToken.styleId) >= 0;
    }
    return false;
  }
  
  /**
   * <p>Zwraca ci¹g znaków aktualnego tokenu
   * @return
   */
  public String getString() {
    if (currentToken != null) {
      return currentToken.token;
    }
    return null;
  }
  
  /**
   * <p>Zwraca bierz¹cy token
   * @return null jeœli nie ma tokena
   */
  public TokenRef token() {
    return currentToken;
  }
  
  public Iterator<TokenRef> getIterator() {
    return list.iterator();
  }
  
  public Object clone() {
    return new TokenCursor(this);
  }
  
}
