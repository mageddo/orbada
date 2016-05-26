/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.cm;

import orbada.gui.IRootTabObjectInfo;
import orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.gui.wizards.CommentUniversalWizard;
import pl.mpak.orbada.universal.cm.UniversalViewTableAction;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CommentViewAction extends UniversalViewTableAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public CommentViewAction() {
    super();
    setText(stringManager.getString("comment-edit"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/comment_edit.gif"));
    setActionCommandKey("CommentViewAction");
  }

  @Override
  public boolean isToolButton() {
    return false;
  }

  @Override
  protected boolean doAction(ViewTable vt, IRootTabObjectInfo ip) throws Exception {
    return SqlCodeWizardDialog.show(
      new CommentUniversalWizard(
        vt.getQuery().getDatabase(),
        "view-dd", "VIEW", 
        vt.getQuery().fieldByName("full_object_name").getString(),
        vt.getQuery().fieldByName("description").getString()
      ), true) != null;
  }

}
