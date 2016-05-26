/*
 * DriverEditDialog.java
 *
 * Created on 11 paüdziernik 2007, 22:33
 */

package pl.mpak.orbada.todo.gui;

import java.beans.IntrospectionException;
import javax.swing.JComponent;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.todo.OrbadaTodoPlugin;
import pl.mpak.orbada.todo.db.Todo;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.FieldLinkType;
import pl.mpak.usedb.gui.RecordLink;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author  akaluza
 */
public class TodoEditDialog extends javax.swing.JDialog {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaTodoPlugin.class);

  private String td_id;
  private String td_sch_id;
  private int modalResult = ModalResult.NONE;
  private Todo todo;
  private RecordLink driverLink;
  private boolean editable;
  
  /** Creates new form DriverEditDialog
   * @param td_id
   * @param td_sch_id 
   * @throws pl.mpak.usedb.UseDBException
   * @throws java.beans.IntrospectionException
   */
  public TodoEditDialog(String td_id, String td_sch_id) throws IntrospectionException, UseDBException {
    super();
    this.td_id = td_id;
    this.td_sch_id = td_sch_id;
    this.editable = true;
    initComponents();
    init();
  }
  
  public TodoEditDialog(Todo todo, boolean editable) throws IntrospectionException, UseDBException {
    super();
    this.todo = todo;
    this.editable = editable;
    initComponents();
    init();
  }
  
  /**
   *
   * @param td_id
   * @return
   * @throws java.beans.IntrospectionException
   * @throws pl.mpak.usedb.UseDBException
   */
  public static int showDialog(String td_id, String td_sch_id) throws IntrospectionException, UseDBException {
    TodoEditDialog dialog = new TodoEditDialog(td_id, td_sch_id);
    dialog.setVisible(true);
    return dialog.getModalResult();
  }
  
  public static int showDialog(Todo todo, boolean editable) throws IntrospectionException, UseDBException {
    TodoEditDialog dialog = new TodoEditDialog(todo, editable);
    dialog.setVisible(true);
    return dialog.getModalResult();
  }
  
  private void init() throws IntrospectionException, UseDBException {
    textTodoDescription.setFont(getFont());
    textTodoWorkoround.setFont(getFont());
      
    Query query = InternalDatabase.get().createQuery();
    try {
      try {
        query.open("select distinct td_state from todos order by td_state");
        comboTodoState.setModel(new javax.swing.DefaultComboBoxModel(QueryUtil.queryToArray(null, query)));
      } catch(Exception e) {
        ExceptionUtil.processException(e);
      }
    } finally {
      query.close();
    }
    
    driverLink = new RecordLink();
    driverLink.add("TD_TITLE", textTodoTitle);
    driverLink.add("TD_STATE", comboTodoState, "selectedItem");
    driverLink.add("TD_PRIORITY", spinTodoPriority, "value", VariantType.varLong);
    driverLink.add("TD_DESCRIPTION", textTodoDescription);
    driverLink.add("TD_ENABLE", checkTodoEnable, "selected", FieldLinkType.Boolean_TF);
    driverLink.add("TD_WORKAROUND", textTodoWorkoround);
    driverLink.add("TD_PLAN_END", textTodoPlanEnd);
    driverLink.add("TD_ENDED", textTodoEnded);
    driverLink.add("TD_APP_VERSION", textTodoAppVersion);
    driverLink.add("TD_ORBADA", checkTodoOrbada, "selected", FieldLinkType.Boolean_TF);
    
    if (todo == null) {
      if (td_id != null) {
        todo = new Todo(td_id);
      } else {
        todo = new Todo();
        todo.fieldByName("TD_SCH_ID").setValue(new Variant(td_sch_id));
        todo.fieldByName("TD_PRIORITY").setValue(new Variant(1));
        todo.fieldByName("TD_ENABLE").setValue(new Variant("T"));
      }
    }
    textTodoId.setText(todo.fieldByName("TD_ID").getValue().toString());
    driverLink.updateComponents(todo);
    
    cmOk.setEnabled(editable);
    textTodoTitle.setEnabled(editable);
    comboTodoState.setEnabled(editable);
    spinTodoPriority.setEnabled(editable);
    textTodoDescription.setEnabled(editable);
    checkTodoEnable.setEnabled(editable);
    textTodoWorkoround.setEnabled(editable);
    textTodoPlanEnd.setEnabled(editable);
    textTodoEnded.setEnabled(editable);
    textTodoAppVersion.setEnabled(editable);
    checkTodoOrbada.setEnabled(editable);
    
    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    SwingUtil.centerWithinScreen(this);
  }
  
  public int getModalResult() {
    return modalResult;
  }
  
  @Override
  public void dispose() {
    super.dispose();
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
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    textTodoTitle = new pl.mpak.sky.gui.swing.comp.TextField();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    comboTodoState = new javax.swing.JComboBox();
    jLabel3 = new javax.swing.JLabel();
    spinTodoPriority = new javax.swing.JSpinner();
    jLabel4 = new javax.swing.JLabel();
    checkTodoEnable = new javax.swing.JCheckBox();
    jLabel5 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textTodoDescription = new pl.mpak.sky.gui.swing.comp.TextArea();
    jScrollPane2 = new javax.swing.JScrollPane();
    textTodoWorkoround = new pl.mpak.sky.gui.swing.comp.TextArea();
    textTodoEnded = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel6 = new javax.swing.JLabel();
    textTodoPlanEnd = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel7 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    textTodoAppVersion = new pl.mpak.sky.gui.swing.comp.TextField();
    checkTodoOrbada = new javax.swing.JCheckBox();
    textTodoId = new pl.mpak.sky.gui.swing.comp.TextField();

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
    setTitle(stringManager.getString("TodoEditDialog-title")); // NOI18N
    setModal(true);

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setLabelFor(textTodoTitle);
    jLabel1.setText(stringManager.getString("title-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("status-dd")); // NOI18N

    buttonOk.setAction(cmOk);
    buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

    comboTodoState.setEditable(true);

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("priority-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("description-dd")); // NOI18N

    checkTodoEnable.setText(stringManager.getString("task-is-active")); // NOI18N
    checkTodoEnable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkTodoEnable.setMargin(new java.awt.Insets(0, 0, 0, 0));

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("solution-dd")); // NOI18N

    textTodoDescription.setColumns(20);
    textTodoDescription.setLineWrap(true);
    textTodoDescription.setRows(5);
    jScrollPane1.setViewportView(textTodoDescription);

    textTodoWorkoround.setColumns(20);
    textTodoWorkoround.setLineWrap(true);
    textTodoWorkoround.setRows(5);
    jScrollPane2.setViewportView(textTodoWorkoround);

    textTodoEnded.setEditable(false);

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setLabelFor(textTodoTitle);
    jLabel6.setText(stringManager.getString("planned-finish-dd")); // NOI18N

    jLabel7.setText("(yyyy-mm-dd hh:mm:ss)");

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setLabelFor(textTodoTitle);
    jLabel8.setText(stringManager.getString("application-version-dd")); // NOI18N

    checkTodoOrbada.setText(stringManager.getString("the-notification-concerns-the-ORBADA")); // NOI18N
    checkTodoOrbada.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkTodoOrbada.setMargin(new java.awt.Insets(0, 0, 0, 0));

    textTodoId.setEditable(false);
    textTodoId.setFocusable(false);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
              .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
              .addComponent(spinTodoPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(textTodoPlanEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7))
              .addComponent(checkTodoOrbada)
              .addComponent(textTodoTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
              .addComponent(comboTodoState, 0, 322, Short.MAX_VALUE)
              .addComponent(textTodoAppVersion, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
              .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
              .addGroup(layout.createSequentialGroup()
                .addComponent(checkTodoEnable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textTodoEnded, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(textTodoId, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textTodoTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboTodoState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(spinTodoPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(5, 5, 5)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(jLabel7)
          .addComponent(textTodoPlanEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(textTodoAppVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkTodoOrbada)
        .addGap(8, 8, 8)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel4)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel5)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(checkTodoEnable)
          .addComponent(textTodoEnded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textTodoId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  try {
    driverLink.updateRecord(todo);
    if (todo.isChanged()) {
      if (td_id == null) {
        todo.applyInsert();
      } else {
        todo.applyUpdate();
      }
    }
    modalResult = ModalResult.OK;
    dispose();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
  }
}//GEN-LAST:event_cmOkActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JCheckBox checkTodoEnable;
  private javax.swing.JCheckBox checkTodoOrbada;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private javax.swing.JComboBox comboTodoState;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JSpinner spinTodoPriority;
  private pl.mpak.sky.gui.swing.comp.TextField textTodoAppVersion;
  private pl.mpak.sky.gui.swing.comp.TextArea textTodoDescription;
  private pl.mpak.sky.gui.swing.comp.TextField textTodoEnded;
  private pl.mpak.sky.gui.swing.comp.TextField textTodoId;
  private pl.mpak.sky.gui.swing.comp.TextField textTodoPlanEnd;
  private pl.mpak.sky.gui.swing.comp.TextField textTodoTitle;
  private pl.mpak.sky.gui.swing.comp.TextArea textTodoWorkoround;
  // End of variables declaration//GEN-END:variables
  
}
