/*
 * ExportToDbf.java
 *
 * Created on 2008-10-26, 18:04:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.datamove.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTable;
import pl.mpak.orbada.datamove.OrbadaDataMovePlugin;
import pl.mpak.orbada.datamove.gui.QueryCreateTableWizardPanel;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author proznicki
 */
public class QueryCreateTableService extends ExportTableActionProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("data-move");

  public QueryCreateTableService() {
    setText(stringManager.getString("qcts-text"));
    setTooltip(stringManager.getString("qcts-hint"));
    setActionCommandKey("cmQueryCreateTableService");
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return stringManager.getString("qcts-description");
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        final JTable table = getTable(event);
        if (table != null && table instanceof QueryTable) {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              SqlCodeWizardDialog.show(new QueryCreateTableWizardPanel(((QueryTable)table).getQuery()), true, true);
            }
          });
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaDataMovePlugin.pluginGroupName;
  }
}
