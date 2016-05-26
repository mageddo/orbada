/*
 * CopyCodyDialog.java
 *
 * Created on 3 listopad 2007, 16:16
 */

package pl.mpak.orbada.programmers.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParameterList;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.array.StringList;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class CopyCodeDialog extends javax.swing.JDialog {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaProgrammersPlugin.class);

  private ISettings settings;

  private String text;
  private IApplication application;
  private boolean trimSpaceBars = false;
  private boolean keepFormating = false;
  private DocumentListener documentListener;
  private boolean groupUpdate;
  
  /** 
   * Creates new form CopyCodyDialog 
   * @param text 
   * @param application 
   */
  public CopyCodeDialog(String text, IApplication application) {
    super(SwingUtil.getRootFrame());
    this.text = text;
    this.application = application;
    initComponents();
    init();
  }
  
  /**
   * 
   * @param text 
   * @param application 
   */
  public static void show(String text, IApplication application) {
    CopyCodeDialog dialog = new CopyCodeDialog(text, application);
    dialog.setVisible(true);
  }
  
  private void init() {
    settings = application.getSettings("orbada-programmers-copy-code");
    try {
      setBounds(0, 0, settings.getValue("dialog-width", new Variant(getWidth())).getInteger(), settings.getValue("dialog-height", new Variant(getHeight())).getInteger());
    } catch (Exception ex) {
    }
    SwingUtil.centerWithinScreen(this);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonOk);
    
    textBeforeAll.getDocument().addDocumentListener(getDocumentListener());
    textAddBefore.getDocument().addDocumentListener(getDocumentListener());
    textBeforeChars.getDocument().addDocumentListener(getDocumentListener());
    textAddAfter.getDocument().addDocumentListener(getDocumentListener());
    textChars.getDocument().addDocumentListener(getDocumentListener());
    textEndLast.getDocument().addDocumentListener(getDocumentListener());
    textAfterAll.getDocument().addDocumentListener(getDocumentListener());
    textParamAs.getDocument().addDocumentListener(getDocumentListener());
    
    textResult.setFont(syntaxSource.getEditorArea().getFont());
    syntaxSource.setText(text);

    comboDefinitions.addItem(new CopyCodeDefinition("Pascal", "", "  '", "'#13#10 +", "';", "'", "'", "", "", false));
    comboDefinitions.addItem(new CopyCodeDefinition("C++", "", "  \"", "\\n\"", "\";", "\"\\", "\\", "", "", false));
    comboDefinitions.addItem(new CopyCodeDefinition("Java", "", "  \"", "\\n\" +", "\";", "\"\\", "\\", "", "? /* ($(index)) $(name) */", false));
    long userDefinitions = settings.getValue("user-defnition-count", 0L);
    for (int i=0; i<userDefinitions; i++) {
      CopyCodeDefinition cc = new CopyCodeDefinition(settings.getValue("user-defnition-" +i, "Unknown name"));
      cc.load(settings);
      comboDefinitions.addItem(cc);
    }
    trimSpaceBars = settings.getValue("trim-spacebars", trimSpaceBars);
    checkTrimSpaceBars.setSelected(trimSpaceBars);
    keepFormating = settings.getValue("keep-formating", keepFormating);
    checkKeepFormatting.setSelected(keepFormating);
    String lastSelected = settings.getValue("last-selected", "");
    if (!StringUtil.isEmpty(lastSelected)) {
      for (int i=0; i<comboDefinitions.getItemCount(); i++) {
        CopyCodeDefinition cc = (CopyCodeDefinition)comboDefinitions.getItemAt(i);
        if (cc.getName().equals(lastSelected)) {
          comboDefinitions.setSelectedItem(cc);
        }
      }
    }

    checkKeepFormatting.setEnabled(checkTrimSpaceBars.isSelected());
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel});
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonUpdate, buttonDelete});

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        buttonOk.requestFocus();
      }
    });
  }
  
  @Override
  public void dispose() {
    int userDefinitions = 0;
    for (int i=0; i<comboDefinitions.getItemCount(); i++) {
      CopyCodeDefinition cc = (CopyCodeDefinition)comboDefinitions.getItemAt(i);
      if (cc.isUpdatable()) {
        settings.setValue("user-defnition-" +userDefinitions, cc.getName());
        cc.store(settings);
        userDefinitions++;
      }
    }
    settings.setValue("user-defnition-count", (long)userDefinitions);
    settings.setValue("trim-spacebars", checkTrimSpaceBars.isSelected());
    settings.setValue("keep-formating", checkKeepFormatting.isSelected());
    if (comboDefinitions.getSelectedItem() != null) {
      CopyCodeDefinition cc = (CopyCodeDefinition)comboDefinitions.getSelectedItem();
      settings.setValue("last-selected", cc.getName());
    }
    
    settings.setValue("dialog-width", new Variant(getWidth()));
    settings.setValue("dialog-height", new Variant(getHeight()));
    settings.store();
    super.dispose();
  }
  
  private DocumentListener getDocumentListener() {
    if (documentListener == null) {
      documentListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          if (isVisible()) {
            updateResultText();
          }
        }
        public void removeUpdate(DocumentEvent e) {
          if (isVisible()) {
            updateResultText();
          }
        }
        public void changedUpdate(DocumentEvent e) {
        }
      };
    }
    return documentListener;
  }
  
  private void updateResultText() {
    if (groupUpdate) {
      return;
    }
    String beforeChars = textBeforeChars.getText();
    StringList sl = new StringList();
    if ("".equals(textParamAs.getText())) {
      sl.setText(syntaxSource.getText());
    }
    else {
      ParameterList pl = new ParameterList();
      try {
        sl.setText(pl.parseParameters(syntaxSource.getText(), textParamAs.getText()));
      } catch (UseDBException ex) {
        sl.setText(ex.getMessage());
        ExceptionUtil.processException(ex);
        return;
      }
    }
    StringBuilder newLine = new StringBuilder();
    for (int i=0; i<sl.size(); i++) {
      newLine.setLength(0);
      String line = sl.get(i);
      int spaceCount = 0;
      if (checkTrimSpaceBars.isSelected()) {
        if (checkKeepFormatting.isSelected()) {
          spaceCount = line.length() -line.trim().length();
        }
        line = line.trim();
      }
      for (int c=0; c<line.length(); c++) {
        char ch = line.charAt(c);
        if (beforeChars.indexOf(ch) >= 0) {
          newLine.append(textChars.getText());
        }
        newLine.append(ch);
      }
      line = textAddBefore.getText() +newLine.toString() +(i==sl.size() -1 ? textEndLast.getText() : textAddAfter.getText());
      if (spaceCount > 0) {
       line = String.format("%1$" + spaceCount + "s", " ") +line;
      }
      sl.set(i, line);
    }
    if (textBeforeAll.getText().length() > 0) {
      sl.add(0, textBeforeAll.getText());
    }
    if (textAfterAll.getText().length() > 0) {
      sl.add(textAfterAll.getText());
    }
    textResult.setText(sl.getText());
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmCancel = new pl.mpak.sky.gui.swing.Action();
        cmCopy = new pl.mpak.sky.gui.swing.Action();
        cmNewSet = new pl.mpak.sky.gui.swing.Action();
        cmDeleteSet = new pl.mpak.sky.gui.swing.Action();
        cmUpdateSet = new pl.mpak.sky.gui.swing.Action();
        cmUpdateText = new pl.mpak.sky.gui.swing.Action();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        comboDefinitions = new javax.swing.JComboBox();
        buttonNew = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();
        buttonUpdate = new javax.swing.JButton();
        checkTrimSpaceBars = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        textAddBefore = new pl.mpak.sky.gui.swing.comp.TextField();
        jLabel3 = new javax.swing.JLabel();
        textAddAfter = new pl.mpak.sky.gui.swing.comp.TextField();
        jLabel4 = new javax.swing.JLabel();
        textEndLast = new pl.mpak.sky.gui.swing.comp.TextField();
        jLabel5 = new javax.swing.JLabel();
        textBeforeChars = new pl.mpak.sky.gui.swing.comp.TextField();
        jLabel6 = new javax.swing.JLabel();
        textChars = new pl.mpak.sky.gui.swing.comp.TextField();
        syntaxSource = new OrbadaSyntaxTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        textResult = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        textBeforeAll = new pl.mpak.sky.gui.swing.comp.TextField();
        jLabel9 = new javax.swing.JLabel();
        textParamAs = new pl.mpak.sky.gui.swing.comp.TextField();
        jLabel8 = new javax.swing.JLabel();
        textAfterAll = new pl.mpak.sky.gui.swing.comp.TextField();
        checkKeepFormatting = new javax.swing.JCheckBox();

        cmCancel.setActionCommandKey("cmCancel");
        cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
        cmCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCancelActionPerformed(evt);
            }
        });

        cmCopy.setActionCommandKey("cmCopy");
        cmCopy.setText(stringManager.getString("cmCopy-text")); // NOI18N
        cmCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCopyActionPerformed(evt);
            }
        });

        cmNewSet.setText(stringManager.getString("cmNewSet-text")); // NOI18N
        cmNewSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmNewSetActionPerformed(evt);
            }
        });

        cmDeleteSet.setText(stringManager.getString("cmDeleteSet-text")); // NOI18N
        cmDeleteSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDeleteSetActionPerformed(evt);
            }
        });

        cmUpdateSet.setText(stringManager.getString("cmUpdateSet-text")); // NOI18N
        cmUpdateSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmUpdateSetActionPerformed(evt);
            }
        });

        cmUpdateText.setText(stringManager.getString("cmUpdateText-text")); // NOI18N
        cmUpdateText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmUpdateTextActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(stringManager.getString("CopyCodeDialog-title")); // NOI18N
        setModal(true);

        buttonOk.setAction(cmCopy);
        buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonOk.setPreferredSize(new java.awt.Dimension(85, 24));

        buttonCancel.setAction(cmCancel);
        buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonCancel.setPreferredSize(new java.awt.Dimension(85, 24));

        jLabel1.setText(stringManager.getString("defined-settings-dd")); // NOI18N

        comboDefinitions.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboDefinitionsItemStateChanged(evt);
            }
        });

        buttonNew.setAction(cmNewSet);
        buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonNew.setPreferredSize(new java.awt.Dimension(85, 24));

        buttonDelete.setAction(cmDeleteSet);
        buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonDelete.setPreferredSize(new java.awt.Dimension(85, 24));

        buttonUpdate.setAction(cmUpdateSet);
        buttonUpdate.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonUpdate.setPreferredSize(new java.awt.Dimension(85, 24));

        checkTrimSpaceBars.setText(stringManager.getString("remove-extra-spaces")); // NOI18N
        checkTrimSpaceBars.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkTrimSpaceBars.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkTrimSpaceBars.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkTrimSpaceBarsItemStateChanged(evt);
            }
        });
        checkTrimSpaceBars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkTrimSpaceBarsActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("for-each-line"))); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(stringManager.getString("add-before-dd")); // NOI18N

        textAddBefore.setText("  \"");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(stringManager.getString("add-after-dd")); // NOI18N

        textAddAfter.setText("\\n\" +");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(stringManager.getString("finish-last-dd")); // NOI18N

        textEndLast.setText("\";");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(stringManager.getString("precede-characters-dd")); // NOI18N

        textBeforeChars.setText("\"\\");

            jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel6.setText(stringManager.getString("with-char-dd")); // NOI18N

            textChars.setText("\\");

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textAddBefore, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textBeforeChars, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textChars, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textAddAfter, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textEndLast, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(291, Short.MAX_VALUE))
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(textAddBefore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(textBeforeChars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(textChars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(textAddAfter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(textEndLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                );

                syntaxSource.setEditable(false);

                textResult.setColumns(20);
                textResult.setEditable(false);
                textResult.setFont(new java.awt.Font("Courier New", 0, 11));
                textResult.setRows(5);
                jScrollPane1.setViewportView(textResult);

                jLabel7.setText(stringManager.getString("add-before-lines-dd")); // NOI18N

                jLabel9.setText(stringManager.getString("named-params-replace-by-dd")); // NOI18N

                jLabel8.setText(stringManager.getString("add-after-lines-dd")); // NOI18N

                checkKeepFormatting.setText(stringManager.getString("keep-formatting")); // NOI18N
                checkKeepFormatting.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                        checkKeepFormattingItemStateChanged(evt);
                    }
                });

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(syntaxSource, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel7)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(textBeforeAll, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(textParamAs, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGap(12, 12, 12)
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(comboDefinitions, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGap(11, 11, 11)
                                            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(buttonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 412, Short.MAX_VALUE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(buttonOk, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textAfterAll, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkTrimSpaceBars)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkKeepFormatting)))
                        .addContainerGap())
                );
                layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(comboDefinitions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buttonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(textBeforeAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel9)
                                    .addComponent(textParamAs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(textAfterAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkTrimSpaceBars)
                            .addComponent(checkKeepFormatting))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addComponent(syntaxSource, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
                        .addContainerGap())
                );

                pack();
            }// </editor-fold>//GEN-END:initComponents

