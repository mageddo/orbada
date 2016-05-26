package pl.mpak.orbada.firebird.gui.tables;

import java.awt.Component;
import java.awt.Dialog;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.freezing.TriggerFreezeViewService;
import pl.mpak.orbada.firebird.gui.wizards.CreateTriggerWizard;
import pl.mpak.orbada.firebird.syntax.parser.FirebirdPSqlStructureParser;
import pl.mpak.orbada.firebird.util.SourceCreator;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author  akaluza
 */
public class TableTriggersPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  private IViewAccesibilities accesibilities;
  private String currentTableName = "";
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  private ISettings settings;
  
  public TableTriggersPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-table-triggers-panel");
    splitTriggers.setDividerLocation(settings.getValue("split-location", (long)splitTriggers.getDividerLocation()).intValue());
    
    textTrigger.setDatabase(getDatabase());
    textTrigger.setStructureParser(new FirebirdPSqlStructureParser());
    textTrigger.setEditable(false);
    tableTriggers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      String triggerName;
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableTriggers.getSelectedRow();
        if (rowIndex >= 0 && tableTriggers.getQuery().isActive()) {
          try {
            tableTriggers.getQuery().getRecord(rowIndex);
            if (triggerName == null || !triggerName.equals(tableTriggers.getQuery().fieldByName("trigger_name").getString())) {
              updateTriggerBody();
              triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          textTrigger.setText("");
        }
      }
    });
    
    tableTriggers.getQuery().setDatabase(getDatabase());
    try {
      tableTriggers.addColumn(new QueryTableColumn("TRIGGER_NAME", stringManager.getString("TableTriggersPanel-trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableTriggers.addColumn(new QueryTableColumn("RDB$TRIGGER_TYPE", stringManager.getString("TableTriggersPanel-when"), 120, new QueryTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
          label.setForeground(tableTriggers.getForeground());
          label.setHorizontalAlignment(JLabel.LEADING);
          if (value instanceof Variant) {
            try {
              label.setText(SourceCreator.decodeTriggerType(null, ((Variant)value).getInteger()));
            } catch (VariantException ex) {
              ExceptionUtil.processException(ex);
            }
          }
          else {
            label.setText(SourceCreator.decodeTriggerType(null, (Short)value));
          }
          return label;
        }
      }));
      tableTriggers.addColumn(new QueryTableColumn("TRIGGER_SEQUENCE", stringManager.getString("TableTriggersPanel-sequence"), 60));
      tableTriggers.addColumn(new QueryTableColumn("TRIGGER_ACTIVE", stringManager.getString("TableTriggersPanel-active"), 60));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("TRIGGER_NAME", stringManager.getString("TableTriggersPanel-trigger-name"), (String[])null));
      def.add(new SqlFilterDefComponent("TRIGGER_ACTIVE <> 'Y'", stringManager.getString("TableTriggersPanel-activated")));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "firebird-table-triggers-filter"),
        cmFilter, buttonFilter, 
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), tableTriggers, buttonActions, menuActions, "firebird-table-triggers-actions");
  }
  
  private void refreshTask() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }
  
  public void refresh() {
    try {
      requestRefresh = false;
      tableTriggers.getQuery().close();
      tableTriggers.getQuery().setSqlText(Sql.getTableTriggerList(filter.getSqlText()));
      tableTriggers.getQuery().paramByName("table_name").setString(currentTableName);
      tableTriggers.getQuery().open();
      if (!tableTriggers.getQuery().isEmpty()) {
        tableTriggers.changeSelection(0, 0);
        updateTriggerBody();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void updateTriggerBody() {
    try {
      new SourceCreator(getDatabase(), textTrigger).getSource(null, "TRIGGER", tableTriggers.getQuery().fieldByName("trigger_name").getString());
    }
    catch (Exception ex) {
      textTrigger.setDatabaseObject(null, null, null, "");
    }
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("TableTriggersPanel-title");
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentTableName.equals(objectName) || requestRefresh) {
      currentTableName = objectName;
      if (isVisible()) {
        refresh();
      }
      else {
        requestRefresh = true;
      }
    }
  }
  
  @Override
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    closing = true;
    settings.setValue("split-location", (long)splitTriggers.getDividerLocation());
    settings.store();
    tableTriggers.getQuery().close();
    textTrigger.setDatabase(null);
    accesibilities = null;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    cmFilter = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jMenuItem3 = new javax.swing.JMenuItem();
    jMenuItem1 = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    jMenuItem2 = new javax.swing.JMenuItem();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    cmCreateTrigger = new pl.mpak.sky.gui.swing.Action();
    cmDropTrigger = new pl.mpak.sky.gui.swing.Action();
    cmActiveTrigger = new pl.mpak.sky.gui.swing.Action();
    jPanel2 = new javax.swing.JPanel();
    toolBarTriggers = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    toolButton4 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    splitTriggers = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTriggers = new ViewTable();
    statusBarTriggers = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    textTrigger = new OrbadaSyntaxTextArea();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setText(stringManager.getString("cmFilter-text")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    jMenuItem3.setAction(cmActiveTrigger);
    menuActions.add(jMenuItem3);

    jMenuItem1.setAction(cmCreateTrigger);
    menuActions.add(jMenuItem1);
    menuActions.add(jSeparator3);

    jMenuItem2.setAction(cmDropTrigger);
    menuActions.add(jMenuItem2);

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
      }
    });

    cmCreateTrigger.setActionCommandKey("cmCreateTrigger");
    cmCreateTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif")); // NOI18N
    cmCreateTrigger.setText(stringManager.getString("TableTriggersPanel-cmCreateTrigger-text")); // NOI18N
    cmCreateTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateTriggerActionPerformed(evt);
      }
    });

    cmDropTrigger.setActionCommandKey("cmDropTrigger");
    cmDropTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDropTrigger.setText(stringManager.getString("TableTriggersPanel-cmDropTrigger-text")); // NOI18N
    cmDropTrigger.setTooltip(stringManager.getString("TableTriggersPanel-cmDropTrigger-hint")); // NOI18N
    cmDropTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDropTriggerActionPerformed(evt);
      }
    });

    cmActiveTrigger.setActionCommandKey("cmActiveTrigger");
    cmActiveTrigger.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/enabled.gif")); // NOI18N
    cmActiveTrigger.setText(stringManager.getString("TableTriggersPanel-cmActiveTrigger-text")); // NOI18N
    cmActiveTrigger.setTooltip(stringManager.getString("TableTriggersPanel-cmActiveTrigger-hint")); // NOI18N
    cmActiveTrigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmActiveTriggerActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBarTriggers.setFloatable(false);
    toolBarTriggers.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(buttonFilter);

    toolButton1.setAction(cmFreezeObject);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton1);
    toolBarTriggers.add(jSeparator1);

    toolButton4.setAction(cmActiveTrigger);
    toolButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton4);

    toolButton2.setAction(cmCreateTrigger);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton2);
    toolBarTriggers.add(jSeparator2);

    toolButton3.setAction(cmDropTrigger);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarTriggers.add(toolButton3);
    toolBarTriggers.add(buttonActions);

    jPanel2.add(toolBarTriggers);

    add(jPanel2, java.awt.BorderLayout.NORTH);

    splitTriggers.setBorder(null);
    splitTriggers.setDividerLocation(350);
    splitTriggers.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    splitTriggers.setContinuousLayout(true);
    splitTriggers.setOneTouchExpandable(true);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableTriggers);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarTriggers.setShowFieldType(false);
    statusBarTriggers.setShowOpenTime(false);
    statusBarTriggers.setTable(tableTriggers);
    jPanel1.add(statusBarTriggers, java.awt.BorderLayout.SOUTH);

    splitTriggers.setLeftComponent(jPanel1);

    textTrigger.setPreferredSize(new java.awt.Dimension(81, 150));
    splitTriggers.setRightComponent(textTrigger);

    add(splitTriggers, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refresh();
  }
}//GEN-LAST:event_cmFilterActionPerformed
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (requestRefresh && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      final String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      accesibilities.createView(new TriggerFreezeViewService(accesibilities, null, triggerName));
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void cmCreateTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateTriggerActionPerformed
  SqlCodeWizardDialog.WizardResult result = SqlCodeWizardDialog.show(new CreateTriggerWizard(getDatabase(), currentTableName), true);
  if (result != null) {
    refresh();
  }
}//GEN-LAST:event_cmCreateTriggerActionPerformed

