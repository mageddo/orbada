package pl.mpak.orbada.oracle.gui.packages;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.oracle.util.SourceCreator;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.packages.cm.PageDownAction;
import pl.mpak.orbada.oracle.gui.packages.cm.PageUpAction;
import pl.mpak.orbada.oracle.services.OracleCompileErrorSettingsProvider;
import pl.mpak.orbada.oracle.syntax.parser.OraclePlSqlStructureParser;
import pl.mpak.orbada.oracle.util.OracleUtil;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class PackageSourcePanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentObjectName = "";
  private String objectType;
  private boolean requestRefresh = false;
  private boolean closing = false;
  private ImageIcon errIcon;
  private ISettings errorSettings;
  
  public PackageSourcePanel(IViewAccesibilities accesibilities, String objectType) {
    this.accesibilities = accesibilities;
    this.objectType = objectType;
    initComponents();
    init();
  }
  
  private void setEditorText(final String objectName, final String text) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        syntaxSource.setDatabaseObject(currentSchemaName, objectType, objectName, text);
        syntaxSource.getEditorArea().setCaretPosition(0);
      }
    });
  }
  
  private void init() {
    syntaxSource.getStatusBar().addPanel("ddl-status").setText(" ");
    syntaxSource.setDatabase(getDatabase());
    syntaxSource.setStructureParser(new OraclePlSqlStructureParser());
    SwingUtil.addAction(syntaxSource.getEditorArea(), cmStore);
    errIcon = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/stop10.gif");
    errorSettings = accesibilities.getApplication().getSettings(OracleCompileErrorSettingsProvider.settingsName);
    new ComponentActionsAction(getDatabase(), syntaxSource.getEditorArea(), buttonActions, menuActions, "oracle-package-source-actions");
    SwingUtil.addAction(syntaxSource.getEditorArea(), new PageUpAction(this));
    SwingUtil.addAction(syntaxSource.getEditorArea(), new PageDownAction(this));
  }
  
  public String getObjectName() {
    return syntaxSource.getObjectName();
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("PackageSourcePanel-title");
  }
  
  public void gotoPoint(int line, int column) {
    int offset = syntaxSource.getEditorArea().getLineStartOffset(line -1) +column -1;
    syntaxSource.getEditorArea().setCaretPosition(offset);
  }
  
  public void gotoMethod(String method, String type) {
    Pattern patt = Pattern.compile(type +"\\s+(" +method +"\\b|(\"" +method +"\\b\"))\\s*?(\\(.*\\))?.*(IS|AS)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    Matcher mat = patt.matcher(syntaxSource.getText());
    if (mat.find()) {
      syntaxSource.getEditorArea().setCaretPosition(mat.start(), true);
    }
  }
  
  @Override
  public boolean requestFocusInWindow() {
    return syntaxSource.getEditorArea().requestFocusInWindow();
  }
  
  private void refreshTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    requestRefresh = false;
    new SourceCreator(getDatabase(), syntaxSource).getSource(currentSchemaName, objectType, currentObjectName);
    syntaxSource.getStatusBar().getPanel("ddl-status").setText(" " +currentObjectName);
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentObjectName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentObjectName = objectName;
      if (isVisible()) {
        refresh();
      } else {
        requestRefresh = true;
        setEditorText(null, "");
      }
    }
  }
  
  @Override
  public boolean canClose() {
    return syntaxSource.canClose();
  }

  public void close() throws IOException {
    closing = true;
    syntaxSource.setDatabase(null);
    accesibilities = null;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefreshSource = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    cmStore = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    toolBarContent = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonStore = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    syntaxSource = new OrbadaSyntaxTextArea();

    cmRefreshSource.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefreshSource.setText(stringManager.getString("cmRefreshSource-text")); // NOI18N
    cmRefreshSource.setTooltip(stringManager.getString("cmRefreshSource-hint")); // NOI18N
    cmRefreshSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshSourceActionPerformed(evt);
      }
    });

    cmStore.setActionCommandKey("cmStore");
    cmStore.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmStore.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/store_db.gif")); // NOI18N
    cmStore.setText(stringManager.getString("cmStore-text")); // NOI18N
    cmStore.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmStoreActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarContent.setFloatable(false);
    toolBarContent.setRollover(true);

    buttonRefresh.setAction(cmRefreshSource);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarContent.add(buttonRefresh);
    toolBarContent.add(jSeparator1);

    buttonStore.setAction(cmStore);
    buttonStore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonStore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarContent.add(buttonStore);
    toolBarContent.add(buttonActions);

    jPanel1.add(toolBarContent);

    add(jPanel1, java.awt.BorderLayout.NORTH);
    add(syntaxSource, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshSourceActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshSourceActionPerformed

private void cmStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmStoreActionPerformed
  PackageErrorsPanel errors = (PackageErrorsPanel)SwingUtil.getTabbedPaneComponent(PackageErrorsPanel.class, this);
  syntaxSource.getStatusBar().getPanel("ddl-status").setText(stringManager.getString("executing-3d"));
  try {
    syntaxSource.storeObject();
    if (errors != null) {
      errors.refresh();
      PackagesPanelView view = (PackagesPanelView)SwingUtil.getOwnerComponent(PackagesPanelView.class, this);
      if (view != null) {
        view.refresh();
      }
      syntaxSource.clearLineMarks();
      if (!errors.getQuery().eof()) {
        syntaxSource.getStatusBar().getPanel("ddl-status").setText(stringManager.getString("error-e") +" " +errors.getQuery().fieldByName("text").getString());
        syntaxSource.setLineMark(OracleUtil.getErrLines(errors.getQuery(), errorSettings.getValue(OracleCompileErrorSettingsProvider.setErrorLineColor, SwingUtil.Color.DARKORANGE), errIcon));
        if (errorSettings.getValue(OracleCompileErrorSettingsProvider.setOnErrorGoToTab, true)) {
          JTabbedPane tp = (JTabbedPane)SwingUtil.getOwnerComponent(JTabbedPane.class, this);
          if (tp != null) {
            tp.setSelectedComponent(errors);
          }
        }
        else {
          errors.getQuery().first();
          gotoPoint(            
            errors.getQuery().fieldByName("line").getInteger(),
            errors.getQuery().fieldByName("position").getInteger());
        }
      }
      else {
        syntaxSource.getStatusBar().getPanel("ddl-status").setText(stringManager.getString("stored"));
      }
    }
  }
  catch (Exception ex) {
    syntaxSource.getStatusBar().getPanel("ddl-status").setText(stringManager.getString("error-e"));
    if (ex instanceof NullPointerException) {
      ExceptionUtil.processException(ex);
    }
    else {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmStoreActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonStore;
  private pl.mpak.sky.gui.swing.Action cmRefreshSource;
  private pl.mpak.sky.gui.swing.Action cmStore;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private OrbadaSyntaxTextArea syntaxSource;
  private javax.swing.JToolBar toolBarContent;
  // End of variables declaration//GEN-END:variables
  
}
