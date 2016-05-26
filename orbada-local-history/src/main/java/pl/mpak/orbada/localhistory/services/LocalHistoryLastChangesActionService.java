/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.gui.LastChangesDialog;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.providers.ToolConfigurationActionProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class LocalHistoryLastChangesActionService extends ToolConfigurationActionProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLocalHistoryPlugin.class);

  public LocalHistoryLastChangesActionService() {
    setText(getDescription());
    setTooltip(stringManager.getString("LocalHistoryLastChangesActionService-hint"));
    setActionCommandKey("LocalHistoryLastChangesActionService");
    setShortCut(KeyEvent.VK_F8, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK);
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return stringManager.getString("LocalHistoryLastChangesActionService-description");
  }

  public String getGroupName() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }

  @Override
  public boolean isButton() {
    return false;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        IPerspectiveAccesibilities perspective = application.getActivePerspective();
        if (perspective != null && perspective.getDatabase() != null) {
          LastChangesDialog.showDialog(perspective);
        }
      }
    };
  }

}
