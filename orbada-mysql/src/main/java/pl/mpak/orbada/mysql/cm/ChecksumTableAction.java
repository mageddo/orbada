/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.tables.TableUtilsPanel;
import pl.mpak.orbada.mysql.gui.wizards.ChecksumTableWizard;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ChecksumTableAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public ChecksumTableAction() {
    super();
    setActionCommandKey("ChecksumTableAction");
    setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/sum.gif"))); // NOI18N
    setText(stringManager.getString("cmChecksumTable-text")); // NOI18N
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          TableUtilsPanel tab = (TableUtilsPanel)SwingUtil.getOwnerComponent(TableUtilsPanel.class, getComponent());
          ChecksumTableWizard wizard = new ChecksumTableWizard(getDatabase(), tab.getCurrentSchemaName(), tab.getCurrentObjectName());
          if (SqlCodeWizardDialog.show(wizard, false) != null) {
            tab.reopen(wizard.getSqlCode());
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
