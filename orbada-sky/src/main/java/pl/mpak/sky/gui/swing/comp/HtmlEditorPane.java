package pl.mpak.sky.gui.swing.comp;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import pl.mpak.sky.gui.swing.CursorChanger;
import pl.mpak.sky.gui.swing.DefaultUndoManager;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.actions.CmTab;
import pl.mpak.sky.gui.swing.comp.actions.CmTabBackward;
import pl.mpak.util.ExceptionUtil;

public class HtmlEditorPane extends JEditorPane {
  private static final long serialVersionUID = 6788534818118401715L;

  protected DefaultUndoManager undoManager;
  private URL homeURL;
  private URL currentURL;
  private final LinkedList<URL> history = new LinkedList<URL>();
  private int historyIndex = -1;

  public HtmlEditorPane() {
    super();
    init();
  }

  public HtmlEditorPane(URL initialPage) throws IOException {
    super(initialPage);
    setURL(initialPage);
    init();
  }

  public HtmlEditorPane(String url) throws IOException {
    super(url);
    setURL(new URL(url));
    init();
  }

  public HtmlEditorPane(String type, String text) {
    super(type, text);
    init();
  }

  private void init() {
    setComponentPopupMenu(new PopupMenuText(this) {
      private static final long serialVersionUID = 1L;
        protected void updateActions() {
          getUndoManager().getCmUndoEdit().setEnabled(HtmlEditorPane.this.isEnabled() && HtmlEditorPane.this.isEditable());
        }
    });

    SwingUtil.addAction(this, "cmTab", new CmTab(this));
    SwingUtil.addAction(this, "cmTabBackward", new CmTabBackward(this));
    getInputMap(JComponent.WHEN_FOCUSED).put(getUndoManager().getCmUndoEdit().getAltShortCut(), getUndoManager().getCmUndoEdit().getActionCommandKey());
    getComponentPopupMenu().insert(new JMenuItem(getUndoManager().getCmUndoEdit()), 0);
    getComponentPopupMenu().insert(new JPopupMenu.Separator(), 1);
    setEditable(false);
    setContentType("text/html");
    HyperlinkListener listener = createHyperLinkListener();
    if (listener != null) {
      addHyperlinkListener(listener);
    }
  }

  public DefaultUndoManager getUndoManager() {
    if (undoManager == null) {
      undoManager = new DefaultUndoManager(getDocument());
    }
    return undoManager;
  }
  
  public URL getURL() {
    return this.currentURL;
  }

  public URL getHomeURL() {
    return this.homeURL;
  }

  public void setHomeURL(URL homeURL) {
    this.homeURL = homeURL;
  }

  public void setPage(URL page) throws IOException {
    if (!page.equals(this.currentURL)) {
      ListIterator<URL> it = this.history.listIterator(this.historyIndex + 1);
      while (it.hasNext()) {
        it.next();
        it.remove();
      }
      this.history.add(page);
      this.historyIndex = this.history.size() - 1;
      super.setPage(page);
      this.currentURL = page;
    }
  }

  public void goBack() {
    if (historyIndex > 0 && historyIndex < history.size()) {
      displayURL((URL) history.get(--historyIndex));
    }
  }

  private void setURL(URL url) {
    if (url != null) {
      CursorChanger cursorChg = new CursorChanger(this);
      cursorChg.show();
      try {
        displayURL(url);
        this.history.add(url);
        this.historyIndex = 0;
      }
      finally {
        cursorChg.restore();
      }
    }
  }

  public void goForward() {
    if (historyIndex > -1 && historyIndex < history.size() - 1) {
      displayURL((URL) history.get(++historyIndex));
    }
  }

  public void goHome() {
    this.historyIndex = 0;
    displayURL(this.homeURL);
  }

  private void displayURL(URL url) {
    if (url != null) {
      try {
        setPage(url);
        this.currentURL = url;
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  protected HyperlinkListener createHyperLinkListener() {
    return new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          if (e instanceof HTMLFrameHyperlinkEvent) {
            ((HTMLDocument) ((JEditorPane) e.getSource()).getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
          }
          else {
            try {
              setPage(e.getURL());
            }
            catch (IOException ex) {
              ExceptionUtil.processException(ex);
            }
          }
        }
      }
    };
  }
  
  public void setText(String text) {
    super.setText(text);
    setCaretPosition(0);
  }
  
}
