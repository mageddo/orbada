package pl.mpak.orbada.firebird.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.Sql;
import pl.mpak.orbada.firebird.gui.functions.FunctionArgumentsPanel;
import pl.mpak.orbada.firebird.gui.procedures.ProcedureArgumentsPanel;
import pl.mpak.orbada.firebird.gui.tables.TableColumnsPanel;
import pl.mpak.orbada.firebird.gui.views.ViewColumnsPanel;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
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
public class UniversalColumnProvider extends UniversalActionProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    setText(stringManager.getString("columns"));
    setShortCut(KeyEvent.VK_F6, KeyEvent.CTRL_MASK);
    setTooltip(stringManager.getString("UniversalColumnProvider-hint"));
    setActionCommandKey("UniversalColumnProvider");
    addActionListener(createActionListener());
    return OrbadaFirebirdPlugin.firebirdDriverType.equals(database.getDriverType());
  }

  public boolean addToolButton() {
    return false;
  }

  public boolean addMenuItem() {
    return true;
  }

  public boolean addToEditor() {
    return true;
  }

  public String getDescription() {
    return "Universal Firebird Column or Param List Provider";
  }
  
  private boolean resolveObject(String type, String object) {
    if ("TABLE".equalsIgnoreCase(type)) {
      setText(stringManager.getString("columns"));
      TableColumnsPanel panel =  new TableColumnsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, null, object);
      accessibilities.addResultTab(getText() +" \"" +object +"\"", panel);
      return true;
    }
    else if ("VIEW".equalsIgnoreCase(type)) {
      setText(stringManager.getString("columns"));
      ViewColumnsPanel panel =  new ViewColumnsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, null, object);
      accessibilities.addResultTab(getText() +" \"" +object +"\"", panel);
      return true;
    }
    else if ("PROCEDURE".equalsIgnoreCase(type)) {
      setText(stringManager.getString("parameters"));
      ProcedureArgumentsPanel panel =  new ProcedureArgumentsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, null, object);
      accessibilities.addResultTab(getText() +" \"" +object +"\"", panel);
      return true;
    }
    else if ("FUNCTION".equalsIgnoreCase(type)) {
      setText(stringManager.getString("parameters"));
      FunctionArgumentsPanel panel =  new FunctionArgumentsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, null, object);
      accessibilities.addResultTab(getText() +" \"" +object +"\"", panel);
      return true;
    }
    return false;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String[] words = accessibilities.getSyntaxEditor().getWordsAt(accessibilities.getSyntaxEditor().getCaretPosition());
          String object = null;
          if (words != null && words.length > 0) {
            if (words.length >= 1) {
              object = SQLUtil.normalizeSqlName(words[0]);
            }
            boolean found = false;
            Query query = accessibilities.getViewAccesibilities().getDatabase().createQuery();
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

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }

}
