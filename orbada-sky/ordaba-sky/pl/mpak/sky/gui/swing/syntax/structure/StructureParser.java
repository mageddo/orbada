package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.HashMap;
import java.util.List;

import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;
import pl.mpak.sky.gui.swing.syntax.TokenCursor;

public abstract class StructureParser {

  private TokenCursor tc;
  private int[] skipBlanks;
  protected HashMap<String, Integer> keyWordList;
  private boolean ignoreCase = true;
  
  public StructureParser(int[] skipBlanks) {
    this.skipBlanks = skipBlanks;
    this.keyWordList = new HashMap<String, Integer>(); 
  }
  
  public void setTokenList(List<TokenRef> list) {
    tc = new TokenCursor(list, skipBlanks);
    tc.setIgnoreCase(ignoreCase);
  }
  
  public void setIgnoreCase(boolean ignoreCase) {
    if (tc != null) {
      tc.setIgnoreCase(ignoreCase);
    }
    else {
      this.ignoreCase = ignoreCase;
    }
  }
  
  protected TokenRef checkNull(TokenRef token) throws ParserException {
    if (token == null) {
      throw new ParserException("Nieoczekiwane zakoñczenie kodu!");
    }
    return token;
  }
  
  /**
   * <p>Zwraca nastêpny element lub null jeœli koniec
   * @return
   * @throws ParserException 
   * @see getToken().prev
   * @see getToken().next
   * @see getToken()
   */
  protected TokenRef nextToken() throws ParserException {
    return checkNull(tc.nextToken(false));
  }
  
  /**
   * <p>Zwraca nastêpny element i przy okazji pomija wszystko co zosta³o zdefiniowane w skipBlank()
   * @param skipBlanks
   * @return
   * @throws ParserException
   * @see skipBlank()
   */
  protected TokenRef nextToken(boolean skipBlanks) throws ParserException {
    return checkNull(tc.nextToken(skipBlanks));
  }
  
  /**
   * <p>Domyœlnie pomija elementy SyntaxDocument.NONE ale mo¿na funkcjê przej¹æ i pomijaæ inne elementy
   * @return
   * @throws ParserException
   * @see nextToken(boolean skipBlanks)
   */
  protected TokenRef skipBlank() throws ParserException {
    return checkNull(tc.skipBlank());
  }
  
  /**
   * <p>Zwraca poprzednio uzyskany token.<br>
   * Nie myliæ z getToken().prev
   * @return
   * @throws ParserException
   */
  protected TokenRef getLastToken() throws ParserException {
    return checkNull(tc.lastToken());
  }
  
  /**
   * <p>Zwraca offset dla okreœlenia pocz¹tku bloku
   * @return
   * @throws ParserException
   */
  protected int getStartOffset() throws ParserException {
    return tc.getStartOffset();
  }
  
  /**
   * <p>Zwraca offset dla okreœlenia koñca bloku
   * @return
   * @throws ParserException
   */
  protected int getEndOffset() throws ParserException {
    checkNull(tc.token());
    return tc.getEndOffset();
  }
  
  /**
   * <p>Sprawdzenie czy token jest identyczny co text
   * @param text
   * @return
   * @throws ParserException
   * @see Parser(Iterator&lt;TokenRef&gt; iterator, boolean <b>ignoreCase</b>)
   */
  protected boolean isToken(String text) throws ParserException {
    checkNull(tc.token());
    return tc.isToken(text);
  }
  
  /**
   * <p>Zwraca ci¹g znaków aktualnego tokenu
   * @return
   * @throws ParserException
   */
  protected String getTokenString() throws ParserException {
    checkNull(tc.token());
    return tc.getString();
  }
  
  /**
   * <p>Pozwla zmieniæ styleId elementu zale¿nego (w edytorze)
   * @param styleId
   * @throws ParserException
   */
  protected void setStyle(int styleId) throws ParserException {
    checkNull(tc.token());
    getToken().ref.styleId = styleId;
  }
  
  /**
   * <p>Zwraca bierz¹cy token
   * @return
   * @throws ParserException
   */
  protected TokenRef getToken() throws ParserException {
    return checkNull(tc.token());
  }
  
  /**
   * <p>Umieszcza s³owo na specjalnej liœcie, która na koñcu parsowania mo¿e byæ u¿yta do ustawienia styli tokenów
   * @param keyWord
   * @param styleId
   * @throws ParserException
   * @see updateKeyWordStyles
   */
  protected void putKeyWord(String keyWord, int styleId) throws ParserException {
    keyWordList.put((tc.isIgnoreCase() ? keyWord.toUpperCase() : keyWord), styleId);
    if (isToken(keyWord)) {
      setStyle(styleId);
    }
  }
  
  protected boolean updateStyle(int orygStyleId) {
    return orygStyleId == SyntaxDocument.NONE;
  }
  
  /**
   * <p>Aktualizuje tokeny edytora stylami okreœlonymi na liœcie s³ów kluczowych
   * @throws ParserException
   * @see putKeyWord
   */
  protected void updateKeyWordStyles() {
    tc.reset();
    try {
      while (nextToken() != null) {
        Integer styleId = keyWordList.get((tc.isIgnoreCase() ? getToken().token.toUpperCase() : getToken().token));
        if (styleId != null && updateStyle(getToken().styleId)) {
          setStyle(styleId);
        }
      }
    } catch (ParserException e) {
    }
  }

  /**
   * <p>G³ówna funkcja do parsowania
   * @return
   * @throws ParserException
   */
  public abstract BlockElement parse() throws ParserException;
  
}
