/*
 * DerbyDbSearchObjectAction.java
 *
 * Created on 2007-11-11, 19:31:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.gui.HSqlDbSearchObjectDialog;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class HSqlDbSearchObjectAction extends Action {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  private IPerspectiveAccesibilities accesibilities;
  
  public HSqlDbSearchObjectAction(IPerspectiveAccesibilities accesibilities) {
    super();
    this.accesibilities = accesibilities;
    setText(stringManager.getString("HSqlDbSearchObjectAction-text"));
    setShortCut(KeyEvent.VK_F7, KeyEvent.CTRL_MASK);
    setTooltip(stringManager.getString("HSqlDbSearchObjectAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/find_object16.gif"));
    setActionCommandKey("HSqlDbSearchObjectAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        HSqlDbSearchObjectDialog.showDialog(accesibilities);
      }
    };
  }
  
}
