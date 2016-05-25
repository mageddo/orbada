/*
 * FindTextDialog.java
 *
 * Created on 10 sierpieñ 2008, 14:44
 */

package pl.mpak.sky.gui.swing.syntax;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.SettingsUtil;
import pl.mpak.util.StringUtil;


/**
 *
 * @author  akaluza
 */
public class FindTextDialog extends javax.swing.JDialog {
  private static final long serialVersionUID = 1653908064683066782L;
  
  private static FindTextDialog dialog;

  private JTextComponent textComponent;
  private enum FindMode {
    fmFind,
    fmFindReplace,
    fmReplace,
    fmReplaceAll
  };
  private boolean firstTime = true;
  private int windowLeft = -1;
  private int windowTop = -1;
  private Pattern pattern;
  private String replaceOryginal;
  private String replaceTextTemp;
  private DocumentListener comboDocumentListener;
  
  /** Creates new form FindTextDialog */
  public FindTextDialog(JTextComponent textComponent) {
    super(SwingUtil.getRootFrame());
    initComponents();
    this.textComponent = textComponent;
    init();
  }
  
  public static void findNext(JTextComponent textComponent) {
    if (dialog == null) {
      show(textComponent);
    }
    else {
      dialog.textComponent = textComponent;
      dialog.checkForward.setSelected(true);
      dialog.find(FindMode.fmFind);
    }
  }

  public static void findPrev(JTextComponent textComponent) {
    if (dialog == null) {
      show(textComponent);
    }
    else {
      dialog.textComponent = textComponent;
      dialog.checkBackward.setSelected(true);
      dialog.find(FindMode.fmFind);
    }
  }

  public static void show(JTextComponent textComponent) {
    if (dialog == null) {
      dialog = new FindTextDialog(textComponent);
      dialog.setVisible(true);
    }
    else {
      dialog.textComponent = textComponent;
      dialog.firstTime = true;
      dialog.setVisible(true);
    }
  }
  
