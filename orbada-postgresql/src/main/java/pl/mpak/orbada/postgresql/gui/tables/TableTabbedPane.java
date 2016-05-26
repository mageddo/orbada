/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.tables;

import java.awt.Component;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.gui.PostgreSQLContentPanel;

/**
 *
 * @author akaluza
 */
public class TableTabbedPane extends OrbadaTabbedPane {
  
  public TableTabbedPane(IViewAccesibilities accesibilities) {
    super("TABLE",       
      new Component[] {
        new TableColumnListTab(accesibilities),
        new TableIndexListTab(accesibilities),
        new TableConstraintListTab(accesibilities),
        new TableTriggerSourceTab(accesibilities),
        new TableTriggerFunctionSourceTab(accesibilities),
        new TableRuleSourceTab(accesibilities),
        new TableReferencedToListTab(accesibilities),
        new TableReferencedFromListTab(accesibilities),
        new TablePrivilegeListTab(accesibilities),
        new TableColumnPrivilegeListTab(accesibilities),
        new TableDetailsTab(accesibilities),
        new TableSizeTab(accesibilities),
        new TableDependsTab(accesibilities),
        new PostgreSQLContentPanel(accesibilities)
    });
  }
  
}