private void cmUpdateTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUpdateTextActionPerformed
  updateResultText();
}//GEN-LAST:event_cmUpdateTextActionPerformed

private void cmDeleteSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteSetActionPerformed
  if (comboDefinitions.getSelectedItem() != null) {
    CopyCodeDefinition cc = (CopyCodeDefinition)comboDefinitions.getSelectedItem();
    if (cc.isUpdatable()) {
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("CopyCodeDialog-delete-def-q"), new Object[] {cc.getName()}), ModalResult.YESNO) == ModalResult.YES) {
        comboDefinitions.removeItem(cc);
      }
    }
  }
}//GEN-LAST:event_cmDeleteSetActionPerformed

private void cmUpdateSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUpdateSetActionPerformed
  if (comboDefinitions.getSelectedItem() != null) {
    CopyCodeDefinition cc = (CopyCodeDefinition)comboDefinitions.getSelectedItem();
    if (cc.isUpdatable()) {
      cc.setBeforeAll(textBeforeAll.getText());
      cc.setAfterAll(textAfterAll.getText());
      cc.setAddBefore(textAddBefore.getText());
      cc.setAddAfter(textAddAfter.getText());
      cc.setEndLast(textEndLast.getText());
      cc.setBeforeChars(textBeforeChars.getText());
      cc.setChars(textChars.getText());
      cc.setParamAs(textParamAs.getText());
    }
  }
}//GEN-LAST:event_cmUpdateSetActionPerformed

