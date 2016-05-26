/*
 * BeanShellEditorPanelView.java
 *
 * Created on 8 listopad 2007, 18:56
 */

package pl.mpak.orbada.beanshell.gui;

import bsh.ConsoleInterface;
import java.awt.Color;
import java.awt.Frame;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import pl.mpak.orbada.gui.comps.OrbadaJavaSyntaxTextArea;
import pl.mpak.orbada.beanshell.BeanShellInterpreter;
import pl.mpak.orbada.beanshell.OrbadaBeanshellPlugin;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.files.PatternFileFilter;
import pl.mpak.util.files.WildCard;
import pl.mpak.util.id.UniqueID;

/**
 *
 * @author  akaluza
 */
public class BeanShellEditorPanelView extends javax.swing.JPanel implements Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaBeanshellPlugin.class);

  private IViewAccesibilities accesibilities;
  private BeanShellInterpreter interpreter;
  
  private File openedFile;
  
  private ArrayList<String> editorContentList;
  private String currentEditorContent;
  
  /** Creates new form BeanShellEditorPanelView
   * @param accesibilities
   */
  public BeanShellEditorPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    menuView.setText(SwingUtil.setButtonText(menuView, stringManager.getString("BeanShellEditorPanelView-menuView-text")));
    editorContentList = new ArrayList<String>();

    accesibilities.addMenu(menuView);
    SwingUtil.addAction(this, "cmRun", cmRun);
    SwingUtil.addAction(syntaxBeanShell.getEditorArea(), "cmSaveFile", cmSaveFile);
        
    interpreter = new BeanShellInterpreter(accesibilities.getDatabase()) {
      public void clearResult() {
        textResult.setText("");
      }
    };
    interpreter.setConsole(new ConsoleInterface() {
      public Reader getIn() {
        return null;
      }
      public PrintStream getOut() {
        return null;
      }
      public PrintStream getErr() {
        return null;
      }
      public void println(Object o) {
        if (o != null) {
          textResult.append(o.toString() +"\n");
        }
      }
      public void print(Object o) {
        if (o != null) {
          textResult.append(o.toString());
        }
      }
      public void error(Object o) {
        if (o != null) {
          SimpleAttributeSet as = new SimpleAttributeSet();
          StyleConstants.setForeground(as, Color.RED);
          try {
            textResult.getDocument().insertString(textResult.getDocument().getLength(), o.toString(), as);
          } catch (BadLocationException badLocationException) {
          }
        }
      }
      
    });
    refreshEditorContentList(true);
  }
  
  public void close() throws IOException {
    if (currentEditorContent != null) {
      try {
        syntaxBeanShell.saveToFile(currentEditorContent);
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
      }
    }
    accesibilities = null;
  }
  
  public IApplication getApplication() {
    return accesibilities.getApplication();
  }
  
  private void updateCurrentEditorContentFile() {
    int index = editorContentList.indexOf(currentEditorContent);
    currentEditorContent = openedFile.getAbsolutePath();
    if (index != -1) {
      editorContentList.set(index, currentEditorContent);
    }
  }
  
  private boolean saveFile() {
    if (openedFile != null) {
      try {
        syntaxBeanShell.saveToFile(openedFile);
        syntaxBeanShell.setChanged(false);
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
    JFileChooser fileChoose = new JFileChooser(".");
    fileChoose.addChoosableFileFilter(new FileExtensionFilter(stringManager.getString("beanshell-files"), new String[] { ".bsh" }));
    int returnVal = fileChoose.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      openedFile = fileChoose.getSelectedFile().getAbsoluteFile();
      if (openedFile != null) {
        return saveFile();
      }
    }
    return false;
  }
  
  private boolean openFile() {
    JFileChooser fileChoose = new JFileChooser(".");
    try {
      if (openedFile != null) {
        fileChoose.setSelectedFile(openedFile);
      }
    } catch (Exception ex) {
    }
    fileChoose.addChoosableFileFilter(new FileExtensionFilter(stringManager.getString("beanshell-files"), new String[] { ".bsh" }));
    int returnVal = fileChoose.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      openedFile = fileChoose.getSelectedFile().getAbsoluteFile();
      try {
        syntaxBeanShell.loadFromFile(openedFile);
        syntaxBeanShell.setChanged(false);
        updateCurrentEditorContentFile();
        return true;
      } catch (IOException ex) {
        MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
      }
    }
    return false;
  }
  
  private String getEditorContentPath() {
    return getApplication().getConfigPath() + "/beanshell-editor-contents";
  }
  
  private void updateEditorContentActions() {
    cmPrevEditor.setEnabled(currentEditorContent != null && editorContentList.indexOf(currentEditorContent) > 0);
    cmNextEditor.setEnabled(currentEditorContent != null && editorContentList.indexOf(currentEditorContent) < editorContentList.size() -1);
  }
  
  private void refreshEditorContentList(boolean autoLoad) {
    String configPath = getEditorContentPath();
    File dir = new File(configPath);
    dir.mkdirs();
    String[] fileList = dir.list(new PatternFileFilter(WildCard.getRegex("*.bsh")));
    Arrays.sort(fileList);
    editorContentList.clear();
    for (int i=0; i<fileList.length; i++) {
      if (i > fileList.length -100) {
        editorContentList.add(configPath +"/" +fileList[i]);
      } else {
        new File(configPath +"/" +fileList[i]).delete();
      }
    }
    if (currentEditorContent == null || !editorContentList.contains(currentEditorContent)) {
      if (editorContentList.size() > 0) {
        currentEditorContent = editorContentList.get(editorContentList.size() -1);
        if (autoLoad) {
          try {
            syntaxBeanShell.loadFromFile(currentEditorContent);
            syntaxBeanShell.setChanged(false);
          } catch (IOException ex) {
          }
        }
      }
    }
    if (currentEditorContent == null) {
      newEditorContent();
    } else {
      updateEditorContentActions();
    }
  }
  
  private void newEditorContent() {
    if (currentEditorContent != null) {
      try {
        syntaxBeanShell.saveToFile(currentEditorContent);
      } catch (IOException ex) {
      }
    }
    currentEditorContent = getEditorContentPath() +"/" +new UniqueID().toString() +".bsh";
    editorContentList.add(currentEditorContent);
    syntaxBeanShell.setText("");
    syntaxBeanShell.setChanged(false);
    updateEditorContentActions();
  }
  
  private void deleteEditorContent() {
    if (currentEditorContent != null) {
      int index = editorContentList.indexOf(currentEditorContent);
      if (openedFile == null || !currentEditorContent.equals(openedFile.getAbsolutePath())) {
        new File(currentEditorContent).delete();
      }
      editorContentList.remove(index);
      currentEditorContent = null;
      if (editorContentList.size() > 0) {
        currentEditorContent = editorContentList.get(index < editorContentList.size() ? index : index -1);
        try {
          syntaxBeanShell.loadFromFile(currentEditorContent);
          syntaxBeanShell.setChanged(false);
        } catch (IOException ex) {
        }
        updateEditorContentActions();
      } else {
        newEditorContent();
      }
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRun = new pl.mpak.sky.gui.swing.Action();
    cmSaveFile = new pl.mpak.sky.gui.swing.Action();
    cmSaveFileAs = new pl.mpak.sky.gui.swing.Action();
    cmOpenFile = new pl.mpak.sky.gui.swing.Action();
    cmPrevEditor = new pl.mpak.sky.gui.swing.Action();
    cmNextEditor = new pl.mpak.sky.gui.swing.Action();
    cmNewEditor = new pl.mpak.sky.gui.swing.Action();
    cmDeleteEditor = new pl.mpak.sky.gui.swing.Action();
    menuView = new javax.swing.JMenu();
    menuOpenFile = new javax.swing.JMenuItem();
    menuSaveFile = new javax.swing.JMenuItem();
    menuSaveFileAs = new javax.swing.JMenuItem();
    jSeparator6 = new javax.swing.JSeparator();
    menuRun = new javax.swing.JMenuItem();
    jSplitPane1 = new javax.swing.JSplitPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    textResult = new javax.swing.JTextArea();
    syntaxBeanShell = new OrbadaJavaSyntaxTextArea();
    jPanel1 = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    buttonOpenFile = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSaveFile = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonSaveFileAs = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JSeparator();
    buttonNewEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonPervEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonNextEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JSeparator();
    buttonDeleteEditor = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator5 = new javax.swing.JSeparator();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();

    cmRun.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmRun.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/run.gif")); // NOI18N
    cmRun.setText(stringManager.getString("cmRun-text")); // NOI18N
    cmRun.setTooltip(stringManager.getString("cmRun-hint")); // NOI18N
    cmRun.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRunActionPerformed(evt);
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
    cmPrevEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/arrowup.gif")); // NOI18N
    cmPrevEditor.setText(stringManager.getString("cmPrevEditor-text")); // NOI18N
    cmPrevEditor.setTooltip(stringManager.getString("cmPrevEditor-hint")); // NOI18N
    cmPrevEditor.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmPrevEditorActionPerformed(evt);
      }
    });

    cmNextEditor.setActionCommandKey("cmNextEditor");
    cmNextEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/arrowdown.gif")); // NOI18N
    cmNextEditor.setText(stringManager.getString("cmNextEditor-text")); // NOI18N
    cmNextEditor.setTooltip(stringManager.getString("cmNextEditor-hint")); // NOI18N
    cmNextEditor.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNextEditorActionPerformed(evt);
      }
    });

    cmNewEditor.setActionCommandKey("cmNewEditor");
    cmNewEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_document16.gif")); // NOI18N
    cmNewEditor.setText(stringManager.getString("cmNewEditor-text")); // NOI18N
    cmNewEditor.setTooltip(stringManager.getString("cmNewEditor-hint")); // NOI18N
    cmNewEditor.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewEditorActionPerformed(evt);
      }
    });

    cmDeleteEditor.setActionCommandKey("cmDeleteEditor");
    cmDeleteEditor.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/minus16.gif")); // NOI18N
    cmDeleteEditor.setText(stringManager.getString("cmDeleteEditor-text")); // NOI18N
    cmDeleteEditor.setTooltip(stringManager.getString("cmDeleteEditor-hint")); // NOI18N
    cmDeleteEditor.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteEditorActionPerformed(evt);
      }
    });

    menuOpenFile.setAction(cmOpenFile);
    menuView.add(menuOpenFile);

    menuSaveFile.setAction(cmSaveFile);
    menuView.add(menuSaveFile);

    menuSaveFileAs.setAction(cmSaveFileAs);
    menuView.add(menuSaveFileAs);
    menuView.add(jSeparator6);

    menuRun.setAction(cmRun);
    menuView.add(menuRun);

    setFocusable(false);
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jSplitPane1.setDividerLocation(400);
    jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setContinuousLayout(true);
    jSplitPane1.setFocusable(false);
    jSplitPane1.setOneTouchExpandable(true);

    jScrollPane1.setPreferredSize(new java.awt.Dimension(106, 200));

    textResult.setColumns(20);
    textResult.setEditable(false);
    textResult.setRows(5);
    jScrollPane1.setViewportView(textResult);

    jSplitPane1.setRightComponent(jScrollPane1);
    jSplitPane1.setLeftComponent(syntaxBeanShell);

    add(jSplitPane1, java.awt.BorderLayout.CENTER);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    buttonOpenFile.setAction(cmOpenFile);
    buttonOpenFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonOpenFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonOpenFile);

    buttonSaveFile.setAction(cmSaveFile);
    buttonSaveFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSaveFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonSaveFile);

    buttonSaveFileAs.setAction(cmSaveFileAs);
    buttonSaveFileAs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSaveFileAs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonSaveFileAs);

    jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator3.setPreferredSize(new java.awt.Dimension(2, 2));
    jToolBar1.add(jSeparator3);

    buttonNewEditor.setAction(cmNewEditor);
    buttonNewEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonNewEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonNewEditor);

    buttonPervEditor.setAction(cmPrevEditor);
    buttonPervEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonPervEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonPervEditor);

    buttonNextEditor.setAction(cmNextEditor);
    buttonNextEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonNextEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonNextEditor);

    jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator4.setPreferredSize(new java.awt.Dimension(2, 2));
    jToolBar1.add(jSeparator4);

    buttonDeleteEditor.setAction(cmDeleteEditor);
    buttonDeleteEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDeleteEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonDeleteEditor);

    jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator5.setPreferredSize(new java.awt.Dimension(2, 2));
    jToolBar1.add(jSeparator5);

    toolButton1.setAction(cmRun);
    toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    toolButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(toolButton1);

    jPanel1.add(jToolBar1, java.awt.BorderLayout.WEST);

    add(jPanel1, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
      syntaxBeanShell.requestFocusInWindow();
    }
  });
}//GEN-LAST:event_formComponentShown

