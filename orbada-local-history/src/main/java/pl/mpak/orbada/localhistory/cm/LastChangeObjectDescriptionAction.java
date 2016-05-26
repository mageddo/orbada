/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.core.SchemaObjects;
import pl.mpak.orbada.localhistory.db.OlhObjectRecord;
import pl.mpak.orbada.localhistory.services.LocalHistoryDatabaseService;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class LastChangeObjectDescriptionAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager("local-history");

  private AbsOrbadaSyntaxTextArea syntaxTextArea;
  
  public LastChangeObjectDescriptionAction(AbsOrbadaSyntaxTextArea syntaxTextArea) {
    super();
    setText(stringManager.getString("LastChangeObjectDescriptionAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/comment_edit.gif"));
    setActionCommandKey("LastChangeObjectDescriptionAction");
    addActionListener(createActionListener());
    this.syntaxTextArea = syntaxTextArea;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SchemaObjects so = LocalHistoryDatabaseService.getSchemaObjects(syntaxTextArea.getDatabase());
        if (so != null) {
          OlhObjectRecord olho = so.getObject(syntaxTextArea.getSchemaName(), syntaxTextArea.getObjectType(), syntaxTextArea.getObjectName());
          if (olho != null) {
            String description = JOptionPane.showInputDialog(stringManager.getString("LastChangeObjectDescriptionAction-change-description"), olho.getDescription());
            if (description != null) {
              olho.setDescription(description);
              try {
                olho.applyUpdate();
              } catch (Exception ex) {
                ExceptionUtil.processException(ex);
                MessageBox.show(syntaxTextArea, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
              }
            }
          }
        }
      }
    };
  }

}
