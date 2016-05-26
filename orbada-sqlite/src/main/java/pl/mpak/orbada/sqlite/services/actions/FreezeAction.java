/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.services.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.gui.PerspectivePanel;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.gui.freezing.FreezeFactory;
import pl.mpak.orbada.sqlite.gui.freezing.FreezeViewService;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FreezeAction extends ComponentActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  private String objectType;

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
              String catalogName = vt.getQuery().fieldByName("database").getString();
              String objectName = vt.getQuery().fieldByName("name").getString();
              FreezeViewService service = freezeFactory.createInstance(objectType, catalogName, objectName);
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
  public boolean isForComponent(Database database, String actionType) {
    if (database == null || !OrbadaSQLitePlugin.driverType.equals(database.getDriverType())) {
      return false;
    }
    if ("sqlite-tables-actions".equals(actionType)) {
      objectType = "TABLE";
    }
    else if ("sqlite-views-actions".equals(actionType)) {
      objectType = "VIEW";
    }
    else if ("sqlite-triggers-actions".equals(actionType)) {
      objectType = "TRIGGER";
    }
    else {
      return false;
    }

    setText(getDescription());
    setTooltip(stringManager.getString("FreezeAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif"));
    setShortCut(KeyEvent.VK_F3, 0);
    setActionCommandKey("FreezeAction");
    addActionListener(createActionListener());
    
    return true;
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("FreezeAction-description");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }

}
