/*
 * SqlQueryPanel.java
 *
 * Created on 18 paüdziernik 2007, 20:25
 */

package pl.mpak.orbada.universal.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.db.ConnectionFactory;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IProcessMessagable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.execmd.ExecutedSqlCommandDialog;
import pl.mpak.orbada.universal.gui.execmd.ExecutedSqlManager;
import pl.mpak.orbada.universal.gui.history.QueryHistoryPanel;
import pl.mpak.orbada.universal.providers.IUniversalViewAccessibilities;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.orbada.universal.providers.UniversalSqlTextTransformProvider;
import pl.mpak.orbada.universal.services.UniversalSettingsProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TabCloseComponent;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.files.PatternFileFilter;
import pl.mpak.util.files.WildCard;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.task.Task;
import pl.mpak.util.timer.Timer;

/**
 *
 * @author  akaluza
 */
public class SqlQueryPanelView extends javax.swing.JPanel implements Closeable, IProcessMessagable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  public final static String PANEL_STATUS = "editor.status";
  public final static String settingsName = "universal-query-panel";

  public enum EventPanel {
    BEFORE_OPEN_QUERY,
    AFTER_OPEN_QUERY,
    BEFORE_CLOSE_QUERY,
    AFTER_CLOSE_QUERY,
    QUERY_ERROR,
    BEFORE_EXECUTE_COMMAND,
    AFTER_EXECUTE_COMMAND,
    COMMAND_ERROR
  }
  
  private IViewAccesibilities accesibilities;
  
  private ToolButton buttonShowHideError;
  private int nextNo = 0;
  private Database currentDatabase;
  private QueryHistoryPanel historyPanel;
  private ScriptResultPanel scriptResultPanel;
  private ISettings settings;
  private ISettings globalSettings;
  private int tabNumber;
  private int autoSaveEditor = UniversalSettingsProvider.AUTO_SAVE_EDITOR_SCHEMA;
  private ArrayList<String> lastOpenedFile;
  private Database clonedDatabase;
  
  private File openedFile;
  
  private ArrayList<String> editorContentList;
  private String currentEditorContent;
  private Timer autoSaveEditorContentTimer;
  private boolean autoSaveEditorContent = true;
  private ArrayList<UniversalActionProvider> actionProviders;
  private String orygTitle;
  
  /** Creates new form SqlQueryPanel
   * 
   * @param accesibilities 
   */
  public SqlQueryPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  public Database getDatabase() {
    if (currentDatabase == null) {
      currentDatabase = accesibilities.getDatabase();
    }
    return currentDatabase;
  }

  private void cloneDatabase() {
    closeCloneDatabase();
    ConnectionFactory factory = new ConnectionFactory(getDatabase());
    try {
      clonedDatabase = factory.createDatabase();
      setCurrentDatabase(clonedDatabase);
      buttonCloneDatabase.setSelected(true);
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    menuCloneDatabase.setSelected(buttonCloneDatabase.isSelected());
  }

  private void closeCloneDatabase() {
    if (clonedDatabase != null) {
      if (clonedDatabase.equals(getDatabase())) {
        setCurrentDatabase(accesibilities.getDatabase());
      }
      clonedDatabase.getTaskPool().addTask(new Task(String.format(stringManager.getString("SqlQueryPanelView-closing-connection"), new Object[] {clonedDatabase.getPublicName()})) {
        Database database = clonedDatabase;
        public void run() {
          database.close();
        }
      });
      clonedDatabase = null;
      buttonCloneDatabase.setSelected(false);
    }
    menuCloneDatabase.setSelected(buttonCloneDatabase.isSelected());
  }
  
  private void setCurrentDatabase(Database database) {
    this.currentDatabase = database;
    textSqlEditor.setDatabase(database);
    if (database != null && !database.equals(accesibilities.getDatabase())) {
      labelSelectedDatabase.setText(database.getPublicName() +" as " +database.getUserName());
    }
    else {
      labelSelectedDatabase.setText(" ");
    }
  }
  
  public IApplication getApplication() {
    return accesibilities.getApplication();
  }

  private int findTabNumber() {
    Component[] tabs = accesibilities.getViewComponentList(accesibilities.getViewProvider());
    if (tabs.length > 0) {
      for (int i=0; i<100; i++) {
        boolean found = false;
        for (int t=0; t<tabs.length; t++) {
          SqlQueryPanelView tab = (SqlQueryPanelView)tabs[t];
          if (tab.getTabNumber() == i) {
            found = true;
            break;
          }
        }
        if (!found) {
          return i;
        }
      }
    }
    return 0;
  }
  
  private void init() {
    tabNumber = findTabNumber();

    menuView.setText(SwingUtil.setButtonText(menuView, stringManager.getString("SqlQueryPanelView-menuView-text")));
    menuRecentFiles.setText(SwingUtil.setButtonText(menuRecentFiles, stringManager.getString("SqlQueryPanelView-menuRecentFiles-text")));
    lastOpenedFile = new ArrayList<String>();
    settings = getApplication().getSettings(accesibilities.getDatabase().getUserProperties().getProperty("schemaId"), settingsName);
    globalSettings = getApplication().getSettings(UniversalSettingsProvider.settingsName);
    autoSaveEditor = globalSettings.getValue(UniversalSettingsProvider.setAutoSaveEditor, (long)autoSaveEditor).intValue();
    editorContentList = new ArrayList<String>();
    accesibilities.addMenu(menuView);

    textSqlEditor.getStatusBar().addPanel(PANEL_STATUS);

    setCurrentDatabase(accesibilities.getDatabase());
    SwingUtil.addAction(textSqlEditor.getEditorArea(), cmExecute);
    SwingUtil.addAction(textSqlEditor.getEditorArea(), cmExecutedSql);
    SwingUtil.addAction(textSqlEditor.getEditorArea(), cmExecutePartial);
    SwingUtil.addAction(textSqlEditor.getEditorArea(), cmExecuteAsScript);
    SwingUtil.addAction(textSqlEditor.getEditorArea(), cmStore);
    SwingUtil.addAction(textSqlEditor.getEditorArea(), cmSaveFile);
    textSqlEditor.getDocument().addDocumentListener(getDocumentListener());
    
    buttonShowHideError = new ToolButton(cmShowHideError);
    buttonShowHideError.setPreferredSize(new Dimension(18, 18));
    textSqlEditor.getStatusBar().addBefore(textSqlEditor.getStatusBar().getComponent(0), buttonShowHideError);
    buttonShowHideError.setVisible(false);
    buttonShowHideError.setMargin(new Insets(0, 0, 0, 0));
    scrollSqlError.setVisible(false);
    
    addHistoryTab();
    addResultTab();
    addScriptResultTab();
    
    refreshEditorContentList(true);
    if (globalSettings.getValue(UniversalSettingsProvider.setNewEditorContent, false) && textSqlEditor.getText().trim().length() != 0) {
      cmNewEditor.performe();
    }
    initUniversalSerives();
    splitPanel.setResizeWeight(1f);
    if (globalSettings.getValue(UniversalSettingsProvider.setSplitPanelVertical, UniversalSettingsProvider.default_setSplitPanelVertical)) {
      splitPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    }
    splitPanel.setDividerLocation(settings.getValue("split-panel-position", (long)splitPanel.getDividerLocation()).intValue());
    autoSaveEditorContentTimer = new Timer(globalSettings.getValue(UniversalSettingsProvider.setAutoSaveEditorContentIntervalSeconds, 60L).intValue() *1000L) {
      public void run() {
        if (autoSaveEditorContent && currentEditorContent != null && textSqlEditor.isChanged()) {
          try {
            textSqlEditor.saveToFile(currentEditorContent);
            textSqlEditor.setChanged(false);
            storeEditorContentSettings(currentEditorContent);
          } catch (IOException ex) {
            ExceptionUtil.processException(ex);
          }
        }
      }
    };
    autoSaveEditorContent = globalSettings.getValue(UniversalSettingsProvider.setAutoSaveEditorContent, autoSaveEditorContent);
    OrbadaUniversalPlugin.refreshQueue.add(autoSaveEditorContentTimer);
    autoSaveEditorContentTimer.setEnabled(autoSaveEditorContent);
    accesibilities.getApplication().registerRequestMessager(this);
    new ComponentActionsAction(getDatabase(), textSqlEditor.getEditorArea(), buttonActions, menuActions, "universal-sql-commands");
    loadLastOpenedFiles();
    setTransactionActionEnabled();
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        if (settings.getValue(UniversalSettingsProvider.setAutoCloneConnection, (long)0) == 0) {
          if (globalSettings.getValue(UniversalSettingsProvider.setAutoCloneConnection, UniversalSettingsProvider.default_setAutoCloneConnection)) {
            cloneDatabase();
          }
        }
        else if (settings.getValue(UniversalSettingsProvider.setAutoCloneConnection, (long)0) == 1) {
          cloneDatabase();
        }
      }
    });
    orygTitle = accesibilities.getTabTitle();
  }
  
  public void close() throws IOException {
    storeLastOpenedFiles();
    accesibilities.getApplication().unregisterRequestMessager(this);
    int i = 0;
    while (i<tabbedViews.getTabCount()) {
      closePanel(tabbedViews.getComponentAt(i));
    }
    if (currentEditorContent != null) {
      try {
        textSqlEditor.saveToFile(currentEditorContent);
        storeEditorContentSettings(currentEditorContent);
      } catch (IOException ex) {
      }
    }
    setCurrentDatabase(null);
    closeCloneDatabase();
    accesibilities = null;
    tabbedViews.removeAll();
    settings.setValue("split-panel-position", (long)splitPanel.getDividerLocation());
    settings.store();
    autoSaveEditorContentTimer.cancel();
    autoSaveEditorContentTimer = null;
    historyPanel.close();
    historyPanel = null;
    scriptResultPanel.close();
    scriptResultPanel = null;
  }

  private void initUniversalSerives() {
    boolean menuFound = false;
    actionProviders = new ArrayList<UniversalActionProvider>();
    UniversalActionProvider[] uaps = accesibilities.getApplication().getServiceArray(UniversalActionProvider.class);
    if (uaps != null && uaps.length > 0) {
      for (UniversalActionProvider uap : uaps) {
        if (uap.isForDatabase(getDatabase())) {
          actionProviders.add(uap);
          uap.setUniversalViewAccessibilities(new UniversalViewAccessibilities());
          if (uap.addMenuItem()) {
            if (!menuFound) {
              menuView.addSeparator();
              menuFound = true;
            }
            menuView.add(uap);
          }
          if (uap.addToolButton()) {
            toolBar.add(new ToolButton(uap));
          }
          if (uap.addToEditor() && !uap.addMenuItem()) {
            SwingUtil.addAction(textSqlEditor.getEditorArea(), uap);
          }
        }
      }
    }
  }
  
  private void addUniversalViewTab(String title, final JComponent component) {
    tabbedViews.addTab(title, component);
    int index = tabbedViews.indexOfComponent(component);
    tabbedViews.setTabComponentAt(index, new TabCloseComponent(title, new TabCloseAction(component)));
    tabbedViews.setSelectedComponent(component);
  }

  public int getTabNumber() {
    return tabNumber;
  }
  
  public void setTransactionActionEnabled() {
    try {
      cmDbCommit.setEnabled(getDatabase().isStartTransaction());
      cmDbRollback.setEnabled(getDatabase().isStartTransaction());
      cmDBStartTransaction.setEnabled(getDatabase().startTransactionAvailable() && !getDatabase().isStartTransaction());
    } catch (SQLException ex) {
      cmDbCommit.setEnabled(true);
      cmDbRollback.setEnabled(true);
      cmDBStartTransaction.setEnabled(false);
    }
  }
  
  private void updateTabToolip() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        SyntaxDocument sd = textSqlEditor.getDocument();
        try {
          String text = sd.getText(sd.getLineStartOffset(0), sd.getLineEndOffset(0));
          if (text.length() > 2 && "--".equals(text.trim().substring(0, 2))) {
            String caption = text.substring(2).trim();
            if (settings.getValue(UniversalSettingsProvider.setCommentAtFirstLineTitle, false)) {
              accesibilities.setTabExtTooltip(caption);
            }
            else {
              if (!StringUtil.isEmpty(caption)) {
                accesibilities.setTabTitle(caption);
              }
              else {
                accesibilities.setTabTitle(orygTitle);
              }
            }
          }
          else {
            if (settings.getValue(UniversalSettingsProvider.setCommentAtFirstLineTitle, false)) {
              accesibilities.setTabExtTooltip(null);
            }
            else {
              accesibilities.setTabTitle(orygTitle);
            }
          }
        } catch (BadLocationException ex) {
          Logger.getLogger(SqlQueryPanelView.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
  }
  
  private DocumentListener getDocumentListener() {
    return new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        if (e.getOffset() <= 500) {
          updateTabToolip();
        }
      }
      public void removeUpdate(DocumentEvent e) {
        if (e.getOffset() <= 500) {
          updateTabToolip();
        }
      }
      public void changedUpdate(DocumentEvent e) {
      }
    };
  }
  
  public Component[] getResultTabs(Class<? extends Component> clazz) {
    ArrayList<Component> list = new ArrayList<Component>();
    for (int i=0; i<tabbedViews.getTabCount(); i++) {
      Component c = tabbedViews.getComponentAt(i);
      if (clazz.isInstance(c)) {
        list.add(c);
      }
    }
    return list.toArray(new JComponent[list.size()]);
  }

  public void setTabTooltip(final Component comp, final String tooltip) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int index = tabbedViews.indexOfComponent(comp);
        if (index >= 0) {
          tabbedViews.setToolTipTextAt(index, tooltip);
        }
      }
    });
  }
  
  public void setTabTitle(final Component comp, final String title) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int index = tabbedViews.indexOfComponent(comp);
        if (index >= 0) {
          Component tabComp = tabbedViews.getTabComponentAt(index);
          if (tabComp instanceof TabCloseComponent) {
            ((TabCloseComponent)tabComp).setTitle(title);
          }
          else {
            tabbedViews.setTitleAt(index, title);
          }
        }
      }
    });
  }

  private void loadLastOpenedFiles() {
    long count = settings.getValue("last-opened-file-count", 0L);
    for (int i=0; i<count; i++) {
      String file = settings.getValue("last-opened-file-" +i, (String)null);
      if (file != null) {
        lastOpenedFile.add(file);
      }
    }
  }
  
  private void storeLastOpenedFiles() {
    settings.setValue("last-opened-file-count", (long)lastOpenedFile.size());
    for (int i=0; i<lastOpenedFile.size(); i++) {
      settings.setValue("last-opened-file-" +i, lastOpenedFile.get(i));
    }
  }
  
  private void updateLastOpenedFileList(String fileName) {
    int index = lastOpenedFile.indexOf(fileName);
    if (index != -1) {
      lastOpenedFile.remove(index);
    }
    lastOpenedFile.add(0, fileName);
    if (lastOpenedFile.size() > 10) {
      lastOpenedFile.remove(lastOpenedFile.size() -1);
    }
  }
  
  private void createMenuLastOpenedFile(JMenu menu) {
    menu.removeAll();
    for (String s : lastOpenedFile) {
      menu.add(new OpenRecentFileAction(s));
    }
    if (lastOpenedFile.size() == 0) {
      menu.add(cmEmpty);
    }
  }
  
  private void createMenuLastOpenedFile(JPopupMenu menu) {
    menu.removeAll();
    for (String s : lastOpenedFile) {
      menu.add(new OpenRecentFileAction(s));
    }
    if (lastOpenedFile.size() == 0) {
      menu.add(cmEmpty);
    }
  }
  
  private void addHistoryTab() {
    historyPanel = new QueryHistoryPanel();
    String name = stringManager.getString("SqlQueryPanelView-history");
    tabbedViews.addTab(name, historyPanel);
    int index = tabbedViews.indexOfComponent(historyPanel);
    tabbedViews.setTabComponentAt(index, new TabCloseComponent(name));
  }
  
  private void addScriptResultTab() {
    scriptResultPanel = new ScriptResultPanel(accesibilities);
    String name = stringManager.getString("SqlQueryPanelView-command-result");
    tabbedViews.addTab(name, scriptResultPanel);
    int index = tabbedViews.indexOfComponent(scriptResultPanel);
    tabbedViews.setTabComponentAt(index, new TabCloseComponent(name));
  }
  
  public SqlQueryResultPanel addResultTab() {
    int insertAt = tabbedViews.getTabCount() -1;
    for (int i=0; i<tabbedViews.getTabCount(); i++) {
      Component c = tabbedViews.getComponentAt(i);
      if (c instanceof SqlQueryResultPanel) {
        insertAt = i +1;
      }
    }
    SqlQueryResultPanel panel = new SqlQueryResultPanel(this);
    String name = stringManager.getString("result") +(nextNo > 0 ? " " +(nextNo +1) : "");
    tabbedViews.insertTab(name, null, panel, null, insertAt);
    int index = tabbedViews.indexOfComponent(panel);
    Action action = nextNo > 0 ? new TabCloseAction(panel) : new TabAddResultAction();
    tabbedViews.setTabComponentAt(index, new TabCloseComponent(name, action));
    tabbedViews.setSelectedComponent(panel);
    nextNo++;
    return panel;
  }
  
  void fireEventPanel(EventPanel event, Query query, Command command, Exception ex) {
    for (UniversalActionProvider uap : actionProviders) {
      switch (event) {
        case BEFORE_OPEN_QUERY: uap.beforeOpenQuery(query); break;
        case AFTER_OPEN_QUERY: uap.afterOpenQuery(query); break;
        case BEFORE_CLOSE_QUERY: uap.beforeCloseQuery(query); break;
        case AFTER_CLOSE_QUERY: uap.afterCloseQuery(query); break;
        case BEFORE_EXECUTE_COMMAND: uap.beforeExecuteCommand(command); break;
        case AFTER_EXECUTE_COMMAND: uap.afterExecuteCommand(command); break;
        case QUERY_ERROR: uap.queryError(query, ex); break;
        case COMMAND_ERROR: uap.commandError(command, ex); break;
      }
    }
  }
  
  void setUniversalActionServicesProperties(boolean commandTransfotmed, String oryginalCommand) {
    for (UniversalActionProvider uap : actionProviders) {
      uap.setCommandTransformed(commandTransfotmed);
      uap.setOryginalCommand(oryginalCommand);
      uap.setScriptResultPanel(scriptResultPanel);
    }
  }
  
  private void closePanel(Component panel) {
    tabbedViews.remove(panel);
    if (panel instanceof Closeable) {
      try {
        ((Closeable)panel).close();
      } catch (IOException ex) {
      }
    }
  }
  
  public SqlQueryResultPanel getResultPanel() {
    SqlQueryResultPanel result = null;
    int index = tabbedViews.getSelectedIndex();
    if (index >= 0) {
      Component c = tabbedViews.getComponentAt(index);
      if (c instanceof SqlQueryResultPanel) {
        if (!((SqlQueryResultPanel)c).getQuery().isOpening()) {
          result = (SqlQueryResultPanel)c;
        }
      }
    }
    if (result == null) {
      for (int i=0; i<tabbedViews.getTabCount(); i++) {
        Component c = tabbedViews.getComponentAt(i);
        if (c instanceof SqlQueryResultPanel) {
          if (!((SqlQueryResultPanel)c).getQuery().isOpening()) {
            result = (SqlQueryResultPanel)c;
            if (!result.isVisible()) {
              tabbedViews.setSelectedComponent(result);
            }
            break;
          }
        }
      }
    }
    if (result == null) {
      result = addResultTab();
    }
    return result;
  }
  
  private boolean isAutoFile(File file) {
    return Pattern.compile("[0-9]{14}-[0-9A-F]{16}-[0-9A-F]{8}\\.sql", Pattern.CASE_INSENSITIVE).matcher(file.getName()).matches();
  }
  
  private void updateCurrentEditorFileNameLabel() {
    if (currentEditorContent != null) {
      File file = new File(currentEditorContent);
      if (isAutoFile(file)) {
        labelFileName.setText(" ");
      }
      else {
        labelFileName.setText(file.getName());
      }
    }
  }
  
  private void updateCurrentEditorContentFile() {
    int index = editorContentList.indexOf(currentEditorContent);
    currentEditorContent = openedFile.getAbsolutePath();
    if (index != -1) {
      editorContentList.set(index, currentEditorContent);
    }
    updateCurrentEditorFileNameLabel();
  }
  
  private boolean saveFile() {
    if (openedFile != null) {
      try {
        updateLastOpenedFileList(openedFile.getAbsolutePath());
        textSqlEditor.saveToFile(openedFile);
        textSqlEditor.setChanged(false);
        updateCurrentEditorContentFile();
      } catch (IOException ex) {
        MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
        return false;
      }
      return true;
    }
    return saveFileAs();
  }
  
  private boolean saveFileAs() {
    File file = FileUtil.selectFileToSave(this, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("sql-files"), new String[] { ".sql" })});
    if (file != null) {
      openedFile = file;
      if (openedFile != null) {
        return saveFile();
      }
    }
    return false;
  }
  
  private boolean openFile(File file) {
    if (file != null) {
      newEditorContent();
      openedFile = file;
      try {
        updateLastOpenedFileList(openedFile.getAbsolutePath());
        textSqlEditor.loadFromFile(openedFile);
        textSqlEditor.setChanged(false);
        textSqlEditor.getEditorArea().getUndoManager().discardAllEdits();
        updateCurrentEditorContentFile();
        return true;
      } catch (IOException ex) {
        MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
      }
    }
    return false;
  }
  
  private boolean openFile() {
    File file = FileUtil.selectFileToOpen(this, openedFile, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("sql-files"), new String[] { ".sql" })});
    return openFile(file);
  }
  
  private String getEditorContentPath() {
    String path = getApplication().getConfigPath() +"/sql-editor-contents/";
    if (autoSaveEditor == UniversalSettingsProvider.AUTO_SAVE_EDITOR_DRIVER) {
      path = path +getDatabase().getDriverType();
    }
    else if (autoSaveEditor == UniversalSettingsProvider.AUTO_SAVE_EDITOR_DB_SERVER) {
      path = path +FileUtil.prepareName(getDatabase().getUrl());
    }
    else if (autoSaveEditor == UniversalSettingsProvider.AUTO_SAVE_EDITOR_SCHEMA) {
      path = path +getDatabase().getUserProperties().getProperty("schemaId");
    }
    else {
      path = path +"unknown";
    }
    if (tabNumber > 0) {
      path = path +"-no-" +tabNumber;
    }
    return path;
  }
  
  private void updateEditorContentActions() {
    cmPrevEditor.setEnabled(currentEditorContent != null && editorContentList.indexOf(currentEditorContent) > 0);
    cmNextEditor.setEnabled(currentEditorContent != null && editorContentList.indexOf(currentEditorContent) < editorContentList.size() -1);
  }
  
  private void refreshEditorContentList(boolean autoLoad) {
    String configPath = getEditorContentPath();
    File dir = new File(configPath);
    dir.mkdirs();
    String[] fileList = dir.list(new PatternFileFilter(WildCard.getRegex("*.sql")));
    Arrays.sort(fileList);
    editorContentList.clear();
    for (int i=0; i<fileList.length; i++) {
      if (i > fileList.length -100) {
        editorContentList.add(configPath +"/" +fileList[i]);
      }
      else {
        new File(configPath +"/" +fileList[i]).delete();
      }
    }
    if (currentEditorContent == null || !editorContentList.contains(currentEditorContent)) {
      if (editorContentList.size() > 0) {
        currentEditorContent = editorContentList.get(editorContentList.size() -1);
        if (autoLoad) {
          try {
            textSqlEditor.loadFromFile(currentEditorContent);
            textSqlEditor.setChanged(false);
            textSqlEditor.getEditorArea().getUndoManager().discardAllEdits();
            restoreEditorContentSettings(currentEditorContent);
          } catch (Throwable ex) {
          }
        }
      }
    }
    if (currentEditorContent == null) {
      newEditorContent();
    }
    else {
      updateEditorContentActions();
    }
    updateCurrentEditorFileNameLabel();
  }
  
  private void newEditorContent() {
    if (currentEditorContent != null) {
      try {
        textSqlEditor.saveToFile(currentEditorContent);
        storeEditorContentSettings(currentEditorContent);
      } catch (IOException ex) {
      }
    }
    openedFile = null;
    currentEditorContent = getEditorContentPath() +"/" +new UniqueID().toString() +".sql";
    editorContentList.add(currentEditorContent);
    textSqlEditor.setText("");
    textSqlEditor.setChanged(false);
    textSqlEditor.getEditorArea().getUndoManager().discardAllEdits();
    updateEditorContentActions();
    updateCurrentEditorFileNameLabel();
  }
  
  private void deleteEditorContent() {
    if (currentEditorContent != null) {
      if (textSqlEditor.isChanged() &&
          MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("SqlQueryPanelView-del-content-no-save-q"), ModalResult.YESNO) != ModalResult.YES) {
        return;
      }
      else if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("SqlQueryPanelView-del-content-q"), ModalResult.YESNO) != ModalResult.YES) {
        return;
      }
      int index = editorContentList.indexOf(currentEditorContent);
      if (openedFile == null || !currentEditorContent.equals(openedFile.getAbsolutePath())) {
        new File(currentEditorContent).delete();
        deleteEditorContentSettings(currentEditorContent);
      }
      editorContentList.remove(index);
      currentEditorContent = null;
      if (editorContentList.size() > 0) {
        currentEditorContent = editorContentList.get(index < editorContentList.size() ? index : index -1);
        try {
          textSqlEditor.loadFromFile(currentEditorContent);
          textSqlEditor.setChanged(false);
          restoreEditorContentSettings(currentEditorContent);
        } catch (IOException ex) {
        }
        File file = new File(currentEditorContent);
        if (!isAutoFile(file)) {
          openedFile = file;
        }
        else {
          openedFile = null;
        }
        updateEditorContentActions();
      }
      else {
        newEditorContent();
      }
    }
    updateCurrentEditorFileNameLabel();
  }
  
  public void processMessage(PluginMessage message) {
    if (message.isMessageId(OrbadaUniversalPlugin.universalSettingsRefresh)) {
      autoSaveEditorContentTimer.setInterval(globalSettings.getValue(UniversalSettingsProvider.setAutoSaveEditorContentIntervalSeconds, 60L).intValue() *1000L);
      autoSaveEditorContent = globalSettings.getValue(UniversalSettingsProvider.setAutoSaveEditorContent, autoSaveEditorContent);
      autoSaveEditorContentTimer.setEnabled(autoSaveEditorContent);
    }
    if (Consts.globalMessageTransactionStateChange.equals(message.getMessageId())) {
      setTransactionActionEnabled();
    }
  }

  private void restoreEditorContentSettings(String fileName) {
    Properties properties = new Properties();
    try {
      FileInputStream fis = new FileInputStream(fileName + ".properties");
      try {
        properties.load(fis);
        String lastCaretPosition = properties.getProperty("last-caret-position");
        String lastCaretView = properties.getProperty("last-caret-view");
        if (lastCaretPosition != null) {
          textSqlEditor.getEditorArea().setCaretPosition(Integer.parseInt(lastCaretPosition));
          if (lastCaretView != null) {
            textSqlEditor.getScrollPane().getVerticalScrollBar().setValue(Integer.parseInt(lastCaretView));
          }
        }
      }
      finally {
        fis.close();
      }
    }
    catch (Exception ex) {
    }
    finally {
      properties = null;
    }
  }

  private void storeEditorContentSettings(String fileName) {
    Properties properties = new Properties();
    try {
      properties.setProperty("last-caret-position", Integer.toString(textSqlEditor.getEditorArea().getCaretPosition()));
      properties.setProperty("last-caret-view", Integer.toString(textSqlEditor.getScrollPane().getVerticalScrollBar().getValue()));
      FileOutputStream fos = new FileOutputStream(fileName + ".properties");
      try {
        properties.store(fos, null);
      }
      finally {
        fos.close();
      }
    }
    catch (Exception ex) {
    }
    finally {
      properties = null;
    }
  }

  private void deleteEditorContentSettings(String fileName) {
    new File(fileName + ".properties").delete();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmExecute = new pl.mpak.sky.gui.swing.Action();
        cmExecuteAsScript = new pl.mpak.sky.gui.swing.Action();
        cmShowHideError = new pl.mpak.sky.gui.swing.Action();
        cmExecutedSql = new pl.mpak.sky.gui.swing.Action();
        cmSaveFile = new pl.mpak.sky.gui.swing.Action();
        cmSaveFileAs = new pl.mpak.sky.gui.swing.Action();
        cmOpenFile = new pl.mpak.sky.gui.swing.Action();
        cmPrevEditor = new pl.mpak.sky.gui.swing.Action();
        cmNextEditor = new pl.mpak.sky.gui.swing.Action();
        cmNewEditor = new pl.mpak.sky.gui.swing.Action();
        cmDeleteEditor = new pl.mpak.sky.gui.swing.Action();
        menuView = new javax.swing.JMenu();
        menuExecute = new javax.swing.JMenuItem();
        menuStore = new javax.swing.JMenuItem();
        menuExecuteAsScript = new javax.swing.JMenuItem();
        menuExecutedSql = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        menuOpenFile = new javax.swing.JMenuItem();
        menuSaveFile = new javax.swing.JMenuItem();
        menuSaveFileAs = new javax.swing.JMenuItem();
        menuRecentFiles = new javax.swing.JMenu();
        jSeparator10 = new javax.swing.JSeparator();
        menuNewEditor = new javax.swing.JMenuItem();
        menuPrevEditor = new javax.swing.JMenuItem();
        menuNextEditor = new javax.swing.JMenuItem();
        menuDeleteEditor = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        menuCloneDatabase = new javax.swing.JMenuItem();
        menuSelectDatabase = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        menuCommit = new javax.swing.JMenuItem();
        menuRollback = new javax.swing.JMenuItem();
        cmSelectDatabase = new pl.mpak.sky.gui.swing.Action();
        cmStore = new pl.mpak.sky.gui.swing.Action();
        cmCloneDatabase = new pl.mpak.sky.gui.swing.Action();
        cmExecutePartial = new pl.mpak.sky.gui.swing.Action();
        menuActions = new javax.swing.JPopupMenu();
        popupRecentFiles = new javax.swing.JPopupMenu();
        cmPopupRecentFiles = new pl.mpak.sky.gui.swing.Action();
        cmDbCommit = new pl.mpak.sky.gui.swing.Action();
        cmDbRollback = new pl.mpak.sky.gui.swing.Action();
        cmEmpty = new pl.mpak.sky.gui.swing.Action();
        cmDBStartTransaction = new pl.mpak.sky.gui.swing.Action();
        jPanel1 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        buttonOpenFile = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonRecentFiles = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        buttonSaveFile = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonSaveFileAs = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator3 = new javax.swing.JSeparator();
        buttonNewEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonPervEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonNextEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator4 = new javax.swing.JSeparator();
        buttonDeleteEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator2 = new javax.swing.JSeparator();
        buttonExecute = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonStore = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonExecuteAsScript = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonExecutedSql = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JSeparator();
        buttonCloneDatabase = new pl.mpak.sky.gui.swing.comp.ToolButton();
        toolButton2 = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonDbCommit = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonDbRollback = new pl.mpak.sky.gui.swing.comp.ToolButton();
        toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
        labelSelectedDatabase = new javax.swing.JLabel();
        buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        labelFileName = new javax.swing.JLabel();
        splitPanel = new javax.swing.JSplitPane();
        panelSqlEditor = new javax.swing.JPanel();
        textSqlEditor = new OrbadaSyntaxTextArea();
        scrollSqlError = new javax.swing.JScrollPane();
        textSqlError = new javax.swing.JTextArea();
        tabbedViews = new javax.swing.JTabbedPane();

        cmExecute.setActionCommandKey("cmExecute");
        cmExecute.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
        cmExecute.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute16.gif")); // NOI18N
        cmExecute.setText(stringManager.getString("cmExecute-text")); // NOI18N
        cmExecute.setTooltip(stringManager.getString("cmExecute-hint")); // NOI18N
        cmExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmExecuteActionPerformed(evt);
            }
        });

        cmExecuteAsScript.setActionCommandKey("cmExecuteAsScript");
        cmExecuteAsScript.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.CTRL_MASK));
        cmExecuteAsScript.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_script_execute.gif")); // NOI18N
        cmExecuteAsScript.setText(stringManager.getString("cmExecuteAsScript-text")); // NOI18N
        cmExecuteAsScript.setTooltip(stringManager.getString("cmExecuteAsScript-hint")); // NOI18N
        cmExecuteAsScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmExecuteAsScriptActionPerformed(evt);
            }
        });

        cmShowHideError.setActionCommandKey("cmShowHideError");
        cmShowHideError.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/delete_square16.gif")); // NOI18N
        cmShowHideError.setTooltip(stringManager.getString("cmShowHideError-hint")); // NOI18N
        cmShowHideError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmShowHideErrorActionPerformed(evt);
            }
        });

        cmExecutedSql.setActionCommandKey("cmExecutedSql");
        cmExecutedSql.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.CTRL_MASK));
        cmExecutedSql.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/executed_sql16.gif")); // NOI18N
        cmExecutedSql.setText(stringManager.getString("cmExecutedSql-text")); // NOI18N
        cmExecutedSql.setTooltip(stringManager.getString("cmExecutedSql-hint")); // NOI18N
        cmExecutedSql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmExecutedSqlActionPerformed(evt);
            }
        });

        cmSaveFile.setActionCommandKey("cmSaveFile");
        cmSaveFile.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        cmSaveFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save16.gif")); // NOI18N
        cmSaveFile.setText(stringManager.getString("cmSaveFile-text")); // NOI18N
        cmSaveFile.setTooltip(stringManager.getString("cmSaveFile-hint")); // NOI18N
        cmSaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSaveFileActionPerformed(evt);
            }
        });

        cmSaveFileAs.setActionCommandKey("cmSaveFileAs");
        cmSaveFileAs.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save_as16.gif")); // NOI18N
        cmSaveFileAs.setText(stringManager.getString("cmSaveFileAs-text")); // NOI18N
        cmSaveFileAs.setTooltip(stringManager.getString("cmSaveFileAs-hint")); // NOI18N
        cmSaveFileAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSaveFileAsActionPerformed(evt);
            }
        });

        cmOpenFile.setActionCommandKey("cmOpenFile");
        cmOpenFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/open_document16.gif")); // NOI18N
        cmOpenFile.setText(stringManager.getString("cmOpenFile-text")); // NOI18N
        cmOpenFile.setTooltip(stringManager.getString("cmOpenFile-hint")); // NOI18N
        cmOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOpenFileActionPerformed(evt);
            }
        });

        cmPrevEditor.setActionCommandKey("cmPrevEditor");
        cmPrevEditor.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        cmPrevEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/arrowup.gif")); // NOI18N
        cmPrevEditor.setText(stringManager.getString("cmPrevEditor-text")); // NOI18N
        cmPrevEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPrevEditorActionPerformed(evt);
            }
        });

        cmNextEditor.setActionCommandKey("cmNextEditor");
        cmNextEditor.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        cmNextEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/arrowdown.gif")); // NOI18N
        cmNextEditor.setText(stringManager.getString("cmNextEditor-text")); // NOI18N
        cmNextEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmNextEditorActionPerformed(evt);
            }
        });

        cmNewEditor.setActionCommandKey("cmNewEditor");
        cmNewEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_document16.gif")); // NOI18N
        cmNewEditor.setText(stringManager.getString("cmNewEditor-text")); // NOI18N
        cmNewEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmNewEditorActionPerformed(evt);
            }
        });

        cmDeleteEditor.setActionCommandKey("cmDeleteEditor");
        cmDeleteEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/minus16.gif")); // NOI18N
        cmDeleteEditor.setText(stringManager.getString("cmDeleteEditor-text")); // NOI18N
        cmDeleteEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDeleteEditorActionPerformed(evt);
            }
        });

        menuExecute.setAction(cmExecute);
        menuView.add(menuExecute);

        menuStore.setAction(cmStore);
        menuView.add(menuStore);

        menuExecuteAsScript.setAction(cmExecuteAsScript);
        menuView.add(menuExecuteAsScript);

        menuExecutedSql.setAction(cmExecutedSql);
        menuView.add(menuExecutedSql);
        menuView.add(jSeparator8);

        menuOpenFile.setAction(cmOpenFile);
        menuView.add(menuOpenFile);

        menuSaveFile.setAction(cmSaveFile);
        menuView.add(menuSaveFile);

        menuSaveFileAs.setAction(cmSaveFileAs);
        menuView.add(menuSaveFileAs);

        menuRecentFiles.setText("Ostatnio otwarte pliki");
        menuRecentFiles.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                menuRecentFilesMenuSelected(evt);
            }
        });
        menuView.add(menuRecentFiles);
        menuView.add(jSeparator10);

        menuNewEditor.setAction(cmNewEditor);
        menuView.add(menuNewEditor);

        menuPrevEditor.setAction(cmPrevEditor);
        menuView.add(menuPrevEditor);

        menuNextEditor.setAction(cmNextEditor);
        menuView.add(menuNextEditor);

        menuDeleteEditor.setAction(cmDeleteEditor);
        menuView.add(menuDeleteEditor);
        menuView.add(jSeparator7);

        menuCloneDatabase.setAction(cmCloneDatabase);
        menuView.add(menuCloneDatabase);

        menuSelectDatabase.setAction(cmSelectDatabase);
        menuView.add(menuSelectDatabase);
        menuView.add(jSeparator5);

        menuCommit.setAction(cmDbCommit);
        menuView.add(menuCommit);

        menuRollback.setAction(cmDbRollback);
        menuView.add(menuRollback);

        cmSelectDatabase.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/select16.gif")); // NOI18N
        cmSelectDatabase.setText(stringManager.getString("cmSelectDatabase-text")); // NOI18N
        cmSelectDatabase.setTooltip(stringManager.getString("cmSelectDatabase-hint")); // NOI18N
        cmSelectDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSelectDatabaseActionPerformed(evt);
            }
        });

        cmStore.setActionCommandKey("cmStore");
        cmStore.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        cmStore.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_store.gif")); // NOI18N
        cmStore.setText(stringManager.getString("cmStore-text")); // NOI18N
        cmStore.setTooltip(stringManager.getString("cmStore-hint")); // NOI18N
        cmStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmStoreActionPerformed(evt);
            }
        });

        cmCloneDatabase.setActionCommandKey("cmCloneDatabase");
        cmCloneDatabase.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/clone_database.gif")); // NOI18N
        cmCloneDatabase.setText(stringManager.getString("cmCloneDatabase-text")); // NOI18N
        cmCloneDatabase.setTooltip(stringManager.getString("cmCloneDatabase-hint")); // NOI18N
        cmCloneDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCloneDatabaseActionPerformed(evt);
            }
        });

        cmExecutePartial.setActionCommandKey("cmExecutePartial");
        cmExecutePartial.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        cmExecutePartial.setText(stringManager.getString("cmExecutePartial-text")); // NOI18N
        cmExecutePartial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmExecutePartialActionPerformed(evt);
            }
        });

        popupRecentFiles.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupRecentFilesPopupMenuWillBecomeVisible(evt);
            }
        });

        cmPopupRecentFiles.setActionCommandKey("cmPopupRecentFiles");
        cmPopupRecentFiles.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/down10.gif")); // NOI18N
        cmPopupRecentFiles.setText(stringManager.getString("cmPopupRecentFiles-text")); // NOI18N
        cmPopupRecentFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPopupRecentFilesActionPerformed(evt);
            }
        });

        cmDbCommit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/commit.gif")); // NOI18N
        cmDbCommit.setText(stringManager.getString("cmDbCommit-text")); // NOI18N
        cmDbCommit.setTooltip(stringManager.getString("cmDbCommit-hint")); // NOI18N
        cmDbCommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDbCommitActionPerformed(evt);
            }
        });

        cmDbRollback.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/rollback.gif")); // NOI18N
        cmDbRollback.setText(stringManager.getString("cmDbRollback-text")); // NOI18N
        cmDbRollback.setTooltip(stringManager.getString("cmDbRollback-hint")); // NOI18N
        cmDbRollback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDbRollbackActionPerformed(evt);
            }
        });

        cmEmpty.setEnabled(false);
        cmEmpty.setText(stringManager.getString("cmEmpty-text")); // NOI18N

        cmDBStartTransaction.setActionCommandKey("cmDBStartTransaction");
        cmDBStartTransaction.setEnabled(false);
        cmDBStartTransaction.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/start_transaction.gif")); // NOI18N
        cmDBStartTransaction.setText(stringManager.getString("cmDBStartTransaction-text")); // NOI18N
        cmDBStartTransaction.setTooltip(stringManager.getString("cmDBStartTransaction-hint")); // NOI18N
        cmDBStartTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDBStartTransactionActionPerformed(evt);
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        buttonOpenFile.setAction(cmOpenFile);
        buttonOpenFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonOpenFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonOpenFile);

        buttonRecentFiles.setAction(cmPopupRecentFiles);
        buttonRecentFiles.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRecentFiles.setMaximumSize(new java.awt.Dimension(12, 26));
        buttonRecentFiles.setMinimumSize(new java.awt.Dimension(12, 26));
        buttonRecentFiles.setPreferredSize(new java.awt.Dimension(12, 26));
        buttonRecentFiles.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonRecentFiles);
        toolBar.add(jSeparator9);

        buttonSaveFile.setAction(cmSaveFile);
        buttonSaveFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSaveFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonSaveFile);

        buttonSaveFileAs.setAction(cmSaveFileAs);
        buttonSaveFileAs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSaveFileAs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonSaveFileAs);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.add(jSeparator3);

        buttonNewEditor.setAction(cmNewEditor);
        buttonNewEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNewEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonNewEditor);

        buttonPervEditor.setAction(cmPrevEditor);
        buttonPervEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPervEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonPervEditor);

        buttonNextEditor.setAction(cmNextEditor);
        buttonNextEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNextEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonNextEditor);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.add(jSeparator4);

        buttonDeleteEditor.setAction(cmDeleteEditor);
        buttonDeleteEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDeleteEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonDeleteEditor);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.add(jSeparator2);

        buttonExecute.setAction(cmExecute);
        buttonExecute.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonExecute.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonExecute);

        buttonStore.setAction(cmStore);
        buttonStore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonStore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonStore);

        buttonExecuteAsScript.setAction(cmExecuteAsScript);
        buttonExecuteAsScript.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonExecuteAsScript.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonExecuteAsScript);

        buttonExecutedSql.setAction(cmExecutedSql);
        toolBar.add(buttonExecutedSql);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.add(jSeparator1);

        buttonCloneDatabase.setAction(cmCloneDatabase);
        buttonCloneDatabase.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCloneDatabase.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonCloneDatabase);

        toolButton2.setAction(cmDBStartTransaction);
        toolButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(toolButton2);

        buttonDbCommit.setAction(cmDbCommit);
        buttonDbCommit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDbCommit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonDbCommit);

        buttonDbRollback.setAction(cmDbRollback);
        buttonDbRollback.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDbRollback.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonDbRollback);

        toolButton1.setAction(cmSelectDatabase);
        toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(toolButton1);

        labelSelectedDatabase.setText(" ");
        toolBar.add(labelSelectedDatabase);

        buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(buttonActions);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.add(jSeparator6);

        jPanel1.add(toolBar, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        labelFileName.setText(" ");
        jPanel2.add(labelFileName);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        splitPanel.setBorder(null);
        splitPanel.setDividerLocation(250);
        splitPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPanel.setResizeWeight(1.0);
        splitPanel.setContinuousLayout(true);
        splitPanel.setOneTouchExpandable(true);

        panelSqlEditor.setPreferredSize(new java.awt.Dimension(146, 300));
        panelSqlEditor.setLayout(new java.awt.BorderLayout());
        panelSqlEditor.add(textSqlEditor, java.awt.BorderLayout.CENTER);

        textSqlError.setColumns(20);
        textSqlError.setEditable(false);
        textSqlError.setFont(new java.awt.Font("Courier New", 0, 11));
        textSqlError.setRows(5);
        scrollSqlError.setViewportView(textSqlError);

        panelSqlEditor.add(scrollSqlError, java.awt.BorderLayout.SOUTH);

        splitPanel.setTopComponent(panelSqlEditor);

        tabbedViews.setFocusable(false);
        tabbedViews.setPreferredSize(new java.awt.Dimension(100, 80));
        splitPanel.setBottomComponent(tabbedViews);

        add(splitPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
      textSqlEditor.getEditorArea().requestFocusInWindow();
    }
  });
}//GEN-LAST:event_formComponentShown

