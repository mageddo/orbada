/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.tables.TableUtilsPanel;
import pl.mpak.orbada.mysql.gui.wizards.OptimyzeTableWizard;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OptimizeTableAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public OptimizeTableAction() {
    super();
    setActionCommandKey("OptimizeTableAction");
    setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/optimize.gif"))); // NOI18N
    setText(stringManager.getString("cmOptimizeTable-text")); // NOI18N
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          TableUtilsPanel tab = (TableUtilsPanel)SwingUtil.getOwnerComponent(TableUtilsPanel.class, getComponent());
          OptimyzeTableWizard wizard = new OptimyzeTableWizard(getDatabase(), tab.getCurrentSchemaName(), tab.getCurrentObjectName());
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
