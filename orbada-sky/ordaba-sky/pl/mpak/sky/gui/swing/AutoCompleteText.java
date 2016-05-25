package pl.mpak.sky.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.swing.comp.HtmlEditorPane;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;


public class AutoCompleteText implements KeyListener, FocusListener, MouseListener {
  
  private final EventListenerList autoCompleteListenerList = new EventListenerList();
  JTextComponent parent = null;
  JList lst = null;
  AutoCompleteListModel model;
  JWindow window;
  JWindow hintWindow;
  HtmlEditorPane hintLabel;
  java.util.TreeSet<String> val = new java.util.TreeSet<String>();
  private boolean active;
  private int caretPosition = -1;
  private boolean bracketMode;

  public AutoCompleteText() {
    model = new AutoCompleteListModel();
    lst = new JList(model);
    lst.addKeyListener(this);
    lst.addFocusListener(this);
    lst.setBorder(BorderFactory.createEmptyBorder());
    lst.setCellRenderer(createListCellRenderer());
    lst.addMouseListener(createListMouseListener());
    lst.getSelectionModel().addListSelectionListener(createListSelectionListener());
  }
  
  private ListSelectionListener createListSelectionListener() {
    return new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updateHintLabel();
      }
    };
  }

  public AutoCompleteText(JTextComponent jc) {
    this();
    parent = (JTextComponent)jc;
  }
  
  public void setTextComponent(JTextComponent jc) {
    parent = jc;
  }

  private ListCellRenderer createListCellRenderer() {
    return new DefaultListCellRenderer() {
      private static final long serialVersionUID = 1L;
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        AutoCompleteItem item = null;
        if (value instanceof AutoCompleteItem) {
          item = (AutoCompleteItem)value;
          if (model.getSize() <= 500) {
            if (item.getDisplayText() != null) {
              value = item.getDisplayText();
            }
          }
          else {
            if (item.getType() != null) {
              value = item.getWord() +" (" +item.getType() +")";
            }
            else {
              value = item.getWord();
            }
          }
        }
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (item != null) {
          setIcon(item.getIcon());
        }
        return this;
      }
    };
  }
  
  private MouseListener createListMouseListener() {
    return new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (window.isVisible() && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
          select(null);
        }
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent e) {
      }
    };
  }

  public void addAutoCompleteListener(AutoCompleteListener listener) {
    autoCompleteListenerList.add(AutoCompleteListener.class, listener);
  }

  public void removeAutoCompleteListener(AutoCompleteListener listener) {
    autoCompleteListenerList.remove(AutoCompleteListener.class, listener);
  }

  private AutoCompleteItem[] fireAutoComplete(String[] words, boolean bracketMode, int commaCount) {
    ArrayList<AutoCompleteItem> populated = new ArrayList<AutoCompleteItem>();
    for (AutoCompleteListener listener : autoCompleteListenerList.getListeners(AutoCompleteListener.class)) {
      AutoCompleteItem[] array = listener.populate(words, bracketMode, commaCount);
      if (array != null && array.length > 0) {
        for (AutoCompleteItem item : array) {
          populated.add(item);
        }
      }
    }
    return populated.toArray(new AutoCompleteItem[populated.size()]);
  }

  private void movePageDown() {
    int visibleRowCount = lst.getVisibleRowCount();
    int i = Math.min(lst.getModel().getSize()-1, lst.getSelectedIndex()+visibleRowCount);
    lst.setSelectedIndex(i);
    lst.ensureIndexIsVisible(i);
  }

  private void movePageUp() {
    int visibleRowCount = lst.getVisibleRowCount();
    int i = Math.max(0, lst.getSelectedIndex()-visibleRowCount);
    lst.setSelectedIndex(i);
    lst.ensureIndexIsVisible(i);
  }
  
  private void moveUp() {
    int index = lst.getSelectedIndex();
    switch (index) {
    case 0:
      index = lst.getModel().getSize() - 1;
      break;
    case -1: // Check for an empty list (would be an error)
      index = lst.getModel().getSize() - 1;
      if (index == -1) {
        return;
      }
      break;
    default:
      index = index - 1;
      break;
    }
    lst.setSelectedIndex(index);
    lst.ensureIndexIsVisible(index);

  }

  private void moveDown() {
    int index = lst.getSelectedIndex();
    if (index > -1) {
      index = (index + 1) % model.getSize();
      lst.setSelectedIndex(index);
      lst.ensureIndexIsVisible(index);
    }
  }

  private void select(AutoCompleteItem selected) {
    if (selected == null) {
      selected = (AutoCompleteItem)lst.getSelectedValue();
    }
    if (parent instanceof JTextArea) {
      String txt = parent.getText();
      String nextChar = null;
      int i = 0, n = parent.getCaretPosition();
      for (; n < txt.length(); n++) {
        if (!Character.isJavaIdentifierPart(txt.charAt(n))) {
          break;
        }
      }
      for (i = n; i > 0; i--) {
        if (!Character.isJavaIdentifierPart(txt.charAt(i -1))) {
          break;
        }
      }
      int j = n;
      while (j < txt.length()) {
        if (!Character.isWhitespace(txt.charAt(n))) {
          nextChar = "" +txt.charAt(n);
          break;
        }
        j++;
      }
      if (i < n) {
        parent.setSelectionStart(i);
        if (SkySetting.getBoolean(SkySetting.AutoCompleteText_InsertionText, false)) {
          parent.setSelectionEnd(parent.getCaretPosition());
        }
        else {
          parent.setSelectionEnd(n);
        }
        parent.replaceSelection(selected.selectedText(parent.getSelectedText(), nextChar));
      }
      else {
        ((JTextArea)parent).insert(selected.selectedText(null, nextChar), parent.getCaretPosition());
      }
    } else {
      parent.setText(selected.selectedText(null, null));
    }
    setActive(false);
  }

  public void focusLost(FocusEvent fe) {
    setActive(false);
  }

  public void focusGained(FocusEvent fe) {
    if (fe.getSource() == lst) {
      parent.requestFocus();
    }
  }

  public void keyPressed(KeyEvent ke) {
    int kc = ke.getKeyCode();
    if (kc == KeyEvent.VK_UP) {
      if (window.isVisible()) {
        moveUp();
        ke.consume();
      }
    } 
    else if (kc == KeyEvent.VK_DOWN) {
      if (window.isVisible()) {
        moveDown();
        ke.consume();
      }
    } 
    else if (kc == KeyEvent.VK_PAGE_UP) {
      movePageUp();
      ke.consume();
    } 
    else if (kc == KeyEvent.VK_PAGE_DOWN) {
      movePageDown();
      ke.consume();
    } 
    else if (kc == KeyEvent.VK_ENTER) {
      ke.consume();
    } 
    else if (kc == KeyEvent.VK_ESCAPE) {
      setActive(false);
      ke.consume();
    }
  }

  public void keyReleased(KeyEvent ke) {
    int kc = ke.getKeyCode();
    if (kc == KeyEvent.VK_ENTER) {
      select(null);
    } else if (active && parent != null && kc != KeyEvent.VK_ESCAPE) {
      populateList();
    } else {
      setActive(false);
    }
  }

  public void keyTyped(KeyEvent e) {
  }
  
  private void updateHintLabel() {
    if (lst.getSelectedValue() instanceof AutoCompleteItem) {
      AutoCompleteItem item = (AutoCompleteItem)lst.getSelectedValue();
      final StringBuilder sb = new StringBuilder();
      if (item.getType() != null) {
        sb.append(item.getType());
      }
      if (item.getWord() != null) {
        if (sb.length() > 0) {
          sb.append("&#32;");
        }
        sb.append("<b>" +item.getWord() +"</b>");
      }
      if (item.getDisplayText() != null) {
        if (sb.length() > 0) {
          sb.append("<hr>");
        }
        String text = item.getDisplayText();
        if (text.length() >= 6 && !text.toUpperCase().startsWith("<HTML>")) {
          sb.append(text.substring(6));
        }
        else {
          sb.append(text);
        }
      }
      if (item.getDescription() != null) {
        if (sb.length() > 0) {
          sb.append("<hr>");
        }
        String text = item.getDescription();
        if (text.length() >= 6 && !text.toUpperCase().startsWith("<HTML>")) {
          sb.append(text.substring(6));
        }
        else {
          sb.append(text);
        }
      }
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          hintLabel.setText("<html><style>body { font-family: " +lst.getFont().getFamily() +", Arial, serif; font-size: " +lst.getFont().getSize() +"; padding: 2px; }</style><body>" +sb.toString() +"</body>");
        }
      });
    }
    else {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          hintLabel.setText("");
        }
      });
    }
  }

  private void populateList() {
    if (parent != null && caretPosition == parent.getCaretPosition()) {
      return;
    }
    caretPosition = parent.getCaretPosition();
    
    int wordsCommaCount = -1;
    String words[] = null;
    String txt = "";
    if (parent instanceof SyntaxEditor) {
      try {
        if (bracketMode) {
          words = ((SyntaxEditor)parent).getWordsBeforeBracket(parent.getCaretPosition());
          wordsCommaCount = ((SyntaxDocument)((SyntaxEditor)parent).getDocument()).getWordsCommaCount();
        }
        else {
          words = ((SyntaxEditor)parent).getWordsTo(parent.getCaretPosition());
        }
      } catch (BadLocationException e) {
      }
    }
    if (words == null) {
      if (parent instanceof SyntaxEditor) {
        setActive(false);
        return;
      }
      if (parent instanceof JTextArea) {
        txt = parent.getText().toLowerCase();
        int i = 0, n = parent.getCaretPosition() - 1;
        for (i = n; i >= 0; i--) {
          if (!Character.isJavaIdentifierPart(txt.charAt(i))) {
            break;
          }
        }
        txt = txt.substring(i + 1, n + 1);
        words = new String[] {txt};
      } else {
        txt = parent.getText().toLowerCase();
        words = new String[] {txt};
      }
    }
    int index = lst.getSelectedIndex();
    AutoCompleteItem[] items = fireAutoComplete(words, bracketMode, wordsCommaCount);
    if (items == null || items.length == 0) {
      setActive(false);
      return;
    }
    if (!bracketMode && SkySetting.getBoolean(SkySetting.AutoCompleteText_AutomaticSingle, true) && items.length == 1 && !window.isVisible()) {
      select(items[0]);
    }
    else {
      model.setContents(items);
      Point p = new Point(0, 0);
      try {
        p = parent.modelToView(parent.getCaretPosition()).getLocation();
      } catch (Exception ex) {
      }
      Point loc = parent.getLocationOnScreen();
      p = new Point(p.x + loc.x, p.y + loc.y);
      p.x++;
      Point cp = new Point(p.x, p.y);
  
      FontMetrics fm = window.getFontMetrics(parent.getFont());
      Rectangle bound = new Rectangle(0, 0, (int)window.getToolkit().getScreenSize().getWidth(), (int)window.getToolkit().getScreenSize().getHeight());
      if (p.y - window.getHeight() > 0) {
        p.y -= window.getHeight();
      }
      else if (p.y + window.getHeight() > bound.y + bound.height) {
        bound.y += (bound.height - window.getHeight());
      }
      else {
        p.y += fm.getHeight();
      }
      if (p.x +window.getWidth() > bound.width) {
        p.x = bound.width -window.getWidth();
      }
      window.setLocation(p.x, p.y);
      if (cp.y >= window.getLocation().y +window.getHeight()) {
        if (cp.y +fm.getHeight() +hintWindow.getHeight() > bound.height) {
          hintWindow.setLocation(window.getLocation().x, window.getLocation().y -hintWindow.getHeight());
        }
        else {
          hintWindow.setLocation(window.getLocation().x, cp.y +fm.getHeight());
        }
      }
      else {
        if (items.length > 1 || !bracketMode) {
          hintWindow.setLocation(window.getLocation().x, window.getLocation().y +window.getHeight());
        }
        else {
          hintWindow.setLocation(window.getLocation().x, window.getLocation().y);
        }
      }
  
      if (bracketMode) {
        lst.setSelectedIndex(index >= lst.getModel().getSize() || index < 0 ? 0 : index);
      }
      else {
        lst.setSelectedIndex(0);
      }
      lst.ensureIndexIsVisible(0);
      if (!window.isVisible()) {
        if (items.length > 1 || !bracketMode) {
          window.setVisible(true);
        }
        hintWindow.setVisible(true);
      }
    }
  }

  public boolean isBracketMode() {
    return bracketMode;
  }

  public void setBracketMode(boolean bracketMode) {
    this.bracketMode = bracketMode;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    caretPosition = -1;
    if (this.active != active && parent != null) {
      this.active = active;
      if (this.active) {
        window = new JWindow();
        window.getContentPane().add(new JScrollPane(lst), BorderLayout.CENTER);
        ((JPanel)window.getContentPane()).setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        window.setPreferredSize(new Dimension(600, 200));
        window.pack();
        
        hintWindow = new JWindow();
        hintWindow.getContentPane().add(new JScrollPane(hintLabel = new HtmlEditorPane()), BorderLayout.CENTER);
        ((JPanel)hintWindow.getContentPane()).setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        hintWindow.setPreferredSize(new Dimension((int)window.getPreferredSize().getWidth(), 150));
        hintWindow.pack();
        
        parent.addFocusListener(this);
        parent.addKeyListener(this);
        parent.addMouseListener(this);
        populateList();
      } else {
        parent.removeFocusListener(this);
        parent.removeKeyListener(this);
        parent.removeMouseListener(this);
        window.dispose();
        window = null;
        hintWindow.dispose();
        hintWindow = null;
      }
    }
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    populateList();
  }

}