private void cmSelectDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectDatabaseActionPerformed
  Database database = SelectDatabaseDialog.show(getDatabase(), false);
  if (database != null) {
    if (clonedDatabase != null && !database.equals(clonedDatabase)) {
      closeCloneDatabase();
      buttonCloneDatabase.setSelected(false);
      menuCloneDatabase.setSelected(false);
    }
    setCurrentDatabase(database);
  }
}//GEN-LAST:event_cmSelectDatabaseActionPerformed

private void cmPrevEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPrevEditorActionPerformed
  if (currentEditorContent != null) {
    try {
      textSqlEditor.saveToFile(currentEditorContent);
      storeEditorContentSettings(currentEditorContent);
    } catch (IOException ex) {
    }
    int index = editorContentList.indexOf(currentEditorContent);
    if (index > 0) {
      currentEditorContent = editorContentList.get(index -1);
      try {
        textSqlEditor.loadFromFile(currentEditorContent);
        restoreEditorContentSettings(currentEditorContent);
      } catch (IOException ex) {
      }
    }
    textSqlEditor.setChanged(false);
    textSqlEditor.getEditorArea().getUndoManager().discardAllEdits();
    File file = new File(currentEditorContent);
    if (!isAutoFile(file)) {
      openedFile = file;
    }
    else {
      openedFile = null;
    }
  }
  updateEditorContentActions();  
  updateCurrentEditorFileNameLabel();
}//GEN-LAST:event_cmPrevEditorActionPerformed

