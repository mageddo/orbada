package pl.mpak.drinkmaster.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.Closeable;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.table.ViewTable;
import pl.mpak.drinkmaster.DrinkMasterPlugin;
import pl.mpak.drinkmaster.Sql;
import pl.mpak.drinkmaster.db.DrinkRecord;
import pl.mpak.drinkmaster.gui.cm.DrinkLetterAction;
import orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDef;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.task.Task;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class DrinkViewPanel extends javax.swing.JPanel implements Closeable {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(DrinkMasterPlugin.class);

  private IViewAccesibilities accesibilities;
  private boolean requestRefresh = false;
  private boolean closing = false;
  private SqlFilter filter;
  private String dnk_id;
  private String letter;
  private boolean refreshing;
  
  /** Creates new form DrinkViewPanel */
  public DrinkViewPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }

  private void init() {
    textFind.addKeyListener(new TableRowChangeKeyListener(tableDrinks));
    tableDrinks.getQuery().setDatabase(getDatabase());
    try {
      tableDrinks.addColumn(new QueryTableColumn("dnk_name", stringManager.getString("name"), 200, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableDrinks.addColumn(new QueryTableColumn("components", stringManager.getString("components"), 600));
      tableDrinks.addColumn(new QueryTableColumn("dnk_make_up", stringManager.getString("make-up"), 600));
      SqlFilterDef def = new SqlFilterDef();
      def.add(new SqlFilterDefComponent("dnk_name", stringManager.getString("name"), (String[])null));
      def.add(new SqlFilterDefComponent("dnk_make_up", stringManager.getString("make-up"), (String[])null));
      def.add(new SqlFilterDefComponent("components", stringManager.getString("components"), (String[])null));
      filter = new SqlFilter(
        accesibilities.getApplication().getSettings("drink-maskter-filter"),
        cmFilter,
        def);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    tableDrinks.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableDrinks.getSelectedRow();
        if (rowIndex >= 0 && tableDrinks.getQuery().isActive()) {
          try {
            tableDrinks.getQuery().getRecord(rowIndex);
            if (dnk_id == null || !tableDrinks.getQuery().fieldByName("dnk_id").getString().equals(dnk_id)) {
              dnk_id = tableDrinks.getQuery().fieldByName("dnk_id").getString();
              refreshDetails();
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          refreshDetails();
        }
      }
    });
    
    Query query = getDatabase().createQuery();
    try {
      query.open(Sql.getLetterList());
      while (!query.eof()) {
        ToolButton button = new ToolButton();
        DrinkLetterAction cm = new DrinkLetterAction(query.fieldByName("letter").getString(), query.fieldByName("cnt").getInteger(), button) {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (isSelected()) {
              letter = null;
            }
            else {
              letter = getLetter();
            }
            setSelected(!isSelected());
            refreshTask();
          }
        };
        barLetters.add(button);
        letterGroup.add(button);
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    
    SwingUtil.addAction(textFind, cmFind);
    new ComponentActionsAction(getDatabase(), tableDrinks, buttonActions, menuActions, "drink-view-actions");
  }
  
  private Database getDatabase() {
    return accesibilities.getApplication().getOrbadaDatabase();
  }

  public void setRefreshing(boolean refreshing) {
    this.refreshing = refreshing;
    cmRefresh.setEnabled(!this.refreshing);
    cmAdd.setEnabled(!this.refreshing);
    cmEdit.setEnabled(!this.refreshing);
    cmDelete.setEnabled(!this.refreshing);
    cmFind.setEnabled(!this.refreshing);
    cmFilter.setEnabled(!this.refreshing);
    panelLetters.setEnabled(!this.refreshing);
  }
  
  private void refreshTask() {
    setRefreshing(true);
    getDatabase().getTaskPool().addTask(new Task() {
      public void run() {
        try {
          refresh();
        }
        finally {
          setRefreshing(false);
        }
      }
    });
  }
  
  public void refresh() {
    refresh(null);
  }
  
  private void refresh(String dnk_id_l) {
    try {
      String findFilter = filter.getSqlText();
      String find = textFind.getText();
      if (!"".equals(find)) {
        if (findFilter != null) {
          findFilter = findFilter +"\n  and ";
        }
        else {
          findFilter = "";
        }
        findFilter = findFilter +
          "(upper(dnk_name) like '%'||upper(:FIND)||'%' or upper(components) like '%'||upper(:FIND)||'%')";
      }
      if (letter != null) {
        if (findFilter != null) {
          findFilter = findFilter +"\n  and ";
        }
        else {
          findFilter = "";
        }
        if (".".equals(letter)) {
          findFilter = findFilter +"substr(dnk_name, 1, 1) not in ('A','B','C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')";
        }
        else {
          findFilter = findFilter +"upper(dnk_name) like '" +letter.toUpperCase() +"%'";
        }
      }
      requestRefresh = false;
      if (dnk_id_l == null && tableDrinks.getQuery().isActive() && tableDrinks.getSelectedRow() >= 0) {
        tableDrinks.getQuery().getRecord(tableDrinks.getSelectedRow());
        dnk_id_l = tableDrinks.getQuery().fieldByName("dnk_id").getString();
      }
      tableDrinks.getQuery().close();
      tableDrinks.getQuery().setSqlText(Sql.getDrinkList(findFilter));
      if (!"".equals(find)) {
        tableDrinks.getQuery().paramByName("FIND").setString(find);
      }
      tableDrinks.getQuery().open();
      if (!tableDrinks.getQuery().isEmpty()) {
        if (dnk_id_l != null && tableDrinks.getQuery().locate("dnk_id", new Variant(dnk_id_l))) {
          tableDrinks.changeSelection(tableDrinks.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          tableDrinks.changeSelection(0, 0);
        }
      }
      else {
        textDetails.setText("<html>");
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void refreshDetails() {
    if (dnk_id == null) {
      textDetails.setText("<html>");
      return;
    }
    StringBuffer html = new StringBuffer("<html>");
    html.append("<style>");
    html.append("  body { font-family: Tahoma, Arial, serif; font-size: 9px; margin: 10 }");
    html.append("</style>");
    html.append("<head></head>");
    html.append("<body>");
    try {
      DrinkRecord drink = new DrinkRecord(getDatabase(), dnk_id);
      html.append("<h2>" +drink.getName() +"</h2><hr>");
      html.append(String.format("<u>%s</u><br>", new Object[] {stringManager.getString("composes-dd")}));
      Query query = getDatabase().createQuery();
      try {
        query.setSqlText(Sql.getComponentList());
        query.paramByName("DNK_ID").setString(drink.getId());
        query.open();
        int i = 0;
        while (!query.eof()) {
          html.append(query.fieldByName("dnc_component").getString() +"<br>");
          i++;
          query.next();
        }
      } catch (Exception ex) {
        html.append("<font color=red><b>" +ex.getMessage() +"</b></font>");
      } finally {
        query.close();
      }
      html.append(String.format("<br><u>%s</u><br>", new Object[] {stringManager.getString("how-made-dd")}));
      html.append(drink.getMakeUp());
    } catch (UseDBException ex) {
      html.append("<font color=red><b>" +ex.getMessage() +"</b></font>");
    }
    
    html.append("</body>");
    html.append("</html>");
    textDetails.setText(html.toString());
  }
  
  public void close() throws IOException {
    closing = true;
    tableDrinks.getQuery().close();
    accesibilities = null;
  }
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmFilter = new pl.mpak.sky.gui.swing.Action();
    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    cmAdd = new pl.mpak.sky.gui.swing.Action();
    cmEdit = new pl.mpak.sky.gui.swing.Action();
    cmDelete = new pl.mpak.sky.gui.swing.Action();
    cmFind = new pl.mpak.sky.gui.swing.Action();
    letterGroup = new javax.swing.ButtonGroup();
    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableDrinks = new ViewTable();
    statusBar = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    jScrollPane3 = new javax.swing.JScrollPane();
    textDetails = new pl.mpak.sky.gui.swing.comp.HtmlEditorPane();
    jPanel4 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFilter = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    buttonAdd = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonEdit = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JToolBar.Separator();
    buttonDelete = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel5 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    textFind = new pl.mpak.sky.gui.swing.comp.TextField();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    panelLetters = new javax.swing.JPanel();
    barLetters = new javax.swing.JToolBar();

    cmFilter.setActionCommandKey("cmFilter");
    cmFilter.setText(stringManager.getString("filter-amp")); // NOI18N
    cmFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFilterActionPerformed(evt);
      }
    });

    cmRefresh.setActionCommandKey("cmRefresh");
    cmRefresh.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/refresh16.gif"))); // NOI18N
    cmRefresh.setText(stringManager.getString("refresh-amp")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("refresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmAdd.setActionCommandKey("cmAdd");
    cmAdd.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/new16.gif"))); // NOI18N
    cmAdd.setText(stringManager.getString("new-drink")); // NOI18N
    cmAdd.setTooltip(stringManager.getString("new-drink-hint")); // NOI18N
    cmAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddActionPerformed(evt);
      }
    });

    cmEdit.setActionCommandKey("cmEdit");
    cmEdit.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/edit16.gif"))); // NOI18N
    cmEdit.setText(stringManager.getString("edit-drink")); // NOI18N
    cmEdit.setTooltip(stringManager.getString("edit-drink-hint")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setActionCommandKey("cmDelete");
    cmDelete.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/trash.gif"))); // NOI18N
    cmDelete.setText(stringManager.getString("delete-drink")); // NOI18N
    cmDelete.setTooltip(stringManager.getString("delete-drink-hint")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    cmFind.setActionCommandKey("cmFind");
    cmFind.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmFind.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/find_source.gif"))); // NOI18N
    cmFind.setText(stringManager.getString("find")); // NOI18N
    cmFind.setTooltip(stringManager.getString("find-drink-hint")); // NOI18N
    cmFind.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFindActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jSplitPane1.setBorder(null);
    jSplitPane1.setDividerLocation(250);
    jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setContinuousLayout(true);
    jSplitPane1.setOneTouchExpandable(true);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableDrinks);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBar.setShowFieldType(false);
    statusBar.setShowOpenTime(false);
    statusBar.setTable(tableDrinks);
    jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

    jSplitPane1.setTopComponent(jPanel1);

    jScrollPane3.setViewportView(textDetails);

    jSplitPane1.setRightComponent(jScrollPane3);

    add(jSplitPane1, java.awt.BorderLayout.CENTER);

    jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);

    buttonFilter.setAction(cmFilter);
    buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonFilter);
    toolBar.add(jSeparator2);

    buttonAdd.setAction(cmAdd);
    buttonAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonAdd);

    buttonEdit.setAction(cmEdit);
    buttonEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonEdit);
    toolBar.add(jSeparator3);

    buttonDelete.setAction(cmDelete);
    buttonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonDelete);
    toolBar.add(jSeparator1);
    toolBar.add(buttonActions);

    jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 3));

    jLabel2.setDisplayedMnemonic('S');
    jLabel2.setText(stringManager.getString("find-dd")); // NOI18N
    jPanel5.add(jLabel2);

    textFind.setPreferredSize(new java.awt.Dimension(120, 20));
    jPanel5.add(textFind);

    toolBar.add(jPanel5);

    toolButton1.setAction(cmFind);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(toolButton1);

    jPanel4.add(toolBar);

    add(jPanel4, java.awt.BorderLayout.NORTH);

    panelLetters.setLayout(new java.awt.BorderLayout());

    barLetters.setFloatable(false);
    barLetters.setOrientation(1);
    barLetters.setRollover(true);
    panelLetters.add(barLetters, java.awt.BorderLayout.NORTH);

    add(panelLetters, java.awt.BorderLayout.WEST);
  }// </editor-fold>//GEN-END:initComponents

