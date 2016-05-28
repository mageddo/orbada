/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.tables;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.ColorListCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class TableListView extends UniversalViewTabs {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public TableListView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public Component getTabbedPane() {
    return new TableTabbedPane(accesibilities);
  }

  @Override
  public String getCurrentSchemaName() {
    return PostgreSQLDbInfoProvider.getConnectedSchema(getDatabase());
  }

  @Override
  public String getPanelName() {
    return "postgresql-tables";
  }

  @Override
  public String getObjectColumnName() {
    return "table_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getDescriptionColumnName() {
    return "description";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("schema_name", stringManager.getString("schema-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("table_name", stringManager.getString("table-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("owner_name", stringManager.getString("owner-name"), 130),
      new QueryTableColumn("table_space", stringManager.getString("table-space"), 130),
      new QueryTableColumn("accessible", stringManager.getString("accessible"), 100, new QueryTableCellRenderer(new ColorListCellRendererFilter(new String[] {"NO"}, new Color[] {Color.RED}))),
      new QueryTableColumn("description", stringManager.getString("description"), 250),
      new QueryTableColumn("inheritance", stringManager.getString("inheritance"), 100, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        @Override
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
          if (value != null && !"".equals(value.toString())) {
            ((JLabel)renderer).setText(stringManager.getString("table-" +value.toString() +"-value"));
          }
        }
      }))
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getTableList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("table_name", stringManager.getString("table-name"), (String[])null),
      new SqlFilterDefComponent("owner_name", stringManager.getString("owner-name"), (String[])null),
      new SqlFilterDefComponent("table_space", stringManager.getString("table-space"), (String[])null),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null),
      new SqlFilterDefComponent("accessible", stringManager.getString("accessible"), (String[])null),
      new SqlFilterDefComponent("inheritance", stringManager.getString("inheritance"), new String[] {"inherits", "inherited"}),
      new SqlFilterDefComponent("coalesce(inheritance, '-') <> 'inherits'", stringManager.getString("skip-inherit"))
    };
  }

  @Override
  public String[] getSchemaList() {
    return PostgreSQLDbInfoProvider.instance.getSchemas(getDatabase());
  }
  
}
