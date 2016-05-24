/*
 * ExportToDbf.java
 *
 * Created on 2008-10-26, 18:04:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.adder.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import pl.mpak.orbada.adder.OrbadaAdderPlugin;
import pl.mpak.orbada.adder.gui.AdderSelectorDialog;
import pl.mpak.orbada.plugins.providers.TableActionProvider;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author proznicki
 */
public class AdderActionService extends TableActionProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaAdderPlugin.class);

  public AdderActionService() {
    setText(stringManager.getString("AdderActionService-text"));
    setTooltip(getDescription());
    setShortCut(KeyEvent.VK_A, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK);
    setActionCommandKey("AdderActionService");
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return stringManager.getString("AdderActionService-hint");
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table instanceof QueryTable && ((QueryTable)table).getQuery() != null && ((QueryTable)table).getQuery().isActive()) {
          AdderSelectorDialog.showDialog((QueryTable)table);
        }
      }
    };
  }

  public String getGroupName() {
    return "Data List Tools";
  }
}
