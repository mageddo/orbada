/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.webapp;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.IWebAppAccessibilities;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class WebAppAccessibilities implements IWebAppAccessibilities {

  @Override
  public String requestPost(String severity, String title, String os, String db, String jdbc, String java, String path, String callstack, String content, String workaround) {
    if (os == null) {
      os = System.getProperty("os.name") + ", " + System.getProperty("os.version");
    }
    if (java == null) {
      java = System.getProperty("java.vendor") + ", " + System.getProperty("java.version");
    }
    String result =
      "(USERID)\n" +Application.get().getUserId() +"\n" +
      //"(ORBADAID)\n" +Application.get().getOrbadaString("unique-id") +"\n" +
      "(OS)\n" +(StringUtil.isEmpty(os) ? "N/A" : os) +"\n" +
      (StringUtil.isEmpty(db) ? "" : "(DB)\n" +db +"\n") +
      (StringUtil.isEmpty(jdbc) ? "" : "(JDBC)\n" +jdbc +"\n") +
      "(JAVA)\n" +(StringUtil.isEmpty(java) ? "N/A" : java) +"\n" +
      (StringUtil.isEmpty(path) ? "" : "(PATH)\n" +path +"\n") +
      (StringUtil.isEmpty(callstack) ? "" : "(CALL STACK)\n" +callstack +"\n") +
      (StringUtil.isEmpty(workaround) ? "" : "(WORKAROUND)\n" +workaround +"\n") +
      content;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection data = new StringSelection(result);
    clipboard.setContents(data, data);
    if (SUGGESTION.equals(severity)) {
      try {
        Desktop.getDesktop().browse(new URI("http://sourceforge.net/tracker/?func=add&group_id=361699&atid=1509186"));
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else if (PROBLEM.equals(severity)) {
      try {
        Desktop.getDesktop().browse(new URI("http://sourceforge.net/tracker/?func=add&group_id=361699&atid=1509183"));
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    else if (ERROR.equals(severity)) {
      try {
        Desktop.getDesktop().browse(new URI("http://sourceforge.net/tracker/?func=add&group_id=361699&atid=1509183"));
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    return result;
  }

}