private void cmNextEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNextEditorActionPerformed
  if (currentEditorContent != null) {
    try {
      textSqlEditor.saveToFile(currentEditorContent);
      storeEditorContentSettings(currentEditorContent);
    } catch (IOException ex) {
    }
    int index = editorContentList.indexOf(currentEditorContent);
    if (index < editorContentList.size() -1) {
      currentEditorContent = editorContentList.get(index +1);
      try {
        textSqlEditor.loadFromFile(currentEditorContent);
        restoreEditorContentSettings(currentEditorContent);
      } catch (IOException ex) {
      }
    }
    textSqlEditor.setChanged(false);
    textSqlEditor.getEditorArea().getUndoManager().discardAllEdits();
    File file = new File(currentEditorContent);
    if (!isAutoFile(file)) {
      openedFile = file;
    }
    else {
      openedFile = null;
    }
  }
  updateEditorContentActions();  
  updateCurrentEditorFileNameLabel();
}//GEN-LAST:event_cmNextEditorActionPerformed

private void cmDeleteEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteEditorActionPerformed
  deleteEditorContent();
}//GEN-LAST:event_cmDeleteEditorActionPerformed

private void cmNewEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewEditorActionPerformed
  newEditorContent();
}//GEN-LAST:event_cmNewEditorActionPerformed

