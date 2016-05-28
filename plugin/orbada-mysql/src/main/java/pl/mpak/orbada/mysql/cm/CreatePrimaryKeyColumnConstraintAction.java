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
import pl.mpak.orbada.mysql.gui.wizards.CreatePrimaryKeyTableConstraintWizardPanel;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreatePrimaryKeyColumnConstraintAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public CreatePrimaryKeyColumnConstraintAction() {
    super();
    setText(stringManager.getString("CreatePrimaryKeyTableConstraintAction-text"));
    setTooltip(stringManager.getString("CreatePrimaryKeyTableConstraintAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/primary_key16.gif"));
    setActionCommandKey("CreatePrimaryKeyColumnConstraintAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          ITabObjectInfo ip = (ITabObjectInfo)SwingUtil.getOwnerComponent(ITabObjectInfo.class, getComponent());
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String databaseName = vt.getQuery().fieldByName("table_schema").getString();
              String tableName = vt.getQuery().fieldByName("table_name").getString();
              String columnName = vt.getQuery().fieldByName("column_name").getString();
              if (SqlCodeWizardDialog.show(new CreatePrimaryKeyTableConstraintWizardPanel(getDatabase(), databaseName, tableName, columnName), true) != null) {
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
