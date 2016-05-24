package pl.mpak.orbada.oracle.gui.wizards;

import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.dbinfo.OracleTableInfo;
import pl.mpak.orbada.oracle.gui.util.TableComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.TableItemListener;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class CreateIndexWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private String schemaName;
  private String tableName;
  private JComboBox comboColumnName;
  private JComboBox comboColumnOrder;
  private StorageOptionsWizard storageOptions;
  
  public CreateIndexWizard(Database database, String schemaName, String tableName) {
    this.database = database;
    this.schemaName = schemaName;
    this.tableName = tableName;
    initComponents();
    init();
  }
  
  private void init() {
    tableColumns.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null}
      },
      new String [] {
        stringManager.getString("CreateIndexWizard-index-columns"), stringManager.getString("CreateIndexWizard-order")
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
    
    comboColumnName = new JComboBox();
    comboColumnOrder = new JComboBox(new DefaultComboBoxModel(new Object[] {"", "ASC", "DESC"}));
    TableColumn tc = tableColumns.getColumnModel().getColumn(0);
    tc.setCellEditor(new DefaultCellEditor(comboColumnName));
    tc = tableColumns.getColumnModel().getColumn(1);
    tc.setCellEditor(new DefaultCellEditor(comboColumnOrder));

    comboTables.addItemListener(new TableItemListener() {
      OracleTableInfo lastTable;
      public void itemChanged(OracleTableInfo info) {
        Query query = database.createQuery();
        try {
          query.setSqlText(Sql.getColumnNameList());
          query.paramByName("SCHEMA_NAME").setString(schemaName);
          query.paramByName("TABLE_NAME").setString(tableName);
          query.open();
          comboColumnName.setModel(new DefaultComboBoxModel(QueryUtil.queryToArray(query)));
        }
        catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
        finally {
          query.close();
        }
      }
    });
    
    storageOptions = new StorageOptionsWizard(database, "INDEX");
    tab.addTab(storageOptions.getTabTitle(), storageOptions);
  }
  
  public void wizardShow() {
    ((TableComboBoxModel)comboTables.getModel()).change(schemaName);
    ((TableComboBoxModel)comboTables.getModel()).select(tableName, comboTables);
    Query query = database.createQuery();
    try {
      query.open(Sql.getIndextypeNameList());
      comboIndextype.setModel(new DefaultComboBoxModel(QueryUtil.queryToArray(query)));
    }
    catch(Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    storageOptions.wizardShow();
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateIndexWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CreateIndexWizard-tab-title");
  }
  
  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    String fkColumns = "";
    if (radioColumns.isSelected()) {
      for (int i=0; i<tableColumns.getRowCount(); i++) {
        Object column = tableColumns.getValueAt(i, 0);
        Object columnOrder = tableColumns.getValueAt(i, 1);
        if (column != null) {
          if (!StringUtil.isEmpty(fkColumns)) {
            fkColumns = fkColumns +", ";
          }
          fkColumns = fkColumns +SQLUtil.createSqlName(column.toString());
          if (columnOrder != null && !StringUtil.isEmpty(columnOrder.toString())) {
            fkColumns = fkColumns +" " +columnOrder.toString();
          }
        }
      }
    }
    else if (radioExpression.isSelected()) {
      fkColumns = textExpression.getText();
    }
    
    String storage = storageOptions.getSqlCode();
    return
      "CREATE " +comboType.getSelectedItem().toString() +
      " INDEX " +textName.getText() +
      " ON " +SQLUtil.createSqlName(comboTables.getSelectedItem().toString()) +
      "(" +fkColumns +")" +
      (checkParallel.isSelected() ? "\n PARALLEL " +textDegree.getText() : "") +
      (!StringUtil.isEmpty(comboIndextype.getText()) ? "\n INDEXTYPE IS " +comboIndextype.getText() : "") +
      (!StringUtil.isEmpty(storage) ? "\n" +storage : "");
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
    group = new javax.swing.ButtonGroup();
    tab = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    comboTables = new javax.swing.JComboBox();
    textDegree = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableColumns = new pl.mpak.orbada.gui.comps.table.Table();
    radioColumns = new javax.swing.JRadioButton();
    buttonAddRow = new javax.swing.JButton();
    radioExpression = new javax.swing.JRadioButton();
    jLabel2 = new javax.swing.JLabel();
    comboType = new javax.swing.JComboBox();
    buttonRemoveRow = new javax.swing.JButton();
    textExpression = new pl.mpak.sky.gui.swing.comp.TextField();
    checkParallel = new javax.swing.JCheckBox();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel5 = new javax.swing.JLabel();
    comboIndextype = new pl.mpak.sky.gui.swing.comp.ComboBox();

    cmAddRow.setActionCommandKey("cmAddRow");
    cmAddRow.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add8.gif")); // NOI18N
    cmAddRow.setText(stringManager.getString("cmAdd-text")); // NOI18N
    cmAddRow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddRowActionPerformed(evt);
      }
    });

    cmRemoveRow.setActionCommandKey("cmRemoveRow");
    cmRemoveRow.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/remove8.gif")); // NOI18N
    cmRemoveRow.setText(stringManager.getString("cmRemove-text")); // NOI18N
    cmRemoveRow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRemoveRowActionPerformed(evt);
      }
    });

    tab.setFocusable(false);

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("index-kind-dd")); // NOI18N

    comboTables.setModel(new TableComboBoxModel(database));

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("index-name-dd")); // NOI18N

    jScrollPane1.setMinimumSize(new java.awt.Dimension(24, 100));
    jScrollPane1.setViewportView(tableColumns);

    group.add(radioColumns);
    radioColumns.setSelected(true);
    radioColumns.setText(stringManager.getString("columns")); // NOI18N

    buttonAddRow.setAction(cmAddRow);
    buttonAddRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    group.add(radioExpression);
    radioExpression.setText(stringManager.getString("expression")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("index-table-dd")); // NOI18N

    comboType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "UNIQUE", "BITMAP" }));

    buttonRemoveRow.setAction(cmRemoveRow);
    buttonRemoveRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    checkParallel.setText(stringManager.getString("parallel")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("index-type-dd")); // NOI18N

    comboIndextype.setEditable(true);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(textExpression, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
          .addComponent(radioExpression)
          .addComponent(radioColumns)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(checkParallel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textDegree, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(buttonRemoveRow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(buttonAddRow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(comboTables, javax.swing.GroupLayout.Alignment.TRAILING, 0, 257, Short.MAX_VALUE)
              .addComponent(comboType, javax.swing.GroupLayout.Alignment.TRAILING, 0, 257, Short.MAX_VALUE)
              .addComponent(comboIndextype, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
              .addComponent(textName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboTables, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(comboIndextype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(radioColumns)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(buttonAddRow)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonRemoveRow))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(radioExpression)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(textExpression, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(checkParallel)
          .addComponent(textDegree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(36, Short.MAX_VALUE))
    );

    tab.addTab(stringManager.getString("basic"), jPanel1); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmRemoveRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRemoveRowActionPerformed
  if (tableColumns.getRowCount() > 1) {
    tableColumns.getColumnModel().getColumn(0).getCellEditor().cancelCellEditing();
    ((DefaultTableModel)tableColumns.getModel()).removeRow(tableColumns.getRowCount() -1);
  }
}//GEN-LAST:event_cmRemoveRowActionPerformed

private void cmAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddRowActionPerformed
  if (tableColumns.getValueAt(tableColumns.getRowCount() -1, 0) != null) {
    ((DefaultTableModel)tableColumns.getModel()).addRow(new Object[] {null, null});
  }
}//GEN-LAST:event_cmAddRowActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonAddRow;
  private javax.swing.JButton buttonRemoveRow;
  private javax.swing.JCheckBox checkParallel;
  private pl.mpak.sky.gui.swing.Action cmAddRow;
  private pl.mpak.sky.gui.swing.Action cmRemoveRow;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboIndextype;
  private javax.swing.JComboBox comboTables;
  private javax.swing.JComboBox comboType;
  private javax.swing.ButtonGroup group;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JRadioButton radioColumns;
  private javax.swing.JRadioButton radioExpression;
  private javax.swing.JTabbedPane tab;
  private pl.mpak.orbada.gui.comps.table.Table tableColumns;
  private pl.mpak.sky.gui.swing.comp.TextField textDegree;
  private pl.mpak.sky.gui.swing.comp.TextField textExpression;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  // End of variables declaration//GEN-END:variables
  
}
