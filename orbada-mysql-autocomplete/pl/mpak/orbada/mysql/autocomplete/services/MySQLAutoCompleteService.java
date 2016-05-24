/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.autocomplete.services;

import java.util.ArrayList;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.autocomplete.OrbadaMySQLAutocompletePlugin;
import pl.mpak.orbada.plugins.providers.SyntaxEditorAutoCompleteProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.gui.swing.AutoCompleteItem;
import pl.mpak.sky.gui.swing.ParametrizedAutoCompleteItem;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MySQLAutoCompleteService extends SyntaxEditorAutoCompleteProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLAutocompletePlugin.class);

  private OrbadaSyntaxTextArea syntaxTextArea;

  @Override
  public boolean isForEditor(SyntaxTextArea syntaxTextArea) {
    if (syntaxTextArea instanceof OrbadaSyntaxTextArea) {
      this.syntaxTextArea = (OrbadaSyntaxTextArea)syntaxTextArea;
      return true;
    }
    return false;
  }

  public String getDescription() {
    return stringManager.getString("MySQLAutoCompleteService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

  public AutoCompleteItem[] populate(String[] words, boolean bracketMode, int commaCount) {
    Database database = syntaxTextArea.getDatabase();
    if (database == null || !OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      return null;
    }
    if (database.isExecutingSql()) {
      application.postPluginMessage(new PluginMessage(Consts.orbadaSystemPluginId, "status-text", stringManager.getString("MySQLAutoCompleteService-sql-executing")));
      return null;
    }
    ArrayList<AutoCompleteItem> items = MySQLAutoCompleteDatabaseService.instance.getAutoCompleteList(database, words, bracketMode, commaCount);
    ArrayList<AutoCompleteItem> populated = new ArrayList<AutoCompleteItem>();
    if (words.length == 0 || items.size() == 0) {
      populated = items;
    }
    else {
      populated = new ArrayList<AutoCompleteItem>();
      String word = words[words.length -1].toUpperCase();
      if (bracketMode) {
        for (AutoCompleteItem item : items) {
          if (item.getWord().toUpperCase().equals(word)) {
            if (item instanceof ParametrizedAutoCompleteItem) {
              ((ParametrizedAutoCompleteItem)item).setCommaCount(commaCount);
            }
            populated.add(item);
          }
        }
      }
      else {
        for (AutoCompleteItem item : items) {
          if (item.getWord().toUpperCase().startsWith(word)) {
            if (item instanceof ParametrizedAutoCompleteItem) {
              ((ParametrizedAutoCompleteItem)item).setCommaCount(-1);
            }
            populated.add(item);
          }
        }
      }
    }
    return populated.toArray(new AutoCompleteItem[populated.size()]);
  }

}