private void cmDropTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDropTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (MessageBox.show((Dialog)null, stringManager.getString("deleting"), String.format(stringManager.getString("TableTriggersPanel-drop-trigger-q"), new Object[] {triggerName}), ModalResult.YESNO) == ModalResult.YES) {
        getDatabase().createCommand("drop trigger " +SQLUtil.createSqlName(triggerName), true);
        refresh();
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmDropTriggerActionPerformed

private void cmActiveTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmActiveTriggerActionPerformed
  if (tableTriggers.getSelectedRow() >= 0) {
    try {
      tableTriggers.getQuery().getRecord(tableTriggers.getSelectedRow());
      String triggerName = tableTriggers.getQuery().fieldByName("trigger_name").getString();
      if (StringUtil.toBoolean(tableTriggers.getQuery().fieldByName("trigger_active").getString())) {
        if (MessageBox.show((Dialog)null, stringManager.getString("TableTriggersPanel-trigger"), String.format(stringManager.getString("TableTriggersPanel-inactive-trigger-q"), new Object[] {triggerName}), ModalResult.YESNO) == ModalResult.YES) {
          getDatabase().createCommand("alter trigger " +SQLUtil.createSqlName(triggerName) +" INACTIVE", true);
          refresh();
        }
      }
      else {
        if (MessageBox.show((Dialog)null, stringManager.getString("TableTriggersPanel-trigger"), String.format(stringManager.getString("TableTriggersPanel-active-trigger-q"), new Object[] {triggerName}), ModalResult.YESNO) == ModalResult.YES) {
          getDatabase().createCommand("alter trigger " +SQLUtil.createSqlName(triggerName) +" ACTIVE", true);
          refresh();
        }
      }
    } catch (Exception ex) {
      MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }
}//GEN-LAST:event_cmActiveTriggerActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmActiveTrigger;
  private pl.mpak.sky.gui.swing.Action cmCreateTrigger;
  private pl.mpak.sky.gui.swing.Action cmDropTrigger;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JSplitPane splitTriggers;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarTriggers;
  private ViewTable tableTriggers;
  private OrbadaSyntaxTextArea textTrigger;
  private javax.swing.JToolBar toolBarTriggers;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton4;
  // End of variables declaration//GEN-END:variables
  
}
