package pl.mpak.orbada.oracle.dba.gui.admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalViewTabs;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.SearchEvent;
import pl.mpak.sky.gui.swing.comp.SearchListener;
import pl.mpak.sky.gui.swing.comp.SearchPanel;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class ParametersPanelView extends UniversalViewTabs {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);
  
  private SearchPanel searchPanel;

  public ParametersPanelView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  @Override
  protected void init() {
    searchPanel = new SearchPanel();
    searchPanel.setTable(getTable());
    searchPanel.addSearchListener(new SearchListener() {
      public void search(SearchEvent e) {
        refresh();
      }
    });
    getToolBar().add(searchPanel);
    super.init();
  }
  
  @Override
  public Component getTabbedPane() {
    return null;
  }

  @Override
  public String getCurrentSchemaName() {
    return null;
  }

  @Override
  public String getPanelName() {
    return "oracle-database-parameters";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return "name";
  }

  @Override
  public String getDescriptionColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    ArrayList<QueryTableColumn> columns = new ArrayList<QueryTableColumn>();
    QueryTableCellRenderer renderer = new QueryTableCellRenderer() {
      HashMap map = new HashMap();
      {
        map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
      }
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        QueryTableColumn tc = (QueryTableColumn)table.getColumnModel().getColumn(column);
        if ("name".equalsIgnoreCase(tc.getFieldName())) {
          label.setFont(label.getFont().deriveFont(Font.BOLD));
        }
        else if ("display_type".equalsIgnoreCase(tc.getFieldName())) {
          label.setForeground(SwingUtil.Color.GREEN);
        }
        try {
          if (StringUtil.toBoolean(getQuery().fieldByName("isdeprecated").getString())) {
            label.setFont(label.getFont().deriveFont(map));
          }
          if (!isSelected) {
            if (!"false".equalsIgnoreCase(getQuery().fieldByName("ismodified").getString())) {
              label.setBackground(new Color(200, 200, 250));
            }
          }
        } catch (Exception ex) {
          label.setText(label.getText() +" [" +ex.getMessage() +"]");
        }
        return label;
      }
    };
    if (StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("rac-detect", "false"))) {
      columns.add(new QueryTableColumn("inst_id", stringManager.getString("instance"), 30, renderer));
    }
    columns.add(new QueryTableColumn("num", stringManager.getString("num"), 50, renderer));
    columns.add(new QueryTableColumn("name", stringManager.getString("name"), 200, renderer));
    columns.add(new QueryTableColumn("display_type", stringManager.getString("display-param-type"), 70, renderer));
    columns.add(new QueryTableColumn("display_value", stringManager.getString("display-param-value"), 250, renderer));
    columns.add(new QueryTableColumn("isdefault", stringManager.getString("is-default"), 60, renderer));
    columns.add(new QueryTableColumn("ismodified", stringManager.getString("is-modified"), 60, renderer));
    if (StringUtil.toBoolean(getDatabase().getUserProperties().getProperty("rac-detect", "false"))) {
      columns.add(new QueryTableColumn("isinstance_modifiable", stringManager.getString("is-instance-modifable"), 60, renderer));
    }
    columns.add(new QueryTableColumn("description", stringManager.getString("description"), 300, renderer));
    columns.add(new QueryTableColumn("update_comment", stringManager.getString("update-comment"), 200, renderer));
    return (QueryTableColumn[])columns.toArray(new QueryTableColumn[columns.size()]);
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getParameterList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("name", stringManager.getString("name"), (String[])null),
      new SqlFilterDefComponent("display_type", stringManager.getString("display-param-type"), new String[] {"'BOOLEAN'", "'STRING'", "'INTEGER'", "'PARAMETER FILE'", "'RESERVED'", "'BIG INTEGER'"}),
      new SqlFilterDefComponent("display_value", stringManager.getString("display-param-value"), (String[])null),
      new SqlFilterDefComponent("ismodified <> 'FALSE'", stringManager.getString("are-modified")),
      new SqlFilterDefComponent("isdeprecated <> 'TRUE'", stringManager.getString("are-deprecated"))
    };
  }

  @Override
  public String[] getSchemaList() {
    return null;
  }
  
  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("user_text").setString(searchPanel.getText());
  }
  
}
