/*
 * DerbyDbSearchObjectAction.java
 *
 * Created on 2007-11-11, 19:31:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.derbydb.util.DerbyDbSearchObjectDialog;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DerbyDbSearchObjectAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  private IPerspectiveAccesibilities accesibilities;
  
  public DerbyDbSearchObjectAction(IPerspectiveAccesibilities accesibilities) {
    super();
    this.accesibilities = accesibilities;
    setText(stringManager.getString("DerbyDbSearchObjectAction-text"));
    setShortCut(KeyEvent.VK_F7, KeyEvent.CTRL_MASK);
    setTooltip(stringManager.getString("DerbyDbSearchObjectAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_object16.gif"));
    setActionCommandKey("cmDerbyDbSearchObject");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DerbyDbSearchObjectDialog.showDialog(accesibilities);
      }
    };
  }
  
}
