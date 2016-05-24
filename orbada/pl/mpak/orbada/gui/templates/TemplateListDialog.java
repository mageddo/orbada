package pl.mpak.orbada.gui.templates;

import java.util.EventObject;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Template;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 * 
 * @author  akaluza
 */
public class TemplateListDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private ISettings settings;
  private int modalResult = ModalResult.NONE;
  private String tpl_id;
  private SyntaxEditor editor;
  
  /** Creates new form SchemaListDialog */
  public TemplateListDialog() {
    super(SwingUtil.getRootFrame());
    initComponents();
    init();
  }
  
  public static void showDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        TemplateListDialog dialog = new TemplateListDialog();
        dialog.setVisible(true);
        if (dialog.modalResult == ModalResult.OK) {
          TemplateUseDialog.showDialog(dialog.tpl_id, dialog.editor);
        }
      }
    });
  }
  
  private void init() {
    settings = Application.get().getSettings("orbada-template-list-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", new Variant(getWidth())).getInteger(), settings.getValue("height", new Variant(getHeight())).getInteger());
    } catch (Exception ex) {
    }
    SwingUtil.centerWithinScreen(this);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmClose");
    getRootPane().getActionMap().put("cmClose", cmClose);
    
    tableTemplates.getQuery().setDatabase(InternalDatabase.get());
    tableTemplates.getQuery().addQueryListener(new QueryListener() {
      public void beforeScroll(EventObject e) {
      }
      public void afterScroll(EventObject e) {
      }
      public void beforeOpen(EventObject e) {
      }
      public void afterOpen(EventObject e) {
        cmNew.setEnabled(true);
      }
      public void beforeClose(EventObject e) {
      }
      public void afterClose(EventObject e) {
        cmNew.setEnabled(false);
        cmEdit.setEnabled(false);
        cmDelete.setEnabled(false);
      }
      public void flushedPerformed(EventObject e) {
      }
      public void errorPerformed(EventObject e) {
      }
    });
    
    tableTemplates.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableTemplates.getSelectedRow() >= 0) {
          try {
            tableTemplates.getQuery().getRecord(tableTemplates.getSelectedRow());
            cmEdit.setEnabled(true);
            cmDelete.setEnabled(
              (!tableTemplates.getQuery().fieldByName("TPL_USR_ID").isNull() ||
              Application.get().isUserAdmin()));
            cmUse.setEnabled(buttonUse.isVisible());
          } catch (Exception ex) {
            MessageBox.show(TemplateListDialog.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
          }
        }
        else {
          cmEdit.setEnabled(false);
          cmDelete.setEnabled(false);
          cmUse.setEnabled(false);
        }
      }
    });
    
    try {
      tableTemplates.addColumn(new QueryTableColumn("tpl_name", stringManager.getString("TemplateListDialog-command"), 180, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableTemplates.addColumn(new QueryTableColumn("range", stringManager.getString("TemplateListDialog-definition"), 80));
      tableTemplates.addColumn(new QueryTableColumn("tpl_description", stringManager.getString("TemplateListDialog-description"), 350));
      tableTemplates.getQuery().setSqlText(
        "select tpl_id, tpl_usr_id, tpl_name, case when tpl_usr_id is null then 'Globalna' else 'U¿ytkownika' end range, tpl_description\n" +
        "  from templates\n" +
        " where (tpl_usr_id = :usr_id or tpl_usr_id is null)\n" +
        " order by tpl_name");
      tableTemplates.getQuery().paramByName("usr_id").setString(Application.get().getUserId());
      tableTemplates.getQuery().open();
      if (!tableTemplates.getQuery().isEmpty()) {
        tableTemplates.setRowSelectionInterval(0, 0);
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
    textFiltr.addKeyListener(new TableRowChangeKeyListener(tableTemplates));
    textFiltr.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        findTemplate();
      }
      public void removeUpdate(DocumentEvent e) {
        findTemplate();
      }
      public void changedUpdate(DocumentEvent e) {
      }
    });
    if (FocusManager.getCurrentManager().getFocusOwner() instanceof SyntaxEditor) {
      editor = (SyntaxEditor)FocusManager.getCurrentManager().getFocusOwner();
      buttonUse.setVisible(true);
      cmUse.setEnabled(true);
      getRootPane().setDefaultButton(buttonUse);
    }
    else {
      buttonUse.setVisible(false);
      cmUse.setEnabled(false);
    }
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textFiltr.requestFocusInWindow();
      }
    });
  }
  
  @Override
  public void dispose() {
    settings.setValue("width", new Variant(getWidth()));
    settings.setValue("height", new Variant(getHeight()));
    settings.store();
    tableTemplates.getQuery().close();
    super.dispose();
  }
  
  private void findTemplate() {
    if (tableTemplates.getQuery().isActive() && !"".equals(textFiltr.getText())) {
      try {
        if (tableTemplates.getQuery().locate("tpl_name", new Variant(textFiltr.getText()), true, true)) {
          tableTemplates.changeSelection(tableTemplates.getQuery().getCurrentRecord().getIndex(), 0);
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  public void refreshQuery() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int lastRow = tableTemplates.getSelectedRow();
        try {
          tableTemplates.getQuery().refresh();
          if (tableTemplates.getRowCount() > lastRow && lastRow >= 0) {
            tableTemplates.setRowSelectionInterval(lastRow, lastRow);
          } else if (tableTemplates.getRowCount() > 0) {
            tableTemplates.setRowSelectionInterval(0, 0);
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    });
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmClose = new pl.mpak.sky.gui.swing.Action();
    cmNew = new pl.mpak.sky.gui.swing.Action();
    cmEdit = new pl.mpak.sky.gui.swing.Action();
    cmDelete = new pl.mpak.sky.gui.swing.Action();
    cmUse = new pl.mpak.sky.gui.swing.Action();
    buttonClose = new javax.swing.JButton();
    buttonNew = new javax.swing.JButton();
    buttonEdit = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTemplates = new pl.mpak.orbada.gui.comps.table.ViewTable();
    jLabel1 = new javax.swing.JLabel();
    textFiltr = new pl.mpak.sky.gui.swing.comp.TextField();
    buttonUse = new javax.swing.JButton();

    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(stringManager.getString("cmClose-text")); // NOI18N
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    cmNew.setEnabled(false);
    cmNew.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new16.gif")); // NOI18N
    cmNew.setText(stringManager.getString("cmNew-text")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setEnabled(false);
    cmEdit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEdit.setText(stringManager.getString("cmEdit-text")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setEnabled(false);
    cmDelete.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDelete.setText(stringManager.getString("cmDelete-text")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    cmUse.setActionCommandKey("cmUse");
    cmUse.setText(stringManager.getString("TemplateListDialog-cmUse-text")); // NOI18N
    cmUse.setTooltip(stringManager.getString("TemplateListDialog-cmUse-hint")); // NOI18N
    cmUse.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmUseActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("TemplateListDialog-title")); // NOI18N
    setModal(true);

    buttonClose.setAction(cmClose);
    buttonClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonClose.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonNew.setAction(cmNew);
    buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNew.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonEdit.setAction(cmEdit);
    buttonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEdit.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDelete.setAction(cmDelete);
    buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDelete.setPreferredSize(new java.awt.Dimension(85, 25));

    jScrollPane1.setViewportView(tableTemplates);

    jLabel1.setText(stringManager.getString("TemplateListDialog-search-dd")); // NOI18N

    buttonUse.setAction(cmUse);
    buttonUse.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonUse.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
            .addComponent(buttonUse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textFiltr, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textFiltr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonUse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  if (tableTemplates.getQuery().isActive() && tableTemplates.getSelectedRow() >= 0) {
    try {
      tableTemplates.getQuery().getRecord(tableTemplates.getSelectedRow());
      String name = tableTemplates.getQuery().fieldByName("TPL_NAME").getString();
      String id = tableTemplates.getQuery().fieldByName("TPL_ID").getString();
      if (MessageBox.show(this,
            stringManager.getString("deleting"),
            String.format(
              stringManager.getString("TemplateListDialog-delete-q"),
              new Object[] {name}),
            ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        Template template = new Template(InternalDatabase.get());
        template.getPrimaryKeyField().setValue(new Variant(id));
        template.applyDelete();
        refreshQuery();
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableTemplates.getQuery().isActive() && tableTemplates.getSelectedRow() >= 0) {
      tableTemplates.getQuery().getRecord(tableTemplates.getSelectedRow());
      if (TemplateEditDialog.showDialog(tableTemplates.getQuery().fieldByName("tpl_id").getString()) == ModalResult.OK) {
        refreshQuery();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
  try {
    if (tableTemplates.getQuery().isActive()) {
      if (TemplateEditDialog.showDialog(null) == ModalResult.OK) {
        refreshQuery();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewActionPerformed

private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCloseActionPerformed

private void cmUseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUseActionPerformed
  if (tableTemplates.getQuery().isActive() && tableTemplates.getSelectedRow() >= 0) {
    try {
      tableTemplates.getQuery().getRecord(tableTemplates.getSelectedRow());
      tpl_id = tableTemplates.getQuery().fieldByName("tpl_id").getString();
      modalResult = ModalResult.OK;
      dispose();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmUseActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonClose;
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonEdit;
  private javax.swing.JButton buttonNew;
  private javax.swing.JButton buttonUse;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmNew;
  private pl.mpak.sky.gui.swing.Action cmUse;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableTemplates;
  private pl.mpak.sky.gui.swing.comp.TextField textFiltr;
  // End of variables declaration//GEN-END:variables
  
}
