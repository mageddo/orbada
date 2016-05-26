package pl.mpak.orbada.oracle.gui.wizards;

import pl.mpak.orbada.gui.comps.table.Table;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableModel;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class TableColumnDefinitionWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private String tableType;
  private int columnCount = 0;
  
  public TableColumnDefinitionWizard(Database database, String tableType) {
    this.database = database;
    this.tableType = tableType;
    initComponents();
    init();
  }
  
  private void init() {
    tableColumns.setRowHeight(20);
    
    columnCount++;
    if ("EXTERNAL".equals(tableType)) {
      tableColumns.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
          {"COLUMNA_1", "VARCHAR2(4000)"}
        },
        new String [] {
          stringManager.getString("column-name"), stringManager.getString("type-and-size")
        }
      ) {
        Class[] types = new Class [] {
          java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
        };

        @Override
        public Class getColumnClass(int columnIndex) {
          return types [columnIndex];
        }
      });
      tableColumns.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboDataTypes));
      tableColumns.getColumnModel().getColumn(0).setPreferredWidth(100);
      tableColumns.getColumnModel().getColumn(1).setPreferredWidth(150);
    }
    else {
      tableColumns.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
          {"COLUMNA_1", "VARCHAR2(4000)", new Boolean(true), new Boolean(false), null, null}
        },
        new String [] {
          stringManager.getString("column-name"), stringManager.getString("type-and-size"), stringManager.getString("is-not-null"), stringManager.getString("pk"), stringManager.getString("default-value"), stringManager.getString("comment")
        }
      ) {
        Class[] types = new Class [] {
          java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
        };

        @Override
        public Class getColumnClass(int columnIndex) {
          return types [columnIndex];
        }
      });
      tableColumns.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboDataTypes));
      tableColumns.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(comboDefaultValue));
      tableColumns.getColumnModel().getColumn(0).setPreferredWidth(100);
      tableColumns.getColumnModel().getColumn(1).setPreferredWidth(150);
      tableColumns.getColumnModel().getColumn(2).setPreferredWidth(50);
      tableColumns.getColumnModel().getColumn(3).setPreferredWidth(50);
      tableColumns.getColumnModel().getColumn(4).setPreferredWidth(150);
      tableColumns.getColumnModel().getColumn(5).setPreferredWidth(300);
    }
  }
  
  public void wizardShow() {
  }
  
  public String getDialogTitle() {
    return stringManager.getString("TableColumnDefinitionWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("TableColumnDefinitionWizard-tab-title");
  }
  
  public String getSqlCode() {
    StringBuffer columns = new StringBuffer();
    if ("EXTERNAL".equals(tableType)) {
      for (int i=0; i<tableColumns.getRowCount(); i++) {
        Object name = tableColumns.getValueAt(i, 0);
        Object dataType = tableColumns.getValueAt(i, 1);
        if (name != null && dataType != null) {
          if (columns.length() > 0) {
            columns.append(",\n");
          }
          columns.append("  " +name);
          columns.append(" " +dataType);
        }
      }
    }
    else {
      for (int i=0; i<tableColumns.getRowCount(); i++) {
        Object name = tableColumns.getValueAt(i, 0);
        Object dataType = tableColumns.getValueAt(i, 1);
        Object notNullValue = tableColumns.getValueAt(i, 2);
        Object defaultValue = tableColumns.getValueAt(i, 4);
        if (name != null && dataType != null) {
          if (columns.length() > 0) {
            columns.append(",\n");
          }
          columns.append("  " +name);
          columns.append(" " +dataType);
          if (defaultValue != null && !StringUtil.isEmpty(defaultValue.toString())) {
            columns.append(" DEFAULT " +defaultValue.toString());
          }
          if (notNullValue != null && (Boolean)notNullValue) {
            columns.append(" NOT NULL");
          }
        }
      }
    }
    return columns.toString();
  }
  
  public String getSqlCodeComments(String schemaName, String tableName) {
    if ("EXTERNAL".equals(tableType)) {
      return "";
    }
    StringBuffer comments = new StringBuffer();
    for (int i=0; i<tableColumns.getRowCount(); i++) {
      Object name = tableColumns.getValueAt(i, 0);
      Object comment = tableColumns.getValueAt(i, 5);
      if (name != null && comment != null && !StringUtil.isEmpty(comment.toString())) {
        if (comments.length() > 0) {
          comments.append("\n");
        }
        comments.append("COMMENT ON COLUMN " +SQLUtil.createSqlName(schemaName) +"." +tableName +"." +name +" IS '" +comment.toString() +"'\n/");
      }
    }
    return comments.toString();
  }
  
  public String getSqlCodePk(String schemaName, String tableName) {
    if ("EXTERNAL".equals(tableType)) {
      return "";
    }
    StringBuffer pks = new StringBuffer();
    for (int i=0; i<tableColumns.getRowCount(); i++) {
      Object name = tableColumns.getValueAt(i, 0);
      Object pk = tableColumns.getValueAt(i, 3);
      if (name != null && pk != null && (Boolean)pk) {
        if (pks.length() > 0) {
          pks.append(", ");
        }
        pks.append(name);
      }
    }
    if (pks.length() > 0) {
      return "ALTER TABLE " +SQLUtil.createSqlName(schemaName) +"." +tableName +" ADD CONSTRAINT " +tableName +"_PK PRIMARY KEY (" +pks.toString() +")\n/";
    }
    return "";
  }
  
  public boolean execute() {
    return false;
  }
  
  private void cancelEdit() {
    tableColumns.getColumnModel().getColumn(1).getCellEditor().cancelCellEditing();
    tableColumns.getColumnModel().getColumn(4).getCellEditor().cancelCellEditing();
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
    cmMoveDown = new pl.mpak.sky.gui.swing.Action();
    cmMoveUp = new pl.mpak.sky.gui.swing.Action();
    comboDataTypes = new javax.swing.JComboBox();
    comboDefaultValue = new javax.swing.JComboBox();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableColumns = new Table();
    buttonAddRow = new javax.swing.JButton();
    buttonRemoveRow = new javax.swing.JButton();
    buttonMoveUp = new javax.swing.JButton();
    buttonMoveDown = new javax.swing.JButton();

    cmAddRow.setActionCommandKey("cmAddRow"); // NOI18N
    cmAddRow.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add8.gif")); // NOI18N
    cmAddRow.setText(stringManager.getString("cmAdd-text")); // NOI18N
    cmAddRow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddRowActionPerformed(evt);
      }
    });

    cmRemoveRow.setActionCommandKey("cmRemoveRow"); // NOI18N
    cmRemoveRow.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/remove8.gif")); // NOI18N
    cmRemoveRow.setText(stringManager.getString("cmRemove-text")); // NOI18N
    cmRemoveRow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRemoveRowActionPerformed(evt);
      }
    });

    cmMoveDown.setActionCommandKey("cmMoveDown"); // NOI18N
    cmMoveDown.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/down10.gif")); // NOI18N
    cmMoveDown.setText(stringManager.getString("cmDown-text")); // NOI18N
    cmMoveDown.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmMoveDownActionPerformed(evt);
      }
    });

    cmMoveUp.setActionCommandKey("cmMoveUp"); // NOI18N
    cmMoveUp.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/up10.gif")); // NOI18N
    cmMoveUp.setText(stringManager.getString("cmUp-text")); // NOI18N
    cmMoveUp.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmMoveUpActionPerformed(evt);
      }
    });

    comboDataTypes.setEditable(true);
    comboDataTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BFILE", "BINARY_DOUBLE", "BINARY_FLOAT", "BLOB", "CHAR (size [|BYTE|CHAR])", "CHAR VARYING (size [|BYTE|CHAR])", "CLOB", "DATE", "DECIMAL (precision, scale)", "DOUBLE PRECISION", "FLOAT (precision)", "INTEGER", "INTERVAL DAY (day_precision) TO SECOND (fractional_precision)", "INTERVAL YEAR (precision) TO MONTH", "LONG", "LONG RAW", "LONG VARCHAR", "NATIONAL CHAR (size)", "NATIONAL CHAR VARYING (size)", "NUMBER (precision, scale)", "NVARCHAR2 (size)", "RAW (size)", "REAL", "ROWID", "UROWID (size)", "SMALLINT", "TIMESTAMP [(precision)] [|WITH TIME ZONE|WITH LOCAL TIME ZONE]", "VARCHAR (size [|BYTE|CHAR])", "VARCHAR2 (size [|BYTE|CHAR])" }));

    comboDefaultValue.setEditable(true);
    comboDefaultValue.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "SYSDATE", "USER" }));

    setPreferredSize(new java.awt.Dimension(600, 300));

    jScrollPane1.setMinimumSize(new java.awt.Dimension(24, 100));

    tableColumns.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jScrollPane1.setViewportView(tableColumns);

    buttonAddRow.setAction(cmAddRow);
    buttonAddRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    buttonRemoveRow.setAction(cmRemoveRow);
    buttonRemoveRow.setMargin(new java.awt.Insets(2, 2, 2, 2));

    buttonMoveUp.setAction(cmMoveUp);
    buttonMoveUp.setMargin(new java.awt.Insets(2, 2, 2, 2));

    buttonMoveDown.setAction(cmMoveDown);
    buttonMoveDown.setMargin(new java.awt.Insets(2, 2, 2, 2));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addComponent(buttonMoveUp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
          .addComponent(buttonRemoveRow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
          .addComponent(buttonAddRow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
          .addComponent(buttonMoveDown, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonAddRow)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonRemoveRow)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonMoveUp)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonMoveDown)))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmRemoveRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRemoveRowActionPerformed
  if (tableColumns.getRowCount() > 1 && tableColumns.getSelectedRow() >= 0) {
    cancelEdit();
    ((DefaultTableModel)tableColumns.getModel()).removeRow(tableColumns.getSelectedRow());
  }
}//GEN-LAST:event_cmRemoveRowActionPerformed

