/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventObject;
import java.util.HashMap;
import javax.swing.event.EventListenerList;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.OrbadaException;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.editor.EditorPopupMenu;
import pl.mpak.orbada.plugins.providers.OrbadaSyntaxTextAreaProvider;
import pl.mpak.orbada.plugins.providers.SyntaxEditorActionProvider;
import pl.mpak.orbada.plugins.providers.SyntaxEditorAutoCompleteProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.script.SimpleSQLScript;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.CloseAbilitable;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class AbsOrbadaSyntaxTextArea extends SyntaxTextArea implements CloseAbilitable {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private static HashMap<String, CaretPosition> positionList = new HashMap<String, CaretPosition>();

  protected Database database;
  protected String schemaName;
  protected String objectType;
  protected String objectName;

  public enum DatabaseObjectEvent {
    BEFORE_CHANGE,
    AFTER_CHANGE,
    BEFORE_STORE_OBJECT,
    AFTER_STORE_OBJECT,
    STORE_OBJECT_ERROR
  }
  protected final EventListenerList objectListenerList = new EventListenerList();

  public AbsOrbadaSyntaxTextArea() {
    super();
    init();
  }

  private void init() {
    getEditorArea().setComponentPopupMenu(new EditorPopupMenu(this));

    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (Application.get() != null) {
          SyntaxEditorActionProvider[] list = Application.get().getServiceArray(SyntaxEditorActionProvider.class);
          if (list.length > 0) {
            getEditorArea().getComponentPopupMenu().addSeparator();
            for (int i=0; i<list.length; i++) {
              list[i].setSyntaxEditr(getEditorArea());
              getEditorArea().getComponentPopupMenu().add(list[i]);
            }
          }
          OrbadaSyntaxTextAreaProvider[] ostaps = Application.get().getServiceArray(OrbadaSyntaxTextAreaProvider.class);
          if (ostaps.length > 0) {
            for (OrbadaSyntaxTextAreaProvider ostap : ostaps) {
              ostap.setSyntaxTextArea(AbsOrbadaSyntaxTextArea.this);
            }
          }
          SyntaxEditorAutoCompleteProvider[] seacps = Application.get().getServiceArray(SyntaxEditorAutoCompleteProvider.class);
          if (seacps.length > 0) {
            for (SyntaxEditorAutoCompleteProvider seacp : seacps) {
              if (seacp.isForEditor(AbsOrbadaSyntaxTextArea.this)) {
                getAutoComplete().addAutoCompleteListener(seacp);
              }
            }
          }
        }
      }
    });

    getEditorArea().addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
      }
      @Override
      public void focusLost(FocusEvent e) {
        updateCaretPositionList();
      }
    });
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    if (this.database != database) {
      this.database = database;
    }
  }

  public void addDatabaseObjectListener(SyntaxDatabaseObjectListener listener) {
    synchronized (objectListenerList) {
      objectListenerList.add(SyntaxDatabaseObjectListener.class, listener);
    }
  }

  public void removeDatabaseObjectListener(SyntaxDatabaseObjectListener listener) {
    synchronized (objectListenerList) {
      objectListenerList.remove(SyntaxDatabaseObjectListener.class, listener);
    }
  }

  public void fireDatabaseObjectListener(DatabaseObjectEvent event) {
    synchronized (objectListenerList) {
      SyntaxDatabaseObjectListener[] listeners = objectListenerList.getListeners(SyntaxDatabaseObjectListener.class);
      EventObject e = new EventObject(this);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
          case BEFORE_CHANGE:
            listeners[i].beforeChanged(e);
            break;
          case AFTER_CHANGE:
            listeners[i].afterChanged(e);
            break;
          case BEFORE_STORE_OBJECT:
            listeners[i].beforeStoreObject(e);
            break;
          case AFTER_STORE_OBJECT:
            listeners[i].afterStoreObject(e);
            break;
          case STORE_OBJECT_ERROR:
            listeners[i].storeObjectError(e);
            break;
        }
      }
    }
  }

  public String getObjectName() {
    return objectName;
  }

  public String getObjectType() {
    return objectType;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setDatabaseObject(String schemaName, String objectType, String objectName, String text) {
    fireDatabaseObjectListener(DatabaseObjectEvent.BEFORE_CHANGE);
    updateCaretPositionList();
    if (getStatusBar().getPanel("ddl-status") == null) {
      getStatusBar().addPanel("ddl-status");
    }
    if (StringUtil.isEmpty(objectName)) {
      getStatusBar().getPanel("ddl-status").setText("");
    }
    else {
      getStatusBar().getPanel("ddl-status").setText(objectType +" " +(StringUtil.isEmpty(schemaName) ? "" : schemaName +".") +objectName);
    }
    this.schemaName = schemaName;
    this.objectType = objectType;
    this.objectName = objectName;
    setText(text);
    setChanged(false);
    getEditorArea().getUndoManager().discardAllEdits();
    updateCaretPosition();
    fireDatabaseObjectListener(DatabaseObjectEvent.AFTER_CHANGE);
  }

  /**
   * <p>Pozwala wstawiæ do bazy danych Ÿród³o obiektu bazy danych ustawione
   * uprzednio przy pomocy setDatabaseObject()
   * @return
   * @throws java.lang.Exception
   */
  public Command storeObject() throws Exception {
    fireDatabaseObjectListener(DatabaseObjectEvent.BEFORE_STORE_OBJECT);
    Command command = getDatabase().createCommand();
    try {
      command.setParamCheck(false);
      command.execute(getText());
      setChanged(false);
      fireDatabaseObjectListener(DatabaseObjectEvent.AFTER_STORE_OBJECT);
    }
    catch (Exception ex) {
      fireDatabaseObjectListener(DatabaseObjectEvent.STORE_OBJECT_ERROR);
      throw ex;
    }
    return command;
  }

  public Command storeObject(String text) throws Exception {
    Command command = getDatabase().createCommand();
    command.setParamCheck(false);
    command.execute(text);
    return command;
  }

  /**
   * <p>Pozwala wstawiæ do bazy danych skrypt obiektu bazy danych ustawione
   * uprzednio przy pomocy setDatabaseObject()
   * @return
   * @throws java.lang.Exception
   */
  public void storeScript() throws Exception {
    String text = getText();
    if (!StringUtil.isEmpty(text)) {
      fireDatabaseObjectListener(DatabaseObjectEvent.BEFORE_STORE_OBJECT);
      SimpleSQLScript script = new SimpleSQLScript(getDatabase());
      if (script.executeScript(text)) {
        setChanged(false);
        fireDatabaseObjectListener(DatabaseObjectEvent.AFTER_STORE_OBJECT);
      }
      else {
        fireDatabaseObjectListener(DatabaseObjectEvent.STORE_OBJECT_ERROR);
        throw new OrbadaException(script.getErrors());
      }
    }
  }

  public void storeScript(String text) throws Exception {
    if (!StringUtil.isEmpty(text)) {
      SimpleSQLScript script = new SimpleSQLScript(getDatabase());
      if (!script.executeScript(text)) {
        throw new OrbadaException(script.getErrors());
      }
    }
  }

  public void updateCaretPositionList() {
    if (!StringUtil.isEmpty(objectType) && !StringUtil.isEmpty(objectName)) {
      positionList.put(
        objectType +":" +schemaName +"." +objectName,
        new CaretPosition(getCaretPosition(), getScrollPane().getVerticalScrollBar().getValue()));
    }
  }

  private void updateCaretPosition() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (!StringUtil.isEmpty(objectType) && !StringUtil.isEmpty(objectName)) {
          CaretPosition p = positionList.get(objectType +":" +schemaName +"." +objectName);
          if (p != null) {
            try {
              getEditorArea().setCaretPosition(p.offset);
              getScrollPane().getVerticalScrollBar().setValue(p.top);
            }
            catch (Throwable ex) {
            }
          }
        }
      }
    });
  }

  @Override
  public boolean canClose() {
    if (getDatabase() != null && !StringUtil.isEmpty(objectName) && isChanged() &&
      MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("change"),
        String.format(stringManager.getString("OrbadaSyntaxTextArea-close-q"), new Object[] {getObjectType() +" " +SQLUtil.createSqlName(getObjectName())}),
        ModalResult.YESNO, MessageBox.WARNING) == ModalResult.YES) {
      return false;
    }
    return true;
  }

  class CaretPosition {
    int offset;
    int top;

    public CaretPosition(int offset, int top) {
      this.offset = offset;
      this.top = top;
    }

  }

}
