package pl.mpak.orbada.universal.gui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.EventObject;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import javax.swing.JTabbedPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.CommandListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.script.ErrorScriptEventObject;
import pl.mpak.usedb.script.SQLScript;
import pl.mpak.usedb.script.ScriptListener;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.TextTransfer;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.task.Task;

/**
 *
 * @author  akaluza
 */
public class ScriptResultPanel extends javax.swing.JPanel implements Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  private Task scriptTask;
  private ArrayList<Integer> errorList;
  private StringBuilder errorListText;
  private int errorPointer;
  private int resultLength;
  private Command command;
  private CommandListener commandListener;
  private IViewAccesibilities accesibilities;
  private boolean firstShown;
  
  /** Creates new form QueryHistoryPanel */
  public ScriptResultPanel(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    errorList = new ArrayList<Integer>();
    errorListText = new StringBuilder();
    initComponents();
    init();
  }
  
  private void init() {
    textResult.setFont(new Font(Font.MONOSPACED, Font.PLAIN, textResult.getFont().getSize()));

    Style style = ((StyledDocument)textResult.getDocument()).addStyle("text", null);
    StyleConstants.setFontFamily(style, Font.MONOSPACED);
    StyleConstants.setForeground(style, textResult.getForeground());

    style = ((StyledDocument)textResult.getDocument()).addStyle("error", null);
    StyleConstants.setFontFamily(style, Font.MONOSPACED);
    StyleConstants.setBold(style, true);
    StyleConstants.setForeground(style, Color.RED);

    clearErrorList();
  }

  private void clearErrorList() {
    cmGotoNextError.setEnabled(false);
    cmGotoPrevError.setEnabled(false);
    errorList.clear();
    errorListText.setLength(0);
    errorPointer = -1;
    resultLength = 0;
  }

  private void activateErrorList() {
    cmGotoNextError.setEnabled(errorList.size() > 0);
    cmGotoPrevError.setEnabled(false);
  }

  private void enableErrorList() {
    cmGotoNextError.setEnabled(errorPointer < errorList.size() -1);
    cmGotoPrevError.setEnabled(errorPointer > 0);
    cmCopyErrorToClipboard.setEnabled(errorList.size() > 0);
  }

  private void updatePositionError() {
    final int offset = errorList.get(errorPointer);
    textResult.setCaretPosition(offset);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          textResult.scrollRectToVisible(textResult.modelToView(offset));
        } catch (BadLocationException ex) {
          ExceptionUtil.processException(ex);
        }
      }
    });
  }
  
  public void append(final String text) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        resultLength += text.length();
        StyledDocument doc = (StyledDocument)textResult.getDocument();
        try {
          doc.insertString(doc.getLength(), text, doc.getStyle("text"));
        } catch (BadLocationException ex) {
          ExceptionUtil.processException(ex);
        }
      }
    });
    if (!firstShown) {
      final JTabbedPane pane = (JTabbedPane)SwingUtil.getOwnerComponent(JTabbedPane.class, this);
      if (pane != null) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            pane.setSelectedComponent(ScriptResultPanel.this);
          }
        });
      }
    }
  }
  
  public void appendError(final String text, final String cmd) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        errorList.add(resultLength);
        StyledDocument doc = (StyledDocument)textResult.getDocument();
        try {
          doc.insertString(doc.getLength(), text, doc.getStyle("error"));
          errorListText.append(cmd +"\n" +text);
        } catch (BadLocationException ex) {
          ExceptionUtil.processException(ex);
        }
        resultLength += text.length();
      }
    });
    if (!firstShown) {
      final JTabbedPane pane = (JTabbedPane)SwingUtil.getOwnerComponent(JTabbedPane.class, this);
      if (pane != null) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            pane.setSelectedComponent(ScriptResultPanel.this);
          }
        });
      }
    }
  }

  public void execute(Database database, final String text) {
    if (textResult.getDocument().getLength() > 0) {
      switch (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("ScriptResultPanel-clear_result-title"), stringManager.getString("ScriptResultPanel-clear_result-q"), ModalResult.YESNOCANCEL, MessageBox.QUESTION)) {
        case ModalResult.CANCEL:
          return;
        case ModalResult.YES:
          cmClearContent.performe();
      }
    }
    else if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("script"), stringManager.getString("ScriptResultPanel-exec_script-q"), ModalResult.YESNO, MessageBox.QUESTION) != ModalResult.YES) {
      return;
    }
    
    final SQLScript script = new SQLScript(database);
    script.addScriptListener(new ScriptListener() {
      String cmdText;
      public void beforeScript(EventObject evt) {
      }
      public void beforeCommand(EventObject evt) {
        Command command = (Command)evt.getSource();
        String sqlText = SQLUtil.removeWhiteSpaces(command.getSqlText());
        cmdText = sqlText.substring(0, Math.min(100, sqlText.length()));
        append(cmdText +"\n");
      }
      public void afterCommand(EventObject evt) {
        Command command = (Command)evt.getSource();
        append(command.getUpdateCount() +" " +stringManager.getString("records-updated-count") +", " +StringUtil.formatTime(command.getExecutionTime()) +"\n\n");
      }
      public void beforeQuery(EventObject evt) {
        Query query = (Query)evt.getSource();
        String sqlText = SQLUtil.removeWhiteSpaces(query.getSqlText());
        cmdText = sqlText.substring(0, Math.min(100, sqlText.length()));
        append(cmdText +"\n");
      }
      public void afterQuery(EventObject evt) {
        Query query = (Query)evt.getSource();
        StringBuilder line = new StringBuilder();
        for (int i=0; i<query.getFieldCount(); i++) {
          QueryField field = query.getField(i);
          line.append(field.getDisplayName());
          int length = Math.max(field.getDisplaySize(), field.getDisplayName().length());
          for (int c=0; c<length -field.getDisplayName().length() +1; c++) {
            line.append(' ');
          }
        }
        line.append("\n");
        append(line.toString());
        line.setLength(0);
        for (int i=0; i<query.getFieldCount(); i++) {
          QueryField field = query.getField(i);
          int length = Math.max(field.getDisplaySize(), field.getDisplayName().length());
          for (int c=0; c<length; c++) {
            line.append('-');
          }
          line.append(' ');
        }
        line.append("\n");
        append(line.toString());
        int records = 0;
        try {
          while (!query.eof()) {
            line.setLength(0);
            for (int i = 0; i < query.getFieldCount(); i++) {
              QueryField field = query.getField(i);
              String value = field.getString();
              line.append(value);
              int length = Math.max(field.getDisplaySize(), field.getDisplayName().length());
              for (int c = 0; c < length -value.length() + 1; c++) {
                line.append(' ');
              }
            }
            records++;
            line.append("\n");
            append(line.toString());
            query.next();
          }
        } catch (Exception ex) {
          appendError(ex.getMessage() +"\n", cmdText);
        }

        append("\n");
        append(records +" " +stringManager.getString("records-selected-count") +", " +StringUtil.formatTime(query.getOpeningTime()) +"\n\n");
      }
      public boolean errorOccured(ErrorScriptEventObject evt) {
        appendError(((Throwable)evt.getSource()).getMessage() +"\n\n", cmdText);
        return false;
      }
      public void afterScript(EventObject evt) {
        append(stringManager.getString("ScriptResultPanel-script_end") +" " +StringUtil.formatTime(script.getScriptTime()) +"\n");
        append(stringManager.getString("ScriptResultPanel-script_errors") +" " +script.getErrorCount() +"\n\n");
      }
    });
    database.getTaskPool().addTask(scriptTask = new Task(stringManager.getString("ScriptResultPanel-executor-task-3d")) {
      public void run() {
        cmCancelScript.setEnabled(true);
        PleaseWait wait = PleaseWait.createSqlWait(stringManager.getString("ScriptResultPanel-script_executing"), 2000);
        accesibilities.getApplication().startPleaseWait(wait);
        try {
          script.execute(new BufferedReader(new StringReader(text)));
        } catch (IOException ex) {
          errorList.add(textResult.getCaretPosition());
          append(ex.getMessage() +"\n");
          ExceptionUtil.processException(ex);
        }
        finally {
          accesibilities.getApplication().stopPleaseWait(wait);
          cmCancelScript.setEnabled(false);
          activateErrorList();
          scriptTask = null;
        }
      }
    });
  }

  public void setCommand(Command command) {
    if (this.command != command) {
      if (this.command != null) {
        this.command.removeCommandListener(getCommandListener());
      }
      this.command = command;
      if (this.command != null) {
        this.command.addCommandListener(getCommandListener());
        //cmClearContent.performe();
      }
    }
  }

  private CommandListener getCommandListener() {
    if (commandListener == null) {
      commandListener = new CommandListener() {
        public void beforeExecute(EventObject e) {
          firstShown = false;
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              cmCancelScript.setEnabled(true);
            }
          });
        }
        public void afterExecute(final EventObject e) {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              cmCancelScript.setEnabled(false);
            }
          });
        }
        public void errorPerformed(EventObject e) {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              cmCancelScript.setEnabled(false);
            }
          });
        }
      };
    }
    return commandListener;
  }

  public void close() throws IOException {
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmClearContent = new pl.mpak.sky.gui.swing.Action();
        cmSaveToFile = new pl.mpak.sky.gui.swing.Action();
        cmCancelScript = new pl.mpak.sky.gui.swing.Action();
        cmGotoNextError = new pl.mpak.sky.gui.swing.Action();
        cmGotoPrevError = new pl.mpak.sky.gui.swing.Action();
        cmCopyErrorToClipboard = new pl.mpak.sky.gui.swing.Action();
        panelEditData = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        buttonClear = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonSave = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        buttonCancelScript = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        buttonGotoNextError = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonGotoPrevError = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonCopyErrorToClipboard = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textResult = new pl.mpak.sky.gui.swing.comp.EditorPane();

        cmClearContent.setActionCommandKey("cmClearContent");
        cmClearContent.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/clear.gif")); // NOI18N
        cmClearContent.setText(stringManager.getString("cmClearContent-text")); // NOI18N
        cmClearContent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmClearContentActionPerformed(evt);
            }
        });

        cmSaveToFile.setActionCommandKey("cmSaveToFile");
        cmSaveToFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save16.gif")); // NOI18N
        cmSaveToFile.setText(stringManager.getString("cmSaveToFile-text")); // NOI18N
        cmSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSaveToFileActionPerformed(evt);
            }
        });

        cmCancelScript.setActionCommandKey("cmCancelScript");
        cmCancelScript.setEnabled(false);
        cmCancelScript.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/cancel.gif")); // NOI18N
        cmCancelScript.setText(stringManager.getString("cmCancelScript-text")); // NOI18N
        cmCancelScript.setTooltip(stringManager.getString("cmCancelScript-hint")); // NOI18N
        cmCancelScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCancelScriptActionPerformed(evt);
            }
        });

        cmGotoNextError.setActionCommandKey("cmGotoNextError");
        cmGotoNextError.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/next_error.gif")); // NOI18N
        cmGotoNextError.setText(stringManager.getString("cmGotoNextError-text")); // NOI18N
        cmGotoNextError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmGotoNextErrorActionPerformed(evt);
            }
        });

        cmGotoPrevError.setActionCommandKey("cmGotoPrevError");
        cmGotoPrevError.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/prev_error.gif")); // NOI18N
        cmGotoPrevError.setText(stringManager.getString("cmGotoPrevError-text")); // NOI18N
        cmGotoPrevError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmGotoPrevErrorActionPerformed(evt);
            }
        });

        cmCopyErrorToClipboard.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/copy-error.gif")); // NOI18N
        cmCopyErrorToClipboard.setText(stringManager.getString("cmCopyErrorToClipboard-text")); // NOI18N
        cmCopyErrorToClipboard.setTooltip(stringManager.getString("cmCopyErrorToClipboard-hint")); // NOI18N
        cmCopyErrorToClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCopyErrorToClipboardActionPerformed(evt);
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        panelEditData.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        buttonClear.setAction(cmClearContent);
        buttonClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonClear);

        buttonSave.setAction(cmSaveToFile);
        buttonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonSave);
        jToolBar1.add(jSeparator1);

        buttonCancelScript.setAction(cmCancelScript);
        buttonCancelScript.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCancelScript.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonCancelScript);
        jToolBar1.add(jSeparator2);

        buttonGotoNextError.setAction(cmGotoNextError);
        buttonGotoNextError.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonGotoNextError.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonGotoNextError);

        buttonGotoPrevError.setAction(cmGotoPrevError);
        buttonGotoPrevError.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonGotoPrevError.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonGotoPrevError);

        buttonCopyErrorToClipboard.setAction(cmCopyErrorToClipboard);
        buttonCopyErrorToClipboard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCopyErrorToClipboard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonCopyErrorToClipboard);

        panelEditData.add(jToolBar1);

        add(panelEditData, java.awt.BorderLayout.PAGE_START);

        textResult.setEditable(false);
        jScrollPane1.setViewportView(textResult);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

  private void cmClearContentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmClearContentActionPerformed
    clearErrorList();
    textResult.setText("");
}//GEN-LAST:event_cmClearContentActionPerformed

  private void cmSaveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSaveToFileActionPerformed
    File file = FileUtil.selectFileToSave(this, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("text-files"), new String[] { ".txt" })});
    if (file != null) {
      try {
        PrintWriter pw = new PrintWriter(new FileOutputStream(file));
        pw.write(textResult.getText());
        pw.close();
      } catch (FileNotFoundException ex) {
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }

    }
  }//GEN-LAST:event_cmSaveToFileActionPerformed