private void cmNewSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewSetActionPerformed
  String name = JOptionPane.showInputDialog(this, stringManager.getString("input-name-dd"));
  if (name != null) {
    CopyCodeDefinition cc = new CopyCodeDefinition(name);
    cc.setBeforeAll(textBeforeAll.getText());
    cc.setAfterAll(textAfterAll.getText());
    cc.setAddBefore(textAddBefore.getText());
    cc.setAddAfter(textAddAfter.getText());
    cc.setEndLast(textEndLast.getText());
    cc.setBeforeChars(textBeforeChars.getText());
    cc.setChars(textChars.getText());
    cc.setParamAs(textParamAs.getText());
    comboDefinitions.addItem(cc);
    comboDefinitions.setSelectedItem(cc);
  }
}//GEN-LAST:event_cmNewSetActionPerformed

private void comboDefinitionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboDefinitionsItemStateChanged
  groupUpdate = true;
  try {
    if (comboDefinitions.getSelectedItem() != null) {
      CopyCodeDefinition cc = (CopyCodeDefinition)comboDefinitions.getSelectedItem();
      cmDeleteSet.setEnabled(cc.isUpdatable());
      cmUpdateSet.setEnabled(cc.isUpdatable());

      textBeforeAll.setText(cc.getBeforeAll());
      textAfterAll.setText(cc.getAfterAll());
      textAddBefore.setText(cc.getAddBefore());
      textAddAfter.setText(cc.getAddAfter());
      textEndLast.setText(cc.getEndLast());
      textBeforeChars.setText(cc.getBeforeChars());
      textChars.setText(cc.getChars());
      textParamAs.setText(cc.getParamAs());
    }
    else {
      cmDeleteSet.setEnabled(false);
      textBeforeAll.setText("");
      textAfterAll.setText("");
      textAddBefore.setText("");
      textAddAfter.setText("");
      textEndLast.setText("");
      textBeforeChars.setText("");
      textChars.setText("");
      textParamAs.setText("");
    }
  }
  finally {
    groupUpdate = false;
    updateResultText();
  }
}//GEN-LAST:event_comboDefinitionsItemStateChanged

