package pl.mpak.orbada.oracle.gui.wizards;

import java.io.IOException;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.oracle.util.SourceCreator;
import pl.mpak.orbada.universal.gui.CommandParametersPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.ParameterList;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class CallObjectWizard extends SqlCodeWizardPanel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private Database database;
  private String schemaName;
  private String objectName;
  private String overload;
  private SourceCreator sourceCreator;
  private String sqlCode;
  private Command command;
  private CommandParametersPanel parameters;
  
  public CallObjectWizard(Database database, String schemaName, String objectName, String overload) {
    this.database = database;
    this.schemaName = schemaName;
    this.objectName = objectName;
    this.overload = overload;
    this.sourceCreator = new SourceCreator(database);
    initComponents();
    init();
  }
  
  private void init() {
    schemaName = schemaName == null ? OracleDbInfoProvider.getCurrentSchema(database) : schemaName;
    setSize(560, 400);
  }
  
  public void wizardShow() {
    sqlCode = sourceCreator.getSource(schemaName, "CALL", objectName, overload);
    command = database.createCommand();
    try {
      command.setSqlText(sqlCode);
    } catch (UseDBException ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    if (command.getParameterCount() > 0) {
      parameters = new CommandParametersPanel(command);
      add(parameters);
      parameters.setFocus();
    }
  }
  
  @Override
  public void close() throws IOException {
    if (parameters != null) {
      parameters.close();
    }
  }

  public String getDialogTitle() {
    return stringManager.getString("CallObjectWizard-dialog-title");
  }
  
  public String getTabTitle() {
    return stringManager.getString("CallObjectWizard-tab-title");
  }
  
  public String getSqlCode() {
    return sqlCode;
  }
  
  public boolean execute() {
    try {
      if (parameters != null) {
        parameters.updateParameters();
      }
      command.execute();
      return true;
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      return false;
    }
  }

  public ParameterList getParameterList() {
    return command.getParameterList();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setLayout(new java.awt.BorderLayout());
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
  
}
