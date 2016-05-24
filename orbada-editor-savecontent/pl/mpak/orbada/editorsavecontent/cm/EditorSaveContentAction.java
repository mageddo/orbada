/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.editorsavecontent.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import pl.mpak.orbada.editorsavecontent.OrbadaEditorSaveContentPlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;

/**
 *
 * @author akaluza
 */
public class EditorSaveContentAction extends Action {

  private SyntaxTextArea syntaxTextArea;
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaEditorSaveContentPlugin.class);
  private File lastFile;
  
  public EditorSaveContentAction(SyntaxTextArea syntaxTextArea) {
    super();
    setText(stringManager.getString("EditorSaveContentAction.text"));
    setTooltip(stringManager.getString("EditorSaveContentAction.tooltip"));
    setShortCut(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    this.syntaxTextArea = syntaxTextArea;
    setActionCommandKey("cmEditorSaveContent");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        lastFile = FileUtil.selectFileToSave(syntaxTextArea, lastFile, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("EditorSaveContentAction.ext_sql"), new String[] {".sql"})});
        if (lastFile != null) {
          try {
            syntaxTextArea.saveToFile(lastFile);
          } catch (IOException ex) {
            MessageBox.show(syntaxTextArea, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
          }
        }
      }
    };
  }

}
