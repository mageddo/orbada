package pl.mpak.orbada.oracle.gui.wizards.table;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.util.DirectoryComboBoxModel;
import pl.mpak.orbada.oracle.gui.wizards.TableColumnDefinitionWizard;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.script.SimpleSQLScript;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class CreateExternalTableWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private String schemaName;
  private TableColumnDefinitionWizard columnDefinition;
  
  public CreateExternalTableWizard(Database database, String schemaName) {
    this.database = database;
    this.schemaName = schemaName;
    initComponents();
    init();
  }
  
  private void init() {
    tableLocalizations.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null}
      },
      new String [] {
        stringManager.getString("catalog"), stringManager.getString("file-name")
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }
    });
    columnDefinition = new TableColumnDefinitionWizard(database, "EXTERNAL");
    tab.addTab(columnDefinition.getTabTitle(), columnDefinition);
    comboDirectory.setModel(new DirectoryComboBoxModel(database));
    comboLocation.setModel(new DirectoryComboBoxModel(database));
    tableLocalizations.setRowHeight(20);
    tableLocalizations.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboLocation));
  }
  
  public void wizardShow() {
    columnDefinition.wizardShow();
    ((DirectoryComboBoxModel)comboDirectory.getModel()).change();
    ((DirectoryComboBoxModel)comboLocation.getModel()).change();
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateExternalTableWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CreateExternalTableWizard-tab-title");
  }
  
  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    String locations = "";
    for (int i=0; i<tableLocalizations.getRowCount(); i++) {
      Object directory = tableLocalizations.getValueAt(i, 0);
      Object patch = tableLocalizations.getValueAt(i, 1);
      if (directory != null) {
        if (!StringUtil.isEmpty(locations)) {
          locations = locations +",\n";
        }
        if (comboDirectory.getSelectedItem().toString().equals(directory.toString())) {
          locations = locations +
            "    '" +patch +"'";
        }
        else {
          locations = locations +
            "    " +SQLUtil.createSqlName(directory.toString()) +": " +
            "'" +patch +"'";
        }
      }
    }
    
    return
      "CREATE TABLE " +SQLUtil.createSqlName(schemaName) +"." +textName.getText() +" (\n" +
      columnDefinition.getSqlCode() +
      "\n)\n" +
      "ORGANIZATION EXTERNAL (\n" +
      "  TYPE " +comboLoader.getSelectedItem().toString() +"\n" +
      (comboDirectory.getSelectedItem() != null ? "  DEFAULT DIRECTORY " +comboDirectory.getSelectedItem().toString() +"\n" : "") +
      "  ACCESS PARAMETERS" +(checkUsingCLOB.isSelected() ? " USING CLOB" : "") +" (\n" +
      "    " +textParameters.getText() +"\n" +
      "  )\n" +
      "  LOCATION (\n" +locations +"\n" +
      "  )\n" +
      ")\n" +
      "REJECT LIMIT " +((JTextField)comboReject.getEditor().getEditorComponent()).getText() +"\n" +
      "/";
  }
  
  public boolean execute() {
    SimpleSQLScript script = new SimpleSQLScript(database);
    if (!script.executeScript(getSqlCode())) {
      MessageBox.show(this, stringManager.getString("error"), script.getErrors(), ModalResult.OK, MessageBox.ERROR);
      return false;
    }
    return true;
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
    comboLocation = new javax.swing.JComboBox();
    tab = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel2 = new javax.swing.JLabel();
    comboDirectory = new javax.swing.JComboBox();
    jLabel5 = new javax.swing.JLabel();
    comboLoader = new javax.swing.JComboBox();
    jLabel6 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textParameters = new pl.mpak.sky.gui.swing.comp.TextArea();
    checkUsingCLOB = new javax.swing.JCheckBox();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableLocalizations = new pl.mpak.orbada.gui.comps.table.Table();
    buttonAddRow = new javax.swing.JButton();
    buttonRemoveRow = new javax.swing.JButton();
    jLabel7 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    comboReject = new javax.swing.JComboBox();

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
    cmRemoveRow.setText(stringManager.getString("cmRemove-dd")); // NOI18N
    cmRemoveRow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRemoveRowActionPerformed(evt);
      }
    });

    tab.setFocusable(false);

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("table-name-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("default-catalog-dd")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("loader-mechanism-dd")); // NOI18N

    comboLoader.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ORACLE_LOADER", "ORACLE_DATAPUMP" }));

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText(stringManager.getString("loading-parameters-dd")); // NOI18N

    textParameters.setColumns(20);
    textParameters.setRows(5);
    jScrollPane1.setViewportView(textParameters);

    checkUsingCLOB.setText(stringManager.getString("use-subquery-for-clob")); // NOI18N

    jScrollPane2.setMinimumSize(new java.awt.Dimension(24, 100));
    jScrollPane2.setViewportView(tableLocalizations);

    buttonAddRow.setAction(cmAddRow);
    buttonAddRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    buttonRemoveRow.setAction(cmRemoveRow);
    buttonRemoveRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(stringManager.getString("data-localizations-dd")); // NOI18N

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setText(stringManager.getString("throw-lines-dd")); // NOI18N

    comboReject.setEditable(true);
    comboReject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "UNLIMITED" }));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
          .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
          .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(comboReject, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(buttonRemoveRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(buttonAddRow, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addComponent(comboDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(comboLoader, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkUsingCLOB)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
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
          .addComponent(comboDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(comboLoader, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkUsingCLOB)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel6)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(buttonAddRow)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonRemoveRow))
          .addComponent(jLabel7)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(comboReject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(23, 23, 23))
    );

    tab.addTab(stringManager.getString("basic"), jPanel1); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tab)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void cmAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddRowActionPerformed
    if (tableLocalizations.getValueAt(tableLocalizations.getRowCount() -1, 0) != null) {
      ((DefaultTableModel)tableLocalizations.getModel()).addRow(new Object[] {null, null});
    }
  }//GEN-LAST:event_cmAddRowActionPerformed

  private void cmRemoveRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRemoveRowActionPerformed
    if (tableLocalizations.getRowCount() > 1) {
      tableLocalizations.getColumnModel().getColumn(0).getCellEditor().cancelCellEditing();
      ((DefaultTableModel)tableLocalizations.getModel()).removeRow(tableLocalizations.getRowCount() -1);
    }
  }//GEN-LAST:event_cmRemoveRowActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonAddRow;
  private javax.swing.JButton buttonRemoveRow;
  private javax.swing.JCheckBox checkUsingCLOB;
  private pl.mpak.sky.gui.swing.Action cmAddRow;
  private pl.mpak.sky.gui.swing.Action cmRemoveRow;
  private javax.swing.JComboBox comboDirectory;
  private javax.swing.JComboBox comboLoader;
  private javax.swing.JComboBox comboLocation;
  private javax.swing.JComboBox comboReject;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTabbedPane tab;
  private pl.mpak.orbada.gui.comps.table.Table tableLocalizations;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  private pl.mpak.sky.gui.swing.comp.TextArea textParameters;
  // End of variables declaration//GEN-END:variables
  
}
