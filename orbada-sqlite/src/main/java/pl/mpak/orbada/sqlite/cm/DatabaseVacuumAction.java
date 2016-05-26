/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DatabaseVacuumAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  private Database database;

  public DatabaseVacuumAction(Database database) {
    this.database = database;
    setText(stringManager.getString("DatabaseVacuumAction-text"));
    setTooltip(stringManager.getString("DatabaseVacuumAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/cleaning.gif"));
    setActionCommandKey("VacuumAction");
    addActionListener(createActionListener());
  }
  
  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("execute"), stringManager.getString("DatabaseVacuumAction-go-q-info"), ModalResult.YESNO, MessageBox.INFORMATION) == ModalResult.YES) {
          try {
            database.executeCommand("VACUUM");
          } catch (Exception ex) {
            MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
          }
        }
      }
    };
  }

}
