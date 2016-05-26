/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.GeneralSettingsPanel;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalSettingsProvider extends SettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  public static String settingsName = "orbada-universal";
  public static String setDeleteAfterDays = "delete-after-days";
  
  public static String setAutoSaveEditor = "auto-save-editor";
  public static String setAutoSaveEditorContentIntervalSeconds = "auto-save-editor-content-interval-seconds";
  public static String setAutoSaveEditorContent = "auto-save-editor-content";
  public static String setOnErrorShowMessageBox = "on-error-show-message-box";
  public static String setAutoExpandSqlText = "auto-expand-sql-text";
  public static String setNewEditorContent = "new-editor-content";
  public static String setMaxColumnListCount = "max-column-list-count";
  public static String setStoringColumnListPosition = "storing-column-list-position";
  public static String setSplitPanelVertical = "split-panel-vertical";
  public static String setAutoCloneConnection = "auto-clone-connection";
  public static String setCommentAtFirstLineTitle = "comment-at-first-line-as-title";

  public static int AUTO_SAVE_EDITOR_DRIVER = 0;
  public static int AUTO_SAVE_EDITOR_DB_SERVER = 1;
  public static int AUTO_SAVE_EDITOR_SCHEMA = 2;

  public final static int default_setMaxColumnListCount = 10;
  public final static boolean default_setStoringColumnListPosition = true;
  public final static boolean default_setSplitPanelVertical = false;
  public final static boolean default_setAutoCloneConnection = false;

  @Override
  public String getSettingsPath() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public Component getSettingsComponent() {
    return new GeneralSettingsPanel(application);
  }

  public String getDescription() {
    return stringManager.getString("UniversalSettingsProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }

}
