package pl.mpak.sky.gui.swing.syntax;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;
import javax.swing.undo.UndoableEdit;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.AutoCompleteText;
import pl.mpak.sky.gui.swing.DefaultUndoManager;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.actions.CmCopyEdit;
import pl.mpak.sky.gui.swing.comp.actions.CmPasteEdit;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.LineInfo;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.LineMark;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.Token;
import pl.mpak.sky.gui.swing.syntax.actions.CmAutoComplete;
import pl.mpak.sky.gui.swing.syntax.actions.CmAutoIndent;
import pl.mpak.sky.gui.swing.syntax.actions.CmCommentUncommentSelected;
import pl.mpak.sky.gui.swing.syntax.actions.CmEatLeftWord;
import pl.mpak.sky.gui.swing.syntax.actions.CmEatLine;
import pl.mpak.sky.gui.swing.syntax.actions.CmEatRightWord;
import pl.mpak.sky.gui.swing.syntax.actions.CmFindNextText;
import pl.mpak.sky.gui.swing.syntax.actions.CmFindPrevText;
import pl.mpak.sky.gui.swing.syntax.actions.CmFindText;
import pl.mpak.sky.gui.swing.syntax.actions.CmGoDownElement;
import pl.mpak.sky.gui.swing.syntax.actions.CmGoUpElement;
import pl.mpak.sky.gui.swing.syntax.actions.CmGotoBookmark;
import pl.mpak.sky.gui.swing.syntax.actions.CmGotoLine;
import pl.mpak.sky.gui.swing.syntax.actions.CmMoveToNextWord;
import pl.mpak.sky.gui.swing.syntax.actions.CmMoveToPreviousWord;
import pl.mpak.sky.gui.swing.syntax.actions.CmOvertypeMode;
import pl.mpak.sky.gui.swing.syntax.actions.CmProgressiveLineSearch;
import pl.mpak.sky.gui.swing.syntax.actions.CmScrollDown;
import pl.mpak.sky.gui.swing.syntax.actions.CmScrollUp;
import pl.mpak.sky.gui.swing.syntax.actions.CmSelectCurrentText;
import pl.mpak.sky.gui.swing.syntax.actions.CmSelectNextTextPart;
import pl.mpak.sky.gui.swing.syntax.actions.CmSelectPreviousTextPart;
import pl.mpak.sky.gui.swing.syntax.actions.CmSelectWord;
import pl.mpak.sky.gui.swing.syntax.actions.CmSetBookmark;
import pl.mpak.sky.gui.swing.syntax.actions.CmShiftSelectedLeft;
import pl.mpak.sky.gui.swing.syntax.actions.CmShiftSelectedRight;
import pl.mpak.sky.gui.swing.syntax.actions.CmShowParams;
import pl.mpak.sky.gui.swing.syntax.actions.CmSmartEnd;
import pl.mpak.sky.gui.swing.syntax.actions.CmSmartHome;
import pl.mpak.sky.gui.swing.syntax.actions.CmStructureElementSelect;
import pl.mpak.sky.gui.swing.syntax.actions.CmTab;
import pl.mpak.sky.gui.swing.syntax.actions.CmTabBackward;
import pl.mpak.sky.gui.swing.syntax.actions.ShowElementTreeAction;
import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.ParserException;
import pl.mpak.sky.gui.swing.syntax.structure.StructureAutoCompleteListener;
import pl.mpak.sky.gui.swing.syntax.structure.StructureParser;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.timer.TimerManager;

public class SyntaxEditor extends JTextArea implements TabExpander, Closeable {
  private static final long serialVersionUID = -2212208481742442037L;
  
  private static int BRACKET_FIND_COUNT = 8000;

  private static final char cParen[] = { '(', ')' };
  private static final char cBracket[] = { '[', ']' };
  private static final char cBrace[] = { '{', '}' };
  private Caret defaultCaret;
  private Caret overtypeCaret;
  private boolean overtypeMode;
  private transient DocumentListener documentListener = null;
  private boolean showEndOfLine = false;
  private transient CaretListener caretListener = null;
  private int bracketStart = -1;
  private int bracketEnd = -1;
  private boolean printMargin = false;
  private int printMarginColumn = 80;
  private EventListenerList lineCountListenerList = new EventListenerList();
  private int oldLineCount = -1;
  protected DefaultUndoManager undoManager;
  private boolean changed;
  private CmShiftSelectedRight shiftSelectedRight;
  private CmShiftSelectedLeft shiftSelectedLeft;
  private CmCommentUncommentSelected commentUncommentSelected;
  private CmGotoLine gotoLine;
  private CmProgressiveLineSearch progressiveLineSearch;
  private CmFindText findText;
  private CmFindNextText findNextText;
  private CmFindPrevText findPrevText;
  CmStructureElementSelect structureElementSelect;
  private CmGoUpElement cmGoUpElement;
  private CmGoDownElement cmGoDownElement;
  private CmScrollUp cmScrollUp;
  private CmScrollDown cmScrollDown;
  private CmAutoComplete cmAutoComplete;
  private CmShowParams cmShowParams;
  private CmSelectCurrentText cmSelectCurrentText;
  private CommentSpec[] commentSpecs;
  private String[] selectedWords;
  private volatile Timer timerStructureParse;
  private volatile Timer timerCaretPos;
  private volatile StructureParser structureParser;
  private volatile BlockElement structure;
  private volatile AutoCompleteText autoComplete;
  private StructureAutoCompleteListener structureCompleteListener;
  Map<?, ?> desktopHints;

