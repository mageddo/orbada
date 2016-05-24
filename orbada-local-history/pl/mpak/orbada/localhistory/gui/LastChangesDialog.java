/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LastChangesDialog.java
 *
 * Created on 2010-11-20, 15:46:58
 */

package pl.mpak.orbada.localhistory.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.plugins.DatabaseObject;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class LastChangesDialog extends javax.swing.JDialog {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLocalHistoryPlugin.class);

  private ISettings settings;
  private IPerspectiveAccesibilities perspective;

  /** Creates new form LastChangesDialog */
  public LastChangesDialog(IPerspectiveAccesibilities perspective) {
    super(SwingUtil.getRootFrame());
    this.perspective = perspective;
    initComponents();
    init();
  }

  public static void showDialog(IPerspectiveAccesibilities perspective) {
    LastChangesDialog dialog = new LastChangesDialog(perspective);
    dialog.setVisible(true);
  }

  private void init() {
    settings = perspective.getApplication().getSettings("local-history-last-changes-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", new Variant(getWidth())).getInteger(), settings.getValue("height", new Variant(getHeight())).getInteger());
    } catch (Exception ex) {
    }

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonShow);

    table.getQuery().setDatabase(InternalDatabase.get());
    table.getQuery().addQueryListener(new DefaultQueryListener() {
      @Override
      public void afterClose(EventObject e) {
        cmShow.setEnabled(false);
      }
    });

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (table.getSelectedRow() >= 0) {
          try {
            table.getQuery().getRecord(table.getSelectedRow());
            cmShow.setEnabled(e.getFirstIndex() >= 0);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        else {
          cmShow.setEnabled(false);
        }
      }
    });

    textSearch.addKeyListener(new TableRowChangeKeyListener(table));
    SwingUtil.addAction(textSearch, cmSearch);

    try {
      table.addColumn(new QueryTableColumn("olho_object_schema", stringManager.getString("schema-name"), 100, new QueryTableCellRenderer(Font.BOLD)));
      table.addColumn(new QueryTableColumn("olho_object_name", stringManager.getString("object-name"), 200, new QueryTableCellRenderer(Font.BOLD)));
      table.addColumn(new QueryTableColumn("olho_object_type", stringManager.getString("object-type"), 100, new QueryTableCellRenderer(SwingUtil.Color.NAVY)));
      table.addColumn(new QueryTableColumn("olho_created", stringManager.getString("change-date"), 120, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      table.addColumn(new QueryTableColumn("changes", stringManager.getString("changed"), 70, new QueryTableCellRenderer(Color.RED)));
      table.getQuery().setSqlText(
        "select olho_object_schema, olho_object_type, olho_object_name, max(olho_created) olho_created, count(0) changes\n" +
        "  from olhobjects\n" +
        " where olho_sch_id = :SCH_ID\n" +
        "   and (:TEXT is null or upper(olho_object_name) like concat(concat('%', upper(:TEXT)), '%'))\n" +
        " group by olho_object_schema, olho_object_type, olho_object_name\n" +
        " order by olho_created desc");
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          refresh();
        }
      });
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }

    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonShow, buttonCancel});
    SwingUtil.centerWithinScreen(this);
  }

  private void refresh() {
    try {
      String objectName = null;
      if (table.getQuery().isActive() && !table.getQuery().isEmpty() && table.getSelectedRow() >= 0) {
        table.getQuery().getRecord(table.getSelectedRow());
        objectName = table.getQuery().fieldByName("olho_object_name").getString();
      }
      table.getQuery().close();
      table.getQuery().paramByName("sch_id").setString(perspective.getDatabase().getUserProperties().getProperty("schemaId"));
      if (!StringUtil.isEmpty(textSearch.getText())) {
        table.getQuery().paramByName("text").setString(textSearch.getText());
      }
      else {
        table.getQuery().paramByName("text").clearValue();
      }
      table.getQuery().open();
      if (!table.getQuery().isEmpty()) {
        if (objectName != null && table.getQuery().locate("olho_object_name", new Variant(objectName))) {
          table.changeSelection(table.getQuery().getCurrentRecord().getIndex(), 0);
        }
        else {
          table.changeSelection(0, 0);
        }
      }
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
  }

  @Override
  public void dispose() {
    settings.setValue("width", new Variant(getWidth()));
    settings.setValue("height", new Variant(getHeight()));
    settings.store();
    super.dispose();
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmShow = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    cmSearch = new pl.mpak.sky.gui.swing.Action();
    jScrollPane1 = new javax.swing.JScrollPane();
    table = new pl.mpak.orbada.gui.comps.table.ViewTable();
    buttonShow = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    textSearch = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel1 = new javax.swing.JLabel();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmShow.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmShow.setText(stringManager.getString("cmShow-text")); // NOI18N
    cmShow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmShowActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    cmSearch.setActionCommandKey("cmSearch");
    cmSearch.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmSearch.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/find_object16.gif"))); // NOI18N
    cmSearch.setText(stringManager.getString("cmSearch-text")); // NOI18N
    cmSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSearchActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("LastChangesDialog-title")); // NOI18N

    jScrollPane1.setViewportView(table);

    buttonShow.setAction(cmShow);
    buttonShow.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonShow.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    jLabel1.setText(stringManager.getString("search-dd")); // NOI18N

    toolButton1.setAction(cmSearch);
    toolButton1.setMaximumSize(new java.awt.Dimension(24, 24));
    toolButton1.setMinimumSize(new java.awt.Dimension(24, 24));
    toolButton1.setPreferredSize(new java.awt.Dimension(24, 24));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(buttonShow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(toolButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(toolButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonShow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void cmShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmShowActionPerformed
      try {
        if (table.getSelectedRow() >= 0) {
          try {
            table.getQuery().getRecord(table.getSelectedRow());
            DatabaseObject dbo = new DatabaseObject(
              table.getQuery().fieldByName("olho_object_schema").getString(),
              table.getQuery().fieldByName("olho_object_name").getString(),
              table.getQuery().fieldByName("olho_object_type").getString());
            perspective.getApplication().postPluginMessage(
              new PluginMessage(
                perspective.getDatabase().getUniqueID(),
                Consts.globalMessageFreezeObject,
                dbo));
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
          dispose();
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
      }
}//GEN-LAST:event_cmShowActionPerformed

    private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
      dispose();
}//GEN-LAST:event_cmCancelActionPerformed

    private void cmSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSearchActionPerformed
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          refresh();
        }
      });
    }//GEN-LAST:event_cmSearchActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonShow;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmSearch;
  private pl.mpak.sky.gui.swing.Action cmShow;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.orbada.gui.comps.table.ViewTable table;
  private pl.mpak.sky.gui.swing.comp.TextField textSearch;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  // End of variables declaration//GEN-END:variables

}
