/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.gui.PerspectivePanel;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.freezing.FreezeFactory;
import pl.mpak.orbada.mysql.gui.freezing.FreezeViewService;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ObjectFreezeAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");
  
  private FreezeFactory freezeFactory;
  private IViewAccesibilities accesibilities;

  public ObjectFreezeAction() {
    super();
    setText(stringManager.getString("FreezeAction-description"));
    setTooltip(stringManager.getString("FreezeAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/freeze.gif"));
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
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              String databaseName = vt.getQuery().fieldByName("object_schema").getString();
              String objectName = vt.getQuery().fieldByName("object_name").getString();
              String objectType = vt.getQuery().fieldByName("object_type").getString();
              FreezeViewService service = freezeFactory.createInstance(objectType, databaseName, objectName);
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
  public void setComponent(Component component) {
    super.setComponent(component);
    if (component instanceof ViewTable) {
      PerspectivePanel panel = (PerspectivePanel)SwingUtil.getOwnerComponent(PerspectivePanel.class, component);
      accesibilities = panel.getViewAccesibilitiesForChild(getComponent());
      freezeFactory = new FreezeFactory(accesibilities);
      final ViewTable vt = (ViewTable)component;
      vt.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (vt.getSelectedRow() >= 0) {
            try {
              vt.getQuery().getRecord(vt.getSelectedRow());
              setEnabled(freezeFactory.canCreate(vt.getQuery().fieldByName("object_type").getString()));
            } catch (Exception ex) {
              ExceptionUtil.processException(ex);
            }
          }
        }
      });
    }
  }
  
  @Override
  public boolean isToolButton() {
    return true;
  }

}
