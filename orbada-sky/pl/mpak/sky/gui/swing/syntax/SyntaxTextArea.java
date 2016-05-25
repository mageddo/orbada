package pl.mpak.sky.gui.swing.syntax;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;

import pl.mpak.sky.gui.swing.AutoCompleteText;
import pl.mpak.sky.gui.swing.comp.StatusBar;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.Bookmark;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.LineMark;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;
import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.StructureParser;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.timer.TimerManager;

public class SyntaxTextArea extends JPanel {
  private static final long serialVersionUID = -8616404311485826810L;

  private SyntaxDocument document = null;
  private SyntaxEditor editorArea = null;
  private TextAreaStatusBar editorStatusBar = null;
  private StatusBar structureBar;
  private JScrollPane scrollPane = null;
  private GutterPanel gutterPanel = null;
  private Timer timerStructureBar;
  private CodeElement lastElement;
  private EventListenerList gutterListenerList = new EventListenerList();
  
  public static int BOOKMARK_WIDTH = 14;
  public static int STRUCTURE_WIDTH = 8;

  public SyntaxTextArea(SyntaxDocument document) {
    super(new BorderLayout());
    init();
    setDocument(document);
  }

  public SyntaxTextArea() {
    this(new SyntaxDocument());
  }
  
  private void init() {
    add(getScrollPane(), BorderLayout.CENTER);
    add(getStatusBar(), BorderLayout.SOUTH);
    add(getStructureBar(), BorderLayout.NORTH);
    add(getGutterPanel(), BorderLayout.WEST);
    
    getEditorArea().addLineCountListener(new TextAreaLineCountListener() {
      public void lineCountChange(TextAreaLineCountEvent e) {
        recalcGutterWidth();
      }
    });
    recalcGutterWidth();
  }
  
  private void clearStructureBar() {
    if (structureBar != null) {
      for (int i=0; i<structureBar.getComponentCount(); i++) {
        Component c = structureBar.getComponent(i);
        if (c instanceof Closeable) {
          try {
            ((Closeable)c).close();
          } catch (IOException e) {
          }
        }
      }
      structureBar.removeAll();
    }
  }
  
  protected void finalize() throws Throwable {
    clearStructureBar();
    if (timerStructureBar != null) {
      timerStructureBar.cancel();
      timerStructureBar = null;
    }
    editorStatusBar.close();
    editorArea.close();
    getScrollPane().setViewportView(null);
    super.finalize();
  }

  public void addGutterListener(GutterListener listener) {
    synchronized (gutterListenerList) {
      gutterListenerList.add(GutterListener.class, listener);
    }
  }

  public void removeGutterListener(GutterListener listener) {
    synchronized (gutterListenerList) {
      gutterListenerList.remove(GutterListener.class, listener);
    }
  }

  public void fireGutterListener(int line, int clickCount, LineMark selected, LineMark[] allMarks) {
    synchronized (gutterListenerList) {
      GutterEvent event = new GutterEvent(gutterPanel, line, clickCount, selected, allMarks);
      for (GutterListener listener : gutterListenerList.getListeners(GutterListener.class)) {
        listener.markLineSelected(event);
      }
    }
  }

  private void recalcGutterWidth() {
    int lineCount = getEditorArea().getLineCount();
    Dimension dim = getGutterPanel().getPreferredSize();
    Dimension newDim = null;
    FontMetrics fm = getGutterPanel().getFontMetrics(getEditorArea().getFont());
    if (lineCount > 999999) {
      newDim = new Dimension(fm.stringWidth("0000000"), dim.height);
    } else if (lineCount > 99999) {
      newDim = new Dimension(fm.stringWidth("000000"), dim.height);
    } else if (lineCount > 9999) {
      newDim = new Dimension(fm.stringWidth("00000"), dim.height);
    } else if (lineCount > 999) {
      newDim = new Dimension(fm.stringWidth("0000"), dim.height);
    } else if (lineCount > 99) {
      newDim = new Dimension(fm.stringWidth("000"), dim.height);
    } else {
      newDim = new Dimension(fm.stringWidth("00"), dim.height);
    }
    newDim.width += BOOKMARK_WIDTH;
    if (getStructureParser() != null && document.getStyle(SyntaxDocument.BLOCK_COLOR, false).isEnabled()) {
      newDim.width += STRUCTURE_WIDTH;
    }
    if (newDim.width != dim.width) {
      getGutterPanel().setPreferredSize(newDim);
    }
    getGutterPanel().repaint();
  }
  
