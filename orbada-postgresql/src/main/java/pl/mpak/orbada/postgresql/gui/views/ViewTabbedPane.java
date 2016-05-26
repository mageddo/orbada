/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.views;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.gui.PostgreSQLContentPanel;

/**
 *
 * @author akaluza
 */
public class ViewTabbedPane extends OrbadaTabbedPane {
  
  public ViewTabbedPane(IViewAccesibilities accesibilities) {
    super("TABLE",       
      new Component[] {
        new ViewColumnListTab(accesibilities),
        new ViewTriggerSourceTab(accesibilities),
        new ViewTriggerFunctionSourceTab(accesibilities),
        new ViewRuleSourceTab(accesibilities),
        new ViewPrivilegeListTab(accesibilities),
        new ViewColumnPrivilegeListTab(accesibilities),
        new ViewDetailsTab(accesibilities),
        new ViewDependsTab(accesibilities),
        new PostgreSQLContentPanel(accesibilities),
        new ViewSourceTab(accesibilities)
    });
  }
  
}
