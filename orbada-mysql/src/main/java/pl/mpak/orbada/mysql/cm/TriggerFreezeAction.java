/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import orbada.gui.PerspectivePanel;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.freezing.FreezeFactory;
import pl.mpak.orbada.mysql.gui.freezing.FreezeViewService;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TriggerFreezeAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public TriggerFreezeAction() {
    super();
    setText(stringManager.getString("FreezeAction-description"));
    setTooltip(stringManager.getString("FreezeAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif"));
    setShortCut(KeyEvent.VK_F3, 0);
    setActionCommandKey("FreezeAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          if (vt.getSelectedRow() >= 0) {
            PerspectivePanel panel = (PerspectivePanel)SwingUtil.getOwnerComponent(PerspectivePanel.class, getComponent());
            IViewAccesibilities accesibilities = panel.getViewAccesibilitiesForChild(getComponent());
            FreezeFactory freezeFactory = new FreezeFactory(accesibilities);
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String databaseName = vt.getQuery().fieldByName("trigger_schema").getString();
              String objectName = vt.getQuery().fieldByName("trigger_name").getString();
              FreezeViewService service = freezeFactory.createInstance("TRIGGER", databaseName, objectName);
              if (service != null) {
                accesibilities.createView(service);
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