  private void init() {
    loadSettings();
    ((JTextComponent)comboFind.getEditor().getEditorComponent()).getDocument().addDocumentListener(getComboDocumentListener());
    ((JTextComponent)comboReplace.getEditor().getEditorComponent()).getDocument().addDocumentListener(getComboDocumentListener());
    
    cmFindReplace.setEnabled(textComponent.isEditable());
    cmReplace.setEnabled(textComponent.isEditable());
    cmReplaceAll.setEnabled(textComponent.isEditable());
    comboReplace.setEnabled(textComponent.isEditable());

    getRootPane().setDefaultButton(buttonFind);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmClose"); //$NON-NLS-1$
    getRootPane().getActionMap().put("cmClose", cmClose); //$NON-NLS-1$
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonClose, buttonFind, buttonFindReplace, buttonReplace, buttonReplaceAll});
    pack();
    if (windowLeft == -1 || windowTop == -1) {
      SwingUtil.centerWithinScreen(this);
    }
    else {
      setBounds(windowLeft, windowTop, getWidth(), getHeight());
    }
    addComponentListener(new ComponentListener() {
      public void componentHidden(ComponentEvent e) {
      }
      public void componentMoved(ComponentEvent e) {
      }
      public void componentResized(ComponentEvent e) {
      }
      public void componentShown(ComponentEvent e) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            if (!StringUtil.isEmpty(comboFind.getText())) {
              comboFind.getEditor().selectAll();
            }
          }
        });
      }
    });
  }

  private DocumentListener getComboDocumentListener() {
    if (comboDocumentListener == null) {
      comboDocumentListener = new DocumentListener () {
        public void changedUpdate(DocumentEvent e) {
          pattern = null;
        }
        public void insertUpdate(DocumentEvent e) {
          pattern = null;
        }
        public void removeUpdate(DocumentEvent e) {
          pattern = null;
        }
      };
    }
    return comboDocumentListener;
  }
  
  private void loadSettings() {
    Properties props = SettingsUtil.get("sky", "find-text-dialog"); //$NON-NLS-1$ //$NON-NLS-2$
    
    int count = Integer.parseInt(props.getProperty("find-list-count", "0")); //$NON-NLS-1$ //$NON-NLS-2$
    for (int i=0; i<count; i++) {
      String s = props.getProperty("find-list-" +i);  //$NON-NLS-1$
      if (s != null) {
        comboFind.addItem(s);
      }
    }
    comboFind.setText(props.getProperty("find-list-text")); //$NON-NLS-1$

    count = Integer.parseInt(props.getProperty("replace-list-count", "0")); //$NON-NLS-1$ //$NON-NLS-2$
    for (int i=0; i<count; i++) {
      String s = props.getProperty("replace-list-" +i); //$NON-NLS-1$
      if (s != null) {
        comboReplace.addItem(s);
      }
    }
    comboReplace.setText(props.getProperty("replace-list-text")); //$NON-NLS-1$
    
    if (StringUtil.toBoolean(props.getProperty("direction-forward", "false"))) { //$NON-NLS-1$ //$NON-NLS-2$
      checkForward.setSelected(true);
    }
    else if (StringUtil.toBoolean(props.getProperty("direction-backward", "false"))) { //$NON-NLS-1$ //$NON-NLS-2$
      checkBackward.setSelected(true);
    }
    if (StringUtil.toBoolean(props.getProperty("scope-from-begin", "false"))) { //$NON-NLS-1$ //$NON-NLS-2$
      checkFromBegin.setSelected(true);
    }
    else if (StringUtil.toBoolean(props.getProperty("scope-at-cursor", "false"))) { //$NON-NLS-1$ //$NON-NLS-2$
      checkAtCursor.setSelected(true);
    }
    checkCaseSensitive.setSelected(StringUtil.toBoolean(props.getProperty("case-sensitive", "false"))); //$NON-NLS-1$ //$NON-NLS-2$
    checkRegularExpr.setSelected(StringUtil.toBoolean(props.getProperty("regular-expression", "false"))); //$NON-NLS-1$ //$NON-NLS-2$
    checkWholeWords.setSelected(StringUtil.toBoolean(props.getProperty("whole-words", "false"))); //$NON-NLS-1$ //$NON-NLS-2$
    windowLeft = Integer.parseInt(props.getProperty("window-left", "" +windowLeft)); //$NON-NLS-1$ //$NON-NLS-2$
    windowTop = Integer.parseInt(props.getProperty("window-top", "" +windowTop)); //$NON-NLS-1$ //$NON-NLS-2$
    
    checkWholeWords.setEnabled(!checkRegularExpr.isSelected());
  }
  
  private void storeSettings() {
    Properties props = SettingsUtil.get("sky", "find-text-dialog"); //$NON-NLS-1$ //$NON-NLS-2$
    
    props.setProperty("find-list-count", "" +comboFind.getItemCount()); //$NON-NLS-1$ //$NON-NLS-2$
    for (int i=0; i<comboFind.getItemCount(); i++) {
      Object o = comboFind.getItemAt(i);
      if (o != null) {
        props.setProperty("find-list-" +i, o.toString()); //$NON-NLS-1$
      }
    }
    props.setProperty("find-list-text", comboFind.getText()); //$NON-NLS-1$
    
    props.setProperty("replace-list-count", "" +comboReplace.getItemCount()); //$NON-NLS-1$ //$NON-NLS-2$
    for (int i=0; i<comboReplace.getItemCount(); i++) {
      Object o = comboReplace.getItemAt(i);
      if (o != null) {
        props.setProperty("replace-list-" +i, o.toString()); //$NON-NLS-1$
      }
    }
    props.setProperty("replace-list-text", comboReplace.getText()); //$NON-NLS-1$
    
    props.setProperty("direction-forward", checkForward.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    props.setProperty("direction-backward", checkBackward.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    props.setProperty("scope-from-begin", checkFromBegin.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    props.setProperty("scope-at-cursor", checkAtCursor.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    props.setProperty("case-sensitive", checkCaseSensitive.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    props.setProperty("case-sensitive", checkCaseSensitive.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    props.setProperty("regular-expression", checkRegularExpr.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    props.setProperty("whole-words", checkWholeWords.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    props.setProperty("window-left", "" +getX()); //$NON-NLS-1$ //$NON-NLS-2$
    props.setProperty("window-top", "" +getY()); //$NON-NLS-1$ //$NON-NLS-2$
    
    SettingsUtil.store("sky", "find-text-dialog"); //$NON-NLS-1$ //$NON-NLS-2$
  }
  
  private int find(int cursor) {
    String text = textComponent.getText();
    String findText = comboFind.getText();

    if (checkRegularExpr.isSelected()) {
      if (pattern == null) {
        pattern = Pattern.compile(findText, Pattern.MULTILINE | Pattern.DOTALL | (!checkCaseSensitive.isSelected() ? 0 : Pattern.CASE_INSENSITIVE));
      }
      Matcher matcher = pattern.matcher(text);
      if (matcher.find(cursor)) {
        textComponent.setSelectionStart(matcher.start());
        textComponent.setSelectionEnd(matcher.end());
        return textComponent.getCaretPosition();
      }
    }
    else if (checkWholeWords.isSelected()) {
      if (pattern == null) {
        pattern = Pattern.compile("\\b" +findText +"\\b", Pattern.MULTILINE | Pattern.DOTALL | (checkCaseSensitive.isSelected() ? 0 : Pattern.CASE_INSENSITIVE)); //$NON-NLS-1$ //$NON-NLS-2$
      }
      Matcher matcher = pattern.matcher(text);
      if (matcher.find(cursor)) {
        textComponent.setSelectionStart(matcher.start());
        textComponent.setSelectionEnd(matcher.end());
        return textComponent.getCaretPosition();
      }
    }
    else {
      if (!checkCaseSensitive.isSelected()) {
        text = text.toUpperCase();
        findText = findText.toUpperCase();
      }
      if (checkForward.isSelected()) {
        int index = text.indexOf(findText, cursor);
        if (index >= 0) {
          textComponent.setSelectionStart(index);
          textComponent.setSelectionEnd(index +findText.length());
          return textComponent.getCaretPosition();
        }
      }
      else if (cursor > 0) {
        int index = text.lastIndexOf(findText, cursor -findText.length() -1);
        if (index >= 0) {
          textComponent.setSelectionStart(index);
          textComponent.setSelectionEnd(index +findText.length());
          return textComponent.getCaretPosition();
        }
      }
    }
    return -1;
  }
  
  private int replace() {
    if (textComponent.getSelectedText() != null) {
      String text = comboReplace.getText();
      if (text.indexOf("\\n") >= 0) {
        if (replaceOryginal == null || !text.equals(replaceOryginal)) {
          StringBuffer sb = new StringBuffer();
          int i = 0;
          while (i < text.length()) {
            char ch = text.charAt(i);
            if (ch == '\\' && (i == 0 || text.charAt(i -1) != '\\') && i < text.length() +1 && text.charAt(i +1) == 'n') {
              sb.append('\n');
              i++;
            }
            else if (ch == '\\' && i > 0 && text.charAt(i -1) == '\\') {
              sb.append('\\');
              i++;
            }
            else {
              sb.append(ch);
            }
            i++;
          }
          text = sb.toString();
          replaceTextTemp = text;
          replaceOryginal = comboReplace.getText();
        }
        else {
          text = replaceTextTemp;
        }
      }
      textComponent.replaceSelection(text);
    }
    return textComponent.getCaretPosition();
  }
  
  private void appendItems() {
    String itemText = comboFind.getText();
    for (int i=0; i<comboFind.getItemCount(); i++) {
      Object ia = comboFind.getItemAt(i);
      if (ia != null) {
        String item = ia.toString();
        if (item != null && item.equals(itemText)) {
          comboFind.removeItemAt(i);
          break;
        }
      }
    }
    comboFind.insertItemAt(itemText, 0);
    if (comboFind.getItemCount() > 50) {
      comboFind.removeItemAt(comboFind.getItemCount() -1);
    }
    comboFind.setText(itemText);

    itemText = comboReplace.getText();
    for (int i=0; i<comboReplace.getItemCount(); i++) {
      String item = comboReplace.getItemAt(i).toString();
      if (item != null && item.equals(itemText)) {
        comboReplace.removeItemAt(i);
        break;
      }
    }
    comboReplace.insertItemAt(itemText, 0);
    if (comboReplace.getItemCount() > 50) {
      comboReplace.removeItemAt(comboReplace.getItemCount() -1);
    }
    comboReplace.setText(itemText);
  }
  
  private void find(FindMode mode) {
    appendItems();
    
    int cursor = textComponent.getCaretPosition();
    if (firstTime && checkFromBegin.isSelected()) {
      cursor = 0;
    }
    firstTime = false;
    
    if (mode == FindMode.fmReplace) {
      replace();
    }
    else if (mode == FindMode.fmFind) {
      find(cursor);
    }
    else if (mode == FindMode.fmFindReplace) {
      replace();
      find(cursor);
    }
    else if (mode == FindMode.fmReplaceAll) {
      while ((cursor = find(cursor)) >= 0) {
        cursor = replace();
      }
    }
  }
  
  @Override
  public void dispose() {
    dialog = null;
    super.dispose();
  }
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    groupDirection = new javax.swing.ButtonGroup();
    groupScope = new javax.swing.ButtonGroup();
    cmClose = new pl.mpak.sky.gui.swing.Action();
    cmFind = new pl.mpak.sky.gui.swing.Action();
    cmFindReplace = new pl.mpak.sky.gui.swing.Action();
    cmReplace = new pl.mpak.sky.gui.swing.Action();
    cmReplaceAll = new pl.mpak.sky.gui.swing.Action();
    jLabel1 = new javax.swing.JLabel();
    comboFind = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel2 = new javax.swing.JLabel();
    comboReplace = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jPanel1 = new javax.swing.JPanel();
    checkForward = new javax.swing.JRadioButton();
    checkBackward = new javax.swing.JRadioButton();
    jPanel2 = new javax.swing.JPanel();
    checkFromBegin = new javax.swing.JRadioButton();
    checkAtCursor = new javax.swing.JRadioButton();
    jPanel3 = new javax.swing.JPanel();
    checkCaseSensitive = new javax.swing.JCheckBox();
    checkRegularExpr = new javax.swing.JCheckBox();
    checkWholeWords = new javax.swing.JCheckBox();
    buttonFindReplace = new javax.swing.JButton();
    buttonFind = new javax.swing.JButton();
    buttonReplace = new javax.swing.JButton();
    buttonReplaceAll = new javax.swing.JButton();
    buttonClose = new javax.swing.JButton();

    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(Messages.getString("FindTextDialog.cmClose-text")); //$NON-NLS-1$
    cmClose.setTooltip(Messages.getString("FindTextDialog.cmClose-hint")); //$NON-NLS-1$
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    cmFind.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmFind.setText(Messages.getString("FindTextDialog.cmFind-text")); //$NON-NLS-1$
    cmFind.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFindActionPerformed(evt);
      }
    });

    cmFindReplace.setText(Messages.getString("FindTextDialog.cmFindReplace-text")); //$NON-NLS-1$
    cmFindReplace.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFindReplaceActionPerformed(evt);
      }
    });

    cmReplace.setText(Messages.getString("FindTextDialog.cmReplace-text")); //$NON-NLS-1$
    cmReplace.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmReplaceActionPerformed(evt);
      }
    });

    cmReplaceAll.setText(Messages.getString("FindTextDialog.cmReplaceAll-text")); //$NON-NLS-1$
    cmReplaceAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmReplaceAllActionPerformed(evt);
      }
    });

    setTitle(Messages.getString("FindTextDialog.title")); //$NON-NLS-1$
    setAlwaysOnTop(true);
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setLabelFor(comboFind);
    jLabel1.setText(Messages.getString("FindTextDialog.find-dd")); //$NON-NLS-1$

    comboFind.setEditable(true);
    comboFind.setMaximumSize(new java.awt.Dimension(124, 22));
    comboFind.setMinimumSize(new java.awt.Dimension(124, 22));

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setLabelFor(comboReplace);
    jLabel2.setText(Messages.getString("FindTextDialog.replace-dd")); //$NON-NLS-1$

    comboReplace.setEditable(true);
    comboReplace.setMaximumSize(new java.awt.Dimension(124, 22));
    comboReplace.setMinimumSize(new java.awt.Dimension(124, 22));

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(Messages.getString("FindTextDialog.direction"))); //$NON-NLS-1$

    groupDirection.add(checkForward);
    checkForward.setSelected(true);
    checkForward.setText(Messages.getString("FindTextDialog.forward")); //$NON-NLS-1$

    groupDirection.add(checkBackward);
    checkBackward.setText(Messages.getString("FindTextDialog.backward")); //$NON-NLS-1$

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(checkForward, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
          .addComponent(checkBackward, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(checkForward)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkBackward)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(Messages.getString("FindTextDialog.scope"))); //$NON-NLS-1$

    groupScope.add(checkFromBegin);
    checkFromBegin.setText(Messages.getString("FindTextDialog.at-begining")); //$NON-NLS-1$

    groupScope.add(checkAtCursor);
    checkAtCursor.setSelected(true);
    checkAtCursor.setText(Messages.getString("FindTextDialog.at-cursor")); //$NON-NLS-1$

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(checkFromBegin, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
          .addComponent(checkAtCursor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addComponent(checkFromBegin)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(checkAtCursor)
        .addContainerGap(4, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(Messages.getString("FindTextDialog.options"))); //$NON-NLS-1$

    checkCaseSensitive.setText(Messages.getString("FindTextDialog.match-case")); //$NON-NLS-1$
    checkCaseSensitive.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkCaseSensitiveStateChanged(evt);
      }
    });

    checkRegularExpr.setText(Messages.getString("FindTextDialog.reg-expr")); //$NON-NLS-1$
    checkRegularExpr.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkRegularExprStateChanged(evt);
      }
    });

    checkWholeWords.setText(Messages.getString("FindTextDialog.whole-words")); //$NON-NLS-1$
    checkWholeWords.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkWholeWordsStateChanged(evt);
      }
    });

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(checkCaseSensitive)
          .addComponent(checkWholeWords)
          .addComponent(checkRegularExpr))
        .addContainerGap(167, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addComponent(checkCaseSensitive)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkWholeWords)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkRegularExpr))
    );

    buttonFindReplace.setAction(cmFindReplace);
    buttonFindReplace.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonFindReplace.setPreferredSize(new java.awt.Dimension(85, 24));

    buttonFind.setAction(cmFind);
    buttonFind.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonFind.setPreferredSize(new java.awt.Dimension(85, 24));

    buttonReplace.setAction(cmReplace);
    buttonReplace.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonReplace.setPreferredSize(new java.awt.Dimension(85, 24));

    buttonReplaceAll.setAction(cmReplaceAll);
    buttonReplaceAll.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonReplaceAll.setPreferredSize(new java.awt.Dimension(85, 24));

    buttonClose.setAction(cmClose);
    buttonClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonClose.setPreferredSize(new java.awt.Dimension(85, 24));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(comboReplace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(comboFind, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonFindReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonReplaceAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
            .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(comboFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jPanel2, 0, 79, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonFindReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonReplaceAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
  storeSettings();
  setVisible(false);
}//GEN-LAST:event_cmCloseActionPerformed

private void cmFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFindActionPerformed
  find(FindMode.fmFind);
}//GEN-LAST:event_cmFindActionPerformed

private void cmFindReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFindReplaceActionPerformed
  find(FindMode.fmFindReplace);
}//GEN-LAST:event_cmFindReplaceActionPerformed

