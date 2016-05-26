/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.gui.freezing.FreezeFactory;
import pl.mpak.orbada.mysql.gui.freezing.FreezeViewService;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class FindObjectFromEditorAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

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

  private boolean resolveObject(String type, String schema, String object) {
    if (StringUtil.anyOfString(type, new String[] {"TABLE", "VIEW", "PROCEDURE", "FUNCTION", "TRIGGER"}, true) != -1) {
      FreezeViewService service = new FreezeFactory().createInstance(type, schema, object);
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
          String schema = null; 
          if (words != null && words.length > 0) {
            if (words.length == 1) {
              object = SQLUtil.normalizeSqlName(words[0]);
            }
            else {
              schema = SQLUtil.normalizeSqlName(words[0]);
              object = SQLUtil.normalizeSqlName(words[1]);
            }
            boolean found = false;
            Query query = database.createQuery();
            try {
              query.setSqlText(Sql.getObjectsType(null));
              query.paramByName("schema_name").setString(schema);
              query.paramByName("object_name").setString(object);
              query.open();
              if (!query.eof()) {
                schema = query.fieldByName("object_schema").getString();
                object = query.fieldByName("object_name").getString();
                found = resolveObject(query.fieldByName("object_type").getString(), schema, object);
              }
              if (!found) {
                object = schema;
                schema = null; 
                query.close();
                query.paramByName("schema_name").setString(schema);
                query.paramByName("object_name").setString(object);
                query.open();
                if (!query.eof()) {
                  schema = query.fieldByName("object_schema").getString();
                  object = query.fieldByName("object_name").getString();
                  found = resolveObject(query.fieldByName("object_type").getString(), schema, object);
                }
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
