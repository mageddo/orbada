package pl.mpak.orbada.sqlite.gui.tables;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.Sql;
import pl.mpak.orbada.sqlite.util.SourceCreator;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropSourceTab;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableTriggersPanel extends UniversalPropSourceTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public TableTriggersPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public void updateBody(OrbadaSyntaxTextArea textArea) {
    try {
      new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "TRIGGER", currentPropName);
    }
    catch (Exception ex) {
      textArea.setDatabaseObject(null, null, null, "");
    }
  }
  
  public String getTitle() {
    return stringManager.getString("TableTriggersPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "sqlite-table-triggers";
  }

  @Override
  public String getObjectColumnName() {
    return null;
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getPropColumnName() {
    return "name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("name", stringManager.getString("trigger-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD))
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableTriggerList(currentSchemaName, currentObjectName);
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return null;
  }

}
