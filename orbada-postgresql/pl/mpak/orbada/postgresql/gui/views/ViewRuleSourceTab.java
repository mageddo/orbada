/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.views;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.util.SourceCreator;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropSourceTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.crf.YesNoCellRendererFilter;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class ViewRuleSourceTab extends UniversalPropSourceTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public ViewRuleSourceTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-view-rules";
  }

  @Override
  public String getObjectColumnName() {
    return "object_name";
  }

  @Override
  public String getSchemaColumnName() {
    return "schema_name";
  }

  @Override
  public String getPropColumnName() {
    return "rule_name";
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return new QueryTableColumn[] {
      new QueryTableColumn("rule_name", stringManager.getString("rule-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)),
      new QueryTableColumn("enable_mode", stringManager.getString("rule-enable-mode"), 130, new QueryTableCellRenderer(new QueryTableCellRendererFilter() {
        @Override
        public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
            if (StringUtil.nvl((String)value, "").equals("DISABLED")) {
              ((JLabel)renderer).setForeground(Color.DARK_GRAY);
            }
            else if (StringUtil.nvl((String)value, "").equals("ENABLED")) {
              ((JLabel)renderer).setForeground(SwingUtil.Color.GREEN);
            }
        }
      })),
      new QueryTableColumn("rule_type", stringManager.getString("rule-type"), 130),
      new QueryTableColumn("is_instead", stringManager.getString("rule-is-instead"), 30, new QueryTableCellRenderer(new YesNoCellRendererFilter(null, null))),
      new QueryTableColumn("description", stringManager.getString("description"), 250)
    };
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getRuleList(filter.getSqlText());
  }

  @Override
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return new SqlFilterDefComponent[] {
      new SqlFilterDefComponent("rule_name", stringManager.getString("rule-name"), (String[])null),
      new SqlFilterDefComponent("enable_mode", stringManager.getString("rule-enable-mode"), new String[] {"'ENABLED'", "'DISABLED'", "'REPLICA'", "'ALWAYS'"}),
      new SqlFilterDefComponent("rule_type", stringManager.getString("rule-type"), (String[])null),
      new SqlFilterDefComponent("is_instead = 'Y'", stringManager.getString("rule-is-instead")),
      new SqlFilterDefComponent("description", stringManager.getString("description"), (String[])null)
    };
  }

  @Override
  public void updateBody(OrbadaSyntaxTextArea textArea) {
    new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "RULE", currentPropName, currentObjectName);
  }

  @Override
  public String getTitle() {
    return stringManager.getString("rules");
  }
  
}