private void cmSaveFileAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSaveFileAsActionPerformed
  saveFileAs();
}//GEN-LAST:event_cmSaveFileAsActionPerformed

private void cmSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSaveFileActionPerformed
  saveFile();
}//GEN-LAST:event_cmSaveFileActionPerformed

private void cmOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOpenFileActionPerformed
  if (textSqlEditor.isChanged() && openedFile != null) {
    switch (MessageBox.show((Frame)null, stringManager.getString("saving-file"), String.format(stringManager.getString("SqlQueryPanelView-content-changed-save-it-q"), new Object[] {openedFile.getName()}), ModalResult.YESNOCANCEL)) {
      case ModalResult.YES: {
        if (!saveFile()) {
          return;
        }
        break;
      }
      case ModalResult.CANCEL:
        return;
    }
  }
  openFile();
}//GEN-LAST:event_cmOpenFileActionPerformed
  
private void cmExecutedSqlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExecutedSqlActionPerformed
  String sqlText = ExecutedSqlCommandDialog.showDialog(getDatabase());
  if (sqlText != null) {
    if (textSqlEditor.getEditorArea().getSelectionEnd() != textSqlEditor.getEditorArea().getSelectionStart()) {
      textSqlEditor.getEditorArea().replaceSelection(sqlText);
    }
    else {
      textSqlEditor.getEditorArea().insert(sqlText +"\n", textSqlEditor.getEditorArea().getCaretPosition());
    }
  }
}//GEN-LAST:event_cmExecutedSqlActionPerformed

