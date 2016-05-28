package pl.mpak.orbada.derbydb.procedures;

import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.derbydb.DerbyDbSql;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.parser.Tokenizer;
import pl.mpak.util.parser.SQLTokenHandle;

/**
 *
 * @author  akaluza
 */
public class ParametersPanel extends javax.swing.JPanel implements ITabObjectInfo {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  private IViewAccesibilities accesibilities;
  private String currentSchemaName = "";
  private String currentProcName = "";
  private boolean requestRefresh = false;
  
  private Timer timer;
  
  /** Creates new form ParametersPanel
   * @param accesibilities
   */
  public ParametersPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    timer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        try {
          requestRefresh = false;
          tableParams.getQuery().close();
          String sqlText = sourceToSqlText();
          if (sqlText != null) {
            tableParams.getQuery().setSqlText(sourceToSqlText());
            tableParams.getQuery().open();
            if (!tableParams.getQuery().isEmpty()) {
              tableParams.changeSelection(0, 0);
            }
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    };
    OrbadaDerbyDbPlugin.getRefreshQueue().add(timer);
    
    tableParams.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableParams.getSelectedRow();
        if (rowIndex >= 0 && tableParams.getQuery().isActive()) {
          try {
            tableParams.getQuery().getRecord(rowIndex);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    });
    
    tableParams.getQuery().setDatabase(getDatabase());
    try {
      tableParams.addColumn(new QueryTableColumn("name", stringManager.getString("ParametersPanel-parameter-name"), 150, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableParams.addColumn(new QueryTableColumn("type", stringManager.getString("ParametersPanel-parameter-type"), 150, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableParams.addColumn(new QueryTableColumn("method", stringManager.getString("ParametersPanel-parameter-method"), 60));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private String sourceToSqlText(String source) {
    try {
      Tokenizer pr = new Tokenizer(source, new SQLTokenHandle());
      StringBuffer sb = new StringBuffer();
      
      while (!pr.isEof()) {
        pr.getNextToken();
        if ("(".equals(pr.getString())) {
          break;
        }
      }
      boolean paramFound = false;
      pr.getNextToken(SQLTokenHandle.BLANK);
      while (!pr.isEof()) {
        String token = pr.getString();
        if (")".equals(token)) {
          break;
        }
        if (",".equals(token)) {
          pr.getNextToken(SQLTokenHandle.BLANK);
          token = pr.getString();
        }
        paramFound = true;
        String method = "IN";
        String name = "";
        String type = "";
        if ("IN".equalsIgnoreCase(token) || "OUT".equalsIgnoreCase(token) || "INOUT".equalsIgnoreCase(token)) {
          method = token;
          pr.getNextToken(SQLTokenHandle.BLANK);
          token = pr.getString();
        }
        name = token;
        pr.getNextToken(SQLTokenHandle.BLANK);
        type = pr.getString();
        pr.getNextToken(SQLTokenHandle.BLANK);
        token = pr.getString();
        if ("(".equals(token)) {
          pr.getNextToken(SQLTokenHandle.BLANK);
          type = type +"(" +pr.getString();
          pr.getNextToken(SQLTokenHandle.BLANK);
          token = pr.getString();
          if (",".equals(token)) {
            pr.getNextToken(SQLTokenHandle.BLANK);
            type = type +"," +pr.getString();
            pr.getNextToken(SQLTokenHandle.BLANK);
          }
          pr.getNextToken(SQLTokenHandle.BLANK);
          type = type +")";
        }
        if (sb.length() > 0) {
          sb.append(",\n        ");
        }
        sb.append("('" +name +"', '" +type +"', '" +method +"')");
      }
      pr.getNextToken(SQLTokenHandle.BLANK);
      if ("RETURNS".equalsIgnoreCase(pr.getString())) {
        paramFound = true;
        String name = "{RETURNS}";
        pr.getNextToken(SQLTokenHandle.BLANK);
        String type = pr.getString();
        pr.getNextToken(SQLTokenHandle.BLANK);
        if ("(".equals(pr.getString())) {
          pr.getNextToken(SQLTokenHandle.BLANK);
          type = type +"(" +pr.getString();
          pr.getNextToken(SQLTokenHandle.BLANK);
          String token = pr.getString();
          if (",".equals(token)) {
            pr.getNextToken(SQLTokenHandle.BLANK);
            type = type +"," +pr.getString();
            pr.getNextToken(SQLTokenHandle.BLANK);
          }
          pr.getNextToken(SQLTokenHandle.BLANK);
          type = type +")";
        }
        if (sb.length() > 0) {
          sb.append(",\n        ");
        }
        sb.append("('" +name +"', '" +type +"', 'OUT')");
      }
      else {
        while (!pr.isEof()) {
          pr.getNextToken(SQLTokenHandle.BLANK);
          if ("RESULT".equalsIgnoreCase(pr.getString())) {
            pr.getNextToken(SQLTokenHandle.BLANK);
            if ("SETS".equalsIgnoreCase(pr.getString())) {
              paramFound = true;
              pr.getNextToken(SQLTokenHandle.BLANK);
              String rsc = pr.getString();
              if (sb.length() > 0) {
                sb.append(",\n        ");
              }
              sb.append("('{RETURNS}', 'RESULT SET (" +rsc +")', 'OUT')");
              break;
            }
          }
        }
      }
      
      if (!paramFound) {
        sb.append("('', '', '')");
      }
      sb.insert(0, "select *\n  from (values");
      sb.append(") x(name, type, method)");
      if (!paramFound) {
        sb.append("\n where 1 = 0");
      }
      
      return sb.toString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  private String sourceToSqlText() {
    Query query = getDatabase().createQuery();
    try {
      query.setSqlText(DerbyDbSql.getProcedureSource());
      query.paramByName("schemaname").setString(currentSchemaName);
      query.paramByName("procname").setString(currentProcName);
      query.open();
      if (!query.eof()) {
        return sourceToSqlText(query.fieldByName("source").getString());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    } finally {
      query.close();
    }
    return null;
  }
  
  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public String getTitle() {
    return stringManager.getString("ParametersPanel-title");
  }
  
  public void refresh() {
    refresh(null, currentSchemaName, currentProcName);
  }
  
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentProcName.equals(objectName) || requestRefresh) {
      currentSchemaName = schemaName;
      currentProcName = objectName;
      if (isVisible()) {
        timer.restart();
      } else {
        requestRefresh = true;
      }
    }
  }
  
  @Override
  public boolean canClose() {
    return true;
  }

  public void close() throws IOException {
    timer.cancel();
    tableParams.getQuery().close();
    accesibilities = null;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableParams = new ViewTable();
    statusBarParams = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();
    toolBarParams = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(tableParams);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);

    statusBarParams.setShowFieldType(false);
    statusBarParams.setShowOpenTime(false);
    statusBarParams.setTable(tableParams);
    add(statusBarParams, java.awt.BorderLayout.PAGE_END);

    toolBarParams.setFloatable(false);
    toolBarParams.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBarParams.add(buttonRefresh);

    add(toolBarParams, java.awt.BorderLayout.PAGE_START);
  }// </editor-fold>//GEN-END:initComponents
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  refresh(null, currentSchemaName, currentProcName);
}//GEN-LAST:event_formComponentShown

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  timer.restart();
}//GEN-LAST:event_cmRefreshActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar statusBarParams;
  private ViewTable tableParams;
  private javax.swing.JToolBar toolBarParams;
  // End of variables declaration//GEN-END:variables

}
