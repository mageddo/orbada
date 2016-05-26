/*
 * OrbadaOptionsDialog.java
 *
 * Created on 10 listopad 2007, 20:19
 */

package orbada.gui;

import orbada.Consts;
import orbada.core.Application;
import orbada.gui.comps.OrbadaSQLSyntaxDocument;
import orbada.gui.util.FontChooser;
import orbada.services.DefaultPleaseWaitRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;

import orbada.gui.comps.OrbadaJavaSyntaxDocument;
import orbada.gui.comps.table.DataTable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.providers.LookAndFeelProvider;
import pl.mpak.orbada.plugins.providers.PleaseWaitRendererProvider;
import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.swing.ImageManager;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.JavaSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxStyle;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class OrbadaSettingsDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public enum Tab {
    tabGeneral,
    tabProxy,
    tabApperance,
    tabDataLists,
    tabDataFormat,
    tabSyntaxHighlighting,
    tabEditor
  };

  private ISettings settings;
  private SQLSyntaxDocument sqlDocument;
  private JavaSyntaxDocument javaDocument;
  private boolean updatingSyntaxControls;
  private PleaseWaitRendererProvider[] pwrpa;
  private LookAndFeelProvider[] lafpa;
  private PleaseWait waitTest;
  
  /** Creates new form OrbadaOptionsDialog */
  public OrbadaSettingsDialog() {
    super(SwingUtil.getRootFrame());
    initComponents();
    init();
  }
  
  public static void showDialog() {
    OrbadaSettingsDialog dialog = new OrbadaSettingsDialog();
    dialog.setVisible(true);
  }

  public static void showDialog(Tab tab) {
    OrbadaSettingsDialog dialog = new OrbadaSettingsDialog();
    switch (tab) {
      case tabGeneral:
        break;
      case tabProxy:
        dialog.tabSettings.setSelectedComponent(dialog.panelProxy);
        break;
      case tabApperance:
        dialog.tabSettings.setSelectedComponent(dialog.panelApperance);
        break;
      case tabDataLists:
        dialog.tabSettings.setSelectedComponent(dialog.panelDataList);
        break;
      case tabDataFormat:
        dialog.tabSettings.setSelectedComponent(dialog.panelDataFormat);
        break;
      case tabSyntaxHighlighting:
        dialog.tabSettings.setSelectedComponent(dialog.panelHighlight);
        break;
      case tabEditor:
        dialog.tabSettings.setSelectedComponent(dialog.panelEditor);
        break;
    }
    dialog.setVisible(true);
  }

  private ListCellRenderer getSyntaxCellRenderer() {
    return new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list,	Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof SyntaxStyle) {
          setText(((SyntaxStyle)value).getName());
        }
        return this;
      }
    };
  }
  
  private void init() {
    prepareSyntaxes();

    spinEvenRowShift.setModel(new SpinnerNumberModel(15, -255, 255, 1));
    spinFocusedShift.setModel(new SpinnerNumberModel(30, -255, 255, 1));
    spinTabToSpace.setModel(new SpinnerNumberModel(2, 1, 20, 1));
    comboPleaseWaitService.setModel(new DefaultComboBoxModel());
    DefaultComboBoxModel mwModel = (DefaultComboBoxModel)comboPleaseWaitService.getModel();
    pwrpa = Application.get().getServiceArray(PleaseWaitRendererProvider.class);
    for (PleaseWaitRendererProvider pwrp : pwrpa) {
      mwModel.addElement(pwrp.getDescription());
    }
    DefaultComboBoxModel lafModel = (DefaultComboBoxModel)comboLookAndFeelService.getModel();
    lafpa = Application.get().getServiceArray(LookAndFeelProvider.class);
    for (LookAndFeelProvider lafp : lafpa) {
      lafModel.addElement(lafp.getDescription());
    }

    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    
    settings = Application.get().getSettings();
    settingsToDialog();

    comboSyntaxes.addItem(sqlDocument);
    comboSyntaxes.addItem(javaDocument);
    panelEditorBackgroundColor.setBackground(textEditorPreview.getEditorArea().getBackground());
    labelSyntaxFont.setFont(textEditorPreview.getEditorArea().getFont());
    StringBuilder sb = new StringBuilder();
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-data-types")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-sql-functions")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-identifiers")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-keywords")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-numbers")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-strings")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-symbols")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-tables")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-exceptions")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-user-functions")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-command-parameters")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-doc-keys")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-html-tags")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-hints-or-java-doc")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-one-line-comment")).append("\n");
    sb.append(stringManager.getString("OrbadaSettingsDialog-ed-annotations"));
    textEditorPreview.setText(sb.toString());

    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel});
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonWaitTest}, comboPleaseWaitService.getHeight());
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {
      buttonSelect1, buttonSelect2, buttonSelect3, 
      buttonSelect4, buttonSelect5, buttonSelect6, buttonSelect7, buttonSelect8});
    pack();
    SwingUtil.centerWithinScreen(this);
  }

  @Override
  public void dispose() {
    if (waitTest != null) {
      cmPleaseWaitTest.performe();
    }
    super.dispose();
  }
  
  private void prepareSyntaxes() {
    comboSyntaxes.setModel(new DefaultComboBoxModel());
    sqlDocument = new SQLSyntaxDocument();
    sqlDocument.addKeyWord("CHARACTER,DATE,INTEGER", SQLSyntaxDocument.DATA_TYPE);
    sqlDocument.addKeyWord("UPPER,LOWER,MAX,AVG", SQLSyntaxDocument.SQL_FUNCTION);
    sqlDocument.addKeyWord("POLE,NAZWA", SQLSyntaxDocument.IDENTIFIER);
    sqlDocument.addKeyWord("SELECT,FROM,WHERE,NULL", SQLSyntaxDocument.KEYWORD);
    sqlDocument.addKeyWord("CUSTOM,ADDRS", SQLSyntaxDocument.TABLE);
    sqlDocument.addKeyWord("NO_DATA_FOUND", SQLSyntaxDocument.ERROR);
    sqlDocument.addKeyWord("GETSYSTEMDATE", SQLSyntaxDocument.USER_FUNCTION);
    javaDocument = new JavaSyntaxDocument();
    javaDocument.addKeyWord("CHARACTER,DATE,INTEGER", JavaSyntaxDocument.PRIMITIVE_TYPE);
    javaDocument.addKeyWord("@Column", JavaSyntaxDocument.ANNOTATION);
    javaDocument.addKeyWord("POLE,NAZWA", JavaSyntaxDocument.IDENTIFIER);
    javaDocument.addKeyWord("SELECT,FROM,WHERE,NULL", JavaSyntaxDocument.KEYWORD);
    javaDocument.addKeyWord("CUSTOM,ADDRS", JavaSyntaxDocument.STATIC_FIELD);
    javaDocument.addKeyWord("NO_DATA_FOUND", JavaSyntaxDocument.EXCEPTION);
    listSyntaxElements.setCellRenderer(getSyntaxCellRenderer());
  }
  
  private void settingsToDialog() {
    // general
    checkOpenNewConnectionAtStartup.setSelected(settings.getValue(Consts.newConnectionAtStartup, true));
    checkAutoFitColumnWidth.setSelected(settings.getValue(Consts.dataTableAutoFitWidth, true));
    textNullValue.setText(settings.getValue(Consts.queryTableNullValue, QueryTableCellRenderer.nullValue));
    checkDisableLoadSqlSyntaxInfo.setSelected(settings.getValue(Consts.disableLoadSqlSyntaxInfo, false));
    checkDisableCheckUpdates.setSelected(settings.getValue(Consts.disableCheckUpdates, false));
    checkNoRollbackOnClose.setSelected(settings.getValue(Consts.noRollbackOnClose, false));
    checkCloseWarning.setSelected(settings.getValue(Consts.appCloseWarning, false));

    // proxy
    if (StringUtil.equalsIgnoreCase(settings.getValue(Consts.proxySettings, Consts.proxyDefaultSettings), Consts.proxySettingsNoProxy)) {
      radioNoProxy.setSelected(true);
    }
    else if (StringUtil.equalsIgnoreCase(settings.getValue(Consts.proxySettings, Consts.proxyDefaultSettings), Consts.proxySettingsSystemProxy)) {
      radioUseSystemProxy.setSelected(true);
    }
    else if (StringUtil.equalsIgnoreCase(settings.getValue(Consts.proxySettings, Consts.proxyDefaultSettings), Consts.proxySettingsManualProxy)) {
      radioManulProxy.setSelected(true);
    }
    textHttpProxyAddress.setText(settings.getValue(Consts.proxyHttpAddress, ""));
    textHttpProxyPort.setText(settings.getValue(Consts.proxyHttpPort, ""));
    checkProxyAuthNedded.setSelected(settings.getValue(Consts.proxyAuthNeeded, false));
    textProxyAuthUser.setText(settings.getValue(Consts.proxyAuthUser, ""));
    textProxyAuthPassword.setText(settings.getValue(Consts.proxyAuthPassword, ""));

    // data lists
    checkColorizedQueryTable.setSelected(settings.getValue(Consts.colorizedQueryTable, QueryTableCellRenderer.colorizedCells));
    panelColorNumber.setBackground(settings.getValue(Consts.queryTableColorNumber, QueryTableCellRenderer.numberColor));
    panelColorString.setBackground(settings.getValue(Consts.queryTableColorString, QueryTableCellRenderer.stringColor));
    panelColorBool.setBackground(settings.getValue(Consts.queryTableColorBool, QueryTableCellRenderer.boolColor));
    panelColorDate.setBackground(settings.getValue(Consts.queryTableColorDate, QueryTableCellRenderer.dateColor));
    panelColorNull.setBackground(settings.getValue(Consts.queryTableColorNull, QueryTableCellRenderer.nullColor));
    
    panelColorSelectedTableRow.setBackground(settings.getValue(Consts.colorSelectedTableRow, QueryTableCellRenderer.selectionBackground));
    checkDefaultColorSelectedTableRow.setSelected(settings.getValue(Consts.defaultColorSelectedTableRow, false));
    spinEvenRowShift.setValue(settings.getValue(Consts.tableEvenRowShift, 15L).intValue());
    spinFocusedShift.setValue(settings.getValue(Consts.tableFocusedColumnShift, 30L).intValue());
    
    labelTableDataFont.setFont(settings.getValue(Consts.queryTableDataFont, DataTable.dataFont));
    checkNoViewTabPictures.setSelected(settings.getValue(Consts.noViewTabPictures, false));
    checkNoViewTabTitles.setSelected(settings.getValue(Consts.noViewTabTitles, false));
    checkAutoSaveColumnWidths.setSelected(settings.getValue(Consts.autoSaveColumnWidths, true));
    
    OrbadaSQLSyntaxDocument.loadSettings(sqlDocument);
    OrbadaJavaSyntaxDocument.loadSettings(javaDocument);
    textEditorPreview.getEditorArea().setBackground(settings.getValue("syntax-editor-background-color", textEditorPreview.getEditorArea().getBackground()));
    textEditorPreview.getEditorArea().setFont(settings.getValue("syntax-editor-font", textEditorPreview.getEditorArea().getFont()));

    String pwId = settings.getValue(Consts.pleaseWaitRendererSetting, DefaultPleaseWaitRenderer.uniqueId);
    for (int i=0; i<pwrpa.length; i++) {
      if (pwId.equalsIgnoreCase(pwrpa[i].getRendererId())) {
        comboPleaseWaitService.setSelectedIndex(i);
        break;
      }
    }
    if (comboPleaseWaitService.getSelectedIndex() == -1) {
      comboPleaseWaitService.setSelectedIndex(0);
    }
    checkPleaseWaitOn.setSelected(settings.getValue(Consts.pleaseWaitRendererOnSetting, true));

    String lafId = settings.getValue(Consts.lookAndFeelSetting, "");
    for (int i=0; i<lafpa.length; i++) {
      if (lafId.equalsIgnoreCase(lafpa[i].getLookAndFeelId())) {
        comboLookAndFeelService.setSelectedIndex(i);
        break;
      }
    }
    if (comboLookAndFeelService.getSelectedIndex() == -1) {
      comboLookAndFeelService.setSelectedIndex(0);
    }
    checkDefaultLookAndFeel.setSelected(settings.getValue(Consts.lookAndFeelDefaultSetting, true));

    checkTabMovesSelected.setSelected(settings.getValue(Consts.editorTabMovesSelected, true));
    checkCopySyntaxHighlight.setSelected(settings.getValue(Consts.editorCopySyntaxHighlight, true));
    checkEditorTrimWhitespaces.setSelected(settings.getValue(Consts.editorEditorTrimWhitespaces, false));
    checkSmartHomeEnd.setSelected(settings.getValue(Consts.editorEditorSmartHomeEnd, false));
    checkPredefinedSnippets.setSelected(settings.getValue(Consts.editorPreDefinedSnippets, true));
    checkAutoCompleteDot.setSelected(settings.getValue(Consts.editorAutoCompleteDot, true));
    textAutoCompleteActivate.setText(settings.getValue(Consts.editorAutoCompleteActivateChars, SkySetting.Default_CmAutoComplete_AutoCompleteActiveChars));
    textAutoCompleteInactivate.setText(settings.getValue(Consts.editorAutoCompleteInactivateChars, SkySetting.Default_CmAutoComplete_AutoCompleteInactiveChars));
    checkAutoCompleteInsertion.setSelected(settings.getValue(Consts.editorAutoCompleteInsertion, false));
    checkAutoCompleteReplace.setSelected(!settings.getValue(Consts.editorAutoCompleteInsertion, false));
    checkAutoCompleteInsertSingle.setSelected(settings.getValue(Consts.editorAutoCompleteInsertSingle, false));
    checkAutoCompleteStructureParser.setSelected(settings.getValue(Consts.editorAutoCompleteStructureParser, true));
    checkAutoCompleteStructureParserVariables.setSelected(settings.getValue(Consts.editorTabToSpaceCount, true));
    spinTabToSpace.setValue(settings.getValue(Consts.editorTabToSpaceCount, (long)SkySetting.Default_SyntaxEditor_TabToSpaceCount).intValue());
    checkTabAsSpaces.setSelected(settings.getValue(Consts.editorTabAsSpaces, false));

    checkDateFormatDefault.setSelected(settings.getValue(Consts.dataFormatDateDefault, true));
    textDateFormat.setText(settings.getValue(Consts.dataFormatDateString, Consts.defaultDataFormatDateString));
    checkTimeFormatDefault.setSelected(settings.getValue(Consts.dataFormatTimeDefault, true));
    textTimeFormat.setText(settings.getValue(Consts.dataFormatTimeString, Consts.defaultDataFormatTimeString));
    checkTimeStampFormatDefault.setSelected(settings.getValue(Consts.dataFormatTimestampDefault, true));
    textTimeStampFormat.setText(settings.getValue(Consts.dataFormatTimestampString, Consts.defaultDataFormatTimestampString));
    checkDecimalFormatDefault.setSelected(settings.getValue(Consts.dataFormatNumericDefault, true));
    textDecimalFormat.setText(settings.getValue(Consts.dataFormatNumericString, Consts.defaultDataFormatNumericString));
    checkBigDecimalFormatDefault.setSelected(settings.getValue(Consts.dataFormatBigDecimalDefault, true));
    textBigDecimalFormat.setText(settings.getValue(Consts.dataFormatBigDecimalString, Consts.defaultDataFormatBigDecimalString));
    checkDecimalSeparatorDefault.setSelected(settings.getValue(Consts.dataFormatDecimalSeparatorDefault, false));
    textDecimalSeparator.setText(settings.getValue(Consts.dataFormatDecimalSeparator, Consts.defaultDataFormatDecimalSeparator));

    setLookAndFeelEnabled();
    setDataTypeEnabled();
    setPleaseWaitEnabled();
    setProxyEnabled();

    updateSampleDateTimeFormat();
    updateSampleDecimalFormat();
  }
  
  private void dialogToSettings() {
    String lastLafClass = null;

    // general
    settings.setValue(Consts.newConnectionAtStartup, checkOpenNewConnectionAtStartup.isSelected());
    settings.setValue(Consts.dataTableAutoFitWidth, checkAutoFitColumnWidth.isSelected());
    settings.setValue(Consts.queryTableNullValue, textNullValue.getText());
    settings.setValue(Consts.disableLoadSqlSyntaxInfo, checkDisableLoadSqlSyntaxInfo.isSelected());
    settings.setValue(Consts.disableCheckUpdates, checkDisableCheckUpdates.isSelected());
    settings.setValue(Consts.noRollbackOnClose, checkNoRollbackOnClose.isSelected());
    settings.setValue(Consts.appCloseWarning, checkCloseWarning.isSelected());

    // proxy
    if (radioNoProxy.isSelected()) {
      settings.setValue(Consts.proxySettings, Consts.proxySettingsNoProxy);
    }
    else if (radioUseSystemProxy.isSelected()) {
      settings.setValue(Consts.proxySettings, Consts.proxySettingsSystemProxy);
    }
    else if (radioManulProxy.isSelected()) {
      settings.setValue(Consts.proxySettings, Consts.proxySettingsManualProxy);
    }
    settings.setValue(Consts.proxyHttpAddress, textHttpProxyAddress.getText());
    settings.setValue(Consts.proxyHttpPort, textHttpProxyPort.getText());
    settings.setValue(Consts.proxyAuthNeeded, checkProxyAuthNedded.isSelected());
    settings.setValue(Consts.proxyAuthUser, textProxyAuthUser.getText());
    settings.setValue(Consts.proxyAuthPassword, new String(textProxyAuthPassword.getPassword()));

    // data lists
    settings.setValue(Consts.colorizedQueryTable, checkColorizedQueryTable.isSelected());
    settings.setValue(Consts.queryTableColorNumber, panelColorNumber.getBackground());
    settings.setValue(Consts.queryTableColorString, panelColorString.getBackground());
    settings.setValue(Consts.queryTableColorBool, panelColorBool.getBackground());
    settings.setValue(Consts.queryTableColorDate, panelColorDate.getBackground());
    settings.setValue(Consts.queryTableColorNull, panelColorNull.getBackground());

    settings.setValue(Consts.colorSelectedTableRow, panelColorSelectedTableRow.getBackground());
    settings.setValue(Consts.defaultColorSelectedTableRow, checkDefaultColorSelectedTableRow.isSelected());
    settings.setValue(Consts.tableEvenRowShift, (long)(Integer)spinEvenRowShift.getValue());
    settings.setValue(Consts.tableFocusedColumnShift, (long)(Integer)spinFocusedShift.getValue());
    
    settings.setValue(Consts.queryTableDataFont, labelTableDataFont.getFont());
    settings.setValue(Consts.noViewTabPictures, checkNoViewTabPictures.isSelected());
    settings.setValue(Consts.noViewTabTitles, checkNoViewTabTitles.isSelected());
    settings.getValue(Consts.autoSaveColumnWidths, checkAutoSaveColumnWidths.isSelected());

    OrbadaSQLSyntaxDocument.storeSettings(sqlDocument);
    OrbadaJavaSyntaxDocument.storeSettings(javaDocument);
    settings.setValue("syntax-editor-background-color", textEditorPreview.getEditorArea().getBackground());
    settings.setValue("syntax-editor-font", textEditorPreview.getEditorArea().getFont());

    settings.setValue(Consts.pleaseWaitRendererSetting, pwrpa[comboPleaseWaitService.getSelectedIndex()].getRendererId());
    settings.setValue(Consts.pleaseWaitRendererOnSetting, checkPleaseWaitOn.isSelected());

    if (comboLookAndFeelService.getSelectedIndex() >= 0) {
      settings.setValue(Consts.lookAndFeelSetting, lafpa[comboLookAndFeelService.getSelectedIndex()].getLookAndFeelId());
    }
    else {
      settings.setValue(Consts.lookAndFeelSetting, (String)null);
    }
    settings.setValue(Consts.lookAndFeelDefaultSetting, checkDefaultLookAndFeel.isSelected());
    if (!checkDefaultLookAndFeel.isSelected() && comboLookAndFeelService.getSelectedIndex() >= 0) {
      lastLafClass = Application.get().localProperties.getProperty("orbada.laf.class");
      Application.get().localProperties.setProperty("orbada.laf.class", lafpa[comboLookAndFeelService.getSelectedIndex()].getLookAndFeelClass().getName());
    }
    else {
      Application.get().localProperties.keySet().remove("orbada.laf.class");
    }

    settings.setValue(Consts.editorTabMovesSelected, checkTabMovesSelected.isSelected());
    settings.setValue(Consts.editorCopySyntaxHighlight, checkCopySyntaxHighlight.isSelected());
    settings.setValue(Consts.editorEditorTrimWhitespaces, checkEditorTrimWhitespaces.isSelected());
    settings.setValue(Consts.editorEditorSmartHomeEnd, checkSmartHomeEnd.isSelected());
    settings.setValue(Consts.editorPreDefinedSnippets, checkPredefinedSnippets.isSelected());
    settings.setValue(Consts.editorAutoCompleteDot, checkAutoCompleteDot.isSelected());
    settings.setValue(Consts.editorAutoCompleteActivateChars, textAutoCompleteActivate.getText());
    settings.setValue(Consts.editorAutoCompleteInactivateChars, textAutoCompleteInactivate.getText());
    settings.setValue(Consts.editorAutoCompleteInsertion, checkAutoCompleteInsertion.isSelected());
    settings.setValue(Consts.editorAutoCompleteInsertSingle, checkAutoCompleteInsertSingle.isSelected());
    settings.setValue(Consts.editorAutoCompleteStructureParser, checkAutoCompleteStructureParser.isSelected());
    settings.setValue(Consts.editorAutoCompleteStructureParserVariables, checkAutoCompleteStructureParserVariables.isSelected());
    settings.setValue(Consts.editorTabToSpaceCount, (long)(Integer)spinTabToSpace.getValue());
    settings.setValue(Consts.editorTabAsSpaces, checkTabAsSpaces.isSelected());

    settings.setValue(Consts.dataFormatDateDefault, checkDateFormatDefault.isSelected());
    settings.setValue(Consts.dataFormatDateString, textDateFormat.getText());
    settings.setValue(Consts.dataFormatTimeDefault, checkTimeFormatDefault.isSelected());
    settings.setValue(Consts.dataFormatTimeString, textTimeFormat.getText());
    settings.setValue(Consts.dataFormatTimestampDefault, checkTimeStampFormatDefault.isSelected());
    settings.setValue(Consts.dataFormatTimestampString, textTimeStampFormat.getText());
    settings.setValue(Consts.dataFormatNumericDefault, checkDecimalFormatDefault.isSelected());
    settings.setValue(Consts.dataFormatNumericString, textDecimalFormat.getText());
    settings.setValue(Consts.dataFormatBigDecimalDefault, checkBigDecimalFormatDefault.isSelected());
    settings.setValue(Consts.dataFormatBigDecimalString, textBigDecimalFormat.getText());
    settings.setValue(Consts.dataFormatDecimalSeparatorDefault, checkDecimalSeparatorDefault.isSelected());
    settings.setValue(Consts.dataFormatDecimalSeparator, textDecimalSeparator.getText());

    settings.store();
    Application.get().getMainFrame().applySettings();

    if (!StringUtil.equals(lastLafClass, Application.get().localProperties.getProperty("orbada.laf.class"))) {
      Application.get().updateLAF();
    }
  }
  
  private void changeDataColor(JPanel panel) {
    Color color = JColorChooser.showDialog(this, stringManager.getString("OrbadaSettingsDialog-select-color"), panel.getBackground());
    if (color != null) {
      panel.setBackground(color);
    }
  }
  
  private void listToSyntaxDocument(SyntaxDocument doc) {
    SyntaxStyle styles[] = new SyntaxStyle[doc.getStyleMap().size()];
    DefaultListModel model = new DefaultListModel();
    int index = 0;
    for (SyntaxStyle style : doc.getStyleMap().values()) {
      styles[index] = style;
      index++;
    }
    Arrays.sort(styles, new Comparator<SyntaxStyle>() {
      @Override
      public int compare(SyntaxStyle o1, SyntaxStyle o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    for (SyntaxStyle style : styles) {
      model.addElement(style);
    }
    listSyntaxElements.setModel(model);
    listSyntaxElements.setSelectedIndex(0);
  }
  
  private void syntaxDocumentToList(SyntaxDocument doc) {
  }
  
  private void updateSyntaxStyle() {
    if (!updatingSyntaxControls && comboSyntaxes.getSelectedItem() instanceof SyntaxDocument) {
      SyntaxDocument doc = (SyntaxDocument)comboSyntaxes.getSelectedItem();
      if (listSyntaxElements.getSelectedValue() instanceof SyntaxStyle) {
        SyntaxStyle style = (SyntaxStyle)listSyntaxElements.getSelectedValue();
        style.setForeground(panelEditorForegroundColor.getBackground());
        style.setBold(checkEditorBold.isSelected());
        style.setItalic(checkEditorItalic.isSelected());
        style.setUnderline(checkEditorUnderline.isSelected());
        style.setEnabled(checkEditorEnabled.isSelected());
        textEditorPreview.repaint();
      }
    }
  }
  
  private void updateListSyntaxElement() {
    updatingSyntaxControls = true;
    try {
      listSyntaxElements.setEnabled(comboSyntaxes.getSelectedItem() instanceof SyntaxDocument);
      cmSyntaxForegroundColor.setEnabled(listSyntaxElements.getSelectedValue() instanceof SyntaxStyle);
      checkEditorBold.setEnabled(listSyntaxElements.getSelectedValue() instanceof SyntaxStyle);
      checkEditorItalic.setEnabled(listSyntaxElements.getSelectedValue() instanceof SyntaxStyle);
      checkEditorUnderline.setEnabled(listSyntaxElements.getSelectedValue() instanceof SyntaxStyle);
      checkEditorEnabled.setEnabled(listSyntaxElements.getSelectedValue() instanceof SyntaxStyle);

      if (comboSyntaxes.getSelectedItem() instanceof SyntaxDocument) {
        SyntaxDocument doc = (SyntaxDocument)comboSyntaxes.getSelectedItem();
        if (listSyntaxElements.getSelectedValue() instanceof SyntaxStyle) {
          SyntaxStyle style = (SyntaxStyle)listSyntaxElements.getSelectedValue();
          panelEditorForegroundColor.setBackground(style.getForeground());
          checkEditorBold.setSelected(style.isBold());
          checkEditorItalic.setSelected(style.isItalic());
          checkEditorUnderline.setSelected(style.isUnderline());
          checkEditorEnabled.setSelected(style.isEnabled());
        }
      }
    }
    finally {
      updatingSyntaxControls = false;
    }
  }

  private void setLookAndFeelEnabled() {
    labelLookAndFeel.setEnabled(!checkDefaultLookAndFeel.isSelected());
    comboLookAndFeelService.setEnabled(!checkDefaultLookAndFeel.isSelected());
  }

  private void setDataTypeEnabled() {
    labelDateFormat.setEnabled(!checkDateFormatDefault.isSelected());
    textDateFormat.setEnabled(!checkDateFormatDefault.isSelected());
    labelTimeFormat.setEnabled(!checkTimeFormatDefault.isSelected());
    textTimeFormat.setEnabled(!checkTimeFormatDefault.isSelected());
    labelTimeStampFormat.setEnabled(!checkTimeStampFormatDefault.isSelected());
    textTimeStampFormat.setEnabled(!checkTimeStampFormatDefault.isSelected());
    labelDecimalFormat.setEnabled(!checkDecimalFormatDefault.isSelected());
    textDecimalFormat.setEnabled(!checkDecimalFormatDefault.isSelected());
    labelBigDecimalFormat.setEnabled(!checkBigDecimalFormatDefault.isSelected());
    textBigDecimalFormat.setEnabled(!checkBigDecimalFormatDefault.isSelected());
    labelDecimalSeparator.setEnabled(!checkDecimalSeparatorDefault.isSelected());
    textDecimalSeparator.setEnabled(!checkDecimalSeparatorDefault.isSelected());
  }

  private void setPleaseWaitEnabled() {
    labeloPleaseWaitService.setEnabled(checkPleaseWaitOn.isSelected());
    comboPleaseWaitService.setEnabled(checkPleaseWaitOn.isSelected());
  }

  private void setProxyEnabled() {
    boolean manual = radioManulProxy.isSelected();
    labelHttpProxyAddress.setEnabled(manual);
    textHttpProxyAddress.setEnabled(manual);
    labelHttpProxyPort.setEnabled(manual);
    textHttpProxyPort.setEnabled(manual);
    checkProxyAuthNedded.setEnabled(manual);
    labelProxyAuthUser.setEnabled(manual && checkProxyAuthNedded.isSelected());
    textProxyAuthUser.setEnabled(manual && checkProxyAuthNedded.isSelected());
    labelProxyAuthPassword.setEnabled(manual && checkProxyAuthNedded.isSelected());
    textProxyAuthPassword.setEnabled(manual && checkProxyAuthNedded.isSelected());
  }

  private void updateSampleDateTimeFormat() {
    if (checkDateFormatDefault.isSelected()) {
      labelDateFormatSample.setText(java.text.DateFormat.getDateTimeInstance().format(new Date()));
    }
    else {
      try {
        labelDateFormatSample.setText(new java.text.SimpleDateFormat(textDateFormat.getText()).format(new Date()));
      }
      catch (java.lang.IllegalArgumentException ex) {
        labelDateFormatSample.setText(ex.getMessage());
      }
    }
    if (checkTimeFormatDefault.isSelected()) {
      labelTimeFormatSample.setText(java.text.DateFormat.getTimeInstance().format(new Date()));
    }
    else {
      try {
        labelTimeFormatSample.setText(new java.text.SimpleDateFormat(textTimeFormat.getText()).format(new Date()));
      }
      catch (java.lang.IllegalArgumentException ex) {
        labelTimeFormatSample.setText(ex.getMessage());
      }
    }
    if (checkTimeStampFormatDefault.isSelected()) {
      labelTimeStampFormatSample.setText(java.text.DateFormat.getDateTimeInstance().format(new Date()));
    }
    else {
      try {
        labelTimeStampFormatSample.setText(new java.text.SimpleDateFormat(textTimeStampFormat.getText()).format(new Date()));
      }
      catch (java.lang.IllegalArgumentException ex) {
        labelTimeStampFormatSample.setText(ex.getMessage());
      }
    }
  }

  private void updateSampleDecimalFormat() {
    DecimalFormatSymbols unusualSymbols = null;
    if (!checkDecimalSeparatorDefault.isSelected() && !StringUtil.isEmpty(textDecimalSeparator.getText())) {
      unusualSymbols = new DecimalFormatSymbols();
      unusualSymbols.setDecimalSeparator(textDecimalSeparator.getText().charAt(0));
    }

    if (checkDecimalFormatDefault.isSelected()) {
      NumberFormat df = java.text.DecimalFormat.getNumberInstance();
      df.setMaximumFractionDigits(20);
      labelDecimalFormatSample.setText(df.format(1234567890.1234567890));
    }
    else {
      try {
        if (unusualSymbols != null) {
          labelDecimalFormatSample.setText(new java.text.DecimalFormat(textDecimalFormat.getText(), unusualSymbols).format(1234567890.1234567890));
        }
        else {
          labelDecimalFormatSample.setText(new java.text.DecimalFormat(textDecimalFormat.getText()).format(1234567890.1234567890));
        }
      }
      catch (java.lang.IllegalArgumentException ex) {
        labelDecimalFormatSample.setText(ex.getMessage());
      }
    }
    if (checkBigDecimalFormatDefault.isSelected()) {
      NumberFormat df = java.text.DecimalFormat.getNumberInstance();
      df.setMaximumFractionDigits(20);
      labelBigDecimalFormatSample.setText(df.format(new BigDecimal("1234567890.1234567890")));
    }
    else {
      try {
        if (unusualSymbols != null) {
          labelBigDecimalFormatSample.setText(new java.text.DecimalFormat(textBigDecimalFormat.getText(), unusualSymbols).format(new BigDecimal("1234567890.1234567890")));
        }
        else {
          labelBigDecimalFormatSample.setText(new java.text.DecimalFormat(textBigDecimalFormat.getText()).format(new BigDecimal("1234567890.1234567890")));
        }
      }
      catch (java.lang.IllegalArgumentException ex) {
        labelBigDecimalFormatSample.setText(ex.getMessage());
      }
    }
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
        cmColorNumber = new pl.mpak.sky.gui.swing.Action();
        cmColorString = new pl.mpak.sky.gui.swing.Action();
        cmColorDate = new pl.mpak.sky.gui.swing.Action();
        cmColorBool = new pl.mpak.sky.gui.swing.Action();
        cmColorNull = new pl.mpak.sky.gui.swing.Action();
        cmTableDataFontChange = new pl.mpak.sky.gui.swing.Action();
        cmSyntaxBackgroundColor = new pl.mpak.sky.gui.swing.Action();
        cmSyntaxForegroundColor = new pl.mpak.sky.gui.swing.Action();
        cmSyntaxRestoreSettings = new pl.mpak.sky.gui.swing.Action();
        cmSyntaxFontChange = new pl.mpak.sky.gui.swing.Action();
        cmColorSelectedRow = new pl.mpak.sky.gui.swing.Action();
        autoCompleteInsertion = new javax.swing.ButtonGroup();
        proxyGroup = new javax.swing.ButtonGroup();
        cmPleaseWaitTest = new pl.mpak.sky.gui.swing.Action();
        tabSettings = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        checkOpenNewConnectionAtStartup = new javax.swing.JCheckBox();
        checkDisableLoadSqlSyntaxInfo = new javax.swing.JCheckBox();
        checkAutoSaveColumnWidths = new javax.swing.JCheckBox();
        checkDisableCheckUpdates = new javax.swing.JCheckBox();
        checkNoRollbackOnClose = new javax.swing.JCheckBox();
        checkCloseWarning = new javax.swing.JCheckBox();
        panelProxy = new javax.swing.JPanel();
        radioNoProxy = new javax.swing.JRadioButton();
        radioUseSystemProxy = new javax.swing.JRadioButton();
        radioManulProxy = new javax.swing.JRadioButton();
        labelHttpProxyAddress = new javax.swing.JLabel();
        textHttpProxyAddress = new pl.mpak.sky.gui.swing.comp.TextField();
        labelHttpProxyPort = new javax.swing.JLabel();
        textHttpProxyPort = new pl.mpak.sky.gui.swing.comp.TextField();
        checkProxyAuthNedded = new javax.swing.JCheckBox();
        labelProxyAuthUser = new javax.swing.JLabel();
        textProxyAuthUser = new pl.mpak.sky.gui.swing.comp.TextField();
        labelProxyAuthPassword = new javax.swing.JLabel();
        textProxyAuthPassword = new javax.swing.JPasswordField();
        jLabel11 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        panelApperance = new javax.swing.JPanel();
        checkNoViewTabPictures = new javax.swing.JCheckBox();
        checkNoViewTabTitles = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        labeloPleaseWaitService = new javax.swing.JLabel();
        comboPleaseWaitService = new pl.mpak.sky.gui.swing.comp.ComboBox();
        jLabel12 = new javax.swing.JLabel();
        checkPleaseWaitOn = new javax.swing.JCheckBox();
        buttonWaitTest = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        labelLookAndFeel = new javax.swing.JLabel();
        comboLookAndFeelService = new pl.mpak.sky.gui.swing.comp.ComboBox();
        labelLookAndFeelInfo = new javax.swing.JLabel();
        checkDefaultLookAndFeel = new javax.swing.JCheckBox();
        panelDataList = new javax.swing.JPanel();
        checkColorizedQueryTable = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panelColorNumber = new javax.swing.JPanel();
        buttonSelect1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        panelColorString = new javax.swing.JPanel();
        buttonSelect2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        panelColorDate = new javax.swing.JPanel();
        buttonSelect3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        panelColorBool = new javax.swing.JPanel();
        buttonSelect4 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        panelColorNull = new javax.swing.JPanel();
        buttonSelect5 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        textNullValue = new pl.mpak.sky.gui.swing.comp.TextField();
        checkAutoFitColumnWidth = new javax.swing.JCheckBox();
        panelTableDataFont = new javax.swing.JPanel();
        buttonTableDataFontChange = new javax.swing.JButton();
        labelTableDataFont = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        panelColorSelectedTableRow = new javax.swing.JPanel();
        buttonSelect6 = new javax.swing.JButton();
        checkDefaultColorSelectedTableRow = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        spinEvenRowShift = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        spinFocusedShift = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        panelDataFormat = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        checkDateFormatDefault = new javax.swing.JCheckBox();
        labelDateFormat = new javax.swing.JLabel();
        textDateFormat = new pl.mpak.sky.gui.swing.comp.TextField();
        labelDateFormatSample = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        checkTimeFormatDefault = new javax.swing.JCheckBox();
        labelTimeFormat = new javax.swing.JLabel();
        textTimeFormat = new pl.mpak.sky.gui.swing.comp.TextField();
        labelTimeFormatSample = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        checkTimeStampFormatDefault = new javax.swing.JCheckBox();
        labelTimeStampFormat = new javax.swing.JLabel();
        textTimeStampFormat = new pl.mpak.sky.gui.swing.comp.TextField();
        labelTimeStampFormatSample = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        htmlDateTimeFormatInfo = new pl.mpak.sky.gui.swing.comp.HtmlEditorPane();
        jPanel10 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        checkDecimalFormatDefault = new javax.swing.JCheckBox();
        labelDecimalFormat = new javax.swing.JLabel();
        textDecimalFormat = new pl.mpak.sky.gui.swing.comp.TextField();
        labelDecimalFormatSample = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        checkBigDecimalFormatDefault = new javax.swing.JCheckBox();
        labelBigDecimalFormat = new javax.swing.JLabel();
        textBigDecimalFormat = new pl.mpak.sky.gui.swing.comp.TextField();
        labelBigDecimalFormatSample = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        checkDecimalSeparatorDefault = new javax.swing.JCheckBox();
        labelDecimalSeparator = new javax.swing.JLabel();
        textDecimalSeparator = new pl.mpak.sky.gui.swing.comp.TextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        htmlDecimalFormatInfo = new pl.mpak.sky.gui.swing.comp.HtmlEditorPane();
        panelHighlight = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        panelEditorBackgroundColor = new javax.swing.JPanel();
        buttonSelect7 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        comboSyntaxes = new pl.mpak.sky.gui.swing.comp.ComboBox();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listSyntaxElements = new javax.swing.JList();
        jLabel9 = new javax.swing.JLabel();
        panelEditorForegroundColor = new javax.swing.JPanel();
        buttonSelect8 = new javax.swing.JButton();
        checkEditorBold = new javax.swing.JCheckBox();
        checkEditorItalic = new javax.swing.JCheckBox();
        checkEditorUnderline = new javax.swing.JCheckBox();
        checkEditorEnabled = new javax.swing.JCheckBox();
        textEditorPreview = new pl.mpak.sky.gui.swing.syntax.SyntaxTextArea();
        jButton8 = new javax.swing.JButton();
        panelTableDataFont1 = new javax.swing.JPanel();
        buttonTableDataFontChange1 = new javax.swing.JButton();
        labelSyntaxFont = new javax.swing.JLabel();
        panelEditor = new javax.swing.JPanel();
        checkTabMovesSelected = new javax.swing.JCheckBox();
        panelAutoIndent = new javax.swing.JPanel();
        checkAutoCompleteDot = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        checkAutoCompleteInsertion = new javax.swing.JRadioButton();
        checkAutoCompleteReplace = new javax.swing.JRadioButton();
        checkAutoCompleteInsertSingle = new javax.swing.JCheckBox();
        checkAutoCompleteStructureParser = new javax.swing.JCheckBox();
        checkAutoCompleteStructureParserVariables = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        textAutoCompleteActivate = new pl.mpak.sky.gui.swing.comp.TextField();
        jLabel19 = new javax.swing.JLabel();
        textAutoCompleteInactivate = new pl.mpak.sky.gui.swing.comp.TextField();
        checkCopySyntaxHighlight = new javax.swing.JCheckBox();
        checkEditorTrimWhitespaces = new javax.swing.JCheckBox();
        checkSmartHomeEnd = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        spinTabToSpace = new javax.swing.JSpinner();
        checkPredefinedSnippets = new javax.swing.JCheckBox();
        checkTabAsSpaces = new javax.swing.JCheckBox();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        cmOk.setActionCommandKey("cmOk");
        cmOk.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        cmOk.setText(stringManager.getString("cmOk-text")); // NOI18N
        cmOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOkActionPerformed(evt);
            }
        });

        cmCancel.setActionCommandKey("cmCancel");
        cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
        cmCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCancelActionPerformed(evt);
            }
        });

        cmColorNumber.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmColorNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmColorNumberActionPerformed(evt);
            }
        });

        cmColorString.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmColorString.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmColorStringActionPerformed(evt);
            }
        });

        cmColorDate.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmColorDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmColorDateActionPerformed(evt);
            }
        });

        cmColorBool.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmColorBool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmColorBoolActionPerformed(evt);
            }
        });

        cmColorNull.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmColorNull.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmColorNullActionPerformed(evt);
            }
        });

        cmTableDataFontChange.setActionCommandKey("cmTableDataFontChange");
        cmTableDataFontChange.setText(stringManager.getString("OrbadaSettingsDialog-change")); // NOI18N
        cmTableDataFontChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmTableDataFontChangeActionPerformed(evt);
            }
        });

        cmSyntaxBackgroundColor.setActionCommandKey("cmSyntaxBackgroundColor");
        cmSyntaxBackgroundColor.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmSyntaxBackgroundColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSyntaxBackgroundColorActionPerformed(evt);
            }
        });

        cmSyntaxForegroundColor.setActionCommandKey("cmSyntaxForegroundColor");
        cmSyntaxForegroundColor.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmSyntaxForegroundColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSyntaxForegroundColorActionPerformed(evt);
            }
        });

        cmSyntaxRestoreSettings.setActionCommandKey("cmSyntaxRestoreSettings");
        cmSyntaxRestoreSettings.setText(stringManager.getString("OrbadaSettingsDialog-cmSyntaxRestoreSettings-text")); // NOI18N
        cmSyntaxRestoreSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSyntaxRestoreSettingsActionPerformed(evt);
            }
        });

        cmSyntaxFontChange.setActionCommandKey("cmSyntaxFontChange");
        cmSyntaxFontChange.setText(stringManager.getString("OrbadaSettingsDialog-change")); // NOI18N
        cmSyntaxFontChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSyntaxFontChangeActionPerformed(evt);
            }
        });

        cmColorSelectedRow.setActionCommandKey("cmColorSelectedRow");
        cmColorSelectedRow.setText(stringManager.getString("OrbadaSettingsDialog-select")); // NOI18N
        cmColorSelectedRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmColorSelectedRowActionPerformed(evt);
            }
        });

        cmPleaseWaitTest.setText(stringManager.getString("cmPleaseWaitTest-test-text")); // NOI18N
        cmPleaseWaitTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPleaseWaitTestActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(stringManager.getString("OrbadaSettingsDialog-title")); // NOI18N
        setModal(true);

        tabSettings.setFocusable(false);

        checkOpenNewConnectionAtStartup.setText(stringManager.getString("OrbadaSettingsDialog-checkOpenNewConnectionAtStartup-text")); // NOI18N

        checkDisableLoadSqlSyntaxInfo.setText(stringManager.getString("OrbadaSettingsDialog-checkDisableLoadSqlSyntaxInfo-text")); // NOI18N
        checkDisableLoadSqlSyntaxInfo.setToolTipText(stringManager.getString("OrbadaSettingsDialog-checkDisableLoadSqlSyntaxInfo-hint")); // NOI18N

        checkAutoSaveColumnWidths.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoSaveColumnWidths-text")); // NOI18N

        checkDisableCheckUpdates.setText(stringManager.getString("OrbadaSettingsDialog-checkDisableCheckUpdates-text")); // NOI18N

        checkNoRollbackOnClose.setText(stringManager.getString("OrbadaSettingsDialog-checkNoRollbackOnClose-text")); // NOI18N

        checkCloseWarning.setText(stringManager.getString("OrbadaSettingsDialog-checkCloseWarning-text")); // NOI18N

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkDisableCheckUpdates)
                    .addComponent(checkOpenNewConnectionAtStartup)
                    .addComponent(checkDisableLoadSqlSyntaxInfo)
                    .addComponent(checkAutoSaveColumnWidths)
                    .addComponent(checkNoRollbackOnClose)
                    .addComponent(checkCloseWarning))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkOpenNewConnectionAtStartup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkDisableLoadSqlSyntaxInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAutoSaveColumnWidths)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkDisableCheckUpdates)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkNoRollbackOnClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkCloseWarning)
                .addContainerGap(338, Short.MAX_VALUE))
        );

        tabSettings.addTab(stringManager.getString("OrbadaSettingsDialog-general"), panelGeneral); // NOI18N

        proxyGroup.add(radioNoProxy);
        radioNoProxy.setText(stringManager.getString("radioNoProxy-text")); // NOI18N
        radioNoProxy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioNoProxyItemStateChanged(evt);
            }
        });

        proxyGroup.add(radioUseSystemProxy);
        radioUseSystemProxy.setText(stringManager.getString("radioUseSystemProxy-text")); // NOI18N
        radioUseSystemProxy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioUseSystemProxyItemStateChanged(evt);
            }
        });

        proxyGroup.add(radioManulProxy);
        radioManulProxy.setText(stringManager.getString("radioManulProxy-text")); // NOI18N
        radioManulProxy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioManulProxyItemStateChanged(evt);
            }
        });

        labelHttpProxyAddress.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelHttpProxyAddress.setText(stringManager.getString("http-proxy-address-dd")); // NOI18N

        labelHttpProxyPort.setText(stringManager.getString("http-proxy-port-dd")); // NOI18N

        checkProxyAuthNedded.setText(stringManager.getString("checkProxyAuthNedded-text")); // NOI18N
        checkProxyAuthNedded.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkProxyAuthNeddedItemStateChanged(evt);
            }
        });

        labelProxyAuthUser.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelProxyAuthUser.setText(stringManager.getString("proxy-auth-user-dd")); // NOI18N

        labelProxyAuthPassword.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelProxyAuthPassword.setText(stringManager.getString("proxy-auth-password-dd")); // NOI18N

        jLabel11.setText(stringManager.getString("proxy-settings-info")); // NOI18N

        jLabel21.setText(stringManager.getString("poxy-settings-for")); // NOI18N

        javax.swing.GroupLayout panelProxyLayout = new javax.swing.GroupLayout(panelProxy);
        panelProxy.setLayout(panelProxyLayout);
        panelProxyLayout.setHorizontalGroup(
            panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProxyLayout.createSequentialGroup()
                .addGroup(panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProxyLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelProxyAuthUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(labelProxyAuthPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(labelHttpProxyAddress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProxyLayout.createSequentialGroup()
                                .addComponent(textHttpProxyAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelHttpProxyPort)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textHttpProxyPort, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(textProxyAuthUser, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                            .addComponent(textProxyAuthPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)))
                    .addGroup(panelProxyLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(checkProxyAuthNedded))
                    .addGroup(panelProxyLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(radioManulProxy))
                    .addGroup(panelProxyLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(radioUseSystemProxy))
                    .addGroup(panelProxyLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(radioNoProxy))
                    .addGroup(panelProxyLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE))
                    .addGroup(panelProxyLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelProxyLayout.setVerticalGroup(
            panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProxyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioNoProxy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioUseSystemProxy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioManulProxy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelHttpProxyAddress)
                    .addComponent(textHttpProxyAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textHttpProxyPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelHttpProxyPort))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkProxyAuthNedded)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelProxyAuthUser)
                    .addComponent(textProxyAuthUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProxyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelProxyAuthPassword)
                    .addComponent(textProxyAuthPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 225, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addContainerGap())
        );

        tabSettings.addTab(stringManager.getString("tab-proxy-settings"), panelProxy); // NOI18N

        checkNoViewTabPictures.setText(stringManager.getString("OrbadaSettingsDialog-checkNoViewTabPictures-text")); // NOI18N

        checkNoViewTabTitles.setText(stringManager.getString("OrbadaSettingsDialog-checkNoViewTabTitles-text")); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-wait-renderer-border-title"))); // NOI18N

        labeloPleaseWaitService.setText(stringManager.getString("OrbadaSettingsDialog-select-wait-renderer-dd")); // NOI18N

        jLabel12.setText(stringManager.getString("OrbadaSettingsDialog-wait-renderer-info")); // NOI18N

        checkPleaseWaitOn.setText(stringManager.getString("OrbadaSettingsDialog-checkPleaseWaitOn-text")); // NOI18N
        checkPleaseWaitOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkPleaseWaitOnActionPerformed(evt);
            }
        });

        buttonWaitTest.setAction(cmPleaseWaitTest);
        buttonWaitTest.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonWaitTest.setPreferredSize(new java.awt.Dimension(85, 25));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                    .addComponent(checkPleaseWaitOn, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labeloPleaseWaitService, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(comboPleaseWaitService, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonWaitTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(checkPleaseWaitOn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labeloPleaseWaitService)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboPleaseWaitService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonWaitTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap())
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-lookandfeel-border-title"))); // NOI18N

        labelLookAndFeel.setText(stringManager.getString("OrbadaSettingsDialog-select-lookandfeel-dd")); // NOI18N

        labelLookAndFeelInfo.setText(stringManager.getString("OrbadaSettingsDialog-lookandfeel-info")); // NOI18N

        checkDefaultLookAndFeel.setText(stringManager.getString("OrbadaSettingsDialog-checkDefaultLookAndFeel-text")); // NOI18N
        checkDefaultLookAndFeel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkDefaultLookAndFeelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelLookAndFeelInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                    .addComponent(checkDefaultLookAndFeel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelLookAndFeel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboLookAndFeelService, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(checkDefaultLookAndFeel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLookAndFeel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboLookAndFeelService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLookAndFeelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelApperanceLayout = new javax.swing.GroupLayout(panelApperance);
        panelApperance.setLayout(panelApperanceLayout);
        panelApperanceLayout.setHorizontalGroup(
            panelApperanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelApperanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelApperanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkNoViewTabPictures, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkNoViewTabTitles, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        panelApperanceLayout.setVerticalGroup(
            panelApperanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelApperanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkNoViewTabPictures)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkNoViewTabTitles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(151, Short.MAX_VALUE))
        );

        tabSettings.addTab(stringManager.getString("OrbadaSettingsDialog-look"), panelApperance); // NOI18N

        checkColorizedQueryTable.setText(stringManager.getString("OrbadaSettingsDialog-checkColorizedQueryTable-text")); // NOI18N
        checkColorizedQueryTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkColorizedQueryTable.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-data-types-border-title"))); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(stringManager.getString("OrbadaSettingsDialog-number-color-dd")); // NOI18N

        panelColorNumber.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelColorNumberLayout = new javax.swing.GroupLayout(panelColorNumber);
        panelColorNumber.setLayout(panelColorNumberLayout);
        panelColorNumberLayout.setHorizontalGroup(
            panelColorNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );
        panelColorNumberLayout.setVerticalGroup(
            panelColorNumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect1.setAction(cmColorNumber);
        buttonSelect1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect1.setPreferredSize(new java.awt.Dimension(75, 23));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(stringManager.getString("OrbadaSettingsDialog-string-color-dd")); // NOI18N

        panelColorString.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelColorStringLayout = new javax.swing.GroupLayout(panelColorString);
        panelColorString.setLayout(panelColorStringLayout);
        panelColorStringLayout.setHorizontalGroup(
            panelColorStringLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );
        panelColorStringLayout.setVerticalGroup(
            panelColorStringLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect2.setAction(cmColorString);
        buttonSelect2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect2.setPreferredSize(new java.awt.Dimension(75, 23));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(stringManager.getString("OrbadaSettingsDialog-datetime-color-dd")); // NOI18N

        panelColorDate.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelColorDateLayout = new javax.swing.GroupLayout(panelColorDate);
        panelColorDate.setLayout(panelColorDateLayout);
        panelColorDateLayout.setHorizontalGroup(
            panelColorDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );
        panelColorDateLayout.setVerticalGroup(
            panelColorDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect3.setAction(cmColorDate);
        buttonSelect3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect3.setPreferredSize(new java.awt.Dimension(75, 23));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(stringManager.getString("OrbadaSettingsDialog-bool-color-dd")); // NOI18N

        panelColorBool.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelColorBoolLayout = new javax.swing.GroupLayout(panelColorBool);
        panelColorBool.setLayout(panelColorBoolLayout);
        panelColorBoolLayout.setHorizontalGroup(
            panelColorBoolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );
        panelColorBoolLayout.setVerticalGroup(
            panelColorBoolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect4.setAction(cmColorBool);
        buttonSelect4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect4.setPreferredSize(new java.awt.Dimension(75, 23));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(stringManager.getString("OrbadaSettingsDialog-null-color-dd")); // NOI18N

        panelColorNull.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelColorNullLayout = new javax.swing.GroupLayout(panelColorNull);
        panelColorNull.setLayout(panelColorNullLayout);
        panelColorNullLayout.setHorizontalGroup(
            panelColorNullLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );
        panelColorNullLayout.setVerticalGroup(
            panelColorNullLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect5.setAction(cmColorNull);
        buttonSelect5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect5.setPreferredSize(new java.awt.Dimension(75, 23));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelColorNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelect1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelColorString, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelect2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelColorDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelect3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelColorBool, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelect4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelColorNull, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelect5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonSelect1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelColorNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonSelect2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelColorString, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonSelect3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelColorDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonSelect4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelColorBool, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonSelect5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelColorNull, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setText(stringManager.getString("OrbadaSettingsDialog-text-value-null-dd")); // NOI18N

        textNullValue.setText("textField1");

        checkAutoFitColumnWidth.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoFitColumnWidth-text")); // NOI18N
        checkAutoFitColumnWidth.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkAutoFitColumnWidth.setMargin(new java.awt.Insets(0, 0, 0, 0));

        panelTableDataFont.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-panelTableDataFont-border-title"))); // NOI18N

        buttonTableDataFontChange.setAction(cmTableDataFontChange);
        buttonTableDataFontChange.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonTableDataFontChange.setPreferredSize(new java.awt.Dimension(85, 25));

        labelTableDataFont.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTableDataFont.setText(stringManager.getString("OrbadaSettingsDialog-OrbadaText-text")); // NOI18N

        javax.swing.GroupLayout panelTableDataFontLayout = new javax.swing.GroupLayout(panelTableDataFont);
        panelTableDataFont.setLayout(panelTableDataFontLayout);
        panelTableDataFontLayout.setHorizontalGroup(
            panelTableDataFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTableDataFontLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTableDataFont, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonTableDataFontChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelTableDataFontLayout.setVerticalGroup(
            panelTableDataFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTableDataFontLayout.createSequentialGroup()
                .addGroup(panelTableDataFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelTableDataFont, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                    .addGroup(panelTableDataFontLayout.createSequentialGroup()
                        .addContainerGap(48, Short.MAX_VALUE)
                        .addComponent(buttonTableDataFontChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText(stringManager.getString("OrbadaSettingsDialog-selected-row-color-dd")); // NOI18N

        panelColorSelectedTableRow.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelColorSelectedTableRowLayout = new javax.swing.GroupLayout(panelColorSelectedTableRow);
        panelColorSelectedTableRow.setLayout(panelColorSelectedTableRowLayout);
        panelColorSelectedTableRowLayout.setHorizontalGroup(
            panelColorSelectedTableRowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
        );
        panelColorSelectedTableRowLayout.setVerticalGroup(
            panelColorSelectedTableRowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect6.setAction(cmColorSelectedRow);
        buttonSelect6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect6.setPreferredSize(new java.awt.Dimension(75, 23));

        checkDefaultColorSelectedTableRow.setText(stringManager.getString("OrbadaSettingsDialog-default")); // NOI18N

        jLabel13.setText(stringManager.getString("OrbadaSettingsDialog-even-row-shift-dd")); // NOI18N

        jLabel14.setText(stringManager.getString("OrbadaSettingsDialog-even-row-shift-info")); // NOI18N

        jLabel15.setText(stringManager.getString("OrbadaSettingsDialog-selected-row-exnhanced-dd")); // NOI18N

        jLabel16.setText(stringManager.getString("OrbadaSettingsDialog-selected-row-enhance-info")); // NOI18N

        javax.swing.GroupLayout panelDataListLayout = new javax.swing.GroupLayout(panelDataList);
        panelDataList.setLayout(panelDataListLayout);
        panelDataListLayout.setHorizontalGroup(
            panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkColorizedQueryTable)
                    .addGroup(panelDataListLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelColorSelectedTableRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelect6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkDefaultColorSelectedTableRow))
                    .addComponent(panelTableDataFont, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelDataListLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textNullValue, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkAutoFitColumnWidth)
                    .addGroup(panelDataListLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinEvenRowShift, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14))
                    .addGroup(panelDataListLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinFocusedShift, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)))
                .addContainerGap())
        );
        panelDataListLayout.setVerticalGroup(
            panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkColorizedQueryTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelColorSelectedTableRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addGroup(panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(checkDefaultColorSelectedTableRow)
                        .addComponent(buttonSelect6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(textNullValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAutoFitColumnWidth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTableDataFont, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(spinEvenRowShift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(spinFocusedShift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(21, 21, 21))
        );

        tabSettings.addTab(stringManager.getString("OrbadaSettingsDialog-data-list"), panelDataList); // NOI18N

        jTabbedPane2.setFocusable(false);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-date-format"))); // NOI18N

        checkDateFormatDefault.setText(stringManager.getString("OrbadaSettingsDialog-checkFormatDefault-text")); // NOI18N
        checkDateFormatDefault.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkDateFormatDefaultItemStateChanged(evt);
            }
        });
        checkDateFormatDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkDateFormatDefaultActionPerformed(evt);
            }
        });

        labelDateFormat.setText(stringManager.getString("OrbadaSettingsDialog-date-format-label")); // NOI18N

        textDateFormat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textDateFormatFocusLost(evt);
            }
        });

        labelDateFormatSample.setText("labelDateFormatSample");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(labelDateFormat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textDateFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(checkDateFormatDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 197, Short.MAX_VALUE)
                        .addComponent(labelDateFormatSample)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkDateFormatDefault)
                    .addComponent(labelDateFormatSample))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDateFormat)
                    .addComponent(textDateFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-time-format"))); // NOI18N

        checkTimeFormatDefault.setText(stringManager.getString("OrbadaSettingsDialog-checkFormatDefault-text")); // NOI18N
        checkTimeFormatDefault.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkTimeFormatDefaultItemStateChanged(evt);
            }
        });
        checkTimeFormatDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkTimeFormatDefaultActionPerformed(evt);
            }
        });

        labelTimeFormat.setText(stringManager.getString("OrbadaSettingsDialog-time-format-label")); // NOI18N

        textTimeFormat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textTimeFormatFocusLost(evt);
            }
        });

        labelTimeFormatSample.setText("labelTimeFormatSample");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(labelTimeFormat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textTimeFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(checkTimeFormatDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
                        .addComponent(labelTimeFormatSample)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkTimeFormatDefault)
                    .addComponent(labelTimeFormatSample))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTimeFormat)
                    .addComponent(textTimeFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-timestamp-format"))); // NOI18N

        checkTimeStampFormatDefault.setText(stringManager.getString("OrbadaSettingsDialog-checkFormatDefault-text")); // NOI18N
        checkTimeStampFormatDefault.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkTimeStampFormatDefaultItemStateChanged(evt);
            }
        });
        checkTimeStampFormatDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkTimeStampFormatDefaultActionPerformed(evt);
            }
        });

        labelTimeStampFormat.setText(stringManager.getString("OrbadaSettingsDialog-timestamp-format-label")); // NOI18N

        textTimeStampFormat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textTimeStampFormatFocusLost(evt);
            }
        });

        labelTimeStampFormatSample.setText("labelTimeStampFormatSample");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(labelTimeStampFormat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textTimeStampFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(checkTimeStampFormatDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                        .addComponent(labelTimeStampFormatSample)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkTimeStampFormatDefault)
                    .addComponent(labelTimeStampFormatSample))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTimeStampFormat)
                    .addComponent(textTimeStampFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setBorder(null);

        htmlDateTimeFormatInfo.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        htmlDateTimeFormatInfo.setBorder(null);
        htmlDateTimeFormatInfo.setText("<html>\r\n  <head>\r\n\r\n  </head>\r\n  <body>\r\n<table border=0 cellspacing=1 cellpadding=1>\n     <tr>\n         <th>Letter\n         <th>Date or Time Component\n         <th>Presentation\n         <th>Examples\n     <tr>\n         <td><code>G</code>\n         <td>Era designator\n         <td>Text\n         <td><code>AD</code>\n     <tr>\n         <td><code>y</code>\n         <td>Year\n         <td>Year\n         <td><code>1996</code>; <code>96</code>\n     <tr>\n         <td><code>M</code>\n         <td>Month in year\n         <td>Month\n         <td><code>July</code>; <code>Jul</code>; <code>07</code>\n     <tr>\n         <td><code>w</code>\n         <td>Week in year\n         <td>Number\n         <td><code>27</code>\n     <tr>\n         <td><code>W</code>\n         <td>Week in month\n         <td>Number\n         <td><code>2</code>\n     <tr>\n         <td><code>D</code>\n         <td>Day in year\n         <td>Number\n         <td><code>189</code>\n     <tr>\n         <td><code>d</code>\n         <td>Day in month\n         <td>Number\n         <td><code>10</code>\n     <tr>\n         <td><code>F</code>\n         <td>Day of week in month\n         <td>Number\n         <td><code>2</code>\n     <tr>\n         <td><code>E</code>\n         <td>Day in week\n         <td>Text\n         <td><code>Tuesday</code>; <code>Tue</code>\n     <tr>\n         <td><code>a</code>\n         <td>Am/pm marker\n         <td>Text\n         <td><code>PM</code>\n     <tr>\n         <td><code>H</code>\n         <td>Hour in day (0-23)\n         <td>Number\n         <td><code>0</code>\n     <tr>\n         <td><code>k</code>\n         <td>Hour in day (1-24)\n         <td>Number\n         <td><code>24</code>\n     <tr>\n         <td><code>K</code>\n         <td>Hour in am/pm (0-11)\n         <td>Number\n         <td><code>0</code>\n     <tr>\n         <td><code>h</code>\n         <td>Hour in am/pm (1-12)\n         <td>Number\n         <td><code>12</code>\n     <tr>\n         <td><code>m</code>\n         <td>Minute in hour\n         <td>Number\n         <td><code>30</code>\n     <tr>\n         <td><code>s</code>\n         <td>Second in minute\n         <td>Number\n         <td><code>55</code>\n     <tr>\n         <td><code>S</code>\n         <td>Millisecond\n         <td>Number\n         <td><code>978</code>\n     <tr>\n         <td><code>z</code>\n         <td>Time zone\n         <td>General time zone\n         <td><code>Pacific Standard Time</code>; <code>PST</code>; <code>GMT-08:00</code>\n     <tr>\n         <td><code>Z</code>\n         <td>Time zone\n         <td>RFC 822 time zone\n         <td><code>-0800</code>\n </table>\n  </body>\r\n</html>\r\n");
        jScrollPane2.setViewportView(htmlDateTimeFormatInfo);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab(stringManager.getString("OrbadaSettingsDialog-date-time-format"), jPanel9); // NOI18N

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-decimal-format"))); // NOI18N

        checkDecimalFormatDefault.setText(stringManager.getString("OrbadaSettingsDialog-checkFormatDefault-text")); // NOI18N
        checkDecimalFormatDefault.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkDecimalFormatDefaultItemStateChanged(evt);
            }
        });
        checkDecimalFormatDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkDecimalFormatDefaultActionPerformed(evt);
            }
        });

        labelDecimalFormat.setText(stringManager.getString("OrbadaSettingsDialog-decimal-format-label")); // NOI18N

        textDecimalFormat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textDecimalFormatFocusLost(evt);
            }
        });

        labelDecimalFormatSample.setText("labelDecimalFormatSample");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(labelDecimalFormat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textDecimalFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(checkDecimalFormatDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                        .addComponent(labelDecimalFormatSample)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkDecimalFormatDefault)
                    .addComponent(labelDecimalFormatSample))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDecimalFormat)
                    .addComponent(textDecimalFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-bigdecimal-format"))); // NOI18N

        checkBigDecimalFormatDefault.setText(stringManager.getString("OrbadaSettingsDialog-checkFormatDefault-text")); // NOI18N
        checkBigDecimalFormatDefault.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBigDecimalFormatDefaultItemStateChanged(evt);
            }
        });
        checkBigDecimalFormatDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBigDecimalFormatDefaultActionPerformed(evt);
            }
        });

        labelBigDecimalFormat.setText(stringManager.getString("OrbadaSettingsDialog-bigdecimal-format-label")); // NOI18N

        textBigDecimalFormat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textBigDecimalFormatFocusLost(evt);
            }
        });

        labelBigDecimalFormatSample.setText("labelBigDecimalFormatSample");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(labelBigDecimalFormat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textBigDecimalFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(checkBigDecimalFormatDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                        .addComponent(labelBigDecimalFormatSample)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBigDecimalFormatDefault)
                    .addComponent(labelBigDecimalFormatSample))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBigDecimalFormat)
                    .addComponent(textBigDecimalFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-format-separators"))); // NOI18N

        checkDecimalSeparatorDefault.setText(stringManager.getString("OrbadaSettingsDialog-checkFormatDefault-text")); // NOI18N
        checkDecimalSeparatorDefault.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkDecimalSeparatorDefaultItemStateChanged(evt);
            }
        });
        checkDecimalSeparatorDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkDecimalSeparatorDefaultActionPerformed(evt);
            }
        });

        labelDecimalSeparator.setText(stringManager.getString("OrbadaSettingsDialog-decimal-separator-label")); // NOI18N

        textDecimalSeparator.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textDecimalSeparatorFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(labelDecimalSeparator)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textDecimalSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkDecimalSeparatorDefault))
                .addContainerGap(309, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(checkDecimalSeparatorDefault)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDecimalSeparator)
                    .addComponent(textDecimalSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane3.setBorder(null);

        htmlDecimalFormatInfo.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        htmlDecimalFormatInfo.setBorder(null);
        htmlDecimalFormatInfo.setText("<html>\r\n  <head>\r\n\r\n  </head>\r\n  <body>\r\n<table border=0 cellspacing=1 cellpadding=1>\n     <tr>\n          <th align=left>Symbol\n          <th align=left>Location\n          <th align=left>Localized?\n          <th align=left>Meaning\n     <tr valign=top>\n          <td><code>0</code>\n          <td>Number\n          <td>Yes\n          <td>Digit\n     <tr valign=top>\n          <td><code>#</code>\n          <td>Number\n          <td>Yes\n          <td>Digit, zero shows as absent\n     <tr valign=top>\n          <td><code>.</code>\n          <td>Number\n          <td>Yes\n          <td>Decimal separator or monetary decimal separator\n     <tr valign=top>\n          <td><code>-</code>\n          <td>Number\n          <td>Yes\n          <td>Minus sign\n     <tr valign=top>\n          <td><code>,</code>\n          <td>Number\n          <td>Yes\n          <td>Grouping separator\n     <tr valign=top>\n          <td><code>E</code>\n          <td>Number\n          <td>Yes\n          <td>Separates mantissa and exponent in scientific notation.\n              <em>Need not be quoted in prefix or suffix.</em>\n     <tr valign=top>\n          <td><code>;</code>\n          <td>Subpattern boundary\n          <td>Yes\n          <td>Separates positive and negative subpatterns\n     <tr valign=top>\n          <td><code>%</code>\n          <td>Prefix or suffix\n          <td>Yes\n          <td>Multiply by 100 and show as percentage\n     <tr valign=top>\n          <td><code>&#92;u2030</code>\n          <td>Prefix or suffix\n          <td>Yes\n          <td>Multiply by 1000 and show as per mille\n     <tr valign=top>\n          <td><code>&#164;</code> (<code>&#92;u00A4</code>)\n          <td>Prefix or suffix\n          <td>No\n          <td>Currency sign, replaced by currency symbol.  If\n              doubled, replaced by international currency symbol.\n              If present in a pattern, the monetary decimal separator\n              is used instead of the decimal separator.\n     <tr valign=top>\n          <td><code>'</code>\n          <td>Prefix or suffix\n          <td>No\n          <td>Used to quote special characters in a prefix or suffix,\n              for example, <code>\"'#'#\"</code> formats 123 to\n              <code>\"#123\"</code>.  To create a single quote\n              itself, use two in a row: <code>\"# o''clock\"</code>.\n </table>\n  </body>\r\n</html>\r\n");
        jScrollPane3.setViewportView(htmlDecimalFormatInfo);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab(stringManager.getString("OrbadaSettingsDialog-numeric-format"), jPanel10); // NOI18N

        javax.swing.GroupLayout panelDataFormatLayout = new javax.swing.GroupLayout(panelDataFormat);
        panelDataFormat.setLayout(panelDataFormatLayout);
        panelDataFormatLayout.setHorizontalGroup(
            panelDataFormatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataFormatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelDataFormatLayout.setVerticalGroup(
            panelDataFormatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataFormatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabSettings.addTab(stringManager.getString("OrbadaSettingsDialog-data-format"), panelDataFormat); // NOI18N

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(stringManager.getString("OrbadaSettingsDialog-background-color-dd")); // NOI18N

        panelEditorBackgroundColor.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelEditorBackgroundColorLayout = new javax.swing.GroupLayout(panelEditorBackgroundColor);
        panelEditorBackgroundColor.setLayout(panelEditorBackgroundColorLayout);
        panelEditorBackgroundColorLayout.setHorizontalGroup(
            panelEditorBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );
        panelEditorBackgroundColorLayout.setVerticalGroup(
            panelEditorBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect7.setAction(cmSyntaxBackgroundColor);
        buttonSelect7.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect7.setPreferredSize(new java.awt.Dimension(75, 23));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(stringManager.getString("OrbadaSettingsDialog-syntax-dd")); // NOI18N

        comboSyntaxes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboSyntaxes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboSyntaxesItemStateChanged(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-syntax-el-border-title"))); // NOI18N

        listSyntaxElements.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listSyntaxElementsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listSyntaxElements);

        jLabel9.setText(stringManager.getString("OrbadaSettingsDialog-color-dd")); // NOI18N

        panelEditorForegroundColor.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 2));

        javax.swing.GroupLayout panelEditorForegroundColorLayout = new javax.swing.GroupLayout(panelEditorForegroundColor);
        panelEditorForegroundColor.setLayout(panelEditorForegroundColorLayout);
        panelEditorForegroundColorLayout.setHorizontalGroup(
            panelEditorForegroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 124, Short.MAX_VALUE)
        );
        panelEditorForegroundColorLayout.setVerticalGroup(
            panelEditorForegroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        buttonSelect8.setAction(cmSyntaxForegroundColor);
        buttonSelect8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonSelect8.setPreferredSize(new java.awt.Dimension(75, 23));

        checkEditorBold.setText(stringManager.getString("OrbadaSettingsDialog-checkEditorBold-text")); // NOI18N
        checkEditorBold.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkEditorBoldItemStateChanged(evt);
            }
        });

        checkEditorItalic.setText(stringManager.getString("OrbadaSettingsDialog-checkEditorItalic-text")); // NOI18N
        checkEditorItalic.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkEditorItalicItemStateChanged(evt);
            }
        });

        checkEditorUnderline.setText(stringManager.getString("OrbadaSettingsDialog-checkEditorUnderline-text")); // NOI18N
        checkEditorUnderline.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkEditorUnderlineItemStateChanged(evt);
            }
        });

        checkEditorEnabled.setText(stringManager.getString("OrbadaSettingsDialog-checkEditorEnabled-text")); // NOI18N
        checkEditorEnabled.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkEditorEnabledItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(checkEditorBold)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkEditorItalic))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelEditorForegroundColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelect8, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkEditorEnabled)
                    .addComponent(checkEditorUnderline))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(checkEditorEnabled)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(buttonSelect8, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                            .addComponent(panelEditorForegroundColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(checkEditorBold)
                            .addComponent(checkEditorItalic))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkEditorUnderline)))
                .addContainerGap())
        );

        jButton8.setAction(cmSyntaxRestoreSettings);
        jButton8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton8.setPreferredSize(new java.awt.Dimension(85, 25));

        panelTableDataFont1.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-panelEditorFont-border-title"))); // NOI18N

        buttonTableDataFontChange1.setAction(cmSyntaxFontChange);
        buttonTableDataFontChange1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonTableDataFontChange1.setPreferredSize(new java.awt.Dimension(85, 25));

        labelSyntaxFont.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSyntaxFont.setText(stringManager.getString("OrbadaSettingsDialog-OrbadaText-text")); // NOI18N

        javax.swing.GroupLayout panelTableDataFont1Layout = new javax.swing.GroupLayout(panelTableDataFont1);
        panelTableDataFont1.setLayout(panelTableDataFont1Layout);
        panelTableDataFont1Layout.setHorizontalGroup(
            panelTableDataFont1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTableDataFont1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSyntaxFont, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonTableDataFontChange1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelTableDataFont1Layout.setVerticalGroup(
            panelTableDataFont1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTableDataFont1Layout.createSequentialGroup()
                .addGroup(panelTableDataFont1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelSyntaxFont, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addGroup(panelTableDataFont1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonTableDataFontChange1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelHighlightLayout = new javax.swing.GroupLayout(panelHighlight);
        panelHighlight.setLayout(panelHighlightLayout);
        panelHighlightLayout.setHorizontalGroup(
            panelHighlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHighlightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHighlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textEditorPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHighlightLayout.createSequentialGroup()
                        .addGroup(panelHighlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelHighlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelHighlightLayout.createSequentialGroup()
                                .addComponent(panelEditorBackgroundColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSelect7, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(comboSyntaxes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)))
                    .addComponent(panelTableDataFont1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelHighlightLayout.setVerticalGroup(
            panelHighlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHighlightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHighlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboSyntaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHighlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelEditorBackgroundColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonSelect7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textEditorPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTableDataFont1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabSettings.addTab(stringManager.getString("OrbadaSettingsDialog-editor-highlights"), panelHighlight); // NOI18N

        checkTabMovesSelected.setText(stringManager.getString("OrbadaSettingsDialog-checkTabMovesSelected-text")); // NOI18N

        panelAutoIndent.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("OrbadaSettingsDialog-panelAutoIndent-border-text"))); // NOI18N

        checkAutoCompleteDot.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoCompleteDot-text")); // NOI18N

        jLabel17.setText(stringManager.getString("OrbadaSettingsDialog-selected-list-el-dd")); // NOI18N

        autoCompleteInsertion.add(checkAutoCompleteInsertion);
        checkAutoCompleteInsertion.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoCompleteInsertion-text")); // NOI18N

        autoCompleteInsertion.add(checkAutoCompleteReplace);
        checkAutoCompleteReplace.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoCompleteReplace-text")); // NOI18N

        checkAutoCompleteInsertSingle.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoCompleteInsertSingle-text")); // NOI18N

        checkAutoCompleteStructureParser.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoCompleteStructureParser-text")); // NOI18N

        checkAutoCompleteStructureParserVariables.setText(stringManager.getString("OrbadaSettingsDialog-checkAutoCompleteStructureParserVariables-text")); // NOI18N

        jLabel18.setText(stringManager.getString("OrbadaSettingsDialog-open-after-dd")); // NOI18N

        jLabel19.setText(stringManager.getString("OrbadaSettingsDialog-close-after-dd")); // NOI18N

        javax.swing.GroupLayout panelAutoIndentLayout = new javax.swing.GroupLayout(panelAutoIndent);
        panelAutoIndent.setLayout(panelAutoIndentLayout);
        panelAutoIndentLayout.setHorizontalGroup(
            panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutoIndentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAutoIndentLayout.createSequentialGroup()
                        .addGroup(panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkAutoCompleteStructureParserVariables, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                            .addComponent(checkAutoCompleteInsertSingle)
                            .addComponent(jLabel17)
                            .addGroup(panelAutoIndentLayout.createSequentialGroup()
                                .addComponent(checkAutoCompleteInsertion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkAutoCompleteReplace))
                            .addComponent(checkAutoCompleteStructureParser, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                        .addContainerGap())
                    .addComponent(checkAutoCompleteDot)
                    .addGroup(panelAutoIndentLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelAutoIndentLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textAutoCompleteInactivate, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelAutoIndentLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textAutoCompleteActivate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(205, 205, 205))))
        );
        panelAutoIndentLayout.setVerticalGroup(
            panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutoIndentLayout.createSequentialGroup()
                .addComponent(checkAutoCompleteDot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(textAutoCompleteActivate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(textAutoCompleteInactivate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAutoCompleteInsertSingle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAutoIndentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAutoCompleteInsertion)
                    .addComponent(checkAutoCompleteReplace))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAutoCompleteStructureParser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAutoCompleteStructureParserVariables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkCopySyntaxHighlight.setText(stringManager.getString("OrbadaSettingsDialog-checkCopySyntaxHighlight-text")); // NOI18N

        checkEditorTrimWhitespaces.setText(stringManager.getString("OrbadaSettingsDialog-checkEditorTrimWhitespaces-text")); // NOI18N

        checkSmartHomeEnd.setText(stringManager.getString("OrbadaSettingsDialog-checkSmartHomeEnd-text")); // NOI18N

        jLabel20.setText(stringManager.getString("OrbadaSettingsDialog-tab-replace-spaces-dd")); // NOI18N

        checkPredefinedSnippets.setText(stringManager.getString("OrbadaSettingsDialog-checkPredefinedSnippets")); // NOI18N

        checkTabAsSpaces.setText(stringManager.getString("checkTabAsSpaces-text")); // NOI18N

        javax.swing.GroupLayout panelEditorLayout = new javax.swing.GroupLayout(panelEditor);
        panelEditor.setLayout(panelEditorLayout);
        panelEditorLayout.setHorizontalGroup(
            panelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkTabAsSpaces)
                    .addComponent(checkPredefinedSnippets)
                    .addComponent(checkTabMovesSelected)
                    .addComponent(checkEditorTrimWhitespaces, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(checkCopySyntaxHighlight, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(checkSmartHomeEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addGroup(panelEditorLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinTabToSpace, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelAutoIndent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelEditorLayout.setVerticalGroup(
            panelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkTabMovesSelected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkCopySyntaxHighlight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkEditorTrimWhitespaces, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkSmartHomeEnd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkPredefinedSnippets)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkTabAsSpaces)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(spinTabToSpace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelAutoIndent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        tabSettings.addTab(stringManager.getString("OrbadaSettingsDialog-editor"), panelEditor); // NOI18N

        buttonOk.setAction(cmOk);
        buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

        buttonCancel.setAction(cmCancel);
        buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabSettings, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cmTableDataFontChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmTableDataFontChangeActionPerformed
  Font font = FontChooser.showDialog(stringManager.getString("OrbadaSettingsDialog-select-font"), labelTableDataFont.getFont());
  if (font != null) {
    labelTableDataFont.setFont(font);
  }
}//GEN-LAST:event_cmTableDataFontChangeActionPerformed

private void cmColorNullActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColorNullActionPerformed
  changeDataColor(panelColorNull);
}//GEN-LAST:event_cmColorNullActionPerformed

private void cmColorBoolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColorBoolActionPerformed
  changeDataColor(panelColorBool);
}//GEN-LAST:event_cmColorBoolActionPerformed

private void cmColorDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColorDateActionPerformed
  changeDataColor(panelColorDate);
}//GEN-LAST:event_cmColorDateActionPerformed

private void cmColorStringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColorStringActionPerformed
  changeDataColor(panelColorString);
}//GEN-LAST:event_cmColorStringActionPerformed

private void cmColorNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColorNumberActionPerformed
  changeDataColor(panelColorNumber);
}//GEN-LAST:event_cmColorNumberActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  dialogToSettings();
  dispose();
}//GEN-LAST:event_cmOkActionPerformed

private void comboSyntaxesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboSyntaxesItemStateChanged
  if (evt.getStateChange() == ItemEvent.DESELECTED) {
    if (evt.getItem() instanceof SyntaxDocument) {
      syntaxDocumentToList((SyntaxDocument)evt.getItem());
    } 
  }
  else if (evt.getStateChange() == ItemEvent.SELECTED) {
    if (evt.getItem() instanceof SyntaxDocument) {
      listToSyntaxDocument((SyntaxDocument)evt.getItem());
      String text = textEditorPreview.getText();
      textEditorPreview.setDocument((SyntaxDocument)evt.getItem());
      textEditorPreview.setText(text);
    }
  }
}//GEN-LAST:event_comboSyntaxesItemStateChanged

private void cmSyntaxBackgroundColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSyntaxBackgroundColorActionPerformed
  changeDataColor(panelEditorBackgroundColor);
  textEditorPreview.getEditorArea().setBackground(panelEditorBackgroundColor.getBackground());
  textEditorPreview.repaint();
}//GEN-LAST:event_cmSyntaxBackgroundColorActionPerformed

private void cmSyntaxForegroundColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSyntaxForegroundColorActionPerformed
  changeDataColor(panelEditorForegroundColor);
  updateSyntaxStyle();
}//GEN-LAST:event_cmSyntaxForegroundColorActionPerformed

