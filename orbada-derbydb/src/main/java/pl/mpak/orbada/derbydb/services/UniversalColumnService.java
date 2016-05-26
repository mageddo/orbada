/*
 * UniversalTableColumnProvider.java
 * 
 * Created on 2007-10-30, 21:30:16
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.services;

import pl.mpak.orbada.derbydb.*;
import pl.mpak.orbada.derbydb.tables.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.derbydb.procedures.ParametersPanel;
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
public class UniversalColumnService extends UniversalActionProvider {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  public UniversalColumnService() {
    super();
    setText(stringManager.getString("UniversalColumnService-text"));
    setShortCut(KeyEvent.VK_F6, KeyEvent.CTRL_MASK);
    setActionCommandKey("cmUniversalColumnService");
    addActionListener(createActionListener());
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType.equals(database.getDriverType());
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
    return stringManager.getString("UniversalColumnService-description");
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          String[] words = accessibilities.getSyntaxEditor().getWordsAt(accessibilities.getSyntaxEditor().getCaretPosition());
          String object = null;
          String schema = accessibilities.getViewAccesibilities().getDatabase().getUserName().toUpperCase();
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
              query.setSqlText(DerbyDbSql.getObjectType());
              query.paramByName("schemaname").setString(schema);
              query.paramByName("objectname").setString(object);
              query.open();
              if (!query.eof()) {
                String type = query.fieldByName("objecttype").getString();
                if ("PROCEDURE".equalsIgnoreCase(type) || "FUNCTION".equalsIgnoreCase(type)) {
                  setText("Parametry");
                  ParametersPanel panel = new ParametersPanel(accessibilities.getViewAccesibilities());
                  panel.refresh(null, schema, object);
                  accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
                }
                else if ("TABLE".equalsIgnoreCase(type) || "VIEW".equalsIgnoreCase(type)) {
                  setText("Kolumny");
                  TableColumnsPanel panel =  new TableColumnsPanel(accessibilities.getViewAccesibilities(), "VIEW".equalsIgnoreCase(type));
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
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType;
  }

}