private void cmDeleteEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteEditorActionPerformed
  deleteEditorContent();
}//GEN-LAST:event_cmDeleteEditorActionPerformed

private void cmNewEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewEditorActionPerformed
  newEditorContent();
}//GEN-LAST:event_cmNewEditorActionPerformed

private void cmNextEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNextEditorActionPerformed
  if (currentEditorContent != null) {
    try {
      syntaxBeanShell.saveToFile(currentEditorContent);
      if (openedFile != null && currentEditorContent.equals(openedFile.getAbsolutePath())) {
        syntaxBeanShell.setChanged(false);
      }
    } catch (IOException ex) {
    }
    int index = editorContentList.indexOf(currentEditorContent);
    if (index < editorContentList.size() -1) {
      currentEditorContent = editorContentList.get(index +1);
      try {
        syntaxBeanShell.loadFromFile(currentEditorContent);
      } catch (IOException ex) {
      }
    }
  }
  updateEditorContentActions();
}//GEN-LAST:event_cmNextEditorActionPerformed

private void cmPrevEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPrevEditorActionPerformed
  if (currentEditorContent != null) {
    try {
      syntaxBeanShell.saveToFile(currentEditorContent);
      if (openedFile != null && currentEditorContent.equals(openedFile.getAbsolutePath())) {
        syntaxBeanShell.setChanged(false);
      }
    } catch (IOException ex) {
    }
    int index = editorContentList.indexOf(currentEditorContent);
    if (index > 0) {
      currentEditorContent = editorContentList.get(index -1);
      try {
        syntaxBeanShell.loadFromFile(currentEditorContent);
      } catch (IOException ex) {
      }
    }
  }
  updateEditorContentActions();
}//GEN-LAST:event_cmPrevEditorActionPerformed

