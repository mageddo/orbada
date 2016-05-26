/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.wizards.CallObjectWizard;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
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
public class CallObjectFromEditorAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private SyntaxEditor syntaxEditor;
  
  public CallObjectFromEditorAction() {
    super();
    setText(stringManager.getString("CallObjectFromEditorAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/call_editor.gif"));
    setTooltip(stringManager.getString("CallObjectFromEditorAction-hint"));
    setShortCut(KeyEvent.VK_F8, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    setActionCommandKey("CallObjectFromEditorAction");
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

  private void callFound(final String schemaName, final String objectName) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        SqlCodeWizardDialog.show(new CallObjectWizard(database, schemaName, objectName, null), true);
      }
    });
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String[] words = syntaxEditor.getWordsAt(syntaxEditor.getCaretPosition());
          String packageName = null;
          String object = null;
          String schema = OracleDbInfoProvider.getCurrentSchema(database);
          if (words != null && words.length > 0) {
            if (words.length == 1) {
              object = SQLUtil.normalizeSqlName(words[0]);
            }
            else if (words.length == 3) {
              schema = SQLUtil.normalizeSqlName(words[0]);
              packageName = SQLUtil.normalizeSqlName(words[1]);
              object = SQLUtil.normalizeSqlName(words[2]);
            }
            else {
              packageName = SQLUtil.normalizeSqlName(words[0]);
              object = SQLUtil.normalizeSqlName(words[1]);
            }
            Query query = database.createQuery();
            try {
              query.setSqlText(Sql.getObjectForCall());
              query.paramByName("schema_name").setString(schema);
              query.paramByName("object_name").setString((packageName != null ? packageName +"." : "") +object);
              query.open();
              if (!query.eof()) {
                callFound(schema, (packageName != null ? packageName +"." : "") +object);
              }
              else {
                query.close();
                query.paramByName("schema_name").setString(packageName);
                query.paramByName("object_name").setString(object);
                query.open();
                if (!query.eof()) {
                  callFound(packageName, object);
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
