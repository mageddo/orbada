package pl.mpak.sky.gui.swing.syntax;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

public class SyntaxDocument extends PlainDocument {
  private static final long serialVersionUID = 7416095224814910013L;
  
  public final static int NONE = 0;
  public final static int MAX_LENGTH = 10000;
  private final static String spaces = String.format("%100s", new Object[] {""});
  
  // Wartoœci od 1 do 99 mog¹ byæ u¿yte przez u¿ytkownika/programistê
  
  public final static int END_OF_LINE = 100;
  public final static int GUTTER_LINE_NUMBER = 101;
  public final static int SELECTION_COLOR = 102;
  public final static int SYMBOL_SELECTION_COLOR_1 = 103;
  public final static int SYMBOL_SELECTION_COLOR_2 = 104;
  public final static int SYMBOL_SELECTION_COLOR_3 = 105;
  public final static int SYMBOL_SELECTION_COLOR_4 = 106;
  public final static int BOOKMARK = 107;
  public final static int BRACKET_COLOR = 108;
  public final static int SELECTED_LINE = 109;
  public final static int BLOCK_COLOR = 110;
  
  private SyntaxEditor textComponent = null;

  private final HashMap<Integer, SyntaxStyle> styleMap = new HashMap<Integer, SyntaxStyle>();
  private final HashMap<String, Integer> keyWordMap = new HashMap<String, Integer>();
  private final ArrayList<LineInfo> lines = new ArrayList<LineInfo>(); 
  private final HashMap<Integer, Bookmark> bookmarks = new HashMap<Integer, Bookmark>(); 
  private final ArrayList<LineMark> lineMarks = new ArrayList<LineMark>();
  private final HashMap<String, Snippet> snippets = new HashMap<String, Snippet>();
  private int[] ignoreTokentForImmediateSnippet = null;
  
  private int startCurrentText;
  private int endCurrentText;
  private int wordsCommaCount;
  
  private boolean ignoreCase = false;

  public SyntaxDocument() {
    super();
    init();
  }
  
  private void init() {
    addDocumentListener(getDocumentListener());
    styleMap.put(NONE, new SyntaxStyle("Other Chars"));
    styleMap.put(END_OF_LINE, new SyntaxStyle("End of line char", Color.GRAY));
    styleMap.put(GUTTER_LINE_NUMBER, new SyntaxStyle("Gutter line number", Color.GRAY));
    styleMap.put(SELECTION_COLOR, new SyntaxStyle("Selection Color", Color.LIGHT_GRAY));
    styleMap.put(SYMBOL_SELECTION_COLOR_1, new SyntaxStyle("Word Selection Color 1", new Color(190, 190, 250)));
    styleMap.put(SYMBOL_SELECTION_COLOR_2, new SyntaxStyle("Word Selection Color 2", new Color(190, 250, 190)));
    styleMap.put(SYMBOL_SELECTION_COLOR_3, new SyntaxStyle("Word Selection Color 3", new Color(250, 190, 190)));
    styleMap.put(SYMBOL_SELECTION_COLOR_4, new SyntaxStyle("Word Selection Color 4", new Color(250, 190, 250)));
    styleMap.put(BOOKMARK, new SyntaxStyle("Bookmark", Color.RED, true, false));
    styleMap.put(BRACKET_COLOR, new SyntaxStyle("Bracket Selection Color", new Color(190, 190, 250)));
    styleMap.put(BLOCK_COLOR, new SyntaxStyle("Block Color", new Color(230, 230, 230)));
    SyntaxStyle style = new SyntaxStyle("Selected line", new Color(150, 150, 250));
    style.setEnabled(false);
    styleMap.put(SELECTED_LINE, style);
    lines.add(new LineInfo());
    defaultSnippet();
  }
  
  protected void defaultSnippet() {
    addSnippet(new Snippet("(", "(|)", true, true));
    addSnippet(new Snippet("{", "{|}", true, true));
    addSnippet(new Snippet("[", "[|]", true, true));
    addSnippet(new Snippet("\"", "\"|\"", true, true));
    addSnippet(new Snippet("'", "'|'", true, true));
    addSnippet(new Snippet("`", "`|`", true, true));
  }
  
  public void addStyle(int styleId, SyntaxStyle style) {
    styleMap.put(styleId, style);
  }
  
  /**
   * Zwraca obiekt stylu o podanym id
   * @param styleId
   * @return
   */
  public SyntaxStyle getStyle(int styleId) {
    return styleMap.get(styleId);
  }
  