private void cmFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFilterActionPerformed
  if (SqlFilterDialog.show(filter)) {
    refreshTask();
  }
}//GEN-LAST:event_cmFilterActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTask();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddActionPerformed
  try {
    if (!tableDrinks.getQuery().isActive()) {
      return;
    }
    String dnk_id_l = DrinkEditDialog.showDialog(getDatabase(), (evt.getModifiers() & KeyEvent.CTRL_MASK) != 0);
    if (dnk_id_l != null) {
      refresh(dnk_id_l);
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmAddActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableDrinks.getQuery().isActive() && tableDrinks.getSelectedRow() >= 0) {
      tableDrinks.getQuery().getRecord(tableDrinks.getSelectedRow());
      String dnk_id_l = DrinkEditDialog.showDialog(getDatabase(), tableDrinks.getQuery().fieldByName("dnk_id").getString());
      if (dnk_id_l != null) {
        refresh(dnk_id_l);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  try {
    if (tableDrinks.getQuery().isActive() && tableDrinks.getSelectedRow() >= 0) {
      tableDrinks.getQuery().getRecord(tableDrinks.getSelectedRow());
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("delete-drink-q"), new Object[] {tableDrinks.getQuery().fieldByName("DNK_NAME").getString()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        DrinkRecord record = new DrinkRecord(getDatabase());
        record.setPrimaryKeyValue(new Variant(tableDrinks.getQuery().fieldByName("dnk_id").getString()));
        record.applyDelete();
        refresh();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmDeleteActionPerformed

private void cmFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFindActionPerformed
  refresh();
}//GEN-LAST:event_cmFindActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  if (!tableDrinks.getQuery().isActive() && !closing) {
    refreshTask();
  }
}//GEN-LAST:event_formComponentShown


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JToolBar barLetters;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonAdd;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDelete;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonEdit;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFilter;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmAdd;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmFilter;
  private pl.mpak.sky.gui.swing.Action cmFind;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JToolBar.Separator jSeparator3;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.ButtonGroup letterGroup;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JPanel panelLetters;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBar;
  private ViewTable tableDrinks;
  private pl.mpak.sky.gui.swing.comp.HtmlEditorPane textDetails;
  private pl.mpak.sky.gui.swing.comp.TextField textFind;
  private javax.swing.JToolBar toolBar;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  // End of variables declaration//GEN-END:variables

}