  public void setDocument(SyntaxDocument document) {
    if (this.document != document) {
      this.document = document;
      getEditorArea().setDocument(getDocument());
    }
  }

  public SyntaxDocument getDocument() {
    return document;
  }

  public SyntaxEditor getEditorArea() {
    if (editorArea == null) {
      editorArea = new SyntaxEditor() {
        private static final long serialVersionUID = 1L;
        public void setChanged(boolean changed) {
          boolean temp = SyntaxTextArea.this.isChanged();
          super.setChanged(changed);
          if (temp != changed) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                if (editorStatusBar != null) {
                  editorStatusBar.getPanel(TextAreaStatusBar.PANEL_CHANGED).setText(isChanged() ? " * " : (!isEditable() ? " r/o " : " "));
                }
              }
            });
          }
        }
        public void repaint() {
          super.repaint();
          getGutterPanel().repaint();
        }
        protected void caretUpdate(CaretEvent e) {
          updateStructurePosition(e);
        }
      };
      if (getDocument() != null) {
        editorArea.setDocument(getDocument());
      }
    }
    return editorArea;
  }
  
  public TextAreaStatusBar getStatusBar() {
    if (editorStatusBar == null) {
      editorStatusBar = new TextAreaStatusBar(getEditorArea());
    }
    return editorStatusBar;
  }
  
  public StatusBar getStructureBar() {
    if (structureBar == null) {
      structureBar = new StatusBar();
      structureBar.addPanel(null, " ");
      structureBar.setVisible(false);
    }
    return structureBar;
  }
  
  public JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane(getEditorArea());
      //scrollPane.setBorder(new javax.swing.border.LineBorder(Color.RED, 2));
    }
    return scrollPane;
  }
  
  public GutterPanel getGutterPanel() {
    if (gutterPanel == null) {
      gutterPanel = new GutterPanel();
    }
    return gutterPanel;
  }
  
  public String getText() {
    return getEditorArea().getText();
  }
  
  public void setText(String text) {
    getEditorArea().setText(text);
  }
  
  public void append(String str) {
    getEditorArea().append(str);
  }
  
  public String getSelectedText() {
    return getEditorArea().getSelectedText();
  }
  
  public int getCaretPosition() {
    return getEditorArea().getCaretPosition();
  }
  
  public int getGutterSize() {
    return getGutterPanel().getPreferredSize().width;
  }
  
  public void setGutterSize(int width) {
    getGutterPanel().setPreferredSize(new Dimension(width, 0));
  }
  
  public class GutterPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    public GutterPanel() {
      super();
      setPreferredSize(new Dimension(18, 0));
      setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));
      addMouseMotionListener(new MouseMotionListener() {
        int lastLine = -1;
        public void mouseDragged(MouseEvent e) {
        }
        public void mouseMoved(MouseEvent e) {
          SyntaxEditor area = getEditorArea();
          JScrollBar scroll = getScrollPane().getVerticalScrollBar();
          int fontHeight = area.getFontMetrics(area.getFont()).getHeight();
          int scrolly = (scroll.getValue() +e.getY()) /fontHeight;
          if (lastLine != scrolly) {
            lastLine = scrolly;
            StringBuilder sb = new StringBuilder();
            LineMark[] lma = document.getLineMark(scrolly +2);
            if (lma != null) {
              for (LineMark lm : lma) {
                if (lm.getHint() != null) {
                  if (sb.length() > 0) {
                    sb.append("<br>");
                  }
                  sb.append(lm.getHint());
                }
              }
            }
            if (sb.length() > 0) {
              setToolTipText("<html>" +sb.toString());
            }
            else {
              setToolTipText(null);
            }
          }
        }
      });
      addMouseListener(new MouseListener() {
        public void mouseClicked(MouseEvent e) {
          SyntaxEditor area = getEditorArea();
          JScrollBar scroll = getScrollPane().getVerticalScrollBar();
          int fontHeight = area.getFontMetrics(area.getFont()).getHeight();
          int scrolly = (scroll.getValue() +e.getY()) /fontHeight;
          LineMark[] lma = document.getLineMark(scrolly +2);
          LineMark lms = null;
          if (lma != null) {
            int x = getPreferredSize().width;
            if (getStructureParser() != null && document.getStyle(SyntaxDocument.BLOCK_COLOR, false).isEnabled()) {
              x -= STRUCTURE_WIDTH;
            }
            for (LineMark lm : lma) {
              if (lm.getImageMark() != null) {
                Image img = lm.getImageMark().getImage();
                if (e.getX() < x && e.getX() > x -img.getWidth(null)) {
                  lms = lm;
                }
                x -= img.getWidth(null);
              }
            }
          }
          fireGutterListener(scrolly +1, e.getClickCount(), lms, lma);
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
      });
    }
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      Rectangle rect = getBounds();
      int width = getPreferredSize().width;
      SyntaxEditor area = getEditorArea();
      Rectangle bounds = area.getUIBounds();
      SyntaxDocument document = ((SyntaxDocument)area.getDocument());
      JScrollBar scroll = getScrollPane().getVerticalScrollBar();
      
      if (area.desktopHints != null) {
        g2.addRenderingHints(area.desktopHints);
      }
      setBackground(area.getBackground());
      super.paintComponent(g);
      //g.fillRect(rect.x, rect.y, rect.width, rect.height);
      g.setFont(area.getFont());
      
      int fontHeight = area.getFontMetrics(area.getFont()).getHeight();
      int scrolly = scroll.getValue() /fontHeight;
      int yshift = scrolly *fontHeight -scroll.getValue() +bounds.y;
      CodeElement code = null;
      int structureWidth = 0;
      SyntaxStyle blockStyle = document.getStyle(SyntaxDocument.BLOCK_COLOR, false);
      if (getStructureParser() != null && blockStyle.isEnabled()) {
        structureWidth = STRUCTURE_WIDTH;
        BlockElement block = getStructure(false);
        if (block != null) {
          code = block.getElementAt(getCaretPosition());
        }
      }
      int blockLineY = -1;
      int blockHeight = 0;
      SyntaxStyle gutterStyle = document.getStyle(SyntaxDocument.GUTTER_LINE_NUMBER, true);
      for (int y=Math.max(0, (rect.y /fontHeight) -1); y<Math.min((rect.y +rect.height) /fontHeight +1, area.getLineCount()); y++) {
        if (code != null && blockStyle.isEnabled()) {
          if (getDocument().getLineEndOffset(scrolly) >= code.getStartOffset() && getDocument().getLineStartOffset(scrolly) <= code.getEndOffset()) {
            if (blockLineY == -1) {
              blockLineY = area.lineToY(y);
            }
            blockStyle.updateGraphics(g);
            g.fillRect(width -structureWidth +1, area.lineToY(y) +yshift -fontHeight /2 -4, structureWidth -3, fontHeight);
            blockHeight += fontHeight;
          }
        }
        gutterStyle.updateGraphics(g);
        String ls;
        ls = (Integer.valueOf(scrolly +1)).toString();
        g.drawString(ls, width -2 -8 -structureWidth -g.getFontMetrics().stringWidth(ls), area.lineToY(y) +yshift);
        Bookmark bm = document.getBookmarkByLine(scrolly +1);
        if (bm != null) {
          document.getStyle(SyntaxDocument.BOOKMARK, true).updateGraphics(g);
          ls = (Integer.valueOf(bm.getIndex())).toString();
          g.drawRect(
              width -2 -structureWidth -g.getFontMetrics().stringWidth(ls) -1, 
              area.lineToY(y) +yshift -fontHeight +5, 
              g.getFontMetrics().stringWidth(ls) +2, 
              fontHeight -1);
          g.drawString(ls, width -2 -structureWidth -g.getFontMetrics().stringWidth(ls), area.lineToY(y) +yshift);
          gutterStyle.updateGraphics(g);
        }
        else {
          LineMark[] lma = document.getLineMark(scrolly +2);
          if (lma != null) {
            int x = width;
            for (LineMark lm : lma) {
              if (lm.getImageMark() != null) {
                Image img = lm.getImageMark().getImage();
                x -= img.getWidth(null);
                g.drawImage(img, x -structureWidth, area.lineToY(y) +yshift +5 -(fontHeight +img.getHeight(null)) /2, null);
              }
            }
          }
        }
        scrolly++;
      }
      if (getStructureParser() != null && blockStyle.isEnabled()) {
        blockStyle.updateGraphics(g, -50);
        g.drawRect(width -structureWidth +1, blockLineY +yshift -fontHeight /2 -4, structureWidth -3, blockHeight);
      }
    }
  }
  
  public boolean isChanged() {
    return getEditorArea().isChanged();
  }

  public void setChanged(boolean changed) {
    getEditorArea().setChanged(changed);
  }
  
  public void setEditable(final boolean editable) {
    getEditorArea().setEditable(editable);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        if (editorStatusBar != null) {
          editorStatusBar.getPanel(TextAreaStatusBar.PANEL_CHANGED).setText(!editable ? " r/o " : " ");
        }
      }
    });
  }
  
  public boolean isEditable() {
    return getEditorArea().isEditable();
  }
  
  public void loadFromStream(InputStream stream) throws IOException {
    getEditorArea().loadFromStream(stream);
  }
  
  public void saveToStream(OutputStream stream) throws IOException {
    getEditorArea().saveToStream(stream);
  }
  
  public void loadFromFile(String fileName) throws IOException {
    loadFromStream(new FileInputStream(fileName));
  }
  
  public void saveToFile(String fileName) throws IOException {
    saveToStream(new FileOutputStream(fileName));
  }
  
  public void loadFromFile(File file) throws IOException {
    loadFromStream(new FileInputStream(file));
  }
  
  public void saveToFile(File file) throws IOException {
    saveToStream(new FileOutputStream(file));
  }
  
  /**
   * <p>Funkcja jeœli to mo¿liwe, jeœli getDocument() to SyntaxDocument, zwróci
   * fragment ci¹gu znaków.
   * <p>Jeœli nie mo¿e zwróci null
   * @return
   */
  public String getCurrentText() {
    return editorArea.getCurrentText();
  }
  
  public int getStartCurrentText() {
    return editorArea.getStartCurrentText();
  }

  public int getEndCurrentText() {
    return editorArea.getEndCurrentText();
  }

  /**
   * <p>Zwraca listê s³ów z pozycji off, u¿ywa dedykowanego SyntaxDocument jeœli to mo¿liwe
   * <p>np dla SQLSyntaxDocument w przypadku tekstu |schemat.objekt, inna| zwróci {"SCHEMAT", "OBJECT"}
   * @param off
   * @return
   * @throws BadLocationException
   */
  public String[] getWordsAt(int off) throws BadLocationException {
    return editorArea.getWordsAt(off);
  }
  
  /**
   * <p>Zwraca listê s³ów do pozycji off, u¿ywa dedykowanego SyntaxDocument jeœli to mo¿liwe
   * @param off
   * @return
   * @throws BadLocationException
   */
  public String[] getWordsTo(int off) throws BadLocationException {
    return editorArea.getWordsAt(off);
  }
  
  /**
   * <p>Zwraca listê s³ów sprzed nawiasu ograniczaj¹cego, u¿ywa dedykowanego SyntaxDocument jeœli to mo¿liwe
   * @param off
   * @return
   * @throws BadLocationException
   */
  public String[] getWordsBeforeBracket(int off) throws BadLocationException {
    return editorArea.getWordsBeforeBracket(off);
  }
  
  public void setLineMark(int line, Color bkColor, ImageIcon imageMark) {
    getDocument().setLineMark(line, bkColor, imageMark);
  }
  
  public void setLineMark(LineMark lineMark) {
    getDocument().setLineMark(lineMark);
  }
  
  public void setLineMark(LineMark[] lineMarks) {
    getDocument().setLineMark(lineMarks);
  }
  
  public void clearLineMarks() {
    getDocument().clearLineMark();
  }
  
  public void removeLineMark(LineMark lineMark) {
    getDocument().removeLineMark(lineMark);
  }
  
  public LineMark[] getLineMark(int line) {
    return getDocument().getLineMark(line);
  }
  
  public List<TokenRef> getTokens() throws BadLocationException {
    return getEditorArea().getTokens();
  }
  
  public List<TokenRef> getTokensCurrentText() throws BadLocationException {
    return getEditorArea().getTokensCurrentText();
  }
  
  public List<TokenRef> getTokens(int startOffset, int endOffset) throws BadLocationException {
    return getEditorArea().getTokens(startOffset, endOffset);
  }
  
  public StructureParser getStructureParser() {
    return getEditorArea().getStructureParser();
  }
  
  public void setStructureParser(StructureParser structureParser) {
    getEditorArea().setStructureParser(structureParser);
    structureBar.setVisible(structureParser != null);
  }
  
  public BlockElement getStructure(boolean parse) {
    return getEditorArea().getStructure(parse);
  }
  
  private boolean updateStructureBar() {
    if (getStructureParser() == null) {
      return true;
    }
    final BlockElement block = getStructure(false);
    if (block == null) {
      return false;
    }
    java.awt.EventQueue.invokeLater(new Runnable() {
      CodeElement el = block.getElementAt(getCaretPosition());
      public void run() {
        if (lastElement != el) {
          lastElement = el;
          clearStructureBar();
          while (el != null) {
            structureBar.add(new StructureElementPanel(getEditorArea(), el), 0);
            el = el.getOwner();
          }
          if (structureBar.getComponentCount() == 0) {
            structureBar.addPanel(null, " ");
          }
          structureBar.refresh();
        }
      }
    });
    return true;
  }
  
  private void updateStructurePosition(CaretEvent e) {
    if (getStructureParser() != null) {
      if (timerStructureBar != null) {
        timerStructureBar.restart();
      }
      else {
        timerStructureBar = new Timer(250) {
          public void run() {
            if (updateStructureBar()) {
              timerStructureBar.cancel();
              timerStructureBar = null;
            }
          }
        };
        TimerManager.getTimer("syntax-editor").add(timerStructureBar);
      }
    }
  }
  
  public boolean requestFocusInWindow() {
    return editorArea.requestFocusInWindow();
  }
  
  public boolean requestFocus(boolean temporary) {
    return editorArea.requestFocus(temporary);
  }
  
  public void requestFocus() {
    editorArea.requestFocus();
  }
  
  public void addDocumentListener(DocumentListener listener) {
    getDocument().addDocumentListener(listener);
  }
  
  public AutoCompleteText getAutoComplete() {
    return editorArea.getAutoComplete();
  }

  private static void applySyntaxTextArea(JComponent component, ApplySyntaxTextAreaEvent event) {
    if (component instanceof SyntaxTextArea) {
      event.apply((SyntaxTextArea)component);
    }
    for (int i=0; i<component.getComponentCount(); i++) {
      if (component.getComponent(i) instanceof JComponent) {
        applySyntaxTextArea((JComponent)component.getComponent(i), event);
      }
    }
  }
  
  /**
   * Pozwala wykonac event dla kazdego edytora w programie
   * @param event
   */
  public static void apply(ApplySyntaxTextAreaEvent event) {
    Frame frames[] = Frame.getFrames();
    if (frames != null) {
      for (Frame frame : frames) {
        for (int i=0; i<frame.getComponentCount(); i++) {
          if (frame.getComponent(i) instanceof JComponent) {
            applySyntaxTextArea((JComponent)frame.getComponent(i), event);
          }
        }
      }
    }
  }
  
}
