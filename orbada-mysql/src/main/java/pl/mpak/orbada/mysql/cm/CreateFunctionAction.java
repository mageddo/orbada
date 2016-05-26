/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.functions.FunctionsPanelView;
import pl.mpak.orbada.mysql.gui.wizards.CreateFunctionWizard;
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
public class CreateFunctionAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public CreateFunctionAction() {
    super();
    setText(stringManager.getString("CreateFunctionAction-text"));
    setTooltip(stringManager.getString("CreateFunctionAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/function.gif"));
    setActionCommandKey("CreateFunctionAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          FunctionsPanelView panel = (FunctionsPanelView)SwingUtil.getOwnerComponent(FunctionsPanelView.class, getComponent());
          if (panel != null) {
            try {
              String databaseName = panel.getCurrentSchemaName();
              WizardResult result = SqlCodeWizardDialog.show(new CreateFunctionWizard(getDatabase(), databaseName), true);
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
