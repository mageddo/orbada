/*
 * CreateViewAction.java
 *
 * Created on 2007-10-18, 21:49:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.gui.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import orbada.core.Application;
import pl.mpak.orbada.db.Perspective;
import pl.mpak.orbada.db.Schema;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class CreatePerspectiveAction extends Action {
  
  private Perspective perspective;
  private Database database;
  private Schema schema;
  
  public CreatePerspectiveAction(Perspective perspective, Database database, Schema schema) throws Exception {
    super();
    this.database = database;
    this.schema = schema;
    this.perspective = perspective;
    setTooltip(perspective.getDescription());
    setText(perspective.getDisplayName(database));
    setActionCommandKey("cmCreatePerspective");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Application.get().getMainFrame().addPerspective(database, schema, perspective);
      }
    };
  }
  
}
