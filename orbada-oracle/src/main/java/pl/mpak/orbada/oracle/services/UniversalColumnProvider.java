package pl.mpak.orbada.oracle.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.functions.FunctionArgumentsPanel;
import pl.mpak.orbada.oracle.gui.javas.JavaMethodsPanel;
import pl.mpak.orbada.oracle.gui.mviews.MViewColumnsPanel;
import pl.mpak.orbada.oracle.gui.packages.PackageMethodsPanel;
import pl.mpak.orbada.oracle.gui.procedures.ProcedureArgumentsPanel;
import pl.mpak.orbada.oracle.gui.tables.TableColumnsPanel;
import pl.mpak.orbada.oracle.gui.types.TypeMethodsPanel;
import pl.mpak.orbada.oracle.gui.views.ViewColumnsPanel;
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
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    if (OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType())) {
      setText(stringManager.getString("UniversalColumnProvider-text"));
      setShortCut(KeyEvent.VK_F6, KeyEvent.CTRL_MASK);
      setTooltip(stringManager.getString("UniversalColumnProvider-hint"));
      setActionCommandKey("UniversalColumnProvider");
      addActionListener(createActionListener());
      return true;
    }
    return false;
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
  
  private boolean resolveObject(String type, String schema, String object) {
    if ("TABLE".equalsIgnoreCase(type)) {
      setText(stringManager.getString("columns"));
      TableColumnsPanel panel =  new TableColumnsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
      return true;
    }
    else if ("VIEW".equalsIgnoreCase(type)) {
      setText(stringManager.getString("columns"));
      ViewColumnsPanel panel =  new ViewColumnsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
      return true;
    }
    else if ("MATERIALIZED VIEW".equalsIgnoreCase(type)) {
      setText(stringManager.getString("columns"));
      MViewColumnsPanel panel =  new MViewColumnsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
      return true;
    }
    else if ("PROCEDURE".equalsIgnoreCase(type)) {
      setText(stringManager.getString("UniversalColumnProvider-parameters"));
      ProcedureArgumentsPanel panel =  new ProcedureArgumentsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
      return true;
    }
    else if ("FUNCTION".equalsIgnoreCase(type)) {
      setText(stringManager.getString("UniversalColumnProvider-parameters"));
      FunctionArgumentsPanel panel =  new FunctionArgumentsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
      return true;
    }
    else if ("PACKAGE".equalsIgnoreCase(type)) {
      setText(stringManager.getString("package-functions"));
      PackageMethodsPanel panel =  new PackageMethodsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
      return true;
    }
    else if ("JAVA CLASS".equalsIgnoreCase(type)) {
      setText(stringManager.getString("class-methods"));
      JavaMethodsPanel panel =  new JavaMethodsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
      return true;
    }
    else if ("TYPE".equalsIgnoreCase(type)) {
      setText(stringManager.getString("type-methods"));
      TypeMethodsPanel panel =  new TypeMethodsPanel(accessibilities.getViewAccesibilities());
      panel.refresh(null, schema, object);
      accessibilities.addResultTab(getText() +" \"" +schema +"\".\"" +object +"\"", panel);
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
          String schema = null; //OracleDbInfoProvider.getCurrentSchema(accessibilities.getViewAccesibilities().getDatabase());
          if (words != null && words.length > 0) {
            if (words.length == 1) {
              object = SQLUtil.normalizeSqlName(words[0]);
            }
            else {
              schema = SQLUtil.normalizeSqlName(words[0]);
              object = SQLUtil.normalizeSqlName(words[1]);
            }
            boolean found = false;
            Query query = accessibilities.getViewAccesibilities().getDatabase().createQuery();
            try {
              query.setSqlText(Sql.resolveObject());
              query.paramByName("schema_name").setString(schema);
              query.paramByName("object_name").setString(object);
              query.open();
              if (!query.eof()) {
                schema = query.fieldByName("schema_name").getString();
                object = query.fieldByName("object_name").getString();
                found = resolveObject(query.fieldByName("object_type").getString(), schema, object);
              }
              if (!found) {
                object = schema;
                schema = null; //OracleDbInfoProvider.getCurrentSchema(accessibilities.getViewAccesibilities().getDatabase());
                query.close();
                query.paramByName("schema_name").setString(schema);
                query.paramByName("object_name").setString(object);
                query.open();
                if (!query.eof()) {
                  schema = query.fieldByName("schema_name").getString();
                  object = query.fieldByName("object_name").getString();
                  String type = query.fieldByName("object_type").getString();
                  found = resolveObject(type, schema, object);
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
    return OrbadaOraclePlugin.oracleDriverType;
  }

}
