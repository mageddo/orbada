/*
 * CommandParametersPanel.java
 *
 * Created on 1 paŸdziernik 2008, 19:44
 */

package pl.mpak.orbada.universal.gui;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.Table;
import orbada.db.InternalDatabase;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Parameter;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.ParameterList;
import pl.mpak.usedb.gui.swing.ParametersTableModel;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.Titleable;
import pl.mpak.util.task.Task;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class CommandParametersPanel extends javax.swing.JPanel implements Closeable, Titleable {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  private ParameterList parameterList;
  private String schemaId;
  private Query sqlParams;

  public CommandParametersPanel(ParametrizedCommand command) {
    this(command.getParameterList(), command.getDatabase().getUserProperties().getProperty("schemaId"));
  }

  public CommandParametersPanel(ParameterList parameterList, String schemaId) {
    this.parameterList = parameterList;
    this.schemaId = schemaId;
    initComponents();
    init();
  }

  public void updateParameters() {
    if (tableParameters.getSelectedRow() >= 0) {
      updateParameter(tableParameters.getSelectedRow());
    }
  }

  public void close() throws IOException {
    try {
      updateParameters();
      saveSqlParams();
      sqlParams.close();
    }
    catch (java.lang.IndexOutOfBoundsException ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public String getTitle() {
    return stringManager.getString("CommandParametersPanel-title");
  }

  private void init() {
    openSqlParams();
    TableRowChangeKeyListener trckl = new TableRowChangeKeyListener(tableParameters);
    textParamName.addKeyListener(trckl);
    textParamValue.addKeyListener(trckl);

    tableParameters.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      int rowSelected = -1;
      public void valueChanged(ListSelectionEvent e) {
        if (rowSelected != -1) {
          updateParameter(rowSelected);
        }
        rowSelected = tableParameters.getSelectedRow();
        if (rowSelected != -1) {
          textParamName.setText(tableParameters.getValueAt(rowSelected, 0).toString());
          comboParamType.setSelectedItem(tableParameters.getValueAt(rowSelected, 1).toString());
          comboParamMode.setSelectedItem(tableParameters.getValueAt(rowSelected, 2).toString());
          if (tableParameters.getValueAt(rowSelected, 3) != null) {
            textParamValue.setText(tableParameters.getValueAt(rowSelected, 3).toString());
          } else {
            textParamValue.setText("");
          }
        }
      }
    });
    tableParameters.setModel(new ParametersTableModel(parameterList));
    ((ParametersTableModel)tableParameters.getModel()).configureTable(tableParameters);
    enableControls();
  }
  
  public void setFocus() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textParamValue.requestFocus();
      }
    });
  }
  
  private void openSqlParams() {
    Database database = InternalDatabase.get();
    sqlParams = database.createQuery();
    try {
      sqlParams.setFlushMode(Query.FlushMode.fmSynch);
      StringBuilder sb = new StringBuilder();
      for (int i=0; i<parameterList.parameterCount(); i++) {
        Parameter p = parameterList.getParameter(i);
        if (sb.length() != 0) {
          sb.append(',');
        }
        sb.append("'" +p.getParamName().toUpperCase() +"'");
      }
      sqlParams.setSqlText(
        "select sqlp_name, sqlp_type, sqlp_mode, sqlp_value\n" +
        "  from sqlparams\n" +
        " where sqlp_sch_id " +(schemaId != null ? (" = '" +schemaId +"'") : "is null") +"\n" +
        "   and sqlp_name in (" +sb.toString() +")");
      sqlParams.open();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    if (sqlParams.isActive()) {
      for (int i=0; i<parameterList.parameterCount(); i++) {
        Parameter p = parameterList.getParameter(i);
        try {
          if (sqlParams.locate("sqlp_name", new Variant(p.getParamName().toUpperCase()))) {
            p.setString(sqlParams.fieldByName("sqlp_value").getString());
            p.setParamDataType(SQLUtil.stringToType(sqlParams.fieldByName("sqlp_type").getString()));
            p.setParamMode(SQLUtil.stringToParamMode(sqlParams.fieldByName("sqlp_mode").getString()));
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    }
  }
  
  private void saveSqlParams() {
    if (sqlParams.isActive()) {
      for (int i=0; i<parameterList.parameterCount(); i++) {
        Parameter p = parameterList.getParameter(i);
        try {
          if (sqlParams.locate("sqlp_name", new Variant(p.getParamName().toUpperCase()))) {
            if (!sqlParams.fieldByName("sqlp_type").getString().equals(SQLUtil.typeToString(p.getParamDataType())) ||
                !sqlParams.fieldByName("sqlp_mode").getString().equals(SQLUtil.paramModeToString(p.getParamMode())) ||
                !sqlParams.fieldByName("sqlp_value").getString().equals(p.getString())) {
              InternalDatabase.get().getTaskPool().addTask(
                new UpdateParamTask(schemaId, p.getParamName().toUpperCase(), 
                  SQLUtil.typeToString(p.getParamDataType()), 
                  SQLUtil.paramModeToString(p.getParamMode()),
                  p.getString()));
            }
          }
          else {
            InternalDatabase.get().getTaskPool().addTask(
              new InsertParamTask(schemaId, p.getParamName().toUpperCase(), 
                SQLUtil.typeToString(p.getParamDataType()), 
                SQLUtil.paramModeToString(p.getParamMode()),
                p.getString()));
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    }
  }
  
  private void updateParameter(int row) {
    tableParameters.getModel().setValueAt(comboParamType.getSelectedItem(), row, 1);
    tableParameters.getModel().setValueAt(comboParamMode.getSelectedItem(), row, 2);
    tableParameters.getModel().setValueAt(textParamValue.getText(), row, 3);
  }

  private void enableControls() {
    buttonOpenFile.setVisible(StringUtil.equalAnyOfString(comboParamType.getSelectedItem().toString(), new String[] {"CLOB", "BLOB"}, true));
  }
  
  class UpdateParamTask extends Task {
    private String schId;
    private String type;
    private String mode;
    private String name;
    private String value;
    public UpdateParamTask(String schId, String name, String type, String mode, String value) {
      this.schId = schId;
      this.type = type;
      this.mode = mode;
      this.name = name;
      this.value = value;
    }
    public void run() {
      try {
        Command command = InternalDatabase.get().createCommand(
          "update sqlparams\n" +
          "   set sqlp_type = :sqlp_type,\n" +
          "       sqlp_value = :sqlp_value,\n" +
          "       sqlp_mode = :sqlp_mode\n" +
          " where sqlp_sch_id " +(schId != null ? (" = '" +schId +"'") : "is null") +"\n" +
          "   and sqlp_name = :sqlp_name", false);
        command.paramByName("sqlp_type").setString(type);
        command.paramByName("sqlp_mode").setString(mode);
        command.paramByName("sqlp_value").setString(value);
        command.paramByName("sqlp_name").setString(name);
        command.execute();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  class InsertParamTask extends Task {
    private String schId;
    private String type;
    private String mode;
    private String name;
    private String value;
    public InsertParamTask(String schId, String name, String type, String mode, String value) {
      this.schId = schId;
      this.type = type;
      this.name = name;
      this.mode = mode;
      this.value = value;
    }
    public void run() {
      try {
        Command command = InternalDatabase.get().createCommand(
          "insert into sqlparams (sqlp_sch_id, sqlp_name, sqlp_type, sqlp_mode, sqlp_value)\n" +
          "values (" +(schId != null ? "'" +schId +"'" : "null") +", :sqlp_name, :sqlp_type, :sqlp_mode, :sqlp_value)", false);
        command.paramByName("sqlp_type").setString(type);
        command.paramByName("sqlp_mode").setString(mode);
        command.paramByName("sqlp_value").setString(value);
        command.paramByName("sqlp_name").setString(name);
        command.execute();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmOpenFile = new pl.mpak.sky.gui.swing.Action();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableParameters = new Table();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textParamName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        comboParamType = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        comboParamMode = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        textParamValue = new pl.mpak.sky.gui.swing.comp.TextField();
        buttonOpenFile = new javax.swing.JButton();

        cmOpenFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/open16.gif")); // NOI18N
        cmOpenFile.setText(stringManager.getString("cmOpenFile-text")); // NOI18N
        cmOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOpenFileActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(tableParameters);

        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 0, new java.awt.Color(0, 0, 0)));

        jLabel1.setText(stringManager.getString("parameter-name-dd")); // NOI18N

        textParamName.setEditable(false);

        jLabel2.setText(stringManager.getString("parameter-type-dd")); // NOI18N

        comboParamType.setModel(new DefaultComboBoxModel(Parameter.paramTypesStr));
        comboParamType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboParamTypeItemStateChanged(evt);
            }
        });

        jLabel4.setText(stringManager.getString("parameter-mode-dd")); // NOI18N

        comboParamMode.setModel(new DefaultComboBoxModel(Parameter.paramModesStr));

        jLabel3.setText(stringManager.getString("parameter-value-dd")); // NOI18N

        buttonOpenFile.setAction(cmOpenFile);
        buttonOpenFile.setHideActionText(true);
        buttonOpenFile.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
            .addComponent(textParamName, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
            .addComponent(comboParamType, 0, 250, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
            .addComponent(comboParamMode, 0, 250, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(textParamValue, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonOpenFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textParamName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboParamType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboParamMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonOpenFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textParamValue, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(147, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOpenFileActionPerformed
      File file = FileUtil.selectFileToOpen(this, StringUtil.isEmpty(textParamValue.getText()) ? null : new File(textParamValue.getText()), null);
      if (file != null) {
        textParamValue.setText(file.getAbsoluteFile().toString());
      }
    }//GEN-LAST:event_cmOpenFileActionPerformed

    private void comboParamTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboParamTypeItemStateChanged
      enableControls();
    }//GEN-LAST:event_comboParamTypeItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonOpenFile;
    private pl.mpak.sky.gui.swing.Action cmOpenFile;
    private javax.swing.JComboBox comboParamMode;
    private javax.swing.JComboBox comboParamType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private Table tableParameters;
    private javax.swing.JTextField textParamName;
    private pl.mpak.sky.gui.swing.comp.TextField textParamValue;
    // End of variables declaration//GEN-END:variables

}