private void cmShowHideErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmShowHideErrorActionPerformed
  java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
      scrollSqlError.setVisible(!scrollSqlError.isVisible());
    }
  });
}//GEN-LAST:event_cmShowHideErrorActionPerformed

private void cmExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExecuteActionPerformed
  final java.awt.event.ActionEvent event = evt;
  java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
      scrollSqlError.setVisible(false);
      buttonShowHideError.setVisible(false);
    }
  });
  
  String currentSqlText = textSqlEditor.getCurrentText();
  if (currentSqlText == null) {
    textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +stringManager.getString("SqlQueryPanelView-no-current-sql-e") +" ");
    return;
  }
  
  String oryginalSqlText = currentSqlText;
  boolean changedSqlText = false;
  UniversalSqlTextTransformProvider[] usttp = accesibilities.getApplication().getServiceArray(UniversalSqlTextTransformProvider.class);
  if (usttp != null && usttp.length > 0) {
    for (int i=0; i<usttp.length; i++) {
      if (usttp[i].isForDatabase(getDatabase())) {
        String newSqlText = usttp[i].transformSqlText(currentDatabase, currentSqlText);
        if (newSqlText != null || "".equals(newSqlText)) {
          currentSqlText = newSqlText;
          changedSqlText = true;
          //break;
        }
      }
    }
  }
  
  if (StringUtil.isEmpty(currentSqlText)) {
    textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +stringManager.getString("SqlQueryPanelView-no-command-e") +" ");
    return;
  }
  
  setUniversalActionServicesProperties(changedSqlText, oryginalSqlText);

  final boolean isSelect = SQLUtil.isSelect(currentSqlText);
  if (!"cmStore".equals(event.getActionCommand())) {
    String tempText = SQLUtil.removeComments(currentSqlText);
    tempText = SQLUtil.removeLineComment(tempText);
    tempText = SQLUtil.removeStrings(tempText);
    if (tempText.length() > 1 && StringUtil.charCount(tempText, ';') == 1 && tempText.charAt(tempText.length() -1) == ';') {
      currentSqlText = currentSqlText.substring(0, currentSqlText.length() -1);
    }
  }
  final String sqlText = currentSqlText;
  
  if (globalSettings.getValue(UniversalSettingsProvider.setAutoExpandSqlText, false) && changedSqlText && 
      textSqlEditor.getEditorArea().getSelectionStart() == textSqlEditor.getEditorArea().getSelectionEnd()) {
    textSqlEditor.getEditorArea().setSelectionStart(textSqlEditor.getStartCurrentText());
    textSqlEditor.getEditorArea().setSelectionEnd(textSqlEditor.getEndCurrentText());
    textSqlEditor.getEditorArea().replaceSelection(sqlText);
  }

  textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +stringManager.getString("SqlQueryPanelView-executed-command-3d"));
  if (isSelect && !"cmStore".equals(event.getActionCommand())) {
    final SqlQueryResultPanel panel = getResultPanel();
    final TabCloseComponent tabComponent = (TabCloseComponent)tabbedViews.getTabComponentAt(tabbedViews.indexOfComponent(panel));
    tabComponent.setCloseable(false);
    getDatabase().getTaskPool().addTask(new Task(String.format(stringManager.getString("SqlQueryPanelView-universal-query-exec-3d"), new Object[] {sqlText.substring(0, Math.min(50, sqlText.length()))})) {
      public void run() {
        if (!SqlQueryPanelView.this.isVisible() || isCanceled()) {
          return;
        }
        PleaseWait wait = PleaseWait.createSqlWait(sqlText.substring(0, Math.min(50, sqlText.length())), 2000);
        accesibilities.getApplication().startPleaseWait(wait);
        try {
          if (!panel.openQuery(sqlText)) {
            textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +stringManager.getString("SqlQueryPanelView-cancel-execution-info") +" ");
          }
          else {
            historyPanel.add(panel.getQuery());
            ExecutedSqlManager.get().addSqlText(sqlText, getDatabase().getUserProperties().getProperty("schemaId"));
            textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +stringManager.getString("SqlQueryPanelView-command-executed-properly-info") +" ");
          }
          tabComponent.setCloseable(true);
          accesibilities.getApplication().stopPleaseWait(wait);
        } catch (final Throwable e) {
          accesibilities.getApplication().stopPleaseWait(wait);
          String message = e.getMessage();
          if (message != null) {
            if (message.length() > 120) {
              message = e.getMessage().substring(0, 120) +"...";
            }
            textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +message +" ");
          }
          else {
            ExceptionUtil.processException(e);
          }
          textSqlError.setText(ExceptionUtil.getStackTrace(e));
          if (e instanceof Exception) {
            historyPanel.add(panel.getQuery());
            fireEventPanel(EventPanel.QUERY_ERROR, panel.getQuery(), null, (Exception)e);
          }
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              buttonShowHideError.setVisible(true);
              scrollSqlError.setVisible(false);
              tabComponent.setCloseable(true);
              if (globalSettings.getValue(UniversalSettingsProvider.setOnErrorShowMessageBox, false)) {
                ErrorBox.show(e);
              }
            }
          });
        }
      }
    });
  } else {
    getDatabase().getTaskPool().addTask(new Task(String.format(stringManager.getString("SqlQueryPanelView-universal-call-execution-3d"), new Object[] {sqlText.substring(0, Math.min(50, sqlText.length()))})) {
      public void run() {
        if (!SqlQueryPanelView.this.isVisible() || isCanceled()) {
          return;
        }
        PleaseWait wait = PleaseWait.createSqlWait(sqlText.substring(0, Math.min(50, sqlText.length())), 2000);
        accesibilities.getApplication().startPleaseWait(wait);
        Command command = getDatabase().createCommand();
        try {
          if ("cmStore".equals(event.getActionCommand())) {
            command.setParamCheck(false);
          }
          command.setSqlText(sqlText);
          if (command.getParameterList().parameterCount() > 0) {
            if (!CommandParametersDialog.showDialog(command)) {
              textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +stringManager.getString("SqlQueryPanelView-cancel-execution-info") +" ");
              accesibilities.getApplication().stopPleaseWait(wait);
              return;
            }
          }
          fireEventPanel(EventPanel.BEFORE_EXECUTE_COMMAND, null, command, null);
          scriptResultPanel.setCommand(command);
          try {
            command.execute();
          }
          finally {
            scriptResultPanel.setCommand(null);
          }
          fireEventPanel(EventPanel.AFTER_EXECUTE_COMMAND, null, command, null);
          if (command.getStatement() != null) {
            ResultSet rs = command.getStatement().getResultSet();
            while (rs != null) {
              SqlQueryResultPanel panel = getResultPanel();
              panel.closeQuery();
              panel.getQuery().setResultSet(rs);
              if (!command.getStatement().getMoreResults()) {
                break;
              }
              rs = command.getStatement().getResultSet();
            }
          }
          boolean outParams = false;
          for (int i=0; i<command.getParameterCount(); i++) {
            if (command.getParameter(i).getParamMode() == ParameterMetaData.parameterModeOut || command.getParameter(i).getParamMode() == ParameterMetaData.parameterModeInOut) {
              scriptResultPanel.append(command.getParameter(i).getParamName() +" = [" +command.getParameter(i).getString() +"]\n");
              outParams = true;
            }
          }
          
          historyPanel.add(command);
          ExecutedSqlManager.get().addSqlText(sqlText, getDatabase().getUserProperties().getProperty("schemaId"));
          textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +
            String.format(
              stringManager.getString("SqlQueryPanelView-command-exec-properly-records-info") +", " +
              StringUtil.formatTime(command.getExecutionTime()),
              new Object[] {command.getUpdateCount()}) +
            (outParams ? stringManager.getString("SqlQueryPanelView-command-exec-out-params-info") : "")
          );
          accesibilities.getApplication().stopPleaseWait(wait);
        } catch (final Throwable e) {
          accesibilities.getApplication().stopPleaseWait(wait);
          String message = e.getMessage();
          if (message != null && message.length() > 120) {
            message = e.getMessage().substring(0, 120) +"...";
          }
          textSqlEditor.getStatusBar().getPanel(PANEL_STATUS).setText(" " +message +" ");
          textSqlError.setText(ExceptionUtil.getStackTrace(e));
          if (e instanceof Exception) {
            historyPanel.add(command);
            fireEventPanel(EventPanel.COMMAND_ERROR, null, command, (Exception)e);
          }
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              buttonShowHideError.setVisible(true);
              scrollSqlError.setVisible(false);
              if (globalSettings.getValue(UniversalSettingsProvider.setOnErrorShowMessageBox, false)) {
                ErrorBox.show(e);
              }
            }
          });
        }
      }
    });
  }
}//GEN-LAST:event_cmExecuteActionPerformed

