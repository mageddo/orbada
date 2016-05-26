/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.melddiff.services;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JMenu;
import orbada.gui.PerspectivePanel;
import pl.mpak.orbada.melddiff.OrbadaMeldDiffPlugin;
import pl.mpak.orbada.melddiff.cm.LeftContentAction;
import pl.mpak.orbada.melddiff.cm.RightContentAction;
import pl.mpak.orbada.plugins.providers.OrbadaSyntaxTextAreaProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author akaluza
 */
public class MeldSyntaxService extends OrbadaSyntaxTextAreaProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMeldDiffPlugin.class);

  private JMenu menu;
  
  private static String leftContent;
  private static String rightContent;
  
  public String getDescription() {
    return stringManager.getString("MeldSyntaxService-description");
  }

  public String getGroupName() {
    return OrbadaMeldDiffPlugin.pluginGroupName;
  }
  
  private static void checkFilledContents(PerspectivePanel panel) {
    if (leftContent != null && rightContent != null) {
      String tempDir = Resolvers.expand("$(java.io.tmpdir)");
      try {
        FileOutputStream fos = new FileOutputStream(tempDir + "/ojmeld-left.txt");
        fos.write(leftContent.getBytes());
        fos.close();
        fos = new FileOutputStream(tempDir + "/ojmeld-right.txt");
        fos.write(rightContent.getBytes());
        fos.close();
        panel.getPerspectiveAccesibilities().createView(new DiffViewService(tempDir + "/ojmeld-left.txt", tempDir + "/ojmeld-right.txt"));
        leftContent = null;
        rightContent = null;
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  public static void setLeftContent(String text, PerspectivePanel panel) {
    leftContent = text;
    checkFilledContents(panel);
  }

  public static void setRightContent(String text, PerspectivePanel panel) {
    rightContent = text;
    checkFilledContents(panel);
  }

  @Override
  public void setSyntaxTextArea(SyntaxTextArea syntaxTextArea) {
    super.setSyntaxTextArea(syntaxTextArea);
    if (syntaxTextArea instanceof SyntaxTextArea) {
      menu = new JMenu(stringManager.getString("MeldSyntaxService-menu-diff-content"));
      menu.add(new LeftContentAction(syntaxTextArea));
      menu.add(new RightContentAction(syntaxTextArea));
      syntaxTextArea.getEditorArea().getComponentPopupMenu().add(menu);
    }
  }

}
