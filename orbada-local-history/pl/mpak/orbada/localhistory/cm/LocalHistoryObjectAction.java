/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import orbada.gui.PerspectivePanel;
import orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.core.SchemaObjects;
import pl.mpak.orbada.localhistory.db.OlhObjectRecord;
import pl.mpak.orbada.localhistory.gui.DiffViewService;
import pl.mpak.orbada.localhistory.services.LocalHistoryDatabaseService;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class LocalHistoryObjectAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLocalHistoryPlugin.class);

  private AbsOrbadaSyntaxTextArea syntaxTextArea;
  
  public LocalHistoryObjectAction(AbsOrbadaSyntaxTextArea syntaxTextArea) {
    super();
    setText(stringManager.getString("LocalHistoryObjectAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/diff.gif"));
    setActionCommandKey("LocalHistoryObjectAction");
    addActionListener(createActionListener());
    this.syntaxTextArea = syntaxTextArea;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SchemaObjects so = LocalHistoryDatabaseService.getSchemaObjects(syntaxTextArea.getDatabase());
        if (so != null) {
          PerspectivePanel panel = (PerspectivePanel)getParent(e, PerspectivePanel.class);
          OlhObjectRecord olho = so.getObject(syntaxTextArea.getSchemaName(), syntaxTextArea.getObjectType(), syntaxTextArea.getObjectName());
          if (panel != null && olho != null) {
            panel.getPerspectiveAccesibilities().createView(new DiffViewService(olho, syntaxTextArea));
          }
        }
      }
    };
  }

}