private void cmCancelScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelScriptActionPerformed
  if (scriptTask != null) {
    scriptTask.setCanceled(true);
  }
  else if (command != null && command.getState() == Command.State.EXECUTEING) {
    try {
      command.cancel();
    } catch (SQLException ex) {
      append(ex.getMessage() +"\n");
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmCancelScriptActionPerformed

private void cmGotoNextErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGotoNextErrorActionPerformed
  errorPointer++;
  updatePositionError();
  enableErrorList();
}//GEN-LAST:event_cmGotoNextErrorActionPerformed

private void cmGotoPrevErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGotoPrevErrorActionPerformed
  errorPointer--;
  updatePositionError();
  enableErrorList();
}//GEN-LAST:event_cmGotoPrevErrorActionPerformed

  private void cmCopyErrorToClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCopyErrorToClipboardActionPerformed
    new TextTransfer().setClipboardContents(errorListText.toString());
  }//GEN-LAST:event_cmCopyErrorToClipboardActionPerformed

  private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    firstShown = true;
  }//GEN-LAST:event_formComponentShown
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonCancelScript;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonClear;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonCopyErrorToClipboard;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonGotoNextError;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonGotoPrevError;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSave;
    private pl.mpak.sky.gui.swing.Action cmCancelScript;
    private pl.mpak.sky.gui.swing.Action cmClearContent;
    private pl.mpak.sky.gui.swing.Action cmCopyErrorToClipboard;
    private pl.mpak.sky.gui.swing.Action cmGotoNextError;
    private pl.mpak.sky.gui.swing.Action cmGotoPrevError;
    private pl.mpak.sky.gui.swing.Action cmSaveToFile;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panelEditData;
    private pl.mpak.sky.gui.swing.comp.EditorPane textResult;
    // End of variables declaration//GEN-END:variables

}
