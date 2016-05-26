/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.tables.TableConstraintsPanel;
import pl.mpak.orbada.mysql.gui.wizards.CreateForeignKeyTableConstraintWizardPanel;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class CreateForeignKeyTableConstraintAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public CreateForeignKeyTableConstraintAction() {
    super();
    setText(stringManager.getString("CreateForeignKeyTableConstraintAction-text"));
    setTooltip(stringManager.getString("CreateForeignKeyTableConstraintAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/foreign_key16.gif"));
    setActionCommandKey("CreateForeignKeyTableConstraintAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          TableConstraintsPanel panel = (TableConstraintsPanel)SwingUtil.getOwnerComponent(TableConstraintsPanel.class, getComponent());
          if (!StringUtil.isEmpty(panel.getCurrentObjectName())) {
            ITabObjectInfo ip = (ITabObjectInfo)SwingUtil.getOwnerComponent(ITabObjectInfo.class, getComponent());
            try {
              String databaseName = panel.getCurrentSchemaName();
              String tableName = panel.getCurrentObjectName();
              if (SqlCodeWizardDialog.show(new CreateForeignKeyTableConstraintWizardPanel(getDatabase(), databaseName, tableName), true) != null) {
                ip.refresh();
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
