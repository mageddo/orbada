/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.firebird.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.freezing.FreezeFactory;
import pl.mpak.orbada.firebird.gui.freezing.FreezeViewService;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FindObjectFromEditorAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  private Database database;
  private SyntaxEditor syntaxEditor;
  
  public FindObjectFromEditorAction() {
    super();
    setText(stringManager.getString("FindObjectFromEditorAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_editor.gif"));
    setTooltip(stringManager.getString("FindObjectFromEditorAction-hint"));
    setShortCut(KeyEvent.VK_F7, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    setActionCommandKey("FindObjectFromEditorAction");
    addActionListener(createActionListener());
  }
  
  public SyntaxEditor getSyntaxEditor() {
    return syntaxEditor;
  }

  public void setSyntaxEditor(SyntaxEditor syntaxEditor) {
    this.syntaxEditor = syntaxEditor;
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  private boolean resolveObject(String type, String object) {
    if ("TABLE".equalsIgnoreCase(type) || "VIEW".equalsIgnoreCase(type) || 
        "PROCEDURE".equalsIgnoreCase(type) || "FUNCTION".equalsIgnoreCase(type)) {
      FreezeViewService service = new FreezeFactory().createInstance(type, null, object);
      IPerspectiveAccesibilities accessibilities = Application.get().getMainFrame().getActivePerspective().getPerspectiveAccesibilities();
      if (service != null && accessibilities != null) {
        accessibilities.createView(service);
      }
      return true;
    }
    return false;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String[] words = syntaxEditor.getWordsAt(syntaxEditor.getCaretPosition());
          String object = null;
          if (words != null && words.length > 0) {
            if (words.length >= 1) {
              object = SQLUtil.normalizeSqlName(words[0]);
            }
            boolean found = false;
            Query query = database.createQuery();
            try {
              query.setSqlText(Sql.getObjectTypes());
              query.paramByName("object_name").setString(object);
              query.open();
              if (!query.eof()) {
                found = resolveObject(query.fieldByName("object_type").getString(), object);
              }
            }
            catch (Exception ex) {
              ExceptionUtil.processException(ex);
            }
            finally {
              query.close();
            }
          }
        } catch (BadLocationException ex) {
        }
      }
    };
  }
  
}
