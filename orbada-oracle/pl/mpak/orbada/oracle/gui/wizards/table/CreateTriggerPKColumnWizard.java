package pl.mpak.orbada.oracle.gui.wizards.table;

import java.util.HashMap;
import orbada.core.Application;
import pl.mpak.orbada.db.Template;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.dbinfo.OracleTableInfo;
import pl.mpak.orbada.oracle.gui.util.SequenceComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.TableColumnComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.TableComboBoxModel;
import pl.mpak.orbada.oracle.gui.util.TableItemListener;
import pl.mpak.orbada.oracle.services.OracleTemplatesSettingsProvider;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class CreateTriggerPKColumnWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private String schemaName;
  private String tableName;
  private boolean nameChanged = false;
  private Template template;
  
  public CreateTriggerPKColumnWizard(Database database, String schemaName, String tableName) {
    this.database = database;
    this.schemaName = schemaName;
    this.tableName = tableName;
    initComponents();
    init();
  }
  
  private String getPrimaryKeyColumn(String schemaName, String tableName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getTablePrimaryKey());
      query.paramByName("SCHEMA_NAME").setString(schemaName);
      query.paramByName("TABLE_NAME").setString(tableName);
      query.open();
      if (!query.eof()) {
        return query.fieldByName("column_name").getString();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return null;
  }
  
  private void init() {
    comboColumns.setModel(new TableColumnComboBoxModel(database));
    comboTables.setModel(new TableComboBoxModel(database));
    comboSequences.setModel(new SequenceComboBoxModel(database));
    
    comboTables.addItemListener(new TableItemListener() {
      public void itemChanged(OracleTableInfo info) {
        ((TableColumnComboBoxModel)comboColumns.getModel()).change(info.getSchema().getName(), info.getName());
        String columnName = getPrimaryKeyColumn(info.getSchema().getName(), info.getName());
        if (columnName != null) {
          for (int i=0; i<comboColumns.getItemCount(); i++) {
            if (comboColumns.getItemAt(i).equals(columnName)) {
              comboColumns.setSelectedIndex(i);
              break;
            }
          }
        }
        for (int i=0; i<comboSequences.getItemCount(); i++) {
          if (comboSequences.getItemAt(i).equals(info.getName() +"_SEQ") || 
              comboSequences.getItemAt(i).equals("ID_" +info.getName()) ||
              comboSequences.getItemAt(i).toString().toUpperCase().indexOf(info.getName().toUpperCase()) >= 0) {
            comboSequences.setSelectedIndex(i);
            break;
          }
        }
        if (!nameChanged) {
          textName.setText(info.getName() +"_NEW_TRG");
        }
      }
    });
  }
  
  public void wizardShow() {
    ISettings oracle = Application.get().getSettings(OracleTemplatesSettingsProvider.settingsName);
    template = new Template(Application.get().getOrbadaDatabase()).loadByName(oracle.getValue(OracleTemplatesSettingsProvider.setTrigger, "oracle-trigger"));

    ((TableComboBoxModel)comboTables.getModel()).change(schemaName);
    ((SequenceComboBoxModel)comboSequences.getModel()).change(schemaName);
    ((TableComboBoxModel)comboTables.getModel()).select(tableName, comboTables);
  }
  
  public String getDialogTitle() {
    return stringManager.getString("CreateTriggerPKColumnWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CreateTriggerPKColumnWizard-tab-title");
  }
  
  public String getSqlCode() {
    getResultMap().put("object_name", textName.getText());
    String body = String.format(
      "  IF :NEW.%1$s IS NULL THEN\n" +
      "    SELECT %2$s.NEXTVAL INTO :NEW.%1$s FROM DUAL;\n" +
      "  END IF;",
      new Object[] {
        comboColumns.getSelectedItem().toString(),
        comboSequences.getSelectedItem().toString()
      });
    String type = String.format(
      "  BEFORE INSERT ON %1$s\n" +
      "  FOR EACH ROW",
      new Object[] {
        comboTables.getSelectedItem().toString()
      });
      
    if (template == null) {
      return String.format(
        "CREATE OR REPLACE TRIGGER %1$s\n" +type +"\n" +
        "BEGIN\n" +body +"\n" +
        "END;",
        new Object[] {
          textName.getText()
        });
    }
    else {
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("&name", textName.getText());
      map.put("&type", type);
      map.put("&body", body);
      map.put("&description", stringManager.getString("CreateTriggerPKColumnWizard-trigger-info"));
      return template.expand(map);
    }
  }
  
  public boolean execute() {
    try {
      Command command = database.createCommand();
      command.setParamCheck(false);
      command.execute(getSqlCode());
      return true;
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      ExceptionUtil.processException(ex);
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

    jLabel3 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel2 = new javax.swing.JLabel();
    comboTables = new javax.swing.JComboBox();
    jLabel4 = new javax.swing.JLabel();
    comboColumns = new javax.swing.JComboBox();
    jLabel5 = new javax.swing.JLabel();
    comboSequences = new javax.swing.JComboBox();

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("trigger-name-dd")); // NOI18N

    textName.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        textNameKeyPressed(evt);
      }
    });

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("table-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("column-list-dd")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("sequence-list-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTables, 0, 270, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboColumns, 0, 270, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboSequences, 0, 270, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(comboTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(comboColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(comboSequences, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void textNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textNameKeyPressed
    nameChanged = true;
}//GEN-LAST:event_textNameKeyPressed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox comboColumns;
  private javax.swing.JComboBox comboSequences;
  private javax.swing.JComboBox comboTables;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  // End of variables declaration//GEN-END:variables
  
}
