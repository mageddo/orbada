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
import pl.mpak.orbada.mysql.gui.wizards.AnalyzeTableWizard;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class AnalyzeTableAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public AnalyzeTableAction() {
    super();
    setActionCommandKey("AnalyzeTableAction");
    setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/analyze.gif"))); // NOI18N
    setText(stringManager.getString("cmAnalyzeTable-text")); // NOI18N
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          TableUtilsPanel tab = (TableUtilsPanel)SwingUtil.getOwnerComponent(TableUtilsPanel.class, getComponent());
          AnalyzeTableWizard wizard = new AnalyzeTableWizard(getDatabase(), tab.getCurrentSchemaName(), tab.getCurrentObjectName());
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
