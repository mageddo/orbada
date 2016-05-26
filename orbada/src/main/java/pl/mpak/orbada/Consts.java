/*
 * Consts.java
 *
 * Created on 20 stycze� 2007, 13:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada;

import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.id.VersionID;

/**
 *
 * @author akaluza
 */
public class Consts {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  public final static VersionID orbadaVersion = new VersionID(1, 2, 4, 369);
  public final static String orbadaReleaseDate = "2015-04-02";
  public final static String orbadaSubname = "Dzianisz, Polska";
  
  public final static String orbadaUserId = "20080808194053-00000D1B617B0B5A-EBA871AB";
  public final static String orbadaAutor = "Andrzej Ka�u�a";
  public final static String orbadaYears = "2007-2015";
  public final static String orbadaCopyrights = "All interested";
  public final static String orbadaFirstRun = stringManager.getString("splash-orbada-first-run-3d");

  public final static String orbadaGroupName = "orbada";
  public final static String orbadaToolsGroupName = "Orbada Tools";
  public final static String orbadaLookAndFeelGroupName = "Look And Feel";
  public final static int splashscreenVerticalShift = 20;
  public final static int splashscreenBottomShift = 20;
  
  /**
   * <p>Identyfikatory standardowych wtyczek - do r�nych zastosowa�
   */
  public final static String orbadaSystemPluginId = "orbada-system-plugin";

  public final static String orbadaUniversalPluginId = "orbada-universal-plugin";
  public final static String orbadaTodoPluginId = "orbada-todo-plugin";
  public final static String orbadaProgrammersPluginId = "orbada-programmers-plugin";
  public final static String orbadaExportPluginId = "orbada-export-plugin";
  public final static String orbadaExportDbfPluginId = "orbada-export-dbf-plugin";
  public final static String orbadaExportCsvPluginId = "orbada-export-csv-plugin";
  public final static String orbadaImportCsvPluginId = "orbada-import-csv-plugin";
  public final static String orbadaDerbyDbPluginId = "orbada-derbydb-plugin";
  public final static String orbadaHSqlDbPluginId = "orbada-hsqldb-plugin";
  public final static String orbadaCommanderPluginId = "orbada-commander-plugin";
  public final static String orbadaBeanShellPluginId = "orbada-beanshell-plugin";
  public final static String orbadaWelcomePluginId = "orbada-welcome-plugin";
  public final static String orbadaOraclePluginId = "orbada-oracle-plugin";
  public final static String orbadaOracleDbaPluginId = "orbada-oracle-dba-plugin";
  public final static String orbadaOracleTunePluginId = "orbada-oracle-tune-plugin";
  public final static String orbadaOracleDebugPluginId = "orbada-oracle-debug-plugin";
  public final static String orbadaProjectsPluginId = "orbada-projects-plugin";
  public final static String orbadaGadgetsPluginId = "orbada-gadgets-plugin";
  public final static String orbadaReportsPluginId = "orbada-reports-plugin";
  public final static String orbadaExportPdfPluginId = "orbada-export-pdf-plugin";
  public final static String orbadaExportExcelPluginId = "orbada-export-excel-plugin";
  public final static String orbadaSqlMacrosPluginId = "orbada-sql-macros-plugin";
  public final static String orbadaSqlScriptsPluginId = "orbada-sql-scripts-plugin";
  public final static String orbadaFirebirdPluginId = "orbada-firebird-plugin";
  public final static String orbadaJaybirdPluginId = "orbada-jaybird-plugin";
  public final static String orbadaLocalHistoryPluginId = "orbada-local-history-plugin";
  public final static String orbadaMeldDiffPluginId = "orbada-meld-diff-plugin";
  public final static String orbadaMySQLPluginId = "orbada-mysql-plugin";
  public final static String orbadaDataMovePluginId = "orbada-data-move-plugin";
  public final static String orbadaSQLitePluginId = "orbada-sqlite-plugin";
  public final static String orbadaFirebirdAutocompletePluginId = "orbada-firebird-autocomplete-plugin";
  public final static String orbadaLafJGoodiesPluginId = "orbada-laf-jgoodies-plugin";
  public final static String orbadaLafTinyLaFPluginId = "orbada-laf-tinylaf-plugin";
  public final static String orbadaLafNimRODPluginId = "orbada-laf-nimrod-plugin";
  public final static String orbadaLafSubstancePluginId = "orbada-laf-substance-plugin";
  public final static String orbadaLafJTatooPluginId = "orbada-laf-jtatoo-plugin";
  public final static String orbadaExportDataTextPluginId = "orbada-export-data-text-plugin";
  public final static String orbadaAdderPluginId = "orbada-adder-plugin";
  public final static String orbadaMySQLAutocompletePluginId = "orbada-mysql-autocomplete-plugin";
  public final static String orbadaPostgreSQLPluginId = "orbada-postgresql-plugin";
  public final static String orbadaSnippetsPluginId = "orbada-snippets-plugin";
  