private void cmAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddRowActionPerformed
  if (tableColumns.getValueAt(tableColumns.getRowCount() -1, 0) != null) {
    columnCount++;
    if ("EXTERNAL".equals(tableType)) {
      ((DefaultTableModel)tableColumns.getModel()).addRow(new Object[] {"COLUMNA_" +columnCount, "VARCHAR2(4000)"});
    }
    else {
      ((DefaultTableModel)tableColumns.getModel()).addRow(new Object[] {"COLUMNA_" +columnCount, "VARCHAR2(4000)", new Boolean(true), new Boolean(false), null, null});
    }
    tableColumns.changeSelection(tableColumns.getRowCount() -1, tableColumns.getRowCount() -1);
  }
}//GEN-LAST:event_cmAddRowActionPerformed

private void cmMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveDownActionPerformed
  if (tableColumns.getSelectedRow() >= 0 && tableColumns.getSelectedRow() < tableColumns.getRowCount() -1) {
    cancelEdit();
    ((DefaultTableModel)tableColumns.getModel()).moveRow(tableColumns.getSelectedRow(), tableColumns.getSelectedRow(), tableColumns.getSelectedRow() +1);
    tableColumns.changeSelection(tableColumns.getSelectedRow() +1, tableColumns.getSelectedRow() +1);
  }
}//GEN-LAST:event_cmMoveDownActionPerformed

private void cmMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveUpActionPerformed
  if (tableColumns.getSelectedRow() > 0) {
    cancelEdit();
    ((DefaultTableModel)tableColumns.getModel()).moveRow(tableColumns.getSelectedRow(), tableColumns.getSelectedRow(), tableColumns.getSelectedRow() -1);
    tableColumns.changeSelection(tableColumns.getSelectedRow() -1, tableColumns.getSelectedRow() -1);
  }
}//GEN-LAST:event_cmMoveUpActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonAddRow;
  private javax.swing.JButton buttonMoveDown;
  private javax.swing.JButton buttonMoveUp;
  private javax.swing.JButton buttonRemoveRow;
  private pl.mpak.sky.gui.swing.Action cmAddRow;
  private pl.mpak.sky.gui.swing.Action cmMoveDown;
  private pl.mpak.sky.gui.swing.Action cmMoveUp;
  private pl.mpak.sky.gui.swing.Action cmRemoveRow;
  private javax.swing.JComboBox comboDataTypes;
  private javax.swing.JComboBox comboDefaultValue;
  private javax.swing.JScrollPane jScrollPane1;
  private Table tableColumns;
  // End of variables declaration//GEN-END:variables
}
