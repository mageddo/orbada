package pl.mpak.orbada.mysql.gui.tables;

import java.awt.Component;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.ContentPanel;

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
        new TablePrivilegesPanel(accesibilities),
        new TableColumnPrivilegesPanel(accesibilities),
        new TableReferencedToPanel(accesibilities),
        new TableReferencedFromPanel(accesibilities),
        new TableDetailsPanel(accesibilities),
        new TableUtilsPanel(accesibilities),
        new ContentPanel(accesibilities),
        new TableSourcePanel(accesibilities)
    });
  }
  
}