private void listSyntaxElementsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listSyntaxElementsValueChanged
  updateListSyntaxElement();
}//GEN-LAST:event_listSyntaxElementsValueChanged

private void checkEditorEnabledItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkEditorEnabledItemStateChanged
  updateSyntaxStyle();
}//GEN-LAST:event_checkEditorEnabledItemStateChanged

private void checkEditorBoldItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkEditorBoldItemStateChanged
  updateSyntaxStyle();
}//GEN-LAST:event_checkEditorBoldItemStateChanged

private void checkEditorItalicItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkEditorItalicItemStateChanged
  updateSyntaxStyle();
}//GEN-LAST:event_checkEditorItalicItemStateChanged

private void checkEditorUnderlineItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkEditorUnderlineItemStateChanged
  updateSyntaxStyle();
}//GEN-LAST:event_checkEditorUnderlineItemStateChanged

private void cmSyntaxRestoreSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSyntaxRestoreSettingsActionPerformed
  prepareSyntaxes();
  comboSyntaxes.addItem(sqlDocument);
  comboSyntaxes.addItem(javaDocument);
}//GEN-LAST:event_cmSyntaxRestoreSettingsActionPerformed

private void cmSyntaxFontChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSyntaxFontChangeActionPerformed
  Font font = FontChooser.showDialog(stringManager.getString("OrbadaSettingsDialog-select-font"), labelSyntaxFont.getFont());
  if (font != null) {
    labelSyntaxFont.setFont(font);
    textEditorPreview.getEditorArea().setFont(font);
    textEditorPreview.repaint();
  }
}//GEN-LAST:event_cmSyntaxFontChangeActionPerformed

