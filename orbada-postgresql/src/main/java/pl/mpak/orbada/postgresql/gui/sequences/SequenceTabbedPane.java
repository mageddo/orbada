/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.gui.sequences;

import java.awt.Component;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class SequenceTabbedPane extends OrbadaTabbedPane {
  
  public SequenceTabbedPane(IViewAccesibilities accesibilities) {
    super("SEQUENCE",       
      new Component[] {
        new SequenceDetailsTab(accesibilities),
        new SequenceDependsTab(accesibilities),
        new SequencePrivilegeListTab(accesibilities)
    });
  }
  
}