  /**
   * <p>Globalne komunikaty, PluginUniqueId = null
   */
  public final static String globalMessageOrbadaStarted = "orbada-started";
  public final static String globalMessagePerspectiveOpened = "perspective-opened";
  public final static String globalMessagePerspectiveClosed = "perspective-closed";
  public final static String globalMessageViewOpened = "view-opened";
  public final static String globalMessageViewClosed = "view-closed";
  public final static String globalMessageEditorRefreshInfo = "syntax-editor-refresh-info";
  public final static String globalMessageFreezeObject = "freeze-object";
  public final static String globalMessageTransactionStateChange = "transaction-state-change";
  /**
   * <p>Nast�pi�o usuni�cie, edycja lub wstawienie schematu po��czenia
   * <p>W parametrze zostanie przekazany id schematu
   */
  public final static String globalMessageSchemaDeleted = "schema-deleted";
  public final static String globalMessageSchemaUpdated = "schema-updated";
  public final static String globalMessageSchemaInserted = "schema-inserted";
  
  public final static String statusBarPanelMemmory = "orbada-memmory";
  public final static String statusBarPanelGC = "orbada-gc";
  public final static String statusBarPanelTime = "orbada-time";
  
  public final static int STATUS_PANEL_HEIGHT = 20;
  
  public final static String orbadaSettings = "orbada-settings";
  public final static String dataTableAutoFitWidth = "data-table-auto-fit-width";
  public final static String newConnectionAtStartup = "new-connection-at-startup";
  public final static String queryTableNullValue = "query-table-null-value";
  public final static String queryTableDataFont = "query-table-data-font";
  public final static String disableLoadSqlSyntaxInfo = "disable-load-sql-syntax-info";
  public final static String useGlobalSettingsDisableLoadSqlSyntaxInfo = "use-global-settings-disable-load-sql-syntax-info";
  public final static String autoSaveColumnWidths = "auto-save-column-widths";
  public final static String disableCheckUpdates = "disable-check-updates";
  public final static String noRollbackOnClose = "no-rollback-on-close";
  public final static String appCloseWarning = "app-close-warning";

  public final static String sqlSyntaxSettings = "sql-syntax-settings";
  public final static String javaSyntaxSettings = "java-syntax-settings";

  public final static String colorizedQueryTable = "colorized-query-table";
  public final static String queryTableColorNumber = "query-table-color-number";
  public final static String queryTableColorDate = "query-table-color-date";
  public final static String queryTableColorBool = "query-table-color-boolean";
  public final static String queryTableColorString = "query-table-color-string";
  public final static String queryTableColorNull = "query-table-color-null";
  public final static String colorSelectedTableRow = "color-selected-table-row";
  public final static String defaultColorSelectedTableRow = "default-color-selected-table-row";
  public final static String tableEvenRowShift = "table-even-row-shift";
  public final static String tableFocusedColumnShift = "table-focused-column-shift";