private void cmColorSelectedRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmColorSelectedRowActionPerformed
  changeDataColor(panelColorSelectedTableRow);
}//GEN-LAST:event_cmColorSelectedRowActionPerformed

private void checkDefaultLookAndFeelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkDefaultLookAndFeelActionPerformed
  setLookAndFeelEnabled();
}//GEN-LAST:event_checkDefaultLookAndFeelActionPerformed

private void checkDateFormatDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkDateFormatDefaultActionPerformed
  setDataTypeEnabled();
}//GEN-LAST:event_checkDateFormatDefaultActionPerformed

private void checkTimeFormatDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkTimeFormatDefaultActionPerformed
  setDataTypeEnabled();
}//GEN-LAST:event_checkTimeFormatDefaultActionPerformed

private void checkTimeStampFormatDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkTimeStampFormatDefaultActionPerformed
  setDataTypeEnabled();
}//GEN-LAST:event_checkTimeStampFormatDefaultActionPerformed

private void checkDecimalFormatDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkDecimalFormatDefaultActionPerformed
  setDataTypeEnabled();
}//GEN-LAST:event_checkDecimalFormatDefaultActionPerformed

private void checkBigDecimalFormatDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBigDecimalFormatDefaultActionPerformed
  setDataTypeEnabled();
}//GEN-LAST:event_checkBigDecimalFormatDefaultActionPerformed

