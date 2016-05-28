/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.aggregates;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class AggregateTabbedPane extends OrbadaTabbedPane {
  
  public AggregateTabbedPane(IViewAccesibilities accesibilities) {
    super("AGGREGATE",
      new Component[] {
        new AggregateAttributeListTab(accesibilities),
        new AggregateDetailsTab(accesibilities),
        new AggregateDependsTab(accesibilities),
        new AggregateSourceTab(accesibilities)
    });
  }
  
}
