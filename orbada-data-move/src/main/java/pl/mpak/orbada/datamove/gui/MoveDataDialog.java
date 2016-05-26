/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MoveDataDialog.java
 *
 * Created on 2009-04-04, 20:42:39
 */
package pl.mpak.orbada.datamove.gui;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import orbada.gui.comps.OrbadaSyntaxTextArea;
import orbada.core.Application;
import pl.mpak.orbada.datamove.OrbadaDataMovePlugin;
import orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseManager;
import pl.mpak.usedb.core.Parameter;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.files.PatternFilenameFilter;
import pl.mpak.util.files.WildCard;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskExecutor;
import pl.mpak.util.task.TaskPool;

/**
 *
 * @author akaluza
 */
public class MoveDataDialog extends javax.swing.JDialog {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaDataMovePlugin.class);

  private Query query;
  private ISettings settings;
  private DataMoveConfig config;
  private int modalResult = ModalResult.NONE;
  private boolean nameChanged;

  public MoveDataDialog(Query query) {
    super(SwingUtil.getRootFrame(), true);
    this.config = new DataMoveConfig();
    this.query = query;
    initComponents();
    init();
  }

  public static boolean show(Query query) {
    MoveDataDialog dialog = new MoveDataDialog(query);
    dialog.setVisible(true);
    return dialog.modalResult == ModalResult.OK;
  }

  private void init() {
    settings = Application.get().getSettings("orbada-data-move-dialog");

    textInsert.setDatabase(query.getDatabase());
    textCheckSelect.setDatabase(query.getDatabase());
    textUpdate.setDatabase(query.getDatabase());

    ((JTextField)comboDefs.getEditor().getEditorComponent()).getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        nameChanged = true;
      }
      public void removeUpdate(DocumentEvent e) {
        nameChanged = true;
      }
      public void changedUpdate(DocumentEvent e) {
      }
    });
    comboDefs.getEditor().getEditorComponent().addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
      }
      public void focusLost(FocusEvent e) {
        if (nameChanged && !StringUtil.isEmpty(comboDefs.getText())) {
          loadConfig();
        }
      }
    });

    File[] files = new File(OrbadaDataMovePlugin.getDefaultPath()).listFiles(new PatternFilenameFilter(WildCard.getRegex("*.datamove")));
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    model.addElement("");
    for (File file : files) {
      model.addElement(file.getPath());
    }
    comboDefs.setModel(model);

    model = new DefaultComboBoxModel();
    for (int i=0; i<DatabaseManager.getManager().getDatabaseCount(); i++) {
      Database database = DatabaseManager.getManager().getDatabase(i);
      if (!database.equals(InternalDatabase.get()) || Application.get().isUserAdmin()) {
        model.addElement(database);
      }
    }
    comboConnections.setModel(model);
    comboConnections.setRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value != null) {
          Database database = (Database)value;
          String text = "<html>";
          if (database.getPublicName() == null) {
            text = text +"<b>" +database.getUserName() +"</b>";
          } else {
            text = text +"<b>" +database.getPublicName() +"</b> as " +database.getUserName();
          }
          text = text +" (" +(database.getUrl().length() > 40 ? database.getUrl().substring(0, 39) +"..." : database.getUrl()) +")";
          label.setText(text);
          label.setVerticalAlignment(JLabel.TOP);
        }
        return label;
      }
    });
    textInsert.addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        enableActions();
      }
      public void removeUpdate(DocumentEvent e) {
        enableActions();
      }
      public void changedUpdate(DocumentEvent e) {
      }
    });

    enableActions();

    checkBackgroundTask.setSelected(settings.getValue("background-task", false));
    setBounds(0, 0, settings.getValue("width", (long)650).intValue(), settings.getValue("height", (long)450).intValue());
    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    SwingUtil.centerWithinScreen(this);
  }

  private void enableActions() {
    cmOk.setEnabled(comboConnections.getItemCount() > 0 && !StringUtil.isEmpty(textInsert.getText()));
  }

  private void loadConfig() {
    nameChanged = false;
    try {
      config.load(new File(comboDefs.getText()));
      comboTable.setText(config.getTableName());
      textInsert.setText(config.getInsertCommand());
      textInsert.setChanged(false);
      textInsert.getEditorArea().getUndoManager().discardAllEdits();
      textCheckSelect.setText(config.getSelectCommand());
      textCheckSelect.setChanged(false);
      textCheckSelect.getEditorArea().getUndoManager().discardAllEdits();
      textUpdate.setText(config.getUpdateCommand());
      textUpdate.setChanged(false);
      textUpdate.getEditorArea().getUndoManager().discardAllEdits();
      checkCommit.setSelected(config.isCommit());
      spinCommit.setValue((Integer)config.getCommitCount());
      checkNoUpdate.setSelected(config.isNoUpdate());
      checkIgnoreIU.setSelected(config.isIgnoreErrors());
      checkDestTypes.setSelected(config.isDestParamTypes());
    } catch (IOException ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }

  private void updateConfig() {
    config.setTableName(comboTable.getText());
    config.setInsertCommand(textInsert.getText());
    config.setSelectCommand(textCheckSelect.getText());
    config.setUpdateCommand(textUpdate.getText());
    config.setCommit(checkCommit.isSelected());
    config.setCommitCount((Integer)spinCommit.getValue());
    config.setNoUpdate(checkNoUpdate.isSelected() || StringUtil.isEmpty(textCheckSelect.getText()) || StringUtil.isEmpty(textUpdate.getText()));
    config.setIgnoreErrors(checkIgnoreIU.isSelected());
    config.setDestParamTypes(checkDestTypes.isSelected());
  }

  private void saveConfig(File file) {
    comboDefs.setText(file.getPath());
    nameChanged = false;
    try {
      updateConfig();
      config.save(file);
    } catch (IOException ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }

  @Override
  public void dispose() {
    settings.setValue("background-task", checkBackgroundTask.isSelected());
    settings.setValue("width", (long)getWidth());
    settings.setValue("height", (long)getHeight());
    settings.store();
    super.dispose();
  }

  private boolean moveData() {
    PleaseWait pw = null;
    Query source = query;
    Database destDatabase = (Database)comboConnections.getSelectedItem();
    Query select = destDatabase.createQuery();
    Command insert = destDatabase.createCommand();
    Command update = destDatabase.createCommand();
    Command commit = destDatabase.createCommand();
    Query dest = destDatabase.createQuery();
    Task task = null;
    if (Thread.currentThread() instanceof TaskExecutor) {
      task = ((TaskExecutor)Thread.currentThread()).getCurrentTask();
    }
    final boolean background = checkBackgroundTask.isSelected();
    try {
      if (background) {
        source = (Query)query.clone();
        if (source == null) {
          throw new UseDBException(stringManager.getString("mdd-cant-background-move-data"));
        }
        source.setCacheData(false);
        source.open();
        pw = new PleaseWait((Image)null, stringManager.getString("mdd-moving-data"));
        Application.get().startPleaseWait(pw);
      }
      else {
        source.first();
      }
      insert.setSqlText(config.getInsertCommand());
      if (!config.isNoUpdate()) {
        select.setSqlText(config.getSelectCommand());
        update.setSqlText(config.getUpdateCommand());
      }
      commit.setSqlText("COMMIT");
      if (config.isDestParamTypes()) {
        try {
          dest.setSqlText("select * from " +config.getTableName() +" where 1 = 0");
          dest.open();
        }
        catch (Exception ex) {
          dest.close();
          dest = null;
        }
      }
      Parameter insParams[] = new Parameter[source.getFieldCount()];
      Parameter selParams[] = new Parameter[source.getFieldCount()];
      Parameter updParams[] = new Parameter[source.getFieldCount()];
      QueryField destFields[] = new QueryField[source.getFieldCount()];
      for (int i=0; i<source.getFieldCount(); i++) {
        QueryField field = source.getField(i);
        String paramName = SQLUtil.paramNameFromColumnName(field.getFieldName().toUpperCase());
        // insert
        insParams[i] = insert.getParameterList().findParamByName(paramName);
        if (insParams[i] == null) {
          insParams[i] = insert.getParameterList().findParamByName('&' +paramName);
        }
        if (dest != null) {
          destFields[i] = dest.findFieldByName(field.getFieldName());
        }
        if (!config.isNoUpdate()) {
          // select
          selParams[i] = select.getParameterList().findParamByName(paramName);
          if (selParams[i] == null) {
            selParams[i] = select.getParameterList().findParamByName('&' +paramName);
          }
          // update
          updParams[i] = update.getParameterList().findParamByName(paramName);
          if (updParams[i] == null) {
            updParams[i] = update.getParameterList().findParamByName('&' +paramName);
          }
        }
      }
      int count = 1;
      while (!source.eof()) {
        if (pw != null && count % 100 == 0) {
          pw.setMessage(String.format(stringManager.getString("mdd-moving-data-count"), new Object[] {count}));
        }
        for (int i=0; i<source.getFieldCount(); i++) {
          QueryField field = source.getField(i);
          // insert
          if (insParams[i] != null) {
            insParams[i].setValue(field.getValue(), destFields[i] != null ? destFields[i].getDataType() : field.getDataType());
          }
          if (!config.isNoUpdate()) {
            if (select.isActive()) {
              select.close();
            }
            // select
            if (selParams[i] != null) {
              selParams[i].setValue(field.getValue(), destFields[i] != null ? destFields[i].getDataType() : field.getDataType());
            }
            // update
            if (updParams[i] != null) {
              updParams[i].setValue(field.getValue(), destFields[i] != null ? destFields[i].getDataType() : field.getDataType());
            }
          }
        }
        try {
          if (config.isNoUpdate()) {
            insert.execute();
          }
          else {
            select.open();
            if (!select.eof()) {
              update.execute();
            }
            else {
              insert.execute();
            }
          }
          if (config.isCommit() && count % config.getCommitCount() == 0) {
            commit.execute();
          }
        }
        catch (Exception ex) {
          if (!config.isIgnoreErrors()) {
            throw ex;
          }
        }
        if (task != null) {
          if (task.isCanceled()) {
            break;
          }
          if (count % 100 == 0) {
            task.setDescription(String.format(stringManager.getString("mdd-moving-data-count"), new Object[] {count}));
          }
        }
        count++;
        source.next();
      }
      if (config.isCommit()) {
        commit.execute();
      }
    }
    catch (final Exception ex) {
      ExceptionUtil.processException(ex);
      if (background) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
          }
        });
      }
      else {
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
    finally {
      if (pw != null) {
        Application.get().stopPleaseWait(pw);
      }
      if (dest != null) {
        dest.close();
      }
      if (background) {
        source.close();
      }
      select.close();
    }
    return true;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmOk = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    cmOpen = new pl.mpak.sky.gui.swing.Action();
    cmCreateInsertCommand = new pl.mpak.sky.gui.swing.Action();
    cmCreateUpdateCommand = new pl.mpak.sky.gui.swing.Action();
    cmSave = new pl.mpak.sky.gui.swing.Action();
    cmCreateSelectCommand = new pl.mpak.sky.gui.swing.Action();
    buttonCancel = new javax.swing.JButton();
    buttonOk = new javax.swing.JButton();
    jTabbedPane1 = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    comboDefs = new pl.mpak.sky.gui.swing.comp.ComboBox();
    buttonOpen = new javax.swing.JButton();
    jLabel2 = new javax.swing.JLabel();
    comboConnections = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jPanel2 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    comboTable = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jToolBar1 = new javax.swing.JToolBar();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    textInsert = new OrbadaSyntaxTextArea();
    jPanel3 = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    textCheckSelect = new OrbadaSyntaxTextArea();
    jLabel5 = new javax.swing.JLabel();
    jToolBar2 = new javax.swing.JToolBar();
    toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    textUpdate = new OrbadaSyntaxTextArea();
    jToolBar3 = new javax.swing.JToolBar();
    toolButton3 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jPanel4 = new javax.swing.JPanel();
    checkCommit = new javax.swing.JCheckBox();
    spinCommit = new javax.swing.JSpinner();
    jLabel6 = new javax.swing.JLabel();
    jButton1 = new javax.swing.JButton();
    checkNoUpdate = new javax.swing.JCheckBox();
    checkIgnoreIU = new javax.swing.JCheckBox();
    checkDestTypes = new javax.swing.JCheckBox();
    checkBackgroundTask = new javax.swing.JCheckBox();

    cmOk.setActionCommandKey("cmOk");
    cmOk.setText(stringManager.getString("ok-amp")); // NOI18N
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setActionCommandKey("cmCancel");
    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cancel-amp")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    cmOpen.setActionCommandKey("cmOpen");
    cmOpen.setText(stringManager.getString("mdd-cmopen-text")); // NOI18N
    cmOpen.setTooltip(stringManager.getString("mdd-cmopen-hint")); // NOI18N
    cmOpen.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOpenActionPerformed(evt);
      }
    });

    cmCreateInsertCommand.setActionCommandKey("cmCreateInsertCommand");
    cmCreateInsertCommand.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add_table16.gif")); // NOI18N
    cmCreateInsertCommand.setText(stringManager.getString("mdd-cmcreateinsert-text")); // NOI18N
    cmCreateInsertCommand.setTooltip(stringManager.getString("mdd-cmcreateinsert-hint")); // NOI18N
    cmCreateInsertCommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateInsertCommandActionPerformed(evt);
      }
    });

    cmCreateUpdateCommand.setActionCommandKey("cmCreateUpdateCommand");
    cmCreateUpdateCommand.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add_table16.gif")); // NOI18N
    cmCreateUpdateCommand.setText(stringManager.getString("mdd-cmcreateupdate-text")); // NOI18N
    cmCreateUpdateCommand.setTooltip(stringManager.getString("mdd-cmcreateupdate-hint")); // NOI18N
    cmCreateUpdateCommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateUpdateCommandActionPerformed(evt);
      }
    });

    cmSave.setActionCommandKey("cmSave");
    cmSave.setText(stringManager.getString("save-amp")); // NOI18N
    cmSave.setTooltip(stringManager.getString("mdd-cmsave-hint")); // NOI18N
    cmSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSaveActionPerformed(evt);
      }
    });

    cmCreateSelectCommand.setActionCommandKey("cmCreateSelectCommand");
    cmCreateSelectCommand.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add_table16.gif")); // NOI18N
    cmCreateSelectCommand.setText(stringManager.getString("mdd-cmcreateselect-text")); // NOI18N
    cmCreateSelectCommand.setTooltip(stringManager.getString("mdd-cmcreateselect-hint")); // NOI18N
    cmCreateSelectCommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCreateSelectCommandActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("mdd-title")); // NOI18N

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    jTabbedPane1.setFocusable(false);

    jLabel1.setText(stringManager.getString("mdd-select-definition-dd")); // NOI18N

    comboDefs.setEditable(true);

    buttonOpen.setAction(cmOpen);
    buttonOpen.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOpen.setPreferredSize(new java.awt.Dimension(85, 25));

    jLabel2.setText(stringManager.getString("mdd-select-dest-connection-dd")); // NOI18N

    comboConnections.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboConnectionsItemStateChanged(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(comboConnections, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addComponent(comboDefs, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonOpen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jLabel1)
          .addComponent(jLabel2))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(comboDefs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOpen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(comboConnections, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(222, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab(stringManager.getString("definition"), jPanel1); // NOI18N

    jLabel3.setText(stringManager.getString("mdd-dest-table-dd")); // NOI18N

    comboTable.setEditable(true);

    jToolBar1.setFloatable(false);
    jToolBar1.setOrientation(1);
    jToolBar1.setRollover(true);

    toolButton1.setAction(cmCreateInsertCommand);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(toolButton1);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(comboTable, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textInsert, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(comboTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
          .addComponent(textInsert, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
        .addContainerGap())
    );

    jTabbedPane1.addTab(stringManager.getString("insert-command"), jPanel2); // NOI18N

    jLabel4.setText(stringManager.getString("mdd-select-update-dd")); // NOI18N

    jLabel5.setText(stringManager.getString("mdd-update-data-command-dd")); // NOI18N

    jToolBar2.setFloatable(false);
    jToolBar2.setOrientation(1);
    jToolBar2.setRollover(true);

    toolButton2.setAction(cmCreateUpdateCommand);
    toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar2.add(toolButton2);

    jToolBar3.setFloatable(false);
    jToolBar3.setOrientation(1);
    jToolBar3.setRollover(true);

    toolButton3.setAction(cmCreateSelectCommand);
    toolButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar3.add(toolButton3);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE))
          .addComponent(jLabel4)
          .addComponent(jLabel5)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textCheckSelect, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel4)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(textCheckSelect, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
          .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel5)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(textUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
          .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
        .addContainerGap())
    );

    jTabbedPane1.addTab(stringManager.getString("update-command"), jPanel3); // NOI18N

    checkCommit.setText(stringManager.getString("mdd-execute-commit")); // NOI18N

    spinCommit.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(0), null, Integer.valueOf(1)));

    jLabel6.setText(stringManager.getString("mdd-commands")); // NOI18N

    jButton1.setAction(cmSave);
    jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jButton1.setPreferredSize(new java.awt.Dimension(85, 25));

    checkNoUpdate.setText(stringManager.getString("mdd-do-not-execute-update")); // NOI18N

    checkIgnoreIU.setText(stringManager.getString("mdd-ignore-error-insert-update")); // NOI18N

    checkDestTypes.setText(stringManager.getString("mdd-types-equal-dest-table")); // NOI18N

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkCommit)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(spinCommit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel6)
        .addContainerGap(366, Short.MAX_VALUE))
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
        .addContainerGap(505, Short.MAX_VALUE)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkNoUpdate)
        .addContainerGap(407, Short.MAX_VALUE))
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkIgnoreIU)
        .addContainerGap(323, Short.MAX_VALUE))
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(checkDestTypes)
        .addContainerGap(357, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(checkCommit)
          .addComponent(spinCommit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel6))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkNoUpdate)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkIgnoreIU)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkDestTypes)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 201, Short.MAX_VALUE)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    jTabbedPane1.addTab(stringManager.getString("execution"), jPanel4); // NOI18N

    checkBackgroundTask.setText(stringManager.getString("execute-background")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(checkBackgroundTask)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 336, Short.MAX_VALUE)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkBackgroundTask))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
    updateConfig();
    if (checkBackgroundTask.isSelected()) {
      TaskPool.getTaskPool("move-data-plugin").addTask(new Task() {
        public void run() {
          moveData();
        }
      });
      modalResult = ModalResult.OK;
      dispose();
    }
    else if (moveData()) {
      modalResult = ModalResult.OK;
      dispose();
    }
  }//GEN-LAST:event_cmOkActionPerformed

  private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
    modalResult = ModalResult.CANCEL;
    dispose();
  }//GEN-LAST:event_cmCancelActionPerformed

  private void cmOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOpenActionPerformed
    File file = FileUtil.selectFileToOpen(this, stringManager.getString("mdd-open-data-move-file"), OrbadaDataMovePlugin.getDefaultPath(), new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("data-move-file"), ".datamove")});
    if (file != null) {
      comboDefs.setText(file.getPath());
    }
  }//GEN-LAST:event_cmOpenActionPerformed

  private void cmSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSaveActionPerformed
    File lastFile = null;
    if (!StringUtil.isEmpty(comboDefs.getText())) {
      lastFile = new File(comboDefs.getText());
    }
    File file = FileUtil.selectFileToSave(this, stringManager.getString("mdd-save-data-move-file"), OrbadaDataMovePlugin.getDefaultPath(), lastFile, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("data-move-file"), ".datamove")});
    if (file != null) {
      saveConfig(file);
    }
  }//GEN-LAST:event_cmSaveActionPerformed

  private void cmCreateInsertCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateInsertCommandActionPerformed
    StringBuffer columns = new StringBuffer();
    StringBuffer params = new StringBuffer();
    for (int i=0; i<query.getFieldCount(); i++) {
      QueryField field = query.getField(i);
      columns.append((columns.length() > 0 ? ", " : "") +SQLUtil.createSqlName(field.getFieldName()));
      params.append((params.length() > 0 ? ", " : "") +":" +SQLUtil.paramNameFromColumnName(field.getFieldName().toUpperCase()));
    }
    textInsert.setText(
      "INSERT INTO " +comboTable.getText() +" (" +columns.toString() +")\n" +
      "VALUES (" +params.toString() +")");
  }//GEN-LAST:event_cmCreateInsertCommandActionPerformed

  private void cmCreateUpdateCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateUpdateCommandActionPerformed
    StringBuffer columns = new StringBuffer();
    QueryField firstField = null;
    for (int i=0; i<query.getFieldCount(); i++) {
      QueryField field = query.getField(i);
      if (firstField == null) {
        firstField = field;
      }
      else {
        columns.append(
          (columns.length() > 0 ? ",\n  " : "") +SQLUtil.createSqlName(field.getFieldName()) +
          " = :" +SQLUtil.paramNameFromColumnName(field.getFieldName().toUpperCase()));
      }
    }
    textUpdate.setText(
      "UPDATE " +comboTable.getText() +" SET\n" +
      "  " +columns.toString() +"\n" +
      " WHERE " +SQLUtil.createSqlName(firstField.getFieldName()) +" = :" +SQLUtil.paramNameFromColumnName(firstField.getFieldName().toUpperCase()));
  }//GEN-LAST:event_cmCreateUpdateCommandActionPerformed

  private void cmCreateSelectCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCreateSelectCommandActionPerformed
    QueryField firstField = query.getField(0);
    textCheckSelect.setText(
      "SELECT 0 TEST_COLUMN\n" +
      "  FROM " +comboTable.getText() +"\n" +
      " WHERE " +SQLUtil.createSqlName(firstField.getFieldName()) +" = :" +SQLUtil.paramNameFromColumnName(firstField.getFieldName().toUpperCase()));
  }//GEN-LAST:event_cmCreateSelectCommandActionPerformed

  private void comboConnectionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboConnectionsItemStateChanged
    if (evt.getStateChange() == ItemEvent.SELECTED) {
      textInsert.setDatabase((Database)evt.getItem());
      textCheckSelect.setDatabase((Database)evt.getItem());
      textUpdate.setDatabase((Database)evt.getItem());
    }
  }//GEN-LAST:event_comboConnectionsItemStateChanged

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JButton buttonOpen;
  private javax.swing.JCheckBox checkBackgroundTask;
  private javax.swing.JCheckBox checkCommit;
  private javax.swing.JCheckBox checkDestTypes;
  private javax.swing.JCheckBox checkIgnoreIU;
  private javax.swing.JCheckBox checkNoUpdate;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmCreateInsertCommand;
  private pl.mpak.sky.gui.swing.Action cmCreateSelectCommand;
  private pl.mpak.sky.gui.swing.Action cmCreateUpdateCommand;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private pl.mpak.sky.gui.swing.Action cmOpen;
  private pl.mpak.sky.gui.swing.Action cmSave;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboConnections;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboDefs;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboTable;
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JToolBar jToolBar2;
  private javax.swing.JToolBar jToolBar3;
  private javax.swing.JSpinner spinCommit;
  private OrbadaSyntaxTextArea textCheckSelect;
  private OrbadaSyntaxTextArea textInsert;
  private OrbadaSyntaxTextArea textUpdate;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton3;
  // End of variables declaration//GEN-END:variables
}
