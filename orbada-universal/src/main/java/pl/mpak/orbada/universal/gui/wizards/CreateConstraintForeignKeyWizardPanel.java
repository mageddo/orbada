package pl.mpak.orbada.universal.gui.wizards;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import pl.mpak.orbada.gui.comps.table.Table;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbSchemaInfo;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbTableInfo;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.util.TableColumnComboBoxModel;
import pl.mpak.orbada.universal.gui.util.TableComboBoxModel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.comp.ComboBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class CreateConstraintForeignKeyWizardPanel extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  private Database database;
  private String schemaName;
  private String tableName;
  private String columnName;
  private ComboBox comboColumnForeignKey;
  private ComboBox comboColumnReference;
  
  public CreateConstraintForeignKeyWizardPanel(Database database, String schemaName, String tableName) {
    this(database, schemaName, tableName, null);
  }
  
  public CreateConstraintForeignKeyWizardPanel(Database database, String schemaName, String tableName, String columnName) {
    this.database = database;
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.columnName = columnName;
    initComponents();
    init();
  }

  private void init() {
    tableColumns.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {columnName, null}
      },
      new String [] {
        stringManager.getString("foreign-key-columns"), stringManager.getString("reference-columns")
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }
    });
    tableColumns.setRowHeight(20);
    
    comboColumnForeignKey = new ComboBox(new TableColumnComboBoxModel(database));
    comboColumnReference = new ComboBox(new TableColumnComboBoxModel(database));
    TableColumn tc = tableColumns.getColumnModel().getColumn(0);
    tc.setCellEditor(new DefaultCellEditor(comboColumnForeignKey));
    tc = tableColumns.getColumnModel().getColumn(1);
    tc.setCellEditor(new DefaultCellEditor(comboColumnReference));

    comboTablesConstraints.addItemListener(new ItemListener() {
      JdbcDbTableInfo lastTable;
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof JdbcDbTableInfo) {
          JdbcDbTableInfo ti = (JdbcDbTableInfo)e.getItem();
          if (!ti.equals(lastTable) || lastTable == null) {
            JdbcDbSchemaInfo schema = ti.getSchema();
            ((TableColumnComboBoxModel)comboColumnForeignKey.getModel()).change(schema == null ? null : schema.getName(), ti.getName());
            lastTable = ti;
          }
        }
      }
    });
    comboTablesReferences.addItemListener(new ItemListener() {
      JdbcDbTableInfo lastTable;
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null && e.getItem() instanceof JdbcDbTableInfo) {
          JdbcDbTableInfo ti = (JdbcDbTableInfo)e.getItem();
          if (!ti.equals(lastTable) || lastTable == null) {
            JdbcDbSchemaInfo schema = ti.getSchema();
            ((TableColumnComboBoxModel)comboColumnReference.getModel()).change(schema == null ? null : schema.getName(), ti.getName());
            lastTable = ti;
          }
        }
      }
    });
  }
  
  public void wizardShow() {
    ((TableComboBoxModel)comboTablesConstraints.getModel()).change(schemaName);
    ((TableComboBoxModel)comboTablesReferences.getModel()).change(schemaName);
    ((TableComboBoxModel)comboTablesConstraints.getModel()).select(tableName, comboTablesConstraints);
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateConstraintForeignKeyWizardPanel-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CreateConstraintForeignKeyWizardPanel-tab-title");
  }
  
  public String getSqlCode() {
    String fkColumns = "";
    for (int i=0; i<tableColumns.getRowCount(); i++) {
      Object column = tableColumns.getValueAt(i, 0);
      if (column != null) {
        if (!StringUtil.isEmpty(fkColumns)) {
          fkColumns = fkColumns +", ";
        }
        fkColumns = fkColumns +SQLUtil.createSqlName(column.toString());
      }
    }
    String refColumns = "";
    for (int i=0; i<tableColumns.getRowCount(); i++) {
      Object column = tableColumns.getValueAt(i, 1);
      if (column != null) {
        if (!StringUtil.isEmpty(refColumns)) {
          refColumns = refColumns +", ";
        }
        refColumns = refColumns +SQLUtil.createSqlName(column.toString());
      }
    }
    String onDelete = ("NO ACTION".equals(comboOnDelete.getSelectedItem().toString()) ? "" : "\n  ON DELETE " +comboOnDelete.getSelectedItem().toString());
    String onUpdate = ("NO ACTION".equals(comboOnUpdate.getSelectedItem().toString()) ? "" : "\n  ON UPDATE " +comboOnUpdate.getSelectedItem().toString());
    
    return
      "ALTER TABLE " +SQLUtil.createSqlName(schemaName, comboTablesConstraints.getSelectedItem().toString()) +"\n" +
      "  ADD CONSTRAINT " +textName.getText() +"\n" +
      "  FOREIGN KEY (" +fkColumns +")\n" +
      "  REFERENCES " +SQLUtil.createSqlName(comboTablesReferences.getSelectedItem().toString()) +" (" +refColumns +")" +
      onDelete +onUpdate;
  }
  
  public boolean execute() {
    try {
      database.executeCommand(getSqlCode());
      return true;
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      return false;
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmAddRow = new pl.mpak.sky.gui.swing.Action();
    cmRemoveRow = new pl.mpak.sky.gui.swing.Action();
    jLabel2 = new javax.swing.JLabel();
    comboTablesConstraints = new javax.swing.JComboBox();
    jLabel3 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    comboTablesReferences = new javax.swing.JComboBox();
    jLabel4 = new javax.swing.JLabel();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableColumns = new Table();
    jLabel6 = new javax.swing.JLabel();
    comboOnDelete = new javax.swing.JComboBox();
    jLabel7 = new javax.swing.JLabel();
    comboOnUpdate = new javax.swing.JComboBox();
    buttonAddRow = new javax.swing.JButton();
    buttonRemoveRow = new javax.swing.JButton();

    cmAddRow.setActionCommandKey("cmAddRow");
    cmAddRow.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/add8.gif")); // NOI18N
    cmAddRow.setText(stringManager.getString("cmAddRow-text")); // NOI18N
    cmAddRow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddRowActionPerformed(evt);
      }
    });

    cmRemoveRow.setActionCommandKey("cmRemoveRow");
    cmRemoveRow.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/remove8.gif")); // NOI18N
    cmRemoveRow.setText(stringManager.getString("cmRemoveRow-text")); // NOI18N
    cmRemoveRow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRemoveRowActionPerformed(evt);
      }
    });

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("constraint-table-dd")); // NOI18N

    comboTablesConstraints.setModel(new TableComboBoxModel(database));

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("constraint-name-dd")); // NOI18N

    comboTablesReferences.setModel(new TableComboBoxModel(database));

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("reference-table-dd")); // NOI18N

    jLabel1.setText(stringManager.getString("foreign-key-column-dd")); // NOI18N

    jScrollPane1.setMinimumSize(new java.awt.Dimension(24, 100));
    jScrollPane1.setViewportView(tableColumns);

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText(stringManager.getString("delete-rule-dd")); // NOI18N

    comboOnDelete.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NO ACTION", "CASCADE", "SET NULL", "RESTRICT" }));

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(stringManager.getString("update-rule-dd")); // NOI18N

    comboOnUpdate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NO ACTION", "CASCADE", "SET NULL", "RESTRICT" }));

    buttonAddRow.setAction(cmAddRow);
    buttonAddRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    buttonRemoveRow.setAction(cmRemoveRow);
    buttonRemoveRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(buttonRemoveRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(buttonAddRow, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(comboTablesConstraints, javax.swing.GroupLayout.Alignment.TRAILING, 0, 324, Short.MAX_VALUE)
              .addComponent(comboTablesReferences, javax.swing.GroupLayout.Alignment.TRAILING, 0, 324, Short.MAX_VALUE)
              .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)))
          .addComponent(jLabel1)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
              .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(comboOnDelete, javax.swing.GroupLayout.Alignment.TRAILING, 0, 324, Short.MAX_VALUE)
              .addComponent(comboOnUpdate, javax.swing.GroupLayout.Alignment.TRAILING, 0, 324, Short.MAX_VALUE))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboTablesConstraints, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(comboTablesReferences, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonAddRow)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonRemoveRow))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(comboOnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel6))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(comboOnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmRemoveRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRemoveRowActionPerformed
  if (tableColumns.getRowCount() > 1) {
    tableColumns.getColumnModel().getColumn(0).getCellEditor().cancelCellEditing();
    tableColumns.getColumnModel().getColumn(1).getCellEditor().cancelCellEditing();
    ((DefaultTableModel)tableColumns.getModel()).removeRow(tableColumns.getRowCount() -1);
  }
}//GEN-LAST:event_cmRemoveRowActionPerformed

private void cmAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddRowActionPerformed
  if (tableColumns.getValueAt(tableColumns.getRowCount() -1, 0) != null &&
      tableColumns.getValueAt(tableColumns.getRowCount() -1, 1) != null) {
    ((DefaultTableModel)tableColumns.getModel()).addRow(new Object[] {null, null});
  }
}//GEN-LAST:event_cmAddRowActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonAddRow;
  private javax.swing.JButton buttonRemoveRow;
  private pl.mpak.sky.gui.swing.Action cmAddRow;
  private pl.mpak.sky.gui.swing.Action cmRemoveRow;
  private javax.swing.JComboBox comboOnDelete;
  private javax.swing.JComboBox comboOnUpdate;
  private javax.swing.JComboBox comboTablesConstraints;
  private javax.swing.JComboBox comboTablesReferences;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JScrollPane jScrollPane1;
  private Table tableColumns;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  // End of variables declaration//GEN-END:variables
  
}
