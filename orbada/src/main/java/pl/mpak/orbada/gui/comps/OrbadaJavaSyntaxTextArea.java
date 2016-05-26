package pl.mpak.orbada.gui.comps;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.providers.OrbadaJavaSyntaxTextAreaProvider;

/**
 *
 * @author akaluza
 */
public class OrbadaJavaSyntaxTextArea extends AbsOrbadaSyntaxTextArea {
  
  private OrbadaJavaSyntaxDocument syntaxDocument;
  
  public OrbadaJavaSyntaxTextArea() {
    super();
    init();
  }
  
  private void init() {
    setDocument(syntaxDocument = new OrbadaJavaSyntaxDocument());
    
    if (Application.get() != null) {
      OrbadaJavaSyntaxTextAreaProvider[] ojstaps = Application.get().getServiceArray(OrbadaJavaSyntaxTextAreaProvider.class);
      if (ojstaps.length > 0) {
        for (OrbadaJavaSyntaxTextAreaProvider ostap : ojstaps) {
          ostap.setSyntaxTextArea(this);
        }
      }
      OrbadaJavaSyntaxDocument.loadSettings(syntaxDocument);
      getEditorArea().setBackground(Application.get().getSettings().getValue("syntax-editor-background-color", getEditorArea().getBackground()));
      getEditorArea().setFont(Application.get().getSettings().getValue("syntax-editor-font", getEditorArea().getFont()));
    }
  }
  
}
