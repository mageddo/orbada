package pl.mpak.orbada.universal.gui.tabbed;

import java.awt.Color;
import java.io.IOException;
import javax.swing.JToolBar;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.LineMark;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public abstract class UniversalSourceTab extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  protected IViewAccesibilities accesibilities;
  protected String currentSchemaName = "";
  protected String currentObjectName = "";
  protected boolean requestRefresh = false;
  protected boolean closing = false;
  protected String lastSource;
  protected ISettings settings;
  protected ComponentActionsAction componentActions;
  private LineMark lineMark;
  
  /** 
   * @param accesibilities
   */
  public UniversalSourceTab(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        init();
      }
    });
  }
  
  protected void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), getPanelName() +"-settings");
    textProp.setDatabase(getDatabase());
    if (isToolbar()) {
      SwingUtil.addAction(textProp.getEditorArea(), cmStore);
      componentActions = new ComponentActionsAction(getDatabase(), textProp.getEditorArea(), buttonActions, menuActions, getPanelName() +"-actions");
      cmStore.setEnabled(isStorable());
      buttonStore.setVisible(isStorable());
    }
    else {
      toolBarPanel.setVisible(false);
    }
    textProp.setEditable(isStorable());
    lineMark = new LineMark(-1, Color.getColor("/res/icons/line_point.gif"));
  }
  
  /**
   * panel name used for setting name, filter, actions, etc
   * like "database-panel"
   * @return 
   */
  abstract public String getPanelName();
  
  abstract public void updateBody(AbsOrbadaSyntaxTextArea textArea);
  
  /**
   * true if object can store and edit, false if read/only
   * @return 
   */
  abstract public boolean isStorable();
  
  /**
   * you can refresh other tab after store object in database
   * you can also throw exception if you want restore last source
   */
  public void afterStore() {
    
  }
  
  /**
   * if return false then hide toolbar
   * @return 
   */
  protected boolean isToolbar() {
    return true;
  }
  
  public String getObjectName() {
    return currentObjectName;
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public void gotoPoint(int line, int column) {
    int offset = textProp.getEditorArea().getLineStartOffset(line -1) +column -1;
    textProp.getEditorArea().setCaretPosition(offset);
    textProp.updateCaretPositionList();
    textProp.removeLineMark(lineMark);
    lineMark.setLine(line);
    textProp.setLineMark(lineMark);
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
    updateBody(textProp);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        lastSource = textProp.getText();
      }
    });
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentObjectName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentObjectName = objectName;
      if (SwingUtil.isVisible(this)) {
        refresh();
      } else {
        requestRefresh = true;
        textProp.setDatabaseObject(currentSchemaName, "", currentObjectName, "");
      }
    }
  }

  public String getCurrentObjectName() {
    return currentObjectName;
  }

  public String getCurrentSchemaName() {
    return currentSchemaName;
  }
  
  public AbsOrbadaSyntaxTextArea getTextArea() {
    return textProp;
  }
  
  public JToolBar getToolBar() {
    return toolBarContent;
  }
  
  @Override
  public boolean canClose() {
    return textProp.canClose();
  }

  public void close() throws IOException {
    closing = true;
    settings.store();
    textProp.setDatabase(null);
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
        toolBarPanel = new javax.swing.JPanel();
        toolBarContent = new javax.swing.JToolBar();
        buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        buttonStore = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
        textProp = new OrbadaSyntaxTextArea();

        cmRefreshSource.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
        cmRefreshSource.setText(stringManager.getString("cmRefreshSource-text")); // NOI18N
        cmRefreshSource.setTooltip(stringManager.getString("cmRefreshSource-hint")); // NOI18N
        cmRefreshSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmRefreshSourceActionPerformed(evt);
            }
        });

        cmStore.setActionCommandKey("cmStore");
        cmStore.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
        cmStore.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/store_db.gif")); // NOI18N
        cmStore.setText(stringManager.getString("cmStore-text")); // NOI18N
        cmStore.setTooltip(stringManager.getString("cmStore-hint")); // NOI18N
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

        toolBarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

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

        toolBarPanel.add(toolBarContent);

        add(toolBarPanel, java.awt.BorderLayout.NORTH);
        add(textProp, java.awt.BorderLayout.CENTER);
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
  textProp.getStatusBar().getPanel("ddl-status").setText(stringManager.getString("executing-3d"));
  try {
    textProp.storeScript();
    afterStore();
    lastSource = textProp.getText();
    textProp.getStatusBar().getPanel("ddl-status").setText(stringManager.getString("stored"));
    UniversalViewTabs view = (UniversalViewTabs)SwingUtil.getOwnerComponent(UniversalViewTabs.class, this);
    if (view != null) {
      view.refresh();
    }
  }
  catch (Exception ex) {
    if (lastSource != null) {
      try {
        textProp.storeScript(lastSource);
      }
      catch (Exception ex2) {
        ExceptionUtil.processException(ex2);
      }
    }
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    textProp.getStatusBar().getPanel("ddl-status").setText(stringManager.getString("error-e"));
  }
}//GEN-LAST:event_cmStoreActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonStore;
    private pl.mpak.sky.gui.swing.Action cmRefreshSource;
    private pl.mpak.sky.gui.swing.Action cmStore;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu menuActions;
    private OrbadaSyntaxTextArea textProp;
    private javax.swing.JToolBar toolBarContent;
    private javax.swing.JPanel toolBarPanel;
    // End of variables declaration//GEN-END:variables
  
}
