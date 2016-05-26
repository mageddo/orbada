/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.snippets;

import java.util.ArrayList;
import java.util.List;
import orbada.Consts;
import orbada.gui.comps.OrbadaJavaSyntaxTextArea;
import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.swing.syntax.ApplySyntaxTextAreaEvent;
import pl.mpak.sky.gui.swing.syntax.Snippet;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SnippetsManager {

  private class SnippetFor {
    public String editor;
    public String driverType;
    public Snippet snippet;
  }

  private IApplication application;
  private ArrayList<SnippetFor> snippetList;
  private ISettings settings;
  private boolean inited;
  
  public final static String SET_SNIPPETS_ENABLED = "snippets-enabled";
  
  public SnippetsManager(IApplication application) {
    inited = false;
    this.application = application;
    settings = application.getSettings(Consts.orbadaSnippetsPluginId);
    snippetList = new ArrayList<SnippetFor>();
  }
  
  public void reloadSnippets(boolean reset, boolean refreshEditors) {
    if (reset || !inited) {
      inited = true;
      snippetList.clear();
      try {
        Query query = application.getOrbadaDatabase().createQuery();
        query.setSqlText(
          "select snp_name, snp_code, snp_editor, snp_immediate, case when dtp_id is null then 'all' else dtp_name end driver_type \n" +
          "  from snippets left join driver_types on snp_dtp_id = dtp_id\n" +
          " where snp_active = 'T'\n" +
          "   and (snp_usr_id is null or snp_usr_id = :USR_ID)");
        query.paramByName("USR_ID").setString(application.getUserId());
        query.open();
        while (!query.eof()) {
          SnippetFor sf = new SnippetFor();
          sf.editor = query.fieldByName("snp_editor").getString();
          sf.driverType = query.fieldByName("driver_type").getString();
          sf.snippet = new Snippet(
            query.fieldByName("snp_name").getString(),
            query.fieldByName("snp_code").getString(),
            StringUtil.toBoolean(query.fieldByName("snp_immediate").getString()));
          snippetList.add(sf);
          query.next();
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    if (refreshEditors) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          SyntaxTextArea.apply(new ApplySyntaxTextAreaEvent() {
            @Override
            public void apply(SyntaxTextArea editor) {
              OrbadaSnippetsPlugin.getSnippetsManager().refreshSnippets(editor);
            }
          });
        }
      });
    }
  }
  
  private List<Snippet> getSnippets(String editor, String driverType) {
    ArrayList<Snippet> snippets = new ArrayList<Snippet>();
    for (SnippetFor snippet : snippetList) {
      if (StringUtil.equalsIgnoreCase(snippet.editor, editor) &&
          StringUtil.equalAnyOfString(snippet.driverType, new String[] {driverType, "all"}, true)) {
        snippets.add(snippet.snippet);
      }
    }
    return snippets;
  }
  
  public void refreshSnippets(SyntaxTextArea syntaxTextArea) {
    reloadSnippets(false, false);
    syntaxTextArea.getDocument().clearSnippets();
    if (settings.getValue(SET_SNIPPETS_ENABLED, true) && syntaxTextArea != null) {
      List<Snippet> snippets = null;
      if (syntaxTextArea instanceof OrbadaSyntaxTextArea) {
        OrbadaSyntaxTextArea area = (OrbadaSyntaxTextArea)syntaxTextArea;
        if (area.getDatabase() != null) {
          snippets = getSnippets("sql", area.getDatabase().getDriverType());
        }
      }
      else if (syntaxTextArea instanceof OrbadaJavaSyntaxTextArea) {
        OrbadaJavaSyntaxTextArea area = (OrbadaJavaSyntaxTextArea)syntaxTextArea;
        if (area.getDatabase() != null) {
          snippets = getSnippets("java", area.getDatabase().getDriverType());
        }
      }
      if (snippets != null) {
        for (Snippet snippet : snippets) {
          syntaxTextArea.getDocument().addSnippet(snippet);
        }
      }
    }
  }
  
}