private void checkDecimalSeparatorDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkDecimalSeparatorDefaultActionPerformed
  setDataTypeEnabled();
}//GEN-LAST:event_checkDecimalSeparatorDefaultActionPerformed

private void checkPleaseWaitOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkPleaseWaitOnActionPerformed
  setPleaseWaitEnabled();
}//GEN-LAST:event_checkPleaseWaitOnActionPerformed

private void checkDateFormatDefaultItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkDateFormatDefaultItemStateChanged
  updateSampleDateTimeFormat();
}//GEN-LAST:event_checkDateFormatDefaultItemStateChanged

private void textDateFormatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textDateFormatFocusLost
  updateSampleDateTimeFormat();
}//GEN-LAST:event_textDateFormatFocusLost

private void checkTimeFormatDefaultItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkTimeFormatDefaultItemStateChanged
  updateSampleDateTimeFormat();
}//GEN-LAST:event_checkTimeFormatDefaultItemStateChanged

private void textTimeFormatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textTimeFormatFocusLost
  updateSampleDateTimeFormat();
}//GEN-LAST:event_textTimeFormatFocusLost

private void checkTimeStampFormatDefaultItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkTimeStampFormatDefaultItemStateChanged
  updateSampleDateTimeFormat();
}//GEN-LAST:event_checkTimeStampFormatDefaultItemStateChanged