  /**
   * @param styleId
   * @param enable true - konstrola aktywnoœci pobieranego stylu<br>
   * true - jeœli style jest nie aktywny, funkcja zwróci styl NONE
   * @return
   */
  public SyntaxStyle getStyle(int styleId, boolean enable) {
    SyntaxStyle result = styleMap.get(styleId);
    if (enable && !result.isEnabled()) {
      result = styleMap.get(NONE);
    }
    return result;
  }
  
  public HashMap<Integer, SyntaxStyle> getStyleMap() {
    return styleMap;
  }
  
  public void addSingleKeyWord(String word, int styleId) {
    keyWordMap.put(ignoreCase ? word.toUpperCase() : word, styleId);
  }
  
  public void addKeyWord(String word, int styleId) {
    synchronized (keyWordMap) {
      if (word.indexOf(',') >= 0) {
        StringTokenizer st = new StringTokenizer(ignoreCase ? word.toUpperCase() : word, ",");
        while (st.hasMoreTokens()) {
          keyWordMap.put(st.nextToken().trim(), styleId);
        }
      }
      else {
        keyWordMap.put(ignoreCase ? word.toUpperCase() : word, styleId);
      }
    }
  }
  
  public LineInfo getLineInfo(int index) {
    synchronized (lines) {
      try {
        return lines.get(index);
      }
      catch (IndexOutOfBoundsException e) {
        return null;
      }
    }
  }
  
  /**
   * Zwraca styl podanego s³owa kluczowego
   * @param keyWord
   * @return
   */
  public int getStyle(String keyWord) {
    Integer styleId = keyWordMap.get(ignoreCase ? keyWord.toUpperCase() : keyWord);
    return styleId == null ? NONE : styleId;
  }
  