private void cmReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmReplaceActionPerformed
  find(FindMode.fmReplace);
}//GEN-LAST:event_cmReplaceActionPerformed

private void cmReplaceAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmReplaceAllActionPerformed
  find(FindMode.fmReplaceAll);
}//GEN-LAST:event_cmReplaceAllActionPerformed

private void checkRegularExprStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkRegularExprStateChanged
  checkWholeWords.setEnabled(!checkRegularExpr.isSelected());
  pattern = null;
}//GEN-LAST:event_checkRegularExprStateChanged

private void checkWholeWordsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkWholeWordsStateChanged
  pattern = null;
}//GEN-LAST:event_checkWholeWordsStateChanged

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (textComponent.getSelectedText() != null) {
    String selectedText = textComponent.getSelectedText();
    if (selectedText.indexOf('\n') > 0) {
      selectedText = selectedText.substring(0, selectedText.indexOf('\n'));
    }
    comboFind.setText(selectedText);
  }
}//GEN-LAST:event_formComponentShown

private void checkCaseSensitiveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkCaseSensitiveStateChanged
  pattern = null;
}//GEN-LAST:event_checkCaseSensitiveStateChanged

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonClose;
  private javax.swing.JButton buttonFind;
  private javax.swing.JButton buttonFindReplace;
  private javax.swing.JButton buttonReplace;
  private javax.swing.JButton buttonReplaceAll;
  private javax.swing.JRadioButton checkAtCursor;
  private javax.swing.JRadioButton checkBackward;
  private javax.swing.JCheckBox checkCaseSensitive;
  private javax.swing.JRadioButton checkForward;
  private javax.swing.JRadioButton checkFromBegin;
  private javax.swing.JCheckBox checkRegularExpr;
  private javax.swing.JCheckBox checkWholeWords;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmFind;
  private pl.mpak.sky.gui.swing.Action cmFindReplace;
  private pl.mpak.sky.gui.swing.Action cmReplace;
  private pl.mpak.sky.gui.swing.Action cmReplaceAll;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboFind;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboReplace;
  private javax.swing.ButtonGroup groupDirection;
  private javax.swing.ButtonGroup groupScope;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  // End of variables declaration//GEN-END:variables

}