private void textTimeStampFormatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textTimeStampFormatFocusLost
  updateSampleDateTimeFormat();
}//GEN-LAST:event_textTimeStampFormatFocusLost

private void checkDecimalFormatDefaultItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkDecimalFormatDefaultItemStateChanged
  updateSampleDecimalFormat();
}//GEN-LAST:event_checkDecimalFormatDefaultItemStateChanged

private void textDecimalFormatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textDecimalFormatFocusLost
  updateSampleDecimalFormat();
}//GEN-LAST:event_textDecimalFormatFocusLost

private void checkBigDecimalFormatDefaultItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkBigDecimalFormatDefaultItemStateChanged
  updateSampleDecimalFormat();
}//GEN-LAST:event_checkBigDecimalFormatDefaultItemStateChanged

private void textBigDecimalFormatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textBigDecimalFormatFocusLost
  updateSampleDecimalFormat();
}//GEN-LAST:event_textBigDecimalFormatFocusLost

private void checkDecimalSeparatorDefaultItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkDecimalSeparatorDefaultItemStateChanged
  updateSampleDecimalFormat();
}//GEN-LAST:event_checkDecimalSeparatorDefaultItemStateChanged

private void textDecimalSeparatorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textDecimalSeparatorFocusLost
  updateSampleDecimalFormat();
}//GEN-LAST:event_textDecimalSeparatorFocusLost

