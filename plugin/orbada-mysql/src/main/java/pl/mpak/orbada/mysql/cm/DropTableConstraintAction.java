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
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class DropTableConstraintAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public DropTableConstraintAction() {
    super();
    setText(stringManager.getString("DropTableConstraintAction-text"));
    setTooltip(stringManager.getString("DropTableConstraintAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trash.gif"));
    setActionCommandKey("DropTableConstraintAction");
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
              String constrName = vt.getQuery().fieldByName("constraint_name").getString();
              String constrType = vt.getQuery().fieldByName("constraint_type").getString();
              if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("deleting"), stringManager.getString("DropTableConstraintAction-delete-constr-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
                if (StringUtil.equals("PRIMARY KEY", constrType)) {
                  getDatabase().executeCommand("ALTER TABLE " +SQLUtil.createSqlName(databaseName, tableName, getDatabase()) +" DROP PRIMARY KEY");
                }
                else if (StringUtil.equals("FOREIGN KEY", constrType)) {
                  getDatabase().executeCommand("ALTER TABLE " +SQLUtil.createSqlName(databaseName, tableName, getDatabase()) +" DROP FOREIGN KEY " +SQLUtil.createSqlName(constrName, getDatabase()));
                }
                else if (StringUtil.equals("UNIQUE", constrType)) {
                  getDatabase().executeCommand("ALTER TABLE " +SQLUtil.createSqlName(databaseName, tableName, getDatabase()) +" DROP INDEX " +SQLUtil.createSqlName(constrName, getDatabase()));
                }
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
    return false;
  }

}
