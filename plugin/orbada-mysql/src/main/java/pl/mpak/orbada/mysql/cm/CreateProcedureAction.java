/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.procedures.ProceduresPanelView;
import pl.mpak.orbada.mysql.gui.wizards.CreateProcedureWizard;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog.WizardResult;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateProcedureAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public CreateProcedureAction() {
    super();
    setText(stringManager.getString("CreateProcedureAction-text"));
    setTooltip(stringManager.getString("CreateProcedureAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/procedure.gif"));
    setActionCommandKey("CreateProcedureAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          ProceduresPanelView panel = (ProceduresPanelView)SwingUtil.getOwnerComponent(ProceduresPanelView.class, getComponent());
          if (panel != null) {
            try {
              String databaseName = panel.getCurrentSchemaName();
              WizardResult result = SqlCodeWizardDialog.show(new CreateProcedureWizard(getDatabase(), databaseName), true);
              if (result != null) {
                panel.refresh(result.getResultMap().get("name"));
              }
            } catch (Exception ex) {
              MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
            }
          }
        }
      }
    };
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

}
