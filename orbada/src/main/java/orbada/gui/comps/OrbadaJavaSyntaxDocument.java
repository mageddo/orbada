/*
 * OrbadaJavaSyntaxDocument.java
 *
 * Created on 2007-11-08, 18:50:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.gui.comps;

import orbada.Consts;
import orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.swing.syntax.JavaSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxStyle;

/**
 *
 * @author akaluza
 */
public class OrbadaJavaSyntaxDocument extends JavaSyntaxDocument {
  
  public OrbadaJavaSyntaxDocument() {
    super();
  }
  
  public static void loadSettings(SyntaxDocument doc) {
    ISettings settings = Application.get().getSettings(Consts.javaSyntaxSettings);
    for (SyntaxStyle style : doc.getStyleMap().values()) {
      String ss = settings.getValue(style.getName(), "");
      if (!"".equals(ss)) {
        try {
          style.fromString(ss);
        }
        catch (Exception ex) {
          ;
        }
      }
    }
  }
  
  public static void storeSettings(SyntaxDocument doc) {
    ISettings settings = Application.get().getSettings(Consts.javaSyntaxSettings);
    for (SyntaxStyle style : doc.getStyleMap().values()) {
      settings.setValue(style.getName(), style.toString());
    }
    settings.store();
  }
  
}
