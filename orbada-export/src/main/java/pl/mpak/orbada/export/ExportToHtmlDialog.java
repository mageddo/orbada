/*
 * ExportToHtmlDialog.java
 *
 * Created on 25 paüdziernik 2007, 18:41
 */

package pl.mpak.orbada.export;

import java.io.File;
import java.nio.charset.Charset;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ExportToHtmlDialog extends javax.swing.JDialog {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaExportPlugin.class);

  private ISettings config;
  private int modalResult = pl.mpak.sky.gui.mr.ModalResult.NONE;
  private DocumentListener changeListener;
  
  /** Creates new form ExportToHtmlDialog
   * @param config
   */
  public ExportToHtmlDialog(ISettings config) {
    super(SwingUtil.getRootFrame());
    this.config = config;
    initComponents();
    init();
  }
  
  public static boolean showDialog(ISettings config) {
    ExportToHtmlDialog dialog = new ExportToHtmlDialog(config);
    dialog.setVisible(true);
    return dialog.modalResult == ModalResult.OK;
  }
  
  private void init() {
    for (Charset charset : Charset.availableCharsets().values()) {
      comboCharset.addItem(charset.displayName());
    }
    changeListener = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        updateCodes();
      }
      public void removeUpdate(DocumentEvent e) {
        updateCodes();
      }
      public void changedUpdate(DocumentEvent e) {
      }
    };
    textTableCode.getDocument().addDocumentListener(changeListener);
    textTableRowCode.getDocument().addDocumentListener(changeListener);
    textTableHeaderCode.getDocument().addDocumentListener(changeListener);
    textTableDataCode.getDocument().addDocumentListener(changeListener);

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonOk);
    configToDialog();
    SwingUtil.centerWithinScreen(this);
  }
  
  private void updateCodes() {
    labelEndTableCode.setText(String.format("<html>&lt;<b>/%s</b>&gt;", new Object[] {textTableCode.getText()}));
    labelEndTableRowCode.setText(String.format("<html>&lt;<b>/%s</b>&gt;", new Object[] {textTableRowCode.getText()}));
    labelEndTableHeaderCode.setText(String.format("<html>&lt;<b>/%s</b>&gt;", new Object[] {textTableHeaderCode.getText()}));
    labelEndTableDataCode.setText(String.format("<html>&lt;<b>/%s</b>&gt;", new Object[] {textTableDataCode.getText()}));
  }
  
  private void configToDialog() {
    try {
      textTitle.setText(config.getValue("title", new Variant("Orbada Data Table")).getString());
      textMetaCode.setText(config.getValue("meta-code", new Variant("<META NAME=\"Author\" CONTENT=\"Jan Kowalski\">\n<META NAME=\"Keywords\" CONTENT=\"orbada, table, data\">")).getString());
      textBodyAttrib.setText(config.getValue("body-attrib", new Variant("style=\"font-family:Tahoma,Verdana,Arial,Helvetica,sans-serif;\"")).getString());
      
      textTableCode.setText(config.getValue("table-code", new Variant("TABLE")).getString());
      textTableAttrib.setText(config.getValue("table-attrib", new Variant("width=\"100%\"")).getString());
      textTableRowCode.setText(config.getValue("table-row-code", new Variant("TR")).getString());
      textTableRowAttrib.setText(config.getValue("table-row-attrib").getString());
      textTableHeaderCode.setText(config.getValue("table-header-code", new Variant("TH")).getString());
      textTableHeaderAttrib.setText(config.getValue("table-header-attrib").getString());
      textTableDataCode.setText(config.getValue("table-data-code", new Variant("TD")).getString());
      textTableDataAttrib.setText(config.getValue("table-data-attrib").getString());
      
      textNullDataValue.setText(config.getValue("null-data-value", new Variant("&nbsp;")).getString());
      checkFirstLastAsNbsp.setSelected(config.getValue("first-last-as-nbsp", false));
      
      checkIncludeTitles.setSelected(config.getValue("include-titles", new Variant(true)).getBoolean());
      comboCharset.setSelectedItem(config.getValue("charset", new Variant(Charset.defaultCharset().displayName())).getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void dialogToConfig() {
    config.setValue("title", textTitle.getText());
    config.setValue("meta-code", textMetaCode.getText());
    config.setValue("body-attrib", textBodyAttrib.getText());
    
    config.setValue("table-code", textTableCode.getText());
    config.setValue("table-attrib", textTableAttrib.getText());
    config.setValue("table-row-code", textTableRowCode.getText());
    config.setValue("table-row-attrib", textTableRowAttrib.getText());
    config.setValue("table-header-code", textTableHeaderCode.getText());
    config.setValue("table-header-attrib", textTableHeaderAttrib.getText());
    config.setValue("table-data-code", textTableDataCode.getText());
    config.setValue("table-data-attrib", textTableDataAttrib.getText());
    
    config.setValue("null-data-value", textNullDataValue.getText());
    config.setValue("first-last-as-nbsp", checkFirstLastAsNbsp.isSelected());
    
    config.setValue("include-titles", new Variant(checkIncludeTitles.isSelected()));
    config.setValue("charset", new Variant(comboCharset.getSelectedItem().toString()));
    config.store();
  }
  
  private boolean selectFile() {
    File lastFile = null;
    try {
      if (!config.getValue("file-name").getString().equals("")) {
        lastFile = new File(config.getValue("file-name").getString());
      }
    } catch (Exception ex) {
    }
    lastFile = FileUtil.selectFileToSave(this, null, null, lastFile, new FileExtensionFilter[] {new FileExtensionFilter("Pliki HTML", new String[] { ".htm", ".html" })});
    if (lastFile != null) {
      config.setValue("file-name", new Variant(lastFile.getAbsoluteFile()));
      return true;
    }
    return false;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmOk = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel();
    jPanel18 = new javax.swing.JPanel();
    jLabel13 = new javax.swing.JLabel();
    textTableCode = new pl.mpak.sky.gui.swing.comp.TextField();
    textTableAttrib = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel14 = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    jPanel11 = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    textTitle = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel5 = new javax.swing.JLabel();
    jPanel4 = new javax.swing.JPanel();
    jLabel9 = new javax.swing.JLabel();
    textBodyAttrib = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel10 = new javax.swing.JLabel();
    jPanel5 = new javax.swing.JPanel();
    jPanel19 = new javax.swing.JPanel();
    jLabel15 = new javax.swing.JLabel();
    textTableRowCode = new pl.mpak.sky.gui.swing.comp.TextField();
    textTableRowAttrib = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel16 = new javax.swing.JLabel();
    jPanel6 = new javax.swing.JPanel();
    jPanel20 = new javax.swing.JPanel();
    jLabel21 = new javax.swing.JLabel();
    textTableHeaderCode = new pl.mpak.sky.gui.swing.comp.TextField();
    textTableHeaderAttrib = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel22 = new javax.swing.JLabel();
    checkIncludeTitles = new javax.swing.JCheckBox();
    labelEndTableHeaderCode = new javax.swing.JLabel();
    jPanel7 = new javax.swing.JPanel();
    jPanel21 = new javax.swing.JPanel();
    jLabel23 = new javax.swing.JLabel();
    textTableDataCode = new pl.mpak.sky.gui.swing.comp.TextField();
    textTableDataAttrib = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel24 = new javax.swing.JLabel();
    jLabel17 = new javax.swing.JLabel();
    textNullDataValue = new pl.mpak.sky.gui.swing.comp.TextField();
    labelEndTableDataCode = new javax.swing.JLabel();
    jPanel8 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jPanel9 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    jPanel10 = new javax.swing.JPanel();
    jPanel12 = new javax.swing.JPanel();
    jLabel6 = new javax.swing.JLabel();
    jPanel13 = new javax.swing.JPanel();
    jPanel14 = new javax.swing.JPanel();
    jLabel7 = new javax.swing.JLabel();
    comboCharset = new javax.swing.JComboBox();
    jLabel12 = new javax.swing.JLabel();
    jPanel15 = new javax.swing.JPanel();
    jPanel16 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textMetaCode = new pl.mpak.sky.gui.swing.comp.TextArea();
    jPanel17 = new javax.swing.JPanel();
    jLabel8 = new javax.swing.JLabel();
    jPanel22 = new javax.swing.JPanel();
    jPanel23 = new javax.swing.JPanel();
    labelEndTableRowCode = new javax.swing.JLabel();
    jPanel24 = new javax.swing.JPanel();
    jPanel25 = new javax.swing.JPanel();
    labelEndTableCode = new javax.swing.JLabel();
    jPanel26 = new javax.swing.JPanel();
    jLabel11 = new javax.swing.JLabel();
    jPanel27 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    jPanel28 = new javax.swing.JPanel();
    jPanel29 = new javax.swing.JPanel();
    checkFirstLastAsNbsp = new javax.swing.JCheckBox();

    cmOk.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmOk.setText(stringManager.getString("cmOk-text")); // NOI18N
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("ExportToHtmlDialog-title")); // NOI18N
    setModal(true);

    buttonOk.setAction(cmOk);
    buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel18.setPreferredSize(new java.awt.Dimension(20, 10));

    javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
    jPanel18.setLayout(jPanel18Layout);
    jPanel18Layout.setHorizontalGroup(
      jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 20, Short.MAX_VALUE)
    );
    jPanel18Layout.setVerticalGroup(
      jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel1.add(jPanel18);

    jLabel13.setText("<html>&lt;");
    jPanel1.add(jLabel13);

    textTableCode.setText("TABLE");
    textTableCode.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableCode.setPreferredSize(new java.awt.Dimension(100, 20));
    jPanel1.add(textTableCode);

    textTableAttrib.setText("width=\"100%\"");
    textTableAttrib.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableAttrib.setPreferredSize(new java.awt.Dimension(250, 20));
    jPanel1.add(textTableAttrib);

    jLabel14.setText("<html>&gt;");
    jPanel1.add(jLabel14);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel11.setPreferredSize(new java.awt.Dimension(20, 10));

    javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
    jPanel11.setLayout(jPanel11Layout);
    jPanel11Layout.setHorizontalGroup(
      jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 20, Short.MAX_VALUE)
    );
    jPanel11Layout.setVerticalGroup(
      jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel2.add(jPanel11);

    jLabel4.setText("<html>&lt;<b>TITLE</b>&gt;");
    jPanel2.add(jLabel4);

    textTitle.setText("Orbada Data Table");
    textTitle.setFont(new java.awt.Font("Courier New", 0, 12));
    textTitle.setPreferredSize(new java.awt.Dimension(300, 20));
    jPanel2.add(textTitle);

    jLabel5.setText("<html>&lt;<b>/TITLE</b>&gt;");
    jPanel2.add(jLabel5);

    jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jLabel9.setText("<html>&lt;<b>BODY</b>");
    jPanel4.add(jLabel9);

    textBodyAttrib.setText("style=\"font-family:Tahoma,Verdana,Arial,Helvetica,sans-serif;\"");
    textBodyAttrib.setFont(new java.awt.Font("Courier New", 0, 12));
    textBodyAttrib.setPreferredSize(new java.awt.Dimension(500, 20));
    jPanel4.add(textBodyAttrib);

    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel10.setText("<html>&gt;");
    jPanel4.add(jLabel10);

    jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel19.setPreferredSize(new java.awt.Dimension(40, 10));

    javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
    jPanel19.setLayout(jPanel19Layout);
    jPanel19Layout.setHorizontalGroup(
      jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 40, Short.MAX_VALUE)
    );
    jPanel19Layout.setVerticalGroup(
      jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel5.add(jPanel19);

    jLabel15.setText("<html>&lt;");
    jPanel5.add(jLabel15);

    textTableRowCode.setText("TR");
    textTableRowCode.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableRowCode.setPreferredSize(new java.awt.Dimension(100, 20));
    jPanel5.add(textTableRowCode);

    textTableRowAttrib.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableRowAttrib.setPreferredSize(new java.awt.Dimension(250, 20));
    jPanel5.add(textTableRowAttrib);

    jLabel16.setText("<html>&gt;");
    jPanel5.add(jLabel16);

    jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel20.setPreferredSize(new java.awt.Dimension(60, 10));

    javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
    jPanel20.setLayout(jPanel20Layout);
    jPanel20Layout.setHorizontalGroup(
      jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 60, Short.MAX_VALUE)
    );
    jPanel20Layout.setVerticalGroup(
      jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel6.add(jPanel20);

    jLabel21.setText("<html>&lt;");
    jPanel6.add(jLabel21);

    textTableHeaderCode.setText("TH");
    textTableHeaderCode.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableHeaderCode.setPreferredSize(new java.awt.Dimension(100, 20));
    jPanel6.add(textTableHeaderCode);

    textTableHeaderAttrib.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableHeaderAttrib.setPreferredSize(new java.awt.Dimension(250, 20));
    jPanel6.add(textTableHeaderAttrib);

    jLabel22.setText("<html>&gt;");
    jPanel6.add(jLabel22);

    checkIncludeTitles.setSelected(true);
    checkIncludeTitles.setText(stringManager.getString("ExportToHtmlDialog-checkIncludeTitles-text")); // NOI18N
    checkIncludeTitles.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jPanel6.add(checkIncludeTitles);

    labelEndTableHeaderCode.setText("<html>&lt;<b>/TH</b>&gt;");
    jPanel6.add(labelEndTableHeaderCode);

    jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel21.setPreferredSize(new java.awt.Dimension(60, 10));

    javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
    jPanel21.setLayout(jPanel21Layout);
    jPanel21Layout.setHorizontalGroup(
      jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 60, Short.MAX_VALUE)
    );
    jPanel21Layout.setVerticalGroup(
      jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel7.add(jPanel21);

    jLabel23.setText("<html>&lt;");
    jPanel7.add(jLabel23);

    textTableDataCode.setText("TD");
    textTableDataCode.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableDataCode.setPreferredSize(new java.awt.Dimension(100, 20));
    jPanel7.add(textTableDataCode);

    textTableDataAttrib.setFont(new java.awt.Font("Courier New", 0, 12));
    textTableDataAttrib.setPreferredSize(new java.awt.Dimension(250, 20));
    jPanel7.add(textTableDataAttrib);

    jLabel24.setText("<html>&gt;");
    jPanel7.add(jLabel24);

    jLabel17.setText(stringManager.getString("ExportToHtmlDialog-empty-value-dd")); // NOI18N
    jPanel7.add(jLabel17);

    textNullDataValue.setText("&nbsp;");
    textNullDataValue.setFont(new java.awt.Font("Courier New", 0, 12));
    textNullDataValue.setPreferredSize(new java.awt.Dimension(60, 20));
    jPanel7.add(textNullDataValue);

    labelEndTableDataCode.setText("<html>&lt;<b>/TD</b>&gt;");
    jPanel7.add(labelEndTableDataCode);

    jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jLabel1.setText("<html>&lt;<b>HTML</b>&gt;");
    jPanel8.add(jLabel1);

    jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jLabel3.setText("<html>&lt;<b>HEAD</b>&gt;");
    jPanel9.add(jLabel3);

    jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel12.setPreferredSize(new java.awt.Dimension(20, 10));

    javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
    jPanel12.setLayout(jPanel12Layout);
    jPanel12Layout.setHorizontalGroup(
      jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 20, Short.MAX_VALUE)
    );
    jPanel12Layout.setVerticalGroup(
      jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel10.add(jPanel12);

    jLabel6.setText("<html>&lt;<b>META NAME=\"Generator\" CONTENT=\"Orbada Export To Html\" /</b>&gt;");
    jPanel10.add(jLabel6);

    jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel14.setPreferredSize(new java.awt.Dimension(20, 10));

    javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
    jPanel14.setLayout(jPanel14Layout);
    jPanel14Layout.setHorizontalGroup(
      jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 20, Short.MAX_VALUE)
    );
    jPanel14Layout.setVerticalGroup(
      jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel13.add(jPanel14);

    jLabel7.setText("<html>&lt;<b>META HTTP-EQUIV=\"content-type\" CONTENT=\"text/html; charset=</b>");
    jPanel13.add(jLabel7);

    comboCharset.setEditable(true);
    comboCharset.setFont(new java.awt.Font("Courier New", 0, 12));
    comboCharset.setPreferredSize(new java.awt.Dimension(150, 22));
    jPanel13.add(comboCharset);

    jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel12.setText("<html><b>\" /</b>&gt;");
    jPanel13.add(jLabel12);

    jPanel15.setLayout(new java.awt.BorderLayout());

    jPanel16.setPreferredSize(new java.awt.Dimension(20, 10));

    javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
    jPanel16.setLayout(jPanel16Layout);
    jPanel16Layout.setHorizontalGroup(
      jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 20, Short.MAX_VALUE)
    );
    jPanel16Layout.setVerticalGroup(
      jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 58, Short.MAX_VALUE)
    );

    jPanel15.add(jPanel16, java.awt.BorderLayout.WEST);

    textMetaCode.setColumns(20);
    textMetaCode.setRows(5);
    textMetaCode.setText("<META NAME=\"Author\" CONTENT=\"Jan Kowalski\">\n<META NAME=\"Keywords\" CONTENT=\"table, data\">");
    textMetaCode.setFont(new java.awt.Font("Courier New", 0, 12));
    jScrollPane1.setViewportView(textMetaCode);

    jPanel15.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jLabel8.setText("<html>&lt;<b>/HEAD</b>&gt;");
    jPanel17.add(jLabel8);

    jPanel22.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel23.setPreferredSize(new java.awt.Dimension(40, 10));

    javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
    jPanel23.setLayout(jPanel23Layout);
    jPanel23Layout.setHorizontalGroup(
      jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 40, Short.MAX_VALUE)
    );
    jPanel23Layout.setVerticalGroup(
      jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel22.add(jPanel23);

    labelEndTableRowCode.setText("<html>&lt;<b>/TR</b>&gt;");
    jPanel22.add(labelEndTableRowCode);

    jPanel24.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel25.setPreferredSize(new java.awt.Dimension(20, 10));

    javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
    jPanel25.setLayout(jPanel25Layout);
    jPanel25Layout.setHorizontalGroup(
      jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 20, Short.MAX_VALUE)
    );
    jPanel25Layout.setVerticalGroup(
      jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel24.add(jPanel25);

    labelEndTableCode.setText("<html>&lt;<b>/TABLE</b>&gt;");
    jPanel24.add(labelEndTableCode);

    jPanel26.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jLabel11.setText("<html>&lt;<b>/BODY</b>&gt;");
    jPanel26.add(jLabel11);

    jPanel27.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jLabel2.setText("<html>&lt;<b>/HTML</b>&gt;");
    jPanel27.add(jLabel2);

    jPanel28.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jPanel29.setPreferredSize(new java.awt.Dimension(170, 10));

    javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
    jPanel29.setLayout(jPanel29Layout);
    jPanel29Layout.setHorizontalGroup(
      jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 170, Short.MAX_VALUE)
    );
    jPanel29Layout.setVerticalGroup(
      jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 10, Short.MAX_VALUE)
    );

    jPanel28.add(jPanel29);

    checkFirstLastAsNbsp.setText(stringManager.getString("ExportToHtmlDialog-nbsp-first-last")); // NOI18N
    jPanel28.add(checkFirstLastAsNbsp);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
          .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(8, 8, 8)
        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = pl.mpak.sky.gui.mr.ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  if (selectFile()) {
    modalResult = pl.mpak.sky.gui.mr.ModalResult.OK;
    dialogToConfig();
    dispose();
  }
}//GEN-LAST:event_cmOkActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JCheckBox checkFirstLastAsNbsp;
  private javax.swing.JCheckBox checkIncludeTitles;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private javax.swing.JComboBox comboCharset;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel13;
  private javax.swing.JLabel jLabel14;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel17;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel21;
  private javax.swing.JLabel jLabel22;
  private javax.swing.JLabel jLabel23;
  private javax.swing.JLabel jLabel24;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel10;
  private javax.swing.JPanel jPanel11;
  private javax.swing.JPanel jPanel12;
  private javax.swing.JPanel jPanel13;
  private javax.swing.JPanel jPanel14;
  private javax.swing.JPanel jPanel15;
  private javax.swing.JPanel jPanel16;
  private javax.swing.JPanel jPanel17;
  private javax.swing.JPanel jPanel18;
  private javax.swing.JPanel jPanel19;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel20;
  private javax.swing.JPanel jPanel21;
  private javax.swing.JPanel jPanel22;
  private javax.swing.JPanel jPanel23;
  private javax.swing.JPanel jPanel24;
  private javax.swing.JPanel jPanel25;
  private javax.swing.JPanel jPanel26;
  private javax.swing.JPanel jPanel27;
  private javax.swing.JPanel jPanel28;
  private javax.swing.JPanel jPanel29;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JPanel jPanel7;
  private javax.swing.JPanel jPanel8;
  private javax.swing.JPanel jPanel9;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JLabel labelEndTableCode;
  private javax.swing.JLabel labelEndTableDataCode;
  private javax.swing.JLabel labelEndTableHeaderCode;
  private javax.swing.JLabel labelEndTableRowCode;
  private pl.mpak.sky.gui.swing.comp.TextField textBodyAttrib;
  private pl.mpak.sky.gui.swing.comp.TextArea textMetaCode;
  private pl.mpak.sky.gui.swing.comp.TextField textNullDataValue;
  private pl.mpak.sky.gui.swing.comp.TextField textTableAttrib;
  private pl.mpak.sky.gui.swing.comp.TextField textTableCode;
  private pl.mpak.sky.gui.swing.comp.TextField textTableDataAttrib;
  private pl.mpak.sky.gui.swing.comp.TextField textTableDataCode;
  private pl.mpak.sky.gui.swing.comp.TextField textTableHeaderAttrib;
  private pl.mpak.sky.gui.swing.comp.TextField textTableHeaderCode;
  private pl.mpak.sky.gui.swing.comp.TextField textTableRowAttrib;
  private pl.mpak.sky.gui.swing.comp.TextField textTableRowCode;
  private pl.mpak.sky.gui.swing.comp.TextField textTitle;
  // End of variables declaration//GEN-END:variables
  
}
