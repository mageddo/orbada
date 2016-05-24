package pl.mpak.orbada.sqlite.gui.tables;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
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
        new TableTriggersPanel(accesibilities),
        new TableReferencedToPanel(accesibilities),
        new ContentPanel(accesibilities),
        new TableSourcePanel(accesibilities)
    });
  }
  
}
