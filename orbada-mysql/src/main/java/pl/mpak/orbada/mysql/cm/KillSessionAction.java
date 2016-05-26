/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import orbada.gui.IRootTabObjectInfo;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class KillSessionAction extends ComponentAction {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public KillSessionAction() {
    super();
    setActionCommandKey("KillSessionAction");
    setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/stop16.gif"))); // NOI18N
    setText(stringManager.getString("cmKillSession-text")); // NOI18N
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          final IRootTabObjectInfo ip = (IRootTabObjectInfo)SwingUtil.getOwnerComponent(IRootTabObjectInfo.class, getComponent());
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String session = vt.getQuery().fieldByName("id").getString();
              if (MessageBox.show(getComponent(), stringManager.getString("connection"), stringManager.getString("kill-connection-q", session), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
                getDatabase().executeCommand("KILL CONNECTION " +session);
                java.awt.EventQueue.invokeLater(new Runnable() {
                  public void run() {
                    ip.refresh();
                  }
                });
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