private void cmExecuteAsScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExecuteAsScriptActionPerformed
  tabbedViews.setSelectedComponent(scriptResultPanel);
  scriptResultPanel.execute(getDatabase(), textSqlEditor.getText());
}//GEN-LAST:event_cmExecuteAsScriptActionPerformed

private void cmStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmStoreActionPerformed
  cmExecute.actionPerformed(evt);
}//GEN-LAST:event_cmStoreActionPerformed

private void cmCloneDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloneDatabaseActionPerformed
  if (buttonCloneDatabase.isSelected()) {
    closeCloneDatabase();
  }
  else {
    cloneDatabase();
  }
}//GEN-LAST:event_cmCloneDatabaseActionPerformed

private void cmExecutePartialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExecutePartialActionPerformed
  cmExecute.actionPerformed(evt);
}//GEN-LAST:event_cmExecutePartialActionPerformed

private void menuRecentFilesMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_menuRecentFilesMenuSelected
  createMenuLastOpenedFile(menuRecentFiles);
}//GEN-LAST:event_menuRecentFilesMenuSelected

private void popupRecentFilesPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popupRecentFilesPopupMenuWillBecomeVisible
  createMenuLastOpenedFile(popupRecentFiles);
}//GEN-LAST:event_popupRecentFilesPopupMenuWillBecomeVisible

