/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.mviews;

import java.awt.Component;
import pl.mpak.orbada.gui.ContentPanel;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.ObjectUsedByPanel;
import pl.mpak.orbada.oracle.gui.ObjectUsingPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class MViewTabbedPane extends OrbadaTabbedPane {
  
  public MViewTabbedPane(IViewAccesibilities accesibilities) {
    super("MATERIALIZED VIEW",       
      new Component[] {
        new MViewColumnsPanel(accesibilities),
        new MViewTriggersPanel(accesibilities),
        new MViewIndexesPanel(accesibilities),
        new ObjectUsedByPanel(accesibilities, "MATERIALIZED VIEW"),
        new ObjectUsingPanel(accesibilities, "MATERIALIZED VIEW"),
        new MViewDetailsPanel(accesibilities),
        new MViewGrantsPanel(accesibilities),
        new ContentPanel(accesibilities),
        new MViewSourcePanel(accesibilities)
    });
  }
  
}