private void cmCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCopyActionPerformed
  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  StringSelection data = new StringSelection(textResult.getText());
  clipboard.setContents(data, data);
  dispose();
}//GEN-LAST:event_cmCopyActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void checkTrimSpaceBarsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkTrimSpaceBarsItemStateChanged
  if (isVisible()) {
    updateResultText();
  }
}//GEN-LAST:event_checkTrimSpaceBarsItemStateChanged

private void checkTrimSpaceBarsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkTrimSpaceBarsActionPerformed
  checkKeepFormatting.setEnabled(checkTrimSpaceBars.isSelected());
}//GEN-LAST:event_checkTrimSpaceBarsActionPerformed

private void checkKeepFormattingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkKeepFormattingItemStateChanged
  if (isVisible()) {
    updateResultText();
  }
}//GEN-LAST:event_checkKeepFormattingItemStateChanged
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonNew;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonUpdate;
    private javax.swing.JCheckBox checkKeepFormatting;
    private javax.swing.JCheckBox checkTrimSpaceBars;
    private pl.mpak.sky.gui.swing.Action cmCancel;
    private pl.mpak.sky.gui.swing.Action cmCopy;
    private pl.mpak.sky.gui.swing.Action cmDeleteSet;
    private pl.mpak.sky.gui.swing.Action cmNewSet;
    private pl.mpak.sky.gui.swing.Action cmUpdateSet;
    private pl.mpak.sky.gui.swing.Action cmUpdateText;
    private javax.swing.JComboBox comboDefinitions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private OrbadaSyntaxTextArea syntaxSource;
    private pl.mpak.sky.gui.swing.comp.TextField textAddAfter;
    private pl.mpak.sky.gui.swing.comp.TextField textAddBefore;
    private pl.mpak.sky.gui.swing.comp.TextField textAfterAll;
    private pl.mpak.sky.gui.swing.comp.TextField textBeforeAll;
    private pl.mpak.sky.gui.swing.comp.TextField textBeforeChars;
    private pl.mpak.sky.gui.swing.comp.TextField textChars;
    private pl.mpak.sky.gui.swing.comp.TextField textEndLast;
    private pl.mpak.sky.gui.swing.comp.TextField textParamAs;
    private javax.swing.JTextArea textResult;
    // End of variables declaration//GEN-END:variables
  
}
