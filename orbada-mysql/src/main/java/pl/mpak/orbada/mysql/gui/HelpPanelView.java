package pl.mpak.orbada.mysql.gui;

import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.HtmlUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class HelpPanelView extends javax.swing.JPanel implements IRootTabObjectInfo {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  private IViewAccesibilities accesibilities;
  private ISettings settings;
  private boolean viewClosing = false;
  
  public HelpPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    settings = accesibilities.getApplication().getSettings(getDatabase().getUserProperties().getProperty("schemaId"), "mysql-help-panel");
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        split.setDividerLocation(settings.getValue("split", (long)(split.getWidth() *0.3f)).intValue());
        textSearch.requestFocusInWindow();
      }
    });

    textSearch.setModel(new DefaultComboBoxModel());
    SwingUtil.addAction((JTextField)textSearch.getEditor().getEditorComponent(), cmSearch);
    ((JTextField)textSearch.getEditor().getEditorComponent()).getComponentPopupMenu().addSeparator();
    ((JTextField)textSearch.getEditor().getEditorComponent()).getComponentPopupMenu().add(cmSearch);
    textHelp.setFont(new Font(Font.MONOSPACED, Font.PLAIN, textHelp.getFont().getSize()));
    ((JTextField)textSearch.getEditor().getEditorComponent()).addKeyListener(new TableRowChangeKeyListener(table));

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        fillTextHelp();
      }
    });

    table.getQuery().setDatabase(getDatabase());
    try {
      table.addColumn(new QueryTableColumn("name", stringManager.getString("help-topic"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      table.addColumn(new QueryTableColumn("category_name", stringManager.getString("category-name"), 250));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    new ComponentActionsAction(getDatabase(), table, buttonActions, menuActions, "mysql-help-actions");
  }

  private void fillTextHelp() {
    if (table.getSelectedRow() >= 0) {
      try {
        table.getQuery().getRecord(table.getSelectedRow());
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<style>");
        sb.append("  body { font-family: " +textHelp.getFont().getFontName() +", Tahoma, Arial, serif; font-size: 9px; padding: 5px;}");
        sb.append("</style>");
        sb.append("<head></head>");
        sb.append("<body>");
        sb.append("<h1>" +table.getQuery().fieldByName("name").getString() +"</h1>");
        sb.append(stringManager.getString("category-name-dd") +" <b>" +table.getQuery().fieldByName("category_name").getString() +"</b>");
        sb.append("<br>" +stringManager.getString("see-also-dd") +" <a href=\"" +table.getQuery().fieldByName("url").getString() +"\">" +table.getQuery().fieldByName("url").getString() +"</a>");
        sb.append("<hr>");
        String desc = HtmlUtil.prepareText(table.getQuery().fieldByName("description").getString());
        desc = desc.replace("\n", "<br>");
        desc = desc.replace(" ", "&nbsp;");
        sb.append(desc);
        sb.append("</body>");
        sb.append("</html>");
        textHelp.setText(sb.toString());
      } catch (Exception ex) {
        textHelp.setText(ex.getMessage());
        ExceptionUtil.processException(ex);
      }
    }
    else {
      textHelp.setText("");
    }
  }
  
  private String getFilterText() {
    return textSearch.getText();
  }

  private void setFilterText(String text) {
    textSearch.setText(text);
  }

  private void addTextToList(String text) {
    DefaultComboBoxModel model = (DefaultComboBoxModel)textSearch.getModel();
    int index = model.getIndexOf(text);
    if (index != -1) {
      model.removeElementAt(index);
    }
    model.insertElementAt(text, 0);
    if (model.getSize() > 10) {
      model.removeElementAt(model.getSize() -1);
    }
    setFilterText(text);
  }

  public void refresh() {
    if (!isVisible() || viewClosing) {
      return;
    }
    try {
      String objectName = null;
      if (table.getQuery().isActive() && table.getSelectedRow() >= 0) {
        table.getQuery().getRecord(table.getSelectedRow());
        objectName = table.getQuery().fieldByName("name").getString();
      }
      refresh(objectName);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refresh(String objectName) {
    try {
      int column = table.getSelectedColumn();
      int index = Math.max(0, table.getSelectedRow());
      table.getQuery().close();
      if (StringUtil.isEmpty(textSearch.getText())) {
        table.getQuery().setSqlText(Sql.getHelpList(null));
      }
      else {
        table.getQuery().setSqlText(Sql.getHelpList(
          "h.name = :search\n" +
          "    or h.category_name = :search\n" +
          "    or exists (select null from mysql.help_relation r, mysql.help_keyword k where r.help_topic_id = h.help_topic_id and k.help_keyword_id = r.help_keyword_id and k.name = :search)"));
        table.getQuery().paramByName("search").setString(textSearch.getText());
      }
      table.getQuery().open();
      if (objectName != null && table.getQuery().locate("name", new Variant(objectName))) {
        table.changeSelection(table.getQuery().getCurrentRecord().getIndex(), column);
      } else if (!table.getQuery().isEmpty()) {
        table.changeSelection(Math.min(index, table.getRowCount() -1), column);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    viewClosing = true;
    settings.setValue("split", (long)split.getDividerLocation());
    table.getQuery().close();
    accesibilities = null;
    settings.store();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    cmSearch = new pl.mpak.sky.gui.swing.Action();
    split = new javax.swing.JSplitPane();
    jPanel4 = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    table = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel3 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    textSearch = new pl.mpak.sky.gui.swing.comp.ComboBox();
    buttonSearch = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel5 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    textHelp = new pl.mpak.sky.gui.swing.comp.HtmlEditorPane() {
      protected HyperlinkListener createHyperLinkListener() {
        return null;
      }
      public void setPage(URL page) {
      }
    };

    cmRefresh.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/refresh16.gif"))); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmSearch.setActionCommandKey("cmSearch");
    cmSearch.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmSearch.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/find_source.gif"))); // NOI18N
    cmSearch.setText(stringManager.getString("cmSearch-text")); // NOI18N
    cmSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSearchActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    split.setBorder(null);
    split.setDividerLocation(300);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jPanel4.setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(table);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(table);
    jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

    jPanel4.add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);

    jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 3));

    jLabel1.setText(stringManager.getString("search-dd")); // NOI18N
    jPanel3.add(jLabel1);

    textSearch.setEditable(true);
    textSearch.setPreferredSize(new java.awt.Dimension(150, 22));
    jPanel3.add(textSearch);

    toolBar.add(jPanel3);

    buttonSearch.setAction(cmSearch);
    buttonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonSearch);
    toolBar.add(jSeparator1);
    toolBar.add(buttonActions);

    jPanel2.add(toolBar);

    jPanel4.add(jPanel2, java.awt.BorderLayout.NORTH);

    split.setLeftComponent(jPanel4);

    jPanel5.setLayout(new java.awt.BorderLayout());

    textHelp.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
        textHelpHyperlinkUpdate(evt);
      }
    });
    jScrollPane2.setViewportView(textHelp);

    jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    split.setRightComponent(jPanel5);

    add(split, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!table.getQuery().isActive()) {
    refresh();
  }
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refresh();
}//GEN-LAST:event_cmRefreshActionPerformed

private void textHelpHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_textHelpHyperlinkUpdate
  if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
    try {
      Desktop.getDesktop().browse(evt.getURL().toURI());
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_textHelpHyperlinkUpdate

private void cmSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSearchActionPerformed
  if (!"".equals(getFilterText())) {
    addTextToList(getFilterText());
  }
  refresh();
}//GEN-LAST:event_cmSearchActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSearch;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSearch;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JSplitPane split;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable table;
  private pl.mpak.sky.gui.swing.comp.HtmlEditorPane textHelp;
  private pl.mpak.sky.gui.swing.comp.ComboBox textSearch;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables

}
