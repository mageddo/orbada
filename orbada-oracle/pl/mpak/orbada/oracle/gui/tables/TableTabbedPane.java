/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.tables;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.gui.ContentPanel;

/**
 *
 * @author akaluza
 */
public class TableTabbedPane extends OrbadaTabbedPane {
  
  public TableTabbedPane(IViewAccesibilities accesibilities) {
    super("TABLE",       
      new Component[] {
        new TableColumnsPanel(accesibilities),
        new TableIndexesPanel(accesibilities),
        new TableConstraintsPanel(accesibilities),
        new TableTriggersPanel(accesibilities),
        new TableGrantsPanel(accesibilities),
        new TableColumnGrantsPanel(accesibilities),
        new TableReferencedToPanel(accesibilities),
        new TableReferencedFromPanel(accesibilities),
        new ObjectUsedByPanel(accesibilities, "TABLE"),
        new ObjectUsingPanel(accesibilities, "TABLE"),
        new TableDetailsPanel(accesibilities),
        new TableModificationsPanel(accesibilities),
        new TableSizePanel(accesibilities),
        new TablePartitionsPanel(accesibilities),
        new ContentPanel(accesibilities),
        new TableSourcePanel(accesibilities)
    });
  }
  
}