  SyntaxEditor() {
    super();
    init();
  }

  SyntaxEditor(SyntaxDocument doc) {
    super();
    setDocument(doc);
    init();
  }

  private void init() {
    Toolkit tk = Toolkit.getDefaultToolkit();
    desktopHints = (Map<?, ?>) (tk.getDesktopProperty("awt.font.desktophints"));

    autoComplete = new AutoCompleteText(this);

    setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    setTabSize(2);
    setShowEndOfLine(true);
    setOpaque(true);

    addCaretListener(getCaretListener());
    defaultCaret = getCaret();
    overtypeCaret = new OvertypeCaret();
    overtypeCaret.setBlinkRate(defaultCaret.getBlinkRate());

    setRequestFocusEnabled(true);

    SwingUtil.addAction(this, new CmMoveToPreviousWord(this));
    SwingUtil.addAction(this, new CmMoveToNextWord(this));
    SwingUtil.addAction(this, new CmSelectPreviousTextPart(this));
    SwingUtil.addAction(this, new CmSelectNextTextPart(this));
    SwingUtil.addAction(this, new CmSelectWord(this));
    SwingUtil.addAction(this, new CmSmartHome(this));
    SwingUtil.addAction(this, new CmSmartEnd(this));

    SwingUtil.addAction(this, new CmAutoIndent(this));
    SwingUtil.addAction(this, new CmEatLeftWord(this));
    SwingUtil.addAction(this, new CmEatRightWord(this));
    SwingUtil.addAction(this, new CmEatLine(this));
    SwingUtil.addAction(this, new CmCopyEdit(this) {
      private static final long serialVersionUID = 1L;
      {
        setShortCut(KeyEvent.VK_INSERT, KeyEvent.CTRL_MASK);
      }
    });
    SwingUtil.addAction(this, new CmPasteEdit(this) {
      private static final long serialVersionUID = 1L;
      {
        setShortCut(KeyEvent.VK_INSERT, KeyEvent.SHIFT_MASK);
      }
    });
    getInputMap(JComponent.WHEN_FOCUSED).put(
        getUndoManager().getCmUndoEdit().getAltShortCut(),
        getUndoManager().getCmUndoEdit().getActionCommandKey());
    SwingUtil.addAction(this, gotoLine = new CmGotoLine(this));
    SwingUtil.addAction(this, progressiveLineSearch = new CmProgressiveLineSearch(this));
    SwingUtil.addAction(this, findText = new CmFindText(this));
    SwingUtil.addAction(this, findNextText = new CmFindNextText(this));
    SwingUtil.addAction(this, findPrevText = new CmFindPrevText(this));
    SwingUtil.addAction(this, new CmTab(this));
    SwingUtil.addAction(this, new CmTabBackward(this));
    SwingUtil.addAction(this, cmScrollUp = new CmScrollUp(this));
    SwingUtil.addAction(this, cmScrollDown = new CmScrollDown(this));
    SwingUtil.addAction(this,structureElementSelect = new CmStructureElementSelect(this));
    SwingUtil.addAction(this, cmGoUpElement = new CmGoUpElement(this));
    SwingUtil.addAction(this, cmGoDownElement = new CmGoDownElement(this));
    structureElementSelect.setEnabled(false);

    SwingUtil.addAction(this, shiftSelectedRight = new CmShiftSelectedRight(this));
    SwingUtil.addAction(this, shiftSelectedLeft = new CmShiftSelectedLeft(this));
    SwingUtil.addAction(this, commentUncommentSelected = new CmCommentUncommentSelected(this));

    SwingUtil.addAction(this, new CmSetBookmark(this, 1));
    SwingUtil.addAction(this, new CmSetBookmark(this, 2));
    SwingUtil.addAction(this, new CmSetBookmark(this, 3));
    SwingUtil.addAction(this, new CmSetBookmark(this, 4));
    SwingUtil.addAction(this, new CmSetBookmark(this, 5));
    SwingUtil.addAction(this, new CmSetBookmark(this, 6));
    SwingUtil.addAction(this, new CmSetBookmark(this, 7));
    SwingUtil.addAction(this, new CmSetBookmark(this, 8));
    SwingUtil.addAction(this, new CmSetBookmark(this, 9));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 1));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 2));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 3));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 4));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 5));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 6));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 7));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 8));
    SwingUtil.addAction(this, new CmGotoBookmark(this, 9));

    SwingUtil.addAction(this, new CmOvertypeMode(this));
    SwingUtil.addAction(this, new ShowElementTreeAction(this));
    SwingUtil.addAction(this, cmAutoComplete = new CmAutoComplete(this));
    SwingUtil.addAction(this, cmShowParams = new CmShowParams(this));
    SwingUtil.addAction(this, cmSelectCurrentText = new CmSelectCurrentText(
        this));

    addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
      }

      @Override
      public void focusGained(FocusEvent e) {
        if (!isEditable() && !getCaret().isVisible()) {
          getCaret().setVisible(true);
        }
      }
    });
    addKeyListener(new KeyListener() {
      String name = "";
      Snippet lastSnippet = null;
      @Override
      public void keyTyped(KeyEvent e) {
        if (getDocument() instanceof SyntaxDocument) {
          if (e.getKeyChar() == '\b') {
            if (lastSnippet != null) {
              int cp = getCaretPosition() -lastSnippet.getOffset() +1;
              replaceRange("", cp, cp +lastSnippet.getLength() -1);
              setCaretPosition(cp);
              lastSnippet = null;
              e.consume();
            }
            else if (name.length() > 0) {
              name = name.substring(0, name.length() -1);
            }
          }
          else {
            SyntaxDocument sd = (SyntaxDocument)SyntaxEditor.this.getDocument();
            name = name +e.getKeyChar();
            if (lastSnippet != null) {
              if (lastSnippet.getCodeAtCursor().startsWith(name)) {
                setCaretPosition(getCaretPosition() +1);
                e.consume();
                if (lastSnippet.getCodeAtCursor().length() == name.length()) {
                  lastSnippet = null;
                  name = "";
                }
                return;
              }
              lastSnippet = null;
            }
            int l = sd.partialSnippet(name);
            if (l == 0) {
              name = "";
            }
            else if (l == name.length()) {
              lastSnippet = sd.applySnippet(name, true);
              if (lastSnippet != null) {
                e.consume();
              }
              name = "";
            }
          }
        }
      }
      @Override
      public void keyReleased(KeyEvent e) {
      }
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.isActionKey()) {
          name = "";
          lastSnippet = null;
        }
      }
    });
  }

  protected void finalize() throws Throwable {
    super.finalize();
  }

  public void close() throws IOException {
    removeCaretListener(getCaretListener());
    if (timerStructureParse != null) {
      timerStructureParse.cancel();
      timerStructureParse = null;
    }
    if (timerCaretPos != null) {
      timerCaretPos.cancel();
      timerCaretPos = null;
    }
    undoManager.setDocument(null);
    undoManager = null;
    autoComplete.removeAutoCompleteListener(getStructureAutoCompleteListener());
    getStructureAutoCompleteListener().setEditor(null);
    structureCompleteListener = null;
    autoComplete.setTextComponent(null);
    autoComplete = null;
    structureParser.setTokenList(null);
    structureParser = null;
    getDocument().removeDocumentListener(documentListener);
    if (getDocument() instanceof SyntaxDocument) {
      ((SyntaxDocument) getDocument()).setTextComponent(null);
    }
    super.setDocument(null);
    documentListener = null;
  }

  public void setComponentPopupMenu(JPopupMenu popup) {
    super.setComponentPopupMenu(popup);
    if (popup != null) {
      popup.insert(getUndoManager().getCmUndoEdit(), 0);
      popup.insert(new JPopupMenu.Separator(), 1);
      popup.addSeparator();
      popup.add(cmSelectCurrentText);
      popup.add(shiftSelectedRight);
      popup.add(shiftSelectedLeft);
      popup.add(commentUncommentSelected);
      popup.addSeparator();
      popup.add(findText);
      popup.add(findNextText);
      popup.add(findPrevText);
      popup.addSeparator();
      popup.add(gotoLine);
      popup.add(progressiveLineSearch);
      popup.add(structureElementSelect);
      JMenu navi = new JMenu(Messages.getString("SyntaxEditor.navi-menu-text")); //$NON-NLS-1$
      navi.add(cmScrollUp);
      navi.add(cmScrollDown);
      navi.addSeparator();
      navi.add(cmGoUpElement);
      navi.add(cmGoDownElement);
      popup.add(navi);
      popup.add(cmAutoComplete);
      popup.add(cmShowParams);
    }
  }

  private DocumentListener getDocumentListener() {
    if (documentListener == null) {
      documentListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          if (oldLineCount != getLineCount()) {
            fireLineCountListener();
            oldLineCount = getLineCount();
          }
          updateStructure();
          setChanged(true);
        }

        public void removeUpdate(DocumentEvent e) {
          if (oldLineCount != getLineCount()) {
            fireLineCountListener();
            oldLineCount = getLineCount();
          }
          updateStructure();
          setChanged(true);
        }

        public void changedUpdate(DocumentEvent e) {
        }
      };
    }
    return documentListener;
  }

  public void addLineCountListener(TextAreaLineCountListener listener) {
    lineCountListenerList.add(TextAreaLineCountListener.class, listener);
  }

  public void removeLineCountListener(TextAreaLineCountListener listener) {
    lineCountListenerList.remove(TextAreaLineCountListener.class, listener);
  }

  public void fireLineCountListener() {
    TextAreaLineCountListener[] listeners = lineCountListenerList.getListeners(TextAreaLineCountListener.class);
    TextAreaLineCountEvent event = new TextAreaLineCountEvent(this);
    for (int i = 0; i < listeners.length; i++) {
      listeners[i].lineCountChange(event);
    }
  }

  public int getLineOfOffset(int offset) {
    try {
      return super.getLineOfOffset(offset);
    } catch (BadLocationException e) {
      return -1;
    }
  }

  public int getLineStartOffset(int line) {
    try {
      return super.getLineStartOffset(line);
    } catch (BadLocationException e) {
      return 0;
    }
  }

  public int getLineEndOffset(int line) {
    try {
      return super.getLineEndOffset(line);
    } catch (BadLocationException e) {
      return 0;
    }
  }
  
  public Timer getTimerCaretPos() {
    if (timerCaretPos == null) {
      timerCaretPos = new Timer(500) {
        @Override
        public void run() {
          java.awt.EventQueue.invokeLater(new Runnable() {
            int lastLine = -1;
            public void run() {
              timerCaretPos.setEnabled(false);
              SyntaxDocument document = (SyntaxDocument) getDocument();
              boolean refresh = false;
              if (document.bracketSelectionActive()) {
                refresh = updateBrackets();
              }
              if (document.symbolSelectionActive()) {
                refresh = updateSelectedWord() || refresh;
              }
              if (document.lineSelectionActive()) {
                refresh = (lastLine != getLineOfOffset(getCaretPosition())) || refresh;
              }
              if (refresh) {
                repaint();
              }
              lastLine = getLineOfOffset(getCaretPosition());
            }
          });
        }
      };
      TimerManager.getTimer("syntax-editor").add(timerCaretPos);
    }
    return timerCaretPos;
  }

  public CaretListener getCaretListener() {
    if (caretListener == null) {
      caretListener = new CaretListener() {
        boolean caretUpdating = false;
        public void caretUpdate(final CaretEvent e) {
          caretUpdating = true;
          try {
            bracketStart = -1;
            bracketEnd = -1;
            getTimerCaretPos().restart();
            SyntaxEditor.this.caretUpdate(e);
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                if (!caretUpdating) {
                  repaint();
                }
              }
            });
          }
          finally {
            caretUpdating = false;
          }
        }
      };
    }
    return caretListener;
  }

  private boolean updateSelectedWord() {
    boolean refresh = false;
    try {
      String[] words = getWordsAt(getCaretPosition());
      if (words != null) {
        for (int i = 0; i < words.length; i++) {
          words[i] = words[i].trim();
        }
      }
      if (words != null && selectedWords != null) {
        refresh = !Arrays.equals(words, selectedWords);
      } else if (words != null || selectedWords != null) {
        refresh = true;
      }
      selectedWords = words;
    } catch (BadLocationException e) {
      selectedWords = null;
    }
    return refresh;
  }

  protected boolean updateBrackets() {
    int start = getCaretPosition();
    int end;
    int bracketCount = 0;
    int parenCount = 0;
    int braceCount = 0;
    char strChar = 0;
    char ch;
    String text = getText();
    boolean refresh = false;
    int counter = 0;
    char[] bracket = null;

    if (start > 0 && start < text.length() && (text.charAt(start) == cParen[1] || text.charAt(start) == cBracket[1] || text.charAt(start) == cBrace[1])) {
      start--;
    }
    int startCaret = start;
    while (start >= 0 && counter < BRACKET_FIND_COUNT +1 && start < text.length()) {
      ch = text.charAt(start);
      if ((ch == '\'' || ch == '"') && strChar == 0) {
        strChar = ch;
      } else if (ch == strChar) {
        strChar = 0;
      } else if (strChar == 0) {
        if (ch == cParen[0] && parenCount == 0) {
          bracket = cParen;
          break;
        } else if (ch == cBracket[0] && bracketCount == 0) {
          bracket = cBracket;
          break;
        } else if (ch == cBrace[0] && braceCount == 0) {
          bracket = cBrace;
          break;
        } else if (ch == cParen[1]) {
          parenCount++;
        } else if (ch == cParen[0]) {
          parenCount--;
        } else if (ch == cBracket[1]) {
          bracketCount++;
        } else if (ch == cBracket[0]) {
          bracketCount--;
        } else if (ch == cBrace[1]) {
          braceCount++;
        } else if (ch == cBrace[0]) {
          braceCount--;
        }
      }
      start--;
      counter++;
    }

    if (bracket != null) {
      end = startCaret + 1;
      bracketCount = 0;
      strChar = 0;
      counter = 0;
      while (end < text.length() && counter < BRACKET_FIND_COUNT) {
        ch = text.charAt(end);
        if ((ch == '\'' || ch == '"') && strChar == 0) {
          strChar = ch;
        } else if (ch == strChar) {
          strChar = 0;
        } else if (strChar == 0) {
          if (ch == bracket[1] && bracketCount == 0) {
            break;
          } else if (ch == bracket[0]) {
            bracketCount++;
          } else if (ch == bracket[1]) {
            bracketCount--;
          }
        }
        end++;
        counter++;
      }
      if (end < text.length() && bracket != null && counter < BRACKET_FIND_COUNT +1) {
        if (bracketStart != start || bracketEnd != end) {
          bracketStart = start;
          bracketEnd = end;
          refresh = true;
        }
      } else {
        refresh = bracketStart != -1 || bracketEnd != -1;
        bracketStart = -1;
        bracketEnd = -1;
      }
    } else {
      refresh = bracketStart != -1 || bracketEnd != -1;
      bracketStart = -1;
      bracketEnd = -1;
    }
    return refresh;
  }

  protected void documentChanged(DocumentEvent evt) {
    DocumentEvent.ElementChange ch = evt.getChange(getDocument()
        .getDefaultRootElement());

    int count;
    if (ch == null) {
      count = 0;
    } else {
      count = ch.getChildrenAdded().length - ch.getChildrenRemoved().length;
    }

    int line = getLineOfOffset(evt.getOffset());
    if (count == 0) {
      invalidateLine(line);
    } else {
      repaint();
    }
  }

  public void setDocument(Document doc) {
    if (doc != getDocument()) {
      if (getDocument() != null) {
        getDocument().removeDocumentListener(getDocumentListener());
        if (getDocument() instanceof SyntaxDocument) {
          ((SyntaxDocument) getDocument()).setTextComponent(null);
        }
      }
      super.setDocument(doc);
      getUndoManager().setDocument(doc);
      if (getDocument() != null) {
        if (getDocument() instanceof SyntaxDocument) {
          ((SyntaxDocument) getDocument()).setTextComponent(this);
        }
        getDocument().addDocumentListener(getDocumentListener());
        if (getDocument() instanceof SyntaxDocument) {
          ((SyntaxDocument) getDocument()).setTextComponent(this);
        }
      }
    }
  }

  public DefaultUndoManager getUndoManager() {
    if (undoManager == null) {
      undoManager = new DefaultUndoManager(getDocument()) {
        private static final long serialVersionUID = -1L;

        public void undoPerformed(UndoableEdit edit) {
          SyntaxEditor.this.undoPerformed(edit);
        }

        public void redoPerformed(UndoableEdit edit) {
          SyntaxEditor.this.redoPerformed(edit);
        }
      };
    }
    return undoManager;
  }

  public final void invalidateLine(int line) {
    FontMetrics fm = getGraphics().getFontMetrics();
    repaint(0, lineToY(line) + fm.getMaxDescent() + fm.getLeading(),
        getWidth(), fm.getHeight());
  }

  public final void invalidateLineRange(int firstLine, int lastLine) {
    FontMetrics fm = getGraphics().getFontMetrics();
    repaint(0, lineToY(firstLine) + fm.getMaxDescent() + fm.getLeading(),
        getWidth(), (lastLine - firstLine + 1) * fm.getHeight());
  }

  protected int lineToY(int line) {
    FontMetrics fm = getGraphics().getFontMetrics();
    return (line + 1) * getRowHeight() - (fm.getLeading() + fm.getMaxDescent());
  }

  Rectangle getUIBounds() {
    Rectangle bounds;
    try {
      bounds = getUI().modelToView(this, 0);
    } catch (BadLocationException e) {
      bounds = getBounds();
    }
    return bounds;
  }

  protected void paintLineHighligh(Graphics2D g, Segment line, int x, int index) {
    Rectangle bounds = getUIBounds();
    int ssel = Math.min(getSelectionStart(), getSelectionEnd());
    int esel = Math.max(getSelectionStart(), getSelectionEnd());
    int y = index * getRowHeight() + bounds.y;
    int loffset = getLineStartOffset(index);

    Color c = g.getColor();
    Color fc = getSelectionColor();
    g.setColor(new Color(fc.getRed(), fc.getGreen(), fc.getBlue(), 230));
    if (ssel >= loffset && ssel <= loffset + line.count - 1) {
      ssel = (ssel - loffset) * getColumnWidth();
      if (esel >= loffset && esel <= loffset + line.count - 1) {
        esel = (esel - loffset) * getColumnWidth();
        g.fillRect(x + ssel, y, esel - ssel, getRowHeight());
      } else {
        esel = (line.count - 1) * getColumnWidth();
        g.fillRect(x + ssel, y, esel - ssel, getRowHeight());
      }
    } else if (esel >= loffset && esel <= loffset + line.count - 1) {
      esel = (esel - loffset) * getColumnWidth();
      g.fillRect(x, y, esel, getRowHeight());
    } else if (loffset >= ssel && loffset + line.count - 1 <= esel) {
      g.fillRect(x, y, (line.count - 1) * getColumnWidth(), getRowHeight());
    }
    g.setColor(c);
  }

  protected void paintBracketHighligh(Graphics g, Segment line, int x, int index) {
    Rectangle bounds = getUIBounds();
    SyntaxDocument document = ((SyntaxDocument) getDocument());
    if (document.getStyle(SyntaxDocument.BRACKET_COLOR, false).isEnabled()) {
      int loffset = getLineStartOffset(index);
      int y = index * getRowHeight() + bounds.y;
      if (bracketStart >= loffset && bracketStart <= loffset + line.count - 1) {
        g.setColor(document.getStyle(SyntaxDocument.BRACKET_COLOR, false)
            .getForeground());
        g.fillRect(x + ((bracketStart - loffset) * getColumnWidth()), y,
            getColumnWidth() - 1, getRowHeight() - 1);
        g.setColor(Color.GRAY);
        g.drawRect(x + ((bracketStart - loffset) * getColumnWidth()), y,
            getColumnWidth() - 1, getRowHeight() - 1);
      }
      if (bracketEnd >= loffset && bracketEnd <= loffset + line.count - 1) {
        g.setColor(document.getStyle(SyntaxDocument.BRACKET_COLOR, false)
            .getForeground());
        g.fillRect(x + ((bracketEnd - loffset) * getColumnWidth()), y,
            getColumnWidth() - 1, getRowHeight() - 1);
        g.setColor(Color.GRAY);
        g.drawRect(x + ((bracketEnd - loffset) * getColumnWidth()), y,
            getColumnWidth() - 1, getRowHeight() - 1);
      }
    }
  }

  protected void paintSyntaxLine(Graphics g, Segment line, int x, int index) {
    Rectangle bounds = getUIBounds();
    SyntaxDocument document = ((SyntaxDocument) getDocument());
    SyntaxDocument.LineInfo info = document.getLineInfo(index);
    // int ssel = Math.min(getSelectionStart(), getSelectionEnd());
    // int esel = Math.max(getSelectionStart(), getSelectionEnd());

    if (info == null) {
      return;
    }

    x += bounds.x;
    int y = lineToY(index) + bounds.y;
    FontMetrics fm = g.getFontMetrics();

    if (info.refresh) {
      document.tokenizeLine(line, index);
      info = document.getLineInfo(index);
    }

    LineMark[] lma = document.getLineMark(index + 2);
    if (lma != null) {
      for (LineMark lm : lma) {
        if (lm.getBkColor() != null) {
          g.setColor(lm.getBkColor());
          g.fillRect(x, index * getRowHeight() + bounds.y, getWidth(), getRowHeight());
          break;
        }
      }
    }

    if (document.bracketSelectionActive()) {
      paintBracketHighligh(g, line, x, index);
    }

    if (document.lineSelectionActive() && index == getLineOfOffset(getCaretPosition())) {
      Color c = document.getStyle(SyntaxDocument.SELECTED_LINE).getForeground();
      g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
      g.fillRect(x, index * getRowHeight() + bounds.y, getWidth(), getRowHeight());
    }

    if (selectedWords != null && document.symbolSelectionActive()) {
      Segment wordLine = (Segment) line.clone();
      int wordX = x;
      Color[] wordsColor = {
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_1, false).getForeground(180),
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_2, false).getForeground(180),
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_3, false).getForeground(180),
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_4, false).getForeground(180) };
      Boolean[] wordsColorEnabled = {
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_1, false).isEnabled(),
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_2, false).isEnabled(),
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_3, false).isEnabled(),
          document.getStyle(SyntaxDocument.SYMBOL_SELECTION_COLOR_4, false).isEnabled() };
      for (SyntaxDocument.Token token : info.tokens) {
        int length = token.length;
        wordLine.count = length;
        if (length > 0) {
          String word = wordLine.toString();
          int iWord = StringUtil.anyOfString(word, selectedWords, document.isIgnoreCase());
          if (iWord >= 0 && wordsColorEnabled[iWord % 4]) {
            g.setColor(wordsColor[iWord % 4]);
            g.fillRect(wordX, index * getRowHeight() + bounds.y, getColumnWidth() * word.length(), getRowHeight());
          }
          wordX += fm.stringWidth(word);
        }
        wordLine.offset += length;
      }
    }

    if (getSelectionStart() != getSelectionEnd()) {
      paintLineHighligh((Graphics2D) g, line, x, index);
    }

    SyntaxStyle style;
    for (SyntaxDocument.Token token : info.tokens) {
      int length = token.length;
      line.count = length;
      // System.out.println(token);
      if (length > 0) {
        style = document.getStyle(token.styleId, true);
        style.updateGraphics(g);
        try {
          x = Utilities.drawTabbedText(line, x, y, g, this, 0);
        } catch (Throwable t) {
          // TODO Gdzieœ tu jest babol ale jeszcze nie wiem gdzie!
        }
        if (style.isStrickeout()) {
          int yline = y - (getRowHeight() / 2) + fm.getMaxDescent() + fm.getLeading();
          g.drawLine(x - line.count * getColumnWidth(), yline, x, yline);
        }
        if (token.waveStyleId != SyntaxDocument.NONE) {
          style = document.getStyle(token.waveStyleId, true);
          style.updateGraphics(g);
          int yline = y + fm.getMaxDescent() + fm.getLeading() - 1;
          int xx = x - line.count * getColumnWidth();
          while (xx +3 < x) {
            g.drawLine(xx +0, yline +0, xx +2, yline +2);
            g.drawLine(xx +2, yline +2, xx +4, yline +0);
            xx+=5;
          }
        }
        else if (style.isUnderline()) {
          int yline = y + fm.getMaxDescent() + fm.getLeading() - 1;
          g.drawLine(x - line.count * getColumnWidth(), yline, x, yline);
        }
      }
      line.offset += length;
    }
    if (showEndOfLine && document.getStyle(SyntaxDocument.END_OF_LINE, false).isEnabled()) {
      document.getStyle(SyntaxDocument.END_OF_LINE, false).updateGraphics(g);
      g.drawString("\u00b7", x, y); //$NON-NLS-1$
    }
  }

  protected void paintBackground(Graphics g) {
    Rectangle clipRect = g.getClipBounds();
    g.setColor(getBackground());
    g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

    if (printMargin) {
      SyntaxDocument document = ((SyntaxDocument) getDocument());
      SyntaxStyle style = document.getStyle(SyntaxDocument.GUTTER_LINE_NUMBER,
          true);
      g.setColor(style.getForeground());
      g.drawLine(printMarginColumn * getColumnWidth(), clipRect.y,
          printMarginColumn * getColumnWidth(), clipRect.y + clipRect.height);
    }
  }

  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    if (desktopHints != null) {
      g2.addRenderingHints(desktopHints);
    }
    // poni¿szy super.paintComponent musi byæ wywo³any gdy¿ inaczej nie dzia³aj¹
    // strza³eczki w lewo, prawo, etc
    super.paintComponent(g);
    SyntaxDocument document = ((SyntaxDocument) getDocument());
    SyntaxStyle style = document.getStyle(SyntaxDocument.SELECTION_COLOR, true);
    setSelectionColor(style.getForeground());

    Rectangle clipRect = g.getClipBounds();
    paintBackground(g);

    synchronized (document.getLockLines()) {
      Segment currentLine = new Segment();
      for (int y = Math.max(0, (clipRect.y / getRowHeight()) - 1); y < Math.min((clipRect.y + clipRect.height) / getRowHeight() + 1, getLineCount()); y++) {
        getLineText(y, currentLine);
        paintSyntaxLine(g, currentLine, 1, y);
      }
    }
    if (getCaret() != null && getCaret().isVisible()) {
      getCaret().paint(g);
    }
  }

  public float nextTabStop(float x, int tabOffset) {
    int tabSize = getTabSize() * getGraphics().getFontMetrics().charWidth(' ');
    int ntabs = ((int) x) / tabSize;
    return (ntabs + 1) * tabSize;
  }

  public final void getText(int start, int len, Segment segment) {
    try {
      getDocument().getText(start, len < 0 ? 0 : len, segment);
    } catch (BadLocationException e) {
      ExceptionUtil.processException(e);
    }
  }

  public final String getLineText(int lineIndex) {
    int start;
    try {
      start = getLineStartOffset(lineIndex);
      return getText(start, getLineEndOffset(lineIndex) - start - 1);
    } catch (BadLocationException e) {
      e.printStackTrace();
      return null;
    }
  }

  public final void getLineText(int lineIndex, Segment segment) {
    int start = getLineStartOffset(lineIndex);
    getText(start, getLineEndOffset(lineIndex) - start, segment);
  }

  public void setShowEndOfLine(boolean showEndOfLine) {
    this.showEndOfLine = showEndOfLine;
  }

  public boolean isShowEndOfLine() {
    return showEndOfLine;
  }

  public void revalidate() {
    super.revalidate();
    if (getDocument() != null && getDocument() instanceof SyntaxDocument) {
      ((SyntaxDocument) getDocument()).resetAt(0);
    }
  }

  public void undoPerformed(UndoableEdit edit) {
    revalidate();
    repaint();
  }

  public void redoPerformed(UndoableEdit edit) {
    revalidate();
    repaint();
  }

  public void setPrintMargin(boolean printMargin) {
    this.printMargin = printMargin;
  }

  public boolean isPrintMargin() {
    return printMargin;
  }

  public void setPrintMarginColumn(int printMarginColumn) {
    this.printMarginColumn = printMarginColumn;
  }

  public int getPrintMarginColumn() {
    return printMarginColumn;
  }

  public boolean isChanged() {
    return changed;
  }

  public void setChanged(boolean changed) {
    this.changed = changed;
  }

  public void loadFromStream(InputStream stream) throws IOException {
    setText(StreamUtil.stream2String(stream).replace("\r", "")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void saveToStream(OutputStream stream) throws IOException {
    PrintWriter pw = new PrintWriter(stream);
    pw.write(getText());
    pw.close();
  }

  /**
   * <p>
   * Funkcja jeœli to mo¿liwe, jeœli getDocument() to SyntaxDocument, zwróci
   * fragment ci¹gu znaków.
   * <p>
   * Jeœli nie mo¿e zwróci null
   * 
   * @return
   */
  public String getCurrentText() {
    if (getDocument() instanceof SyntaxDocument) {
      if (getSelectedText() == null && getStructureParser() != null) {
        BlockElement block = getStructure(true);
        if (block != null) {
          CodeElement el = block.getElementAt(getCaretPosition());
          if (el != null) {
            int start = el.getStartOffset();
            int end = el.getEndOffset();
            ((SyntaxDocument) getDocument()).setStartCurrentText(start);
            ((SyntaxDocument) getDocument()).setEndCurrentText(end);
            return getText().substring(start, end).trim();
          }
        }
      }
      return ((SyntaxDocument) getDocument()).getCurrentText();
    }
    return null;
  }

  public int getStartCurrentText() {
    if (getDocument() instanceof SyntaxDocument) {
      return ((SyntaxDocument) getDocument()).getStartCurrentText();
    }
    return -1;
  }

  public int getEndCurrentText() {
    if (getDocument() instanceof SyntaxDocument) {
      return ((SyntaxDocument) getDocument()).getEndCurrentText();
    }
    return -1;
  }

  public void replaceCurrentText(String text) {
    int start = getStartCurrentText();
    int end = getEndCurrentText();
    if (start != -1 && end != -1 && start != end) {
      setSelectionStart(start);
      setSelectionEnd(end);
      replaceSelection(text);
    }
  }

  public String[] getWordsAt(int off) throws BadLocationException {
    if (getDocument() instanceof SyntaxDocument) {
      return ((SyntaxDocument) getDocument()).getWordsAt(off);
    }
    return null;
  }

  public String[] getWordsTo(int off) throws BadLocationException {
    if (getDocument() instanceof SyntaxDocument) {
      return ((SyntaxDocument) getDocument()).getWordsTo(off);
    }
    return null;
  }

  public String[] getWordsBeforeBracket(int off) throws BadLocationException {
    if (getDocument() instanceof SyntaxDocument) {
      return ((SyntaxDocument) getDocument()).getWordsBeforeBracket(off);
    }
    return null;
  }

  public CommentSpec[] getComments() {
    return commentSpecs;
  }

  public void setComments(CommentSpec[] comments) {
    this.commentSpecs = comments;
  }

  public String getText() {
    return super.getText();
  }

  public void setText(String text) {
    super.setText(text);
    if (!isEditable()) {
      setChanged(false);
    }
  }

  public void setOvertypeMode(boolean overtypeMode) {
    this.overtypeMode = overtypeMode;
    int pos = getCaretPosition();

    if (isOvertypeMode()) {
      setCaret(overtypeCaret);
    } else {
      setCaret(defaultCaret);
    }

    setCaretPosition(pos);
  }

  public boolean isOvertypeMode() {
    return overtypeMode;
  }

  public void replaceSelection(String text) {
    // Implement overtype mode by selecting the character at the current
    // caret position

    if (isOvertypeMode()) {
      int pos = getCaretPosition();

      try {
        if (getSelectedText() == null && pos < getDocument().getLength()
            && getDocument().getText(pos, 1).charAt(0) != '\n') {
          moveCaretPosition(pos + 1);
        }
      } catch (BadLocationException e) {
        ExceptionUtil.processException(e);
      }
    }

    super.replaceSelection(text);
  }

  class OvertypeCaret extends DefaultCaret {
    private static final long serialVersionUID = 1L;

    /*
     * The overtype caret will simply be a horizontal line one pixel high (once
     * we determine where to paint it)
     */
    public void paint(Graphics g) {
      if (isVisible()) {
        try {
          JTextComponent component = getComponent();
          TextUI mapper = component.getUI();
          int width = g.getFontMetrics().charWidth('w');
          Rectangle r = mapper.modelToView(component, getDot());
          g.setColor(component.getCaretColor());
          int y = r.y + r.height - 2;
          g.drawLine(r.x, y - 1, r.x + width - 1, y - 1);
          g.drawLine(r.x, y, r.x + width - 1, y);
        } catch (BadLocationException e) {
        }
      }
    }

    /*
     * Damage must be overridden whenever the paint method is overridden (The
     * damaged area is the area the caret is painted in. We must consider the
     * area for the default caret and this caret)
     */
    protected synchronized void damage(Rectangle r) {
      if (r != null) {
        JTextComponent component = getComponent();
        x = r.x;
        y = r.y;
        width = component.getFontMetrics(component.getFont()).charWidth('w');
        height = r.height;
        repaint();
      }
    }
  }

  public static class TokenRef extends Token {

    public Token ref;
    public String token;

    public TokenRef() {
      super();
    }

    public TokenRef(int styleId, int length) {
      super(styleId, length);
    }

    public boolean isToken(String text) {
      return text.equals(this.token);
    }

    public boolean isTokenIgnoreCase(String text) {
      return text.equalsIgnoreCase(this.token);
    }

    public String toString() {
      return "[offset:" + offset + ",styleId:" + styleId + ",length:" + length + ",token:" + token + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }
  }

  public List<TokenRef> getTokens() throws BadLocationException {
    return getTokens(0, getText().length() - 1);
  }

  public List<TokenRef> getTokens(int startOffset, int endOffset) throws BadLocationException {
    SyntaxDocument document = ((SyntaxDocument) getDocument());
    ArrayList<TokenRef> tokenList = new ArrayList<TokenRef>();
    Segment line = new Segment();
    int offset = 0;
    Token lastToken = null;
    synchronized (document.getLockLines()) {
      for (int l = 0; l < document.getLineCount(); l++) {
        LineInfo info = document.getLineInfoAtLine(l, line);
        if (info != null && info.tokens != null) {
          for (Token token : info.tokens) {
            if (token.length > 0 && offset + token.length > startOffset && offset <= endOffset) {
              TokenRef ntoken = new TokenRef(token.styleId, token.length);
              ntoken.ref = token;
              ntoken.offset = offset;
              ntoken.token = line.subSequence(token.offset, token.offset +token.length).toString();
              //ntoken.token = getDocument().getText(ntoken.offset, ntoken.length);
              if (lastToken != null) {
                ntoken.prev = lastToken;
                lastToken.next = ntoken;
              }
              lastToken = ntoken;
              tokenList.add(ntoken);
            }
            offset += token.length;
          }
        }
      }
    }
    return tokenList;
  }

  public List<TokenRef> getTokensCurrentText() throws BadLocationException {
    if (getCurrentText() != null) {
      return getTokens(getStartCurrentText(), getEndCurrentText());
    }
    return new ArrayList<TokenRef>();
  }

  private StructureAutoCompleteListener getStructureAutoCompleteListener() {
    if (structureCompleteListener == null) {
      structureCompleteListener = new StructureAutoCompleteListener(this);
    }
    return structureCompleteListener;
  }

  protected void parseStructure() {
    synchronized (structureParser) {
      try {
        structureParser.setIgnoreCase(((SyntaxDocument) getDocument()).isIgnoreCase());
        structureParser.setTokenList(getTokens());
        structure = structureParser.parse();
      } catch (BadLocationException e) {
        ExceptionUtil.processException(e);
      } catch (ParserException e) {
      } finally {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            repaint();
          }
        });
      }
    }
  }

  private void updateStructure() {
    if (structureParser != null) {
      structure = null;
      if (timerStructureParse != null) {
        timerStructureParse.restart();
      } else {
        timerStructureParse = new Timer(Math.min(Math.max(250, getLineCount() /10), 2000)) {
          public void run() {
            timerStructureParse.setEnabled(false);
            parseStructure();
          }
        };
        TimerManager.getTimer("syntax-editor").add(timerStructureParse); //$NON-NLS-1$
      }
    }
  }

  public StructureParser getStructureParser() {
    return structureParser;
  }

  public void setStructureParser(StructureParser structureParser) {
    if (this.structureParser != structureParser) {
      getAutoComplete().removeAutoCompleteListener(getStructureAutoCompleteListener());
      this.structureParser = structureParser;
      structureElementSelect.setEnabled(structureParser != null);
      getAutoComplete().addAutoCompleteListener(getStructureAutoCompleteListener());
    }
  }

  public BlockElement getStructure(boolean parse) {
    if (structure == null && structureParser != null && parse) {
      parseStructure();
    }
    return structure;
  }

  protected void caretUpdate(CaretEvent e) {

  }

  public void setCaretPosition(int position, boolean ensureVisible) {
    int lastPosition = getCaretPosition();
    setCaretPosition(position);
    if (ensureVisible) {
      Rectangle view;
      try {
        view = modelToView(position);
        view.setSize(getVisibleRect().getSize());
        if (lastPosition < position) {
          view.y = Math.max(view.y - view.height, 0);
        }
        if (!view.intersects(getVisibleRect())) {
          view.x = 0;
          scrollRectToVisible(view);
        }
      } catch (BadLocationException e1) {
      }
    }
  }

  public AutoCompleteText getAutoComplete() {
    return autoComplete;
  }

}
