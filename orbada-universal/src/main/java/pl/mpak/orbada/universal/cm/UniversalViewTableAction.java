/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import orbada.gui.IRootTabObjectInfo;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 * S³u¿y do obs³ugi standardowej akcji opartej o ViewTable
 * Pobiera wszystkie ptrzebne obiekty i przekazuje je do doAction
 * @author akaluza
 */
public abstract class UniversalViewTableAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  public UniversalViewTableAction() {
    super();
    addActionListener(createActionListener());
  }
  
  /**
   * wykonanie akcji
   * sprawdza czy jest wybrany jakiœ element na gridzie wiêc wystarczy pobraæ wartoœæi fieldByName()
   * jeœli funkcja zwróci true wtedy odœwierza listê
   * @param query
   * @param vt
   * @param ip
   * @return
   * @throws Exception 
   */
  protected abstract boolean doAction(ViewTable vt, IRootTabObjectInfo ip) throws Exception;

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
              if (doAction(vt, ip)) {
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

}
