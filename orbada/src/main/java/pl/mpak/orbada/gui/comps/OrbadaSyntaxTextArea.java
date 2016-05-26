package orbada.gui.comps;

import orbada.core.Application;
import orbada.gui.comps.cm.RefreshDatabaseInfoAction;
import orbada.gui.comps.cm.SqlUpperCaseAction;
import orbada.gui.comps.cm.SqlLowerCaseAction;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class OrbadaSyntaxTextArea extends AbsOrbadaSyntaxTextArea {
  
  private OrbadaSQLSyntaxDocument syntaxDocument;
  
  public OrbadaSyntaxTextArea() {
    super();
    init();
  }
  
  private void init() {
    setDocument(syntaxDocument = new OrbadaSQLSyntaxDocument());
    getEditorArea().getComponentPopupMenu().add(new SqlLowerCaseAction());
    getEditorArea().getComponentPopupMenu().add(new SqlUpperCaseAction());
    getEditorArea().getComponentPopupMenu().addSeparator();
    getEditorArea().getComponentPopupMenu().add(new RefreshDatabaseInfoAction());
    
    if (Application.get() != null) {
      OrbadaSQLSyntaxDocument.loadSettings(syntaxDocument);
      getEditorArea().setBackground(Application.get().getSettings().getValue("syntax-editor-background-color", getEditorArea().getBackground()));
      getEditorArea().setFont(Application.get().getSettings().getValue("syntax-editor-font", getEditorArea().getFont()));
    }
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        syntaxDocument.externalInitKeywords();
      }
    });
  }
  
  @Override
  public void setDatabase(Database database) {
    if (this.database != database) {
      syntaxDocument.setDatabase(database);
    }
    super.setDatabase(database);
  }

}