private void radioNoProxyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioNoProxyItemStateChanged
  setProxyEnabled();
}//GEN-LAST:event_radioNoProxyItemStateChanged

private void radioUseSystemProxyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioUseSystemProxyItemStateChanged
  setProxyEnabled();
}//GEN-LAST:event_radioUseSystemProxyItemStateChanged

private void radioManulProxyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioManulProxyItemStateChanged
  setProxyEnabled();
}//GEN-LAST:event_radioManulProxyItemStateChanged

private void checkProxyAuthNeddedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkProxyAuthNeddedItemStateChanged
  setProxyEnabled();
}//GEN-LAST:event_checkProxyAuthNeddedItemStateChanged

private void cmPleaseWaitTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPleaseWaitTestActionPerformed
  if (waitTest == null) {
    waitTest = new PleaseWait(ImageManager.getImage("/res/orbada48.png"), stringManager.getString("please-wait-test-message"));
    cmPleaseWaitTest.setText(stringManager.getString("cmPleaseWaitTest-stop-text"));
    Application.get().getMainFrame().getGlassPane().addPleaseWait(waitTest, pwrpa[comboPleaseWaitService.getSelectedIndex()].getRendererId());
  }
  else {
    Application.get().getMainFrame().getGlassPane().removePleaseWait(waitTest);
    waitTest = null;
    cmPleaseWaitTest.setText(stringManager.getString("cmPleaseWaitTest-test-text"));
  }
}//GEN-LAST:event_cmPleaseWaitTestActionPerformed
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup autoCompleteInsertion;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonSelect1;
    private javax.swing.JButton buttonSelect2;
    private javax.swing.JButton buttonSelect3;
    private javax.swing.JButton buttonSelect4;
    private javax.swing.JButton buttonSelect5;
    private javax.swing.JButton buttonSelect6;
    private javax.swing.JButton buttonSelect7;
    private javax.swing.JButton buttonSelect8;
    private javax.swing.JButton buttonTableDataFontChange;
    private javax.swing.JButton buttonTableDataFontChange1;
    private javax.swing.JButton buttonWaitTest;
    private javax.swing.JCheckBox checkAutoCompleteDot;
    private javax.swing.JCheckBox checkAutoCompleteInsertSingle;
    private javax.swing.JRadioButton checkAutoCompleteInsertion;
    private javax.swing.JRadioButton checkAutoCompleteReplace;
    private javax.swing.JCheckBox checkAutoCompleteStructureParser;
    private javax.swing.JCheckBox checkAutoCompleteStructureParserVariables;
    private javax.swing.JCheckBox checkAutoFitColumnWidth;
    private javax.swing.JCheckBox checkAutoSaveColumnWidths;
    private javax.swing.JCheckBox checkBigDecimalFormatDefault;
    private javax.swing.JCheckBox checkCloseWarning;
    private javax.swing.JCheckBox checkColorizedQueryTable;
    private javax.swing.JCheckBox checkCopySyntaxHighlight;
    private javax.swing.JCheckBox checkDateFormatDefault;
    private javax.swing.JCheckBox checkDecimalFormatDefault;
    private javax.swing.JCheckBox checkDecimalSeparatorDefault;
    private javax.swing.JCheckBox checkDefaultColorSelectedTableRow;
    private javax.swing.JCheckBox checkDefaultLookAndFeel;
    private javax.swing.JCheckBox checkDisableCheckUpdates;
    private javax.swing.JCheckBox checkDisableLoadSqlSyntaxInfo;
    private javax.swing.JCheckBox checkEditorBold;
    private javax.swing.JCheckBox checkEditorEnabled;
    private javax.swing.JCheckBox checkEditorItalic;
    private javax.swing.JCheckBox checkEditorTrimWhitespaces;
    private javax.swing.JCheckBox checkEditorUnderline;
    private javax.swing.JCheckBox checkNoRollbackOnClose;
    private javax.swing.JCheckBox checkNoViewTabPictures;
    private javax.swing.JCheckBox checkNoViewTabTitles;
    private javax.swing.JCheckBox checkOpenNewConnectionAtStartup;
    private javax.swing.JCheckBox checkPleaseWaitOn;
    private javax.swing.JCheckBox checkPredefinedSnippets;
    private javax.swing.JCheckBox checkProxyAuthNedded;
    private javax.swing.JCheckBox checkSmartHomeEnd;
    private javax.swing.JCheckBox checkTabAsSpaces;
    private javax.swing.JCheckBox checkTabMovesSelected;
    private javax.swing.JCheckBox checkTimeFormatDefault;
    private javax.swing.JCheckBox checkTimeStampFormatDefault;
    private pl.mpak.sky.gui.swing.Action cmCancel;
    private pl.mpak.sky.gui.swing.Action cmColorBool;
    private pl.mpak.sky.gui.swing.Action cmColorDate;
    private pl.mpak.sky.gui.swing.Action cmColorNull;
    private pl.mpak.sky.gui.swing.Action cmColorNumber;
    private pl.mpak.sky.gui.swing.Action cmColorSelectedRow;
    private pl.mpak.sky.gui.swing.Action cmColorString;
    private pl.mpak.sky.gui.swing.Action cmOk;
    private pl.mpak.sky.gui.swing.Action cmPleaseWaitTest;
    private pl.mpak.sky.gui.swing.Action cmSyntaxBackgroundColor;
    private pl.mpak.sky.gui.swing.Action cmSyntaxFontChange;
    private pl.mpak.sky.gui.swing.Action cmSyntaxForegroundColor;
    private pl.mpak.sky.gui.swing.Action cmSyntaxRestoreSettings;
    private pl.mpak.sky.gui.swing.Action cmTableDataFontChange;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboLookAndFeelService;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboPleaseWaitService;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboSyntaxes;
    private pl.mpak.sky.gui.swing.comp.HtmlEditorPane htmlDateTimeFormatInfo;
    private pl.mpak.sky.gui.swing.comp.HtmlEditorPane htmlDecimalFormatInfo;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel labelBigDecimalFormat;
    private javax.swing.JLabel labelBigDecimalFormatSample;
    private javax.swing.JLabel labelDateFormat;
    private javax.swing.JLabel labelDateFormatSample;
    private javax.swing.JLabel labelDecimalFormat;
    private javax.swing.JLabel labelDecimalFormatSample;
    private javax.swing.JLabel labelDecimalSeparator;
    private javax.swing.JLabel labelHttpProxyAddress;
    private javax.swing.JLabel labelHttpProxyPort;
    private javax.swing.JLabel labelLookAndFeel;
    private javax.swing.JLabel labelLookAndFeelInfo;
    private javax.swing.JLabel labelProxyAuthPassword;
    private javax.swing.JLabel labelProxyAuthUser;
    private javax.swing.JLabel labelSyntaxFont;
    private javax.swing.JLabel labelTableDataFont;
    private javax.swing.JLabel labelTimeFormat;
    private javax.swing.JLabel labelTimeFormatSample;
    private javax.swing.JLabel labelTimeStampFormat;
    private javax.swing.JLabel labelTimeStampFormatSample;
    private javax.swing.JLabel labeloPleaseWaitService;
    private javax.swing.JList listSyntaxElements;
    private javax.swing.JPanel panelApperance;
    private javax.swing.JPanel panelAutoIndent;
    private javax.swing.JPanel panelColorBool;
    private javax.swing.JPanel panelColorDate;
    private javax.swing.JPanel panelColorNull;
    private javax.swing.JPanel panelColorNumber;
    private javax.swing.JPanel panelColorSelectedTableRow;
    private javax.swing.JPanel panelColorString;
    private javax.swing.JPanel panelDataFormat;
    private javax.swing.JPanel panelDataList;
    private javax.swing.JPanel panelEditor;
    private javax.swing.JPanel panelEditorBackgroundColor;
    private javax.swing.JPanel panelEditorForegroundColor;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelHighlight;
    private javax.swing.JPanel panelProxy;
    private javax.swing.JPanel panelTableDataFont;
    private javax.swing.JPanel panelTableDataFont1;
    private javax.swing.ButtonGroup proxyGroup;
    private javax.swing.JRadioButton radioManulProxy;
    private javax.swing.JRadioButton radioNoProxy;
    private javax.swing.JRadioButton radioUseSystemProxy;
    private javax.swing.JSpinner spinEvenRowShift;
    private javax.swing.JSpinner spinFocusedShift;
    private javax.swing.JSpinner spinTabToSpace;
    private javax.swing.JTabbedPane tabSettings;
    private pl.mpak.sky.gui.swing.comp.TextField textAutoCompleteActivate;
    private pl.mpak.sky.gui.swing.comp.TextField textAutoCompleteInactivate;
    private pl.mpak.sky.gui.swing.comp.TextField textBigDecimalFormat;
    private pl.mpak.sky.gui.swing.comp.TextField textDateFormat;
    private pl.mpak.sky.gui.swing.comp.TextField textDecimalFormat;
    private pl.mpak.sky.gui.swing.comp.TextField textDecimalSeparator;
    private pl.mpak.sky.gui.swing.syntax.SyntaxTextArea textEditorPreview;
    private pl.mpak.sky.gui.swing.comp.TextField textHttpProxyAddress;
    private pl.mpak.sky.gui.swing.comp.TextField textHttpProxyPort;
    private pl.mpak.sky.gui.swing.comp.TextField textNullValue;
    private javax.swing.JPasswordField textProxyAuthPassword;
    private pl.mpak.sky.gui.swing.comp.TextField textProxyAuthUser;
    private pl.mpak.sky.gui.swing.comp.TextField textTimeFormat;
    private pl.mpak.sky.gui.swing.comp.TextField textTimeStampFormat;
    // End of variables declaration//GEN-END:variables
  
}