private void cmPopupRecentFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPopupRecentFilesActionPerformed
  popupRecentFiles.show(buttonRecentFiles, 0, buttonRecentFiles.getHeight());
}//GEN-LAST:event_cmPopupRecentFilesActionPerformed

private void cmDbCommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDbCommitActionPerformed
  try {
    getDatabase().commit();
  } catch (SQLException ex) {
    MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmDbCommitActionPerformed

private void cmDbRollbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDbRollbackActionPerformed
  try {
    getDatabase().rollback();
  } catch (SQLException ex) {
    MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmDbRollbackActionPerformed

  private void cmDBStartTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDBStartTransactionActionPerformed
  try {
    getDatabase().startTransaction();
  } catch (SQLException ex) {
    MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmDBStartTransactionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonCloneDatabase;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonDbCommit;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonDbRollback;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonDeleteEditor;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonExecute;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonExecuteAsScript;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonExecutedSql;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonNewEditor;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonNextEditor;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonOpenFile;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonPervEditor;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonRecentFiles;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSaveFile;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSaveFileAs;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonStore;
    private pl.mpak.sky.gui.swing.Action cmCloneDatabase;
    private pl.mpak.sky.gui.swing.Action cmDBStartTransaction;
    private pl.mpak.sky.gui.swing.Action cmDbCommit;
    private pl.mpak.sky.gui.swing.Action cmDbRollback;
    private pl.mpak.sky.gui.swing.Action cmDeleteEditor;
    private pl.mpak.sky.gui.swing.Action cmEmpty;
    private pl.mpak.sky.gui.swing.Action cmExecute;
    private pl.mpak.sky.gui.swing.Action cmExecuteAsScript;
    private pl.mpak.sky.gui.swing.Action cmExecutePartial;
    private pl.mpak.sky.gui.swing.Action cmExecutedSql;
    private pl.mpak.sky.gui.swing.Action cmNewEditor;
    private pl.mpak.sky.gui.swing.Action cmNextEditor;
    private pl.mpak.sky.gui.swing.Action cmOpenFile;
    private pl.mpak.sky.gui.swing.Action cmPopupRecentFiles;
    private pl.mpak.sky.gui.swing.Action cmPrevEditor;
    private pl.mpak.sky.gui.swing.Action cmSaveFile;
    private pl.mpak.sky.gui.swing.Action cmSaveFileAs;
    private pl.mpak.sky.gui.swing.Action cmSelectDatabase;
    private pl.mpak.sky.gui.swing.Action cmShowHideError;
    private pl.mpak.sky.gui.swing.Action cmStore;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JLabel labelFileName;
    private javax.swing.JLabel labelSelectedDatabase;
    private javax.swing.JPopupMenu menuActions;
    private javax.swing.JMenuItem menuCloneDatabase;
    private javax.swing.JMenuItem menuCommit;
    private javax.swing.JMenuItem menuDeleteEditor;
    private javax.swing.JMenuItem menuExecute;
    private javax.swing.JMenuItem menuExecuteAsScript;
    private javax.swing.JMenuItem menuExecutedSql;
    private javax.swing.JMenuItem menuNewEditor;
    private javax.swing.JMenuItem menuNextEditor;
    private javax.swing.JMenuItem menuOpenFile;
    private javax.swing.JMenuItem menuPrevEditor;
    private javax.swing.JMenu menuRecentFiles;
    private javax.swing.JMenuItem menuRollback;
    private javax.swing.JMenuItem menuSaveFile;
    private javax.swing.JMenuItem menuSaveFileAs;
    private javax.swing.JMenuItem menuSelectDatabase;
    private javax.swing.JMenuItem menuStore;
    private javax.swing.JMenu menuView;
    private javax.swing.JPanel panelSqlEditor;
    private javax.swing.JPopupMenu popupRecentFiles;
    private javax.swing.JScrollPane scrollSqlError;
    private javax.swing.JSplitPane splitPanel;
    private javax.swing.JTabbedPane tabbedViews;
    private OrbadaSyntaxTextArea textSqlEditor;
    private javax.swing.JTextArea textSqlError;
    private javax.swing.JToolBar toolBar;
    private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
    private pl.mpak.sky.gui.swing.comp.ToolButton toolButton2;
    // End of variables declaration//GEN-END:variables
  
  class TabCloseAction extends Action {
    Component component;
    public TabCloseAction(Component component) {
      super();
      this.component = component;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
      closePanel(component);
    }
    
  }

  class TabAddResultAction extends Action {
    public TabAddResultAction() {
      super();
      setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/add12.gif"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
      addResultTab();
    }
    
  }
  
  class OpenRecentFileAction extends Action {
    private File file;
    public OpenRecentFileAction(String fileName) {
      super();
      file = new File(fileName);
      setText(file.getName());
      setTooltip(fileName);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
      openFile(file);
    }
    
  }
  
  private class UniversalViewAccessibilities implements IUniversalViewAccessibilities {
    
    public IViewAccesibilities getViewAccesibilities() {
      return accesibilities;
    }
    public SyntaxEditor getSyntaxEditor() {
      return textSqlEditor.getEditorArea();
    }
    public void addResultTab(String title, JComponent component) {
      addUniversalViewTab(title, component);
    }

    public Component[] getResultTabs(Class<? extends Component> clazz) {
      return SqlQueryPanelView.this.getResultTabs(clazz);
    }

    public void setSelectedTab(Component component) {
      tabbedViews.setSelectedComponent(component);
    }

    public void setTabTooltip(Component component, String tooltip) {
      SqlQueryPanelView.this.setTabTooltip(component, tooltip);
    }

    public void setTabTitle(Component component, String title) {
      SqlQueryPanelView.this.setTabTooltip(component, title);
    }
    
  }
  
  private class ContentEditorFile {
    public String fileName;
    public boolean autoName;
  }

}