private void cmOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOpenFileActionPerformed
  if (syntaxBeanShell.isChanged() && openedFile != null) {
    switch (MessageBox.show((Frame)null, stringManager.getString("save-file"), String.format(stringManager.getString("BeanShellEditorPanelView-save-file-q"), new Object[] {openedFile.getName()}), ModalResult.YESNOCANCEL)) {
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

private void cmSaveFileAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSaveFileAsActionPerformed
  saveFileAs();
}//GEN-LAST:event_cmSaveFileAsActionPerformed

private void cmSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSaveFileActionPerformed
  saveFile();
}//GEN-LAST:event_cmSaveFileActionPerformed

private void cmRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRunActionPerformed
  try {
    Object result = interpreter.eval(syntaxBeanShell.getText());
    if (result != null) {
      textResult.append("<" +result.toString() +">\n");
    } else {
      textResult.append("Result: null\n");
    }
  } catch (Throwable ex) {
    textResult.append(ex.getMessage() +"\n");
  }
}//GEN-LAST:event_cmRunActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDeleteEditor;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonNewEditor;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonNextEditor;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonOpenFile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonPervEditor;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSaveFile;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSaveFileAs;
  private pl.mpak.sky.gui.swing.Action cmDeleteEditor;
  private pl.mpak.sky.gui.swing.Action cmNewEditor;
  private pl.mpak.sky.gui.swing.Action cmNextEditor;
  private pl.mpak.sky.gui.swing.Action cmOpenFile;
  private pl.mpak.sky.gui.swing.Action cmPrevEditor;
  private pl.mpak.sky.gui.swing.Action cmRun;
  private pl.mpak.sky.gui.swing.Action cmSaveFile;
  private pl.mpak.sky.gui.swing.Action cmSaveFileAs;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JSeparator jSeparator4;
  private javax.swing.JSeparator jSeparator5;
  private javax.swing.JSeparator jSeparator6;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JMenuItem menuOpenFile;
  private javax.swing.JMenuItem menuRun;
  private javax.swing.JMenuItem menuSaveFile;
  private javax.swing.JMenuItem menuSaveFileAs;
  private javax.swing.JMenu menuView;
  private OrbadaJavaSyntaxTextArea syntaxBeanShell;
  private javax.swing.JTextArea textResult;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  // End of variables declaration//GEN-END:variables
  
}
