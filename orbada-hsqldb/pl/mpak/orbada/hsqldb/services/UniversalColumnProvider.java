/*
 * UniversalTableColumnProvider.java
 * 
 * Created on 2007-10-30, 21:30:16
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.Sql;
import pl.mpak.orbada.hsqldb.gui.aliases.ProcedureParametersPanel;
import pl.mpak.orbada.hsqldb.gui.tables.TableColumnsPanel;
import pl.mpak.orbada.hsqldb.gui.views.ViewColumnsPanel;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalColumnProvider extends UniversalActionProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaHSqlDbPlugin.class);

  public UniversalColumnProvider() {
    super();
    setText(stringManager.getString("UniversalColumnProvider-text"));
    setShortCut(KeyEvent.VK_F6, KeyEvent.CTRL_MASK);
    setActionCommandKey("UniversalColumnProvider");
    addActionListener(createActionListener());
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaHSqlDbPlugin.hsqlDbDriverType.equals(database.getDriverType());
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
    return stringManager.getString("UniversalColumnProvider-description");
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String[] words = accessibilities.getSyntaxEditor().getWordsAt(accessibilities.getSyntaxEditor().getCaretPosition());
          String object = null;
          String schema = HSqlDbInfoProvider.getCurrentSchema(accessibilities.getViewAccesibilities().getDatabase());
          if (words != null && words.length > 0) {
            if (words.length == 1) {
              object = words[0];
            }
            else {
              schema = words[0];
              object = words[1];
            }
            Query query = accessibilities.getViewAccesibilities().getDatabase().createQuery();
            try {
              query.setSqlText(Sql.getObjectsType(HSqlDbInfoProvider.getVersionTest(accessibilities.getViewAccesibilities().getDatabase())));
              query.paramByName("schema_name").setString(schema);
              query.paramByName("object_name").setString(object);
              query.open();
              if (!query.eof()) {
                String type = query.fieldByName("object_type").getString();
                if ("TABLE".equalsIgnoreCase(type)) {
                  setText(stringManager.getString("columns"));
                  TableColumnsPanel panel =  new TableColumnsPanel(accessibilities.getViewAccesibilities());
                  panel.refresh(null, schema, object);
                  accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
                }
                else if ("VIEW".equalsIgnoreCase(type)) {
                  setText(stringManager.getString("columns"));
                  ViewColumnsPanel panel =  new ViewColumnsPanel(accessibilities.getViewAccesibilities());
                  panel.refresh(null, schema, object);
                  accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
                }
                else if ("PROCEDURE".equalsIgnoreCase(type) || "FUNCTION".equalsIgnoreCase(type)) {
                  setText(stringManager.getString("parameters"));
                  ProcedureParametersPanel panel =  new ProcedureParametersPanel(accessibilities.getViewAccesibilities());
                  panel.refresh(null, schema, object);
                  accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
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

  public String getGroupName() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType;
  }

}