  public final static String dataFormatDateDefault = "data-format-date-default";
  public final static String dataFormatDateString = "data-format-date-string";
  public final static String defaultDataFormatDateString = "yyyy-MM-dd hh:mm:ss";
  public final static String dataFormatTimeDefault = "data-format-time-default";
  public final static String dataFormatTimeString = "data-format-time-string";
  public final static String defaultDataFormatTimeString = "hh:mm:ss";
  public final static String dataFormatTimestampDefault = "data-format-timestamp-default";
  public final static String dataFormatTimestampString = "data-format-timestamp-string";
  public final static String defaultDataFormatTimestampString = "yyyy-MM-dd hh:mm:ss.SSS";
  public final static String dataFormatNumericDefault = "data-format-numeric-default";
  public final static String dataFormatNumericString = "data-format-numeric-string";
  public final static String defaultDataFormatNumericString = ",##0.####################";
  public final static String dataFormatBigDecimalDefault = "data-format-bigdecimal-default";
  public final static String dataFormatBigDecimalString = "data-format-bigdecimal-string";
  public final static String defaultDataFormatBigDecimalString = ",##0.####################";
  public final static String dataFormatDecimalSeparator = "data-format-decimal-separator";
  public final static String dataFormatDecimalSeparatorDefault = "data-format-decimal-separator-default";
  public final static String defaultDataFormatDecimalSeparator = ".";
  public final static String defaultDataExportDecimalFormat = "0.##########";
  public final static String defaultSomeDataExportDecimalSeparator = ".";
  public final static int    defaultNumberFormatMaximumFractionDigits = 20;
  
  public final static String noViewTabPictures = "no-view-tab-pictures";
  public final static String noViewTabTitles = "no-view-tab-titles";

  public final static String pleaseWaitRendererSetting = "please-wait-renderer";
  public final static String pleaseWaitRendererOnSetting = "please-wait-renderer-on";

  public final static String lookAndFeelSetting = "look-and-feel";
  public final static String lookAndFeelDefaultSetting = "look-and-feel-default";
  public final static String lookAndFeelLocalClass = "orbada.laf.class";

  public final static String editorTabMovesSelected = "editor-tab-moves-selected";
  public final static String editorTabAsSpaces = "editor-tab-as-spaces";
  public final static String editorCopySyntaxHighlight = "copy-syntax-highlight";
  public final static String editorEditorTrimWhitespaces = "editor-trim-whitespaces";
  public final static String editorEditorSmartHomeEnd = "editor-smart-home-end";
  public final static String editorAutoCompleteDot = "editor-auto-complete-dot";
  public final static String editorAutoCompleteActivateChars = "editor-auto-complete-activate-chars";
  public final static String editorAutoCompleteInactivateChars = "editor-auto-complete-inactivate-chars";
  public final static String editorAutoCompleteInsertion = "editor-auto-complete-insertion";
  public final static String editorAutoCompleteInsertSingle = "editor-auto-complete-insert-single";
  public final static String editorAutoCompleteStructureParser = "editor-auto-complete-structure-parser";
  public final static String editorAutoCompleteStructureParserVariables = "editor-auto-complete-structure-parser-variables";
  public final static String editorTabToSpaceCount = "editor-tab-to-space-count";
  public final static String editorPreDefinedSnippets = "editor-pre-defined-snippets";

  // proxy
  public final static String proxySettings = "proxy-settings";
  public final static String proxyHttpAddress = "proxy-http-address";
  public final static String proxyHttpPort = "proxy-http-port";
  public final static String proxyAuthNeeded = "proxy-auth-needed";
  public final static String proxyAuthUser = "proxy-auth-user";
  public final static String proxyAuthPassword = "proxy-auth-password";
  public final static String proxySettingsNoProxy = "no-proxy";
  public final static String proxySettingsSystemProxy = "system-proxy";
  public final static String proxySettingsManualProxy = "manual-proxy";
  public final static String proxyDefaultSettings = proxySettingsSystemProxy;

  public final static String orbadaWebPage = "orbada.web.page";
  public final static String orbadaUpdatePage = "orbada.update.page";
  public final static String orbadaWebRootPageUrl = ""; //"http://akaluza_nb:8080/orbada";
  public final static String orbadaUpdateRootPageUrl = ""; //"http://akaluza_nb:8080/orbada";

  public final static int exitCode_Normal = 0;
  public final static int exitCode_Terminate = -1;
  public final static int exitCode_Restart = 100;

}
