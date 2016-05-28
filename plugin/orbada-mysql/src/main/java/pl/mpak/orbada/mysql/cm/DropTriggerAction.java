/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DropTriggerAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public DropTriggerAction() {
    super();
    setText(stringManager.getString("DropTriggerAction-text"));
    setTooltip(stringManager.getString("DropTriggerAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif"));
    setActionCommandKey("DropTriggerAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          IRootTabObjectInfo ip = (IRootTabObjectInfo)SwingUtil.getOwnerComponent(IRootTabObjectInfo.class, getComponent());
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String databaseName = vt.getQuery().fieldByName("trigger_schema").getString();
              String tableName = vt.getQuery().fieldByName("trigger_name").getString();
              if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("deleting"), stringManager.getString("DropTriggerAction-drop-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
                getDatabase().executeCommand("DROP TRIGGER " +SQLUtil.createSqlName(databaseName, tableName, getDatabase()));
                ip.refresh(null);
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
    return false;
  }

}
