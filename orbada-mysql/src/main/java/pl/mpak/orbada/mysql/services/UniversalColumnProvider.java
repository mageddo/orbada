package pl.mpak.orbada.mysql.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.gui.functions.FunctionParametersPanel;
import pl.mpak.orbada.mysql.gui.procedures.ProcedureParametersPanel;
import pl.mpak.orbada.mysql.gui.tables.TableColumnsPanel;
import pl.mpak.orbada.mysql.gui.views.ViewColumnsPanel;
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
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

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
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
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
  
  public Database getDatabase() {
    return accessibilities.getViewAccesibilities().getDatabase();
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      private void refreshPanel(final String schema, final String object, final ITabObjectInfo panel) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            panel.refresh(null, schema, object);
          }
        });
      }
      private void openPanel(final String schema, final String object, final String type) {
        if ("TABLE".equalsIgnoreCase(type)) {
          TableColumnsPanel panel =  new TableColumnsPanel(accessibilities.getViewAccesibilities());
          refreshPanel(schema, object, panel);
          accessibilities.addResultTab(stringManager.getString("columns") +" " +getDatabase().quoteName(schema, object), panel);
        }
        else if ("VIEW".equalsIgnoreCase(type)) {
          ViewColumnsPanel panel =  new ViewColumnsPanel(accessibilities.getViewAccesibilities());
          refreshPanel(schema, object, panel);
          accessibilities.addResultTab(stringManager.getString("columns") +" " +getDatabase().quoteName(schema, object), panel);
        }
        else if ("PROCEDURE".equalsIgnoreCase(type)) {
          ProcedureParametersPanel panel =  new ProcedureParametersPanel(accessibilities.getViewAccesibilities());
          refreshPanel(schema, object, panel);
          accessibilities.addResultTab(stringManager.getString("parameters") +" " +getDatabase().quoteName(schema, object), panel);
        }
        else if ("FUNCTION".equalsIgnoreCase(type)) {
          FunctionParametersPanel panel =  new FunctionParametersPanel(accessibilities.getViewAccesibilities());
          refreshPanel(schema, object, panel);
          accessibilities.addResultTab(stringManager.getString("parameters") +" " +getDatabase().quoteName(schema, object), panel);
        }
      }
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String[] words = accessibilities.getSyntaxEditor().getWordsAt(accessibilities.getSyntaxEditor().getCaretPosition());
          String object = null;
          String schema = MySQLDbInfoProvider.getCurrentDatabase(getDatabase());
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
              query.setSqlText(Sql.getObjectsType());
              query.paramByName("SCHEMA_NAME").setString(schema);
              query.paramByName("OBJECT_NAME").setString(object);
              query.open();
              if (!query.eof()) {
                String type = query.fieldByName("object_type").getString();
                schema = query.fieldByName("object_schema").getString();
                object = query.fieldByName("object_name").getString();
                openPanel(schema, object, type);
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
    return OrbadaMySQLPlugin.driverType;
  }

}
