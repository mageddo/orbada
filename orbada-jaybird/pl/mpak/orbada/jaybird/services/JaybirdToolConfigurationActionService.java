/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jaybird.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.plugins.providers.ToolConfigurationActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class JaybirdToolConfigurationActionService extends ToolConfigurationActionProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJaybirdPlugin.class);
  private boolean visible = false;

  public JaybirdToolConfigurationActionService() {
    setText(stringManager.getString("JaybirdToolConfigurationActionService.text"));
    setTooltip(stringManager.getString("JaybirdToolConfigurationActionService.tooltip"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/orbada/jaybird/res/jaybird.gif", this.getClass()));
    setActionCommandKey("JaybirdToolConfigurationActionService");
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return OrbadaJaybirdPlugin.firebirdDriverType +" Jaybird Service";
  }

  public String getGroupName() {
    return OrbadaJaybirdPlugin.firebirdDriverType;
  }

  @Override
  public boolean isButton() {
    return true;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          getClass().getClassLoader().loadClass("org.firebirdsql.jdbc.FBDriver");
        } catch (ClassNotFoundException ex) {
          MessageBox.show(SwingUtil.getRootFrame(), "Jaybird", stringManager.getString("JaybirdToolConfigurationActionService.turnon_message"), ModalResult.OK, MessageBox.ERROR);
          return;
        }
        visible = !visible;
        JaybirdMenuService.get().setVisible(visible);
        JaybirdToolbarService.get().setVisible(visible);
      }
    };
  }

}