  public boolean symbolSelectionActive() {
    return
      getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_1, false).isEnabled() ||
      getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_2, false).isEnabled() ||
      getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_3, false).isEnabled() ||
      getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_4, false).isEnabled();
  }

  public boolean bracketSelectionActive() {
    return
      getStyle(SyntaxDocument.BRACKET_COLOR, false).isEnabled();
  }

  public boolean lineSelectionActive() {
    return
      getStyle(SyntaxDocument.SELECTED_LINE, false).isEnabled();
  }

  public HashMap<String, Integer> getKeyWordMap() {
    return keyWordMap;
  }
  
  public void clearKeyWords() {
    synchronized (keyWordMap) {
      keyWordMap.clear();
    }
  }
  
  public void tokenizeLines() {
    tokenizeLines(0, getDefaultRootElement().getElementCount());
  }
  
  public void tokenizeLines(int start, int len) {
    Segment lineSegment = new Segment();
    Element map = getDefaultRootElement();

    len += start;

    try {
      for (int i = start; i < len; i++) {
        Element lineElement = map.getElement(i);
        int lineStart = lineElement.getStartOffset();
        getText(lineStart, lineElement.getEndOffset() -lineStart, lineSegment);
        tokenizeLine(lineSegment, i);
      }
    }
    catch (BadLocationException bl) {
      bl.printStackTrace();
    }
  }
  
  public Object getLockLines() {
    return lines;
  }
  
  void resetAt(int index) {
    synchronized (lines) {
      for (int i=index; i<lines.size(); i++) {
        if (lines.get(i).refresh) {
          break;
        }
        lines.get(i).reset();
      }
    }
    if (textComponent != null && textComponent.isShowing()) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          textComponent.repaint();
        }
      });
    }
  }
  
  public void tokenizeLine(Segment line, int index) {
    synchronized (lines) {
      LineInfo lastInfo = index > 0 ? lines.get(index -1) : null;
  
      if (lastInfo == null) {
        LineInfo info = lines.get(index);
        boolean reset = tokenizeLine(line, info, NONE);
        if (reset) {
          resetAt(index +1);
        }
      }
      else if (lastInfo.refresh) {
        int start = index;
        while (start > 0) {
          lastInfo = lines.get(start -1);
          if (!lastInfo.refresh) {
            break;
          }
          start--;
        }
        tokenizeLines(start, index -start +1);
      }
      else {
        LineInfo info = lines.get(index);
        boolean reset = tokenizeLine(line, info, lastInfo.lastStyleId());
        if (reset) {
          resetAt(index +1);
        }
      }
    }
  }
  
  /**
   * Ta implementacja funkcja powinna byæ zast¹piona funkcj¹ u¿ytkownika w celu
   * tokenizacji linii 
   * @param line
   * @param info
   * @param lastToken
   * @return czy zresetowaæ nastêpne linie
   */
  protected boolean tokenizeLine(Segment line, LineInfo info, int lastToken) {
    info.clear();
    info.addToken(lastToken, line.count);
    return false;
  }
  
  public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
    if (str.indexOf('\t') >= 0) {
      str = StringUtil.replaceString(str, "\t", spaces.substring(0, SkySetting.getInteger(SkySetting.SyntaxEditor_TabToSpaceCount, SkySetting.Default_SyntaxEditor_TabToSpaceCount)));
    }
    super.insertString(offs, str, a);
  }
  
  private void insertLines(int index, int lines) {
    synchronized (this.lines) {
      for (int i=index; i<index +lines; i++) {
        this.lines.add(Math.min(i, this.lines.size()), new LineInfo());
      }
    }
    bookmarkInsertedLine(index, index +lines -1);
    lineMarkInsertedLine(index, index +lines -1);
  }
  
  private void deleteLines(int index, int lines) {
    int i = index;
    synchronized (this.lines) {
      int lineCount = this.lines.size();
      while (i < Math.min(index +lines, lineCount)) {
        LineInfo line = this.lines.remove(index);
        line.clear();
        i++;
      }
    }
    bookmarkDeletedLine(index, index +lines -1);
    lineMarkDeletedLine(index, index +lines -1);
  }

  private DocumentListener getDocumentListener() {
    return new DocumentListener() {
      public void changedUpdate(DocumentEvent chng) {
      }
      public void insertUpdate(DocumentEvent chng) {
        DocumentEvent.ElementChange ch = chng.getChange(getDefaultRootElement());
        if (ch != null) {
          insertLines(ch.getIndex() +1, ch.getChildrenAdded().length -ch.getChildrenRemoved().length);
        }
        LineInfo li = getLineInfo(getDefaultRootElement().getElementIndex(chng.getOffset()));
        if (li != null) {
          li.reset();
        }
      }
      public void removeUpdate(DocumentEvent chng) {
        DocumentEvent.ElementChange ch = chng.getChange(getDefaultRootElement());
        if (ch != null) {
          deleteLines(ch.getIndex() +1, ch.getChildrenRemoved().length -ch.getChildrenAdded().length);
        }
        LineInfo li = getLineInfo(getDefaultRootElement().getElementIndex(chng.getOffset()));
        if (li != null) {
          li.reset();
        }
      }
    };
  }

  public int getLineCount() {
    return getDefaultRootElement().getElementCount();
  }
  
  public void setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  public void setTextComponent(SyntaxEditor textComponent) {
    this.textComponent = textComponent;
  }

  public SyntaxEditor getTextComponent() {
    return textComponent;
  }
  
  public void setBookmark(int index, int line, int column) {
    Bookmark bm = bookmarks.get(index);
    if (bm != null) {
      if (bm.getLine() == line) {
        removeBookmark(index);
      }
      else {
        bm.setLine(line);
      }
    }
    else {
      bookmarks.put(index, new Bookmark(index, line, column));
    }
  }
  
  public void removeBookmark(int index) {
    bookmarks.remove(index);
  }
  
  public Bookmark getBookmark(int index) {
    return bookmarks.get(index);
  }
  
  public Bookmark getBookmarkByLine(int line) {
    Iterator<Bookmark> i = bookmarks.values().iterator();
    while (i.hasNext()) {
      Bookmark bm = i.next();
      if (bm.getLine() == line -1) {
        return bm;
      }
    }
    return null;
  }
  
  public void bookmarkDeletedLine(int fromLine, int toLine) {
    Iterator<Bookmark> i = bookmarks.values().iterator();
    while (i.hasNext()) {
      Bookmark bm = i.next();
      if (bm.getLine() >= fromLine) {
        if (bm.getLine() <= toLine -1) {
          i.remove();
        }
        else {
          bm.setLine(bm.getLine() -(toLine -fromLine +1));
        }
      }
    }
  }

  public void bookmarkInsertedLine(int fromLine, int toLine) {
    Iterator<Bookmark> i = bookmarks.values().iterator();
    while (i.hasNext()) {
      Bookmark bm = i.next();
      if (bm.getLine() >= fromLine) {
        bm.setLine(bm.getLine() +(toLine -fromLine +1));
      }
    }
  }

  public void clearLineMark() {
    synchronized (lineMarks) {
      lineMarks.clear();
    }
  }

  public void setLineMark(int line, Color bkColor, ImageIcon markImage) {
    synchronized (lineMarks) {
      if (getLineMark(line) == null) {
        lineMarks.add(new LineMark(line, bkColor, markImage));
      }
    }
  }

  public void setLineMark(LineMark lineMark) {
    synchronized (lineMarks) {
      if (getLineMark(lineMark.getLine()) == null) {
        lineMarks.add(lineMark);
      }
    }
  }

  public void setLineMark(LineMark[] lineMark) {
    if (lineMark != null) {
      synchronized (lineMarks) {
        for (LineMark lm : lineMark) {
          setLineMark(lm);
        }
      }
    }
  }

  public void removeLineMark(LineMark lineMark) {
    synchronized (lineMarks) {
      lineMarks.remove(lineMark);
    }
  }
  
  public LineMark[] getLineMark(int line) {
    synchronized (lineMarks) {
      int count = 0;
      for (LineMark lm : lineMarks) {
        if (lm.getLine() == line -1) {
          count++;
        }
      }
      if (count > 0) {
        LineMark[] result = new LineMark[count];
        count = 0;
        for (LineMark lm : lineMarks) {
          if (lm.getLine() == line -1) {
            result[count] = lm;
            count++;
          }
        }
        return result;
      }
      return null;
    }
  }
  
  public void lineMarkDeletedLine(int fromLine, int toLine) {
    synchronized (lineMarks) {
      Iterator<LineMark> i = lineMarks.iterator();
      while (i.hasNext()) {
        LineMark lm = i.next();
        if (lm.getLine() >= fromLine) {
          if (lm.getLine() <= toLine -1) {
            i.remove();
          }
          else {
            lm.setLine(lm.getLine() -(toLine -fromLine +1));
          }
        }
      }
    }
  }

  public void lineMarkInsertedLine(int fromLine, int toLine) {
    synchronized (lineMarks) {
      Iterator<LineMark> i = lineMarks.iterator();
      while (i.hasNext()) {
        LineMark lm = i.next();
        if (lm.getLine() >= fromLine) {
          lm.setLine(lm.getLine() +(toLine -fromLine +1));
        }
      }
    }
  }

  /**
   * @author Andrzej Ka³u¿a
   * 
   * Klasa elementów linii tekstu
   */
  public static class Token {
    
    public int styleId = NONE;
    public int length = 0;
    public int offset = 0;
    public Token next;
    public Token prev;
    public int waveStyleId = NONE;
    
    public Token() {
      super();
    }

    public Token(int styleId, int length) {
      super();
      this.styleId = styleId;
      this.length = length;
    }
    
    public boolean anyOfStyle(int[] styles) {
      for (int i=0; i<styles.length; i++) {
        if (styles[i] == styleId) {
          return true;
        }
      }
      return false;
    }
    
    public String toString() {
      return "[offset:" +offset +",styleId:" +styleId +",length:" +length +"]";
    }
    
  }
  
  /**
   * @author Andrzej Ka³u¿a
   *
   * Informacje wiersza
   */
  public static class LineInfo {
    
    public boolean refresh = true;
    public ArrayList<Token> tokens = new ArrayList<Token>();
    public int length = 0;
    
    public LineInfo() {
      super();
    }
    
    public void reset() {
      refresh = true;
    }
    
    public void clear() {
      length = 0;
      refresh = true;
      tokens.clear();
    }
    
    public void addToken(Token token) {
      //if (token.length > 0 || tokens.size() > 0) {
        Token prev = (tokens.size() > 0 ? tokens.get(tokens.size() -1) : null);
        token.prev = prev;
        if (prev != null) {
          prev.next = token;
        }
        token.offset = length;
        length += token.length;
        tokens.add(token);
      //}
      refresh = false;
    }
    
    public void addToken(int styleId, int length) {
      addToken(new Token(styleId, length));
    }
    
    public int lastStyleId() {
      if (tokens.size() == 0) {
        return NONE;
      }
      else {
        return tokens.get(tokens.size() -1).styleId;
      }
    }
    
    public int lastTokenId() {
      if (tokens.size() == 0) {
        return NONE;
      }
      else {
        return tokens.get(tokens.size() -1).styleId;
      }
    }
    
    public String buildFromLastTokens(int styleDelimiter, String word, Segment line) {
      String words = "";
      int point = tokens.size();
      while (--point >= 0) {
        Token td = tokens.get(point);
        if (td.styleId != NONE) {
          //System.out.println(td.styleId +":" +getStringAtOffset(td.offset, line));
          if (td.styleId == styleDelimiter && StringUtil.equalAnyOfString(getStringAtOffset(td.offset, line), new String[] {".", "%"})) {
            Token tl = null;
            while (--point >= 0 && (tl = tokens.get(point)).styleId == NONE) ;
            if (point >= 0 && tl != null) {
              words = getStringAtOffset(tl.offset, line) +getStringAtOffset(td.offset, line) +words;
            }
          }
          else {
            return words +word;
          }
        }
      }
      return words +word;
    }
    
    /**
     * <p>Powinna zwróciæ Token dla przekazanego offset-u tekstu dla wybranej linii
     * @param offset od pocz¹tku linii nie tekstu!
     * @return
     */
    public Token getTokenAtOffset(int offset) {
      int toff = 0;
      for (int i=0; i<tokens.size(); i++) {
        if (offset >= toff && offset < toff +tokens.get(i).length) {
          return tokens.get(i);
        } 
        toff += tokens.get(i).length;
      }
      return null;
    }
    
    /**
     * @param offset od pocz¹tku linii nie tekstu!
     * @param segment
     * @return
     */
    public String getStringAtOffset(int offset, Segment segment) {
      int toff = 0;
      for (int i=0; i<tokens.size(); i++) {
        if (tokens.get(i).length > 0 && offset >= toff && offset < toff +Math.max(tokens.get(i).length, 0)) {
          StringBuilder sb = new StringBuilder();
          for (int c=0; c<tokens.get(i).length; c++) {
            if (c +toff +segment.offset < segment.array.length) {
              sb.append(segment.array[c +toff +segment.offset]);
            }
          }
          return sb.toString();
        } 
        toff += tokens.get(i).length;
      }
      return null;
    }
    
    public String toString() {
      return "[refresh:" +(refresh ? "true" : "false") +"," +Arrays.toString(tokens.toArray()) +"]";
    }
  }
  
  public static class Bookmark {
    
    private int index;
    private int line;
    private int column;
    
    public Bookmark(int index, int line, int column) {
      this.index = index;
      this.line = line;
      this.column = column;
    }

    public int getIndex() {
      return index;
    }

    public int getLine() {
      return line;
    }

    public int getColumn() {
      return column;
    }

    public void setLine(int line) {
      this.line = line;
    }
    
  }
  
  public final static class LineMark {
    
    private int line;
    private ImageIcon imageMark;
    private Color bkColor;
    private String hint;
    
    public LineMark(int line, Color bkColor, ImageIcon imageMark, String hint) {
      this.line = line;
      this.imageMark = imageMark;
      this.bkColor = bkColor;
      this.hint = hint;
    }

    public LineMark(int line, Color bkColor, ImageIcon imageMark) {
      this(line, bkColor, imageMark, null);
    }

    public int getLine() {
      return line;
    }

    public void setLine(int line) {
      this.line = line;
    }

    public ImageIcon getImageMark() {
      return imageMark;
    }

    public Color getBkColor() {
      return bkColor;
    }
    
    public String getHint() {
      return hint;
    }
    
  }
  
  private boolean isTrimEmptyLine() {
    return SkySetting.getBoolean(SkySetting.SyntaxEditor_CurrentTextTrimWhitespaces, false);
  }
  
  private String prepareLine(String line) {
    if (isTrimEmptyLine()) {
      return line.trim();
    }
    if (line.length() > 0 && line.charAt(line.length() -1) == '\n') {
      return line.substring(0, line.length() -1);
    }
    return line;
  }
  
  /**
   * <p>Funkcja zwraca zaznaczony tekst lub aktualny fragment tekstu ograniczony
   * pustym wierszem.
   * <p>Mo¿e byæ zast¹piony przez klasy dziedzicz¹ce, które zechc¹ aby funkcja
   * dzia³a³a inaczej.
   * <p>Funkcja z zasady nie powinna zwróciæ null-a, a co najwy¿ej pusty ci¹g znaków.
   * @return
   */
  public String getCurrentText() {
    if (!StringUtil.isEmpty(textComponent.getSelectedText(), true)) {
      startCurrentText = textComponent.getSelectionStart();
      endCurrentText = textComponent.getSelectionEnd();
      return textComponent.getSelectedText();
    }
    String text = textComponent.getText();
    if (StringUtil.isEmpty(text, true)) {
      startCurrentText = 0;
      endCurrentText = 0;
      return "";
    }
    int pos = textComponent.getCaretPosition();
    if (pos == text.length() || text.charAt(pos) == '/' || text.charAt(pos) == '\n') {
      pos--;
    }
    int lineIndex = textComponent.getLineOfOffset(pos);
    int start = textComponent.getLineStartOffset(lineIndex);
    int tend;
    try {
      tend = textComponent.getLineEndOffset(lineIndex);
      String line = prepareLine(textComponent.getText(start, tend -start));
      while (!"".equals(line) && !"/".equals(line.trim()) && start > 0) {
        lineIndex = textComponent.getLineOfOffset(start -1);
        start = textComponent.getLineStartOffset(lineIndex);
        tend = textComponent.getLineEndOffset(lineIndex);
        line = prepareLine(textComponent.getText(start, tend -start));
      }
      if (start > 0) {
        start = tend;
      }
    }
    catch (BadLocationException ex) {
      return "";
    }
    int end = start;
    lineIndex = textComponent.getLineOfOffset(end +1);
    int tstart = textComponent.getLineStartOffset(lineIndex);
    try {
      tend = textComponent.getLineEndOffset(lineIndex);
      String line = prepareLine(textComponent.getText(tstart, tend -tstart));
      while (!"".equals(line) && !"/".equals(line.trim()) && tend < text.length()) {
        lineIndex = textComponent.getLineOfOffset(tend);
        tstart = textComponent.getLineStartOffset(lineIndex);
        tend = textComponent.getLineEndOffset(lineIndex);
        line = prepareLine(textComponent.getText(tstart, tend -tstart));
      }
      end = tend;
      if ("/".equals(line.trim())) {
        end = tstart;
      }
      while (end > 0 && Character.isWhitespace(text.charAt(end -1))) {
        end--;
      }
    }
    catch (BadLocationException ex) {
      return "";
    }
    startCurrentText = start;
    endCurrentText = end;
    if (end > start) {
      return text.substring(start, end).trim();
    }
    return "";
  }
  
  public int getStartCurrentText() {
    return startCurrentText;
  }

  public int getEndCurrentText() {
    return endCurrentText;
  }

  void setStartCurrentText(int startCurrentText) {
    this.startCurrentText = startCurrentText;
  }

  void setEndCurrentText(int endCurrentText) {
    this.endCurrentText = endCurrentText;
  }
  
  /**
   * <p>Zwraca sparsowane LineInfo oraz w segment informacje o wierszu 
   * @param offset
   * @param segment
   * @return
   * @throws BadLocationException
   * @see getWordsAt()
   */
  public LineInfo getLineInfoAtOffset(int offset, Segment segment) throws BadLocationException {
    Element map = getDefaultRootElement();
    return getLineInfoAtLine(map.getElementIndex(offset), segment);
  }
  
  public LineInfo getLineInfoAtLine(int lineIndex, Segment segment) throws BadLocationException {
    synchronized (lines) {
      Element map = getDefaultRootElement();
      Element lineElem = map.getElement(lineIndex);
      LineInfo info = getLineInfo(lineIndex);
      if (info != null) {
        getText(lineElem.getStartOffset(), lineElem.getEndOffset() -lineElem.getStartOffset(), segment);
        if (info.refresh) {
          tokenizeLine(segment, lineIndex);
          info = getLineInfo(lineIndex);
        }
      }
      return info;
    }
  }
  
  /**
   * <p>Zwraca offset pocz¹tku wiersza w którym pozycja kursora wskazuje position
   * @param position
   * @return
   */
  public int getLineOffsetAt(int position) {
    synchronized (lines) {
      Element map = getDefaultRootElement();
      Element lineElem = map.getElement(map.getElementIndex(position));
      return lineElem.getStartOffset();
    }
  }
  
  public int getLineStartOffset(int lineIndex) {
    synchronized (lines) {
      Element map = getDefaultRootElement();
      Element lineElem = map.getElement(lineIndex);
      return lineElem != null ? lineElem.getStartOffset() : 0;
    }
  }
  
  public int getLineEndOffset(int lineIndex) {
    synchronized (lines) {
      Element map = getDefaultRootElement();
      Element lineElem = map.getElement(lineIndex);
      return lineElem != null ? lineElem.getEndOffset() : 0;
    }
  }
  
  public String[] getWordsAt(int off) throws BadLocationException {
    if (textComponent != null && textComponent.getDocument() != null) {
      return getDotWords(textComponent.getText(), off, false, false);
    }
    return null;
  }
  
  public String[] getWordsTo(int off) throws BadLocationException {
    if (textComponent != null && textComponent.getDocument() != null) {
      return getDotWords(textComponent.getText(), off, true, false);
    }
    return null;
  }
  
  public String[] getWordsBeforeBracket(int off) throws BadLocationException {
    if (textComponent != null && textComponent.getDocument() != null) {
      return getDotWords(textComponent.getText(), off, false, true);
    }
    return null;
  }
  
  private void addWordToList(ArrayList<String> wordList, StringBuilder sb, boolean ignoreCase) {
    if (sb.length() > 0) {
      if (ignoreCase) {
        wordList.add(sb.toString().toUpperCase());
      }
      else {
        wordList.add(sb.toString());
      }
      sb.setLength(0);
    }
  }
  
  private String[] getDotWords(String text, int off, boolean toOff, boolean beforeBracket) {
    ArrayList<String> wordList = new ArrayList<String>();
    StringBuilder sb = new StringBuilder();
    int start = findDotWordsStart(text, off, beforeBracket);
    if (start < 0) {
      return null;
    }
    int i = start;
    int length = toOff ? off : text.length();
    int paren = 0;
    int bracket = 0;
    int brace = 0;
    boolean dotPresent = false;
    while (i < length && text.charAt(i) == ' ') {
      i++;
    }
    while (i < length) {
      char ch = text.charAt(i);
      if (paren == 0 && bracket == 0 && brace == 0 && (ch == '"' || ch == '\'' || ch == '`')) {
        dotPresent = false;
        while (i < length -1 && text.charAt(i) != ch) {
          sb.append(text.charAt(i));
          i++;
        }
        addWordToList(wordList, sb, false);
      }
      else if (paren == 0 && bracket == 0 && Character.isJavaIdentifierPart(ch)) {
        dotPresent = false;
        sb.append(ch);
      }
      else if (ch == ')' && paren == 0) { addWordToList(wordList, sb, ignoreCase); break; }
      else if (ch == ']' && bracket == 0) { addWordToList(wordList, sb, ignoreCase); break; }
      else if (ch == '}' && brace == 0) { addWordToList(wordList, sb, ignoreCase); break; }
      else if (ch == '(') { paren++; addWordToList(wordList, sb, ignoreCase); }
      else if (ch == '[') { bracket++; addWordToList(wordList, sb, ignoreCase); }
      else if (ch == '{') { brace++; addWordToList(wordList, sb, ignoreCase); }
      else if (ch == ')') { paren--; addWordToList(wordList, sb, ignoreCase); }
      else if (ch == ']') { bracket--; addWordToList(wordList, sb, ignoreCase); }
      else if (ch == '}') { brace--; addWordToList(wordList, sb, ignoreCase); }
      else if (paren == 0 && bracket == 0 && brace == 0 && !Character.isJavaIdentifierPart(ch)) {
        addWordToList(wordList, sb, ignoreCase);
        while (i < length && text.charAt(i) == ' ') {
          i++;
        }
        if (i < length && text.charAt(i) == '.') {
          i++;
          while (i < length && text.charAt(i) == ' ') {
            i++;
          }
          i--;
          dotPresent = true;
        }
        else {
          i--;
          while (i >= 0 && text.charAt(i) == ' ') {
            i--;
          }
          break;
        }
      }
      i++;
    }
    if (dotPresent) {
      wordList.add("");
    }
    else {
      addWordToList(wordList, sb, ignoreCase);
    }
    return wordList.toArray(new String[wordList.size()]);
  }
  
  private int findDotWordsStart(String text, int off, boolean beforeBracket) {
    wordsCommaCount = 0;
    int roundBracket = (beforeBracket ? 1 : 0);
    int squerBracket = 0;
    int n = off -1;
    int i = n;
    while (i >= 0) {
      char ch = text.charAt(i);
      if (roundBracket == (beforeBracket ? 1 : 0) && squerBracket == 0 && (ch == '"' || ch == '\'' || ch == '`')) {
        while (i >= 0 && text.charAt(i) != ch) {
          i--;
        }
      }
      else if (ch == ',' && roundBracket == 1) { wordsCommaCount++; }
      else if (ch == '(' && roundBracket == 0) { break; }
      else if (ch == '[' && squerBracket == 0) { break; }
      else if (ch == ')') { roundBracket++; }
      else if (ch == ']') { squerBracket++; }
      else if (ch == '(') { roundBracket--; }
      else if (ch == '[') { squerBracket--; }
      else if (roundBracket == 0 && squerBracket == 0 && !Character.isJavaIdentifierPart(ch)) {
        while (i >= 0 && text.charAt(i) == ' ') {
          i--;
        }
        if (i >= 0 && text.charAt(i) == '.') {
          i--;
          while (i >= 0 && text.charAt(i) == ' ') {
            i--;
          }
          i++;
        }
        else {
          while (i < text.length() -1 && text.charAt(i +1) == ' ') {
            i++;
          }
          break;
        }
      }
      i--;
    }
    if (i <= n && roundBracket == 0) {
      //System.out.println(text.substring(i + 1, n + 1));
      return i +1;
    }
    return -1;
  }

  public int getWordsCommaCount() {
    return wordsCommaCount;
  }
  
  public void clearSnippets() {
    snippets.clear();
    defaultSnippet();
  }

  public void addSnippet(Snippet snippet) {
    snippets.put(snippet.getName().toLowerCase(), snippet);
  }

  public Snippet getSnippet(String name) {
    if (name != null) {
      return snippets.get(name.toLowerCase());
    }
    return null;
  }
  
  /**
   * @param namePart
   * @return
   *   0 if not found
   *   snippet name.length() if is partial or found
   */
  public int partialSnippet(String namePart) {
    if (namePart != null) {
      namePart = namePart.toLowerCase();
      for (Snippet snippet : snippets.values()) {
        if (snippet != null && snippet.isActive() && (!snippet.isPreDefined() || SkySetting.getBoolean(SkySetting.SyntaxEditor_PreDefinedSnippets, true)) && snippet.isImmediate()) {
          if (snippet.getName().toLowerCase().startsWith(namePart)) {
            return snippet.getName().length();
          }
        }
      }
    }
    return 0;
  }
  
  public Snippet applySnippet(String name, boolean immediate) {
    try {
      if (immediate) {
        // checking if is not any of ignore tokent
        List<TokenRef> trl = textComponent.getTokens(textComponent.getCaretPosition(), textComponent.getCaretPosition());
        if (trl.size() == 1 && trl.get(0).anyOfStyle(ignoreTokentForImmediateSnippet)) {
          return null;
        }
        // checking if caret is not at identifier char
        String ch = getText(textComponent.getCaretPosition(), 1);
        if (ch != null && ch.length() > 0 && Character.isUnicodeIdentifierPart(ch.charAt(0))) {
          return null;
        }
      }
      if (name == null) {
        List<TokenRef> trl = textComponent.getTokens(textComponent.getCaretPosition() -1, textComponent.getCaretPosition() -1);
        if (trl.size() == 1 && trl.get(0).token != null) {
          Snippet snippet = getSnippet(trl.get(0).token);
          if (snippet != null && snippet.isActive() && (!snippet.isPreDefined() || SkySetting.getBoolean(SkySetting.SyntaxEditor_PreDefinedSnippets, true)) && snippet.isImmediate() == immediate) {
            int offset = textComponent.getCaretPosition() -snippet.getName().length();
            int padd = offset -textComponent.getLineStartOffset(textComponent.getLineOfOffset(offset));
            textComponent.replaceRange(snippet.getExpandedCode(padd), offset, offset +snippet.getName().length());
            textComponent.setCaretPosition(offset +snippet.getOffset(), true);
            return snippet;
          }
        }
      }
      else {
        Snippet snippet = getSnippet(name);
        if (snippet != null && snippet.isActive() && (!snippet.isPreDefined() || SkySetting.getBoolean(SkySetting.SyntaxEditor_PreDefinedSnippets, true)) && snippet.isImmediate() == immediate) {
          int offset = textComponent.getCaretPosition() -snippet.getName().length() +1;
          int padd = offset -textComponent.getLineStartOffset(textComponent.getLineOfOffset(offset));
          if (textComponent.getSelectionEnd() != textComponent.getSelectionStart()) {
            offset = textComponent.getSelectionStart();
            textComponent.replaceSelection(snippet.getExpandedCode(padd));
          }
          else {
            textComponent.replaceRange(snippet.getExpandedCode(padd), offset, offset +snippet.getName().length() -1);
          }
          textComponent.setCaretPosition(offset +snippet.getOffset(), true);
          return snippet;
        }
      }
    } catch (BadLocationException e1) {
      ExceptionUtil.processException(e1);
    }
    return null;
  }

  protected int[] getIgnoreTokentForImmediateSnippet() {
    return ignoreTokentForImmediateSnippet;
  }

  protected void setIgnoreTokentForImmediateSnippet(int[] ignoreTokentForImmediateSnippet) {
    this.ignoreTokentForImmediateSnippet = ignoreTokentForImmediateSnippet;
  }
  
}
