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
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class KillQueryAction extends ComponentAction {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public KillQueryAction() {
    super();
    setActionCommandKey("KillQueryAction");
    setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/stop-query.gif"))); // NOI18N
    setText(stringManager.getString("cmKillQuery-text")); // NOI18N
    setTooltip(stringManager.getString("cmKillQuery-hint")); // NOI18N
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
              if (MessageBox.show(getComponent(), stringManager.getString("connection"), stringManager.getString("kill-query-q", session), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
                getDatabase().executeCommand("KILL QUERY " +session);
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
