/*
 * RefreshDatabaseInfoAction.java
 *
 * Created on 2007-11-01, 19:29:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.comps.OrbadaSQLSyntaxDocument;
import pl.mpak.orbada.gui.comps.OrbadaSQLSyntaxDocument;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class RefreshDatabaseInfoAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  public RefreshDatabaseInfoAction() {
    setText(stringManager.getString("RefreshDatabaseInfoAction-text"));
    setShortCut(KeyEvent.VK_F5, 0);
    setActionCommandKey("cmRefreshDatabaseInfo");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SyntaxEditor se = (SyntaxEditor)getParent(e, SyntaxEditor.class);
        if (se != null) {
          OrbadaSQLSyntaxDocument doc = (OrbadaSQLSyntaxDocument)se.getDocument();
          if (doc != null) {
            doc.refreshKeyWords(true);
            Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageEditorRefreshInfo, se));
          }
        }
      }
    };
  }
  
}
