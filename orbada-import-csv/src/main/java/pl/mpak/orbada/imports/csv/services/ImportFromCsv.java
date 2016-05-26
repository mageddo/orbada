package pl.mpak.orbada.imports.csv.services;

import java.util.Arrays;
import pl.mpak.orbada.imports.csv.OrbadaImportCsvPlugin;
import pl.mpak.orbada.imports.csv.engine.CsvImport;
import pl.mpak.orbada.imports.csv.engine.CsvImportAbortedException;
import pl.mpak.orbada.imports.csv.engine.CsvImportConfiguration;
import pl.mpak.orbada.imports.csv.engine.CsvImportEvent;
import pl.mpak.orbada.imports.csv.engine.CsvImportListener;
import pl.mpak.orbada.imports.csv.gui.ImportFromCsvDialog;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ImportToolActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.task.Task;

/**
 *
 * @author proznicki
 */
public class ImportFromCsv extends ImportToolActionProvider {

  private static final StringManager stringManager = StringManagerFactory.getStringManager("import-csv");
  
  private CsvImportConfiguration config;

  public ImportFromCsv() {
    super();
    config = new CsvImportConfiguration();
    setText(stringManager.getString("ImportFromCsv-text"));
    setActionCommandKey("ImportFromCsv");
  }

  @Override
  public final String getDescription() {
    return stringManager.getString("ImportFromCsv-description");
  }

  private ISettings getSettings() {
    return application.getSettings("import-from-csv-file");
  }
  
  private void storeSettings() {
    ISettings settings = getSettings();
    settings.setValue("encoding", config.getEncoding());
    settings.setValue("file-name", config.getFileName());
    settings.setValue("table-name", config.getTableName());
    settings.setValue("delimiter", "" +config.getDelimiter());
    settings.setValue("escape-mode", (long)config.getEscapeMode());
    settings.setValue("record-delimiter", "" +config.getRecordDelimiter());
    settings.setValue("text-qualifier", "" +config.getTextQualifier());
    settings.setValue("header-present", config.isHeaderPresent());
    settings.setValue("ignore-errors", config.isIgnoreErrors());
    settings.setValue("skip-empty-records", config.isSkipEmptyRecords());
    settings.setValue("trim-whitespace", config.isTrimWhitespace());
    settings.setValue("use-text-qualifier", config.isUseTextQualifier());
    settings.setValue("add-null-check", config.isAddNullCheck());
    settings.setValue("add-primary-key", config.isAddPrimaryKey());
    settings.setValue("precision-round", (long)config.getPrecisionRound());
    if (config.getTablePrimaryKey() != null) {
      settings.setValue("table-primary-key", Arrays.asList(config.getTablePrimaryKey()).toString().replaceAll("[\\[|\\]]", ""));
    }
    else {
      settings.setValue("table-primary-key", "");
    }
    settings.setValue("table-automatic", config.isTableAutomatic());
    settings.store();
  }
  
  private void loadSettings() {
    ISettings settings = getSettings();
    config.setEncoding(settings.getValue("encoding", config.getEncoding()));
    config.setFileName(settings.getValue("file-name", config.getFileName()));
    config.setTableName(settings.getValue("table-name", config.getTableName()));
    config.setDelimiter(settings.getValue("delimiter", "" +config.getDelimiter()));
    config.setEscapeMode(settings.getValue("escape-mode", (long)config.getEscapeMode()).intValue());
    config.setRecordDelimiter(settings.getValue("record-delimiter", "" +config.getRecordDelimiter()));
    config.setTextQualifier(settings.getValue("text-qualifier", "" +config.getTextQualifier()));
    config.setHeaderPresent(settings.getValue("header-present", config.isHeaderPresent()));
    config.setIgnoreErrors(settings.getValue("ignore-errors", config.isIgnoreErrors()));
    config.setSkipEmptyRecords(settings.getValue("skip-empty-records", config.isSkipEmptyRecords()));
    config.setTrimWhitespace(settings.getValue("trim-whitespace", config.isTrimWhitespace()));
    config.setUseTextQualifier(settings.getValue("use-text-qualifier", config.isUseTextQualifier()));
    config.setAddNullCheck(settings.getValue("add-null-check", config.isAddNullCheck()));
    config.setAddPrimaryKey(settings.getValue("add-primary-key", config.isAddPrimaryKey()));
    config.setPrecisionRound(settings.getValue("precision-round", (long)config.getPrecisionRound()).intValue());
    config.setTablePrimaryKey(settings.getValue("table-primary-key", "").split(","));
    config.setTableAutomatic(settings.getValue("table-automatic", config.isTableAutomatic()));
  }

  @Override
  public String getGroupName() {
    return OrbadaImportCsvPlugin.pluginGroupName;
  }

  @Override
  public boolean isButton() {
    return false;
  }
  
  @Override
  protected void doImport(final Database database) {
    loadSettings();
    if (ImportFromCsvDialog.showDialog(config, database)) {
      storeSettings();
      final String description = "Csv import";
      database.getTaskPool().addTask(new Task(description) {
        @Override
        public void run() {
          try {
            CsvImport csv = new CsvImport(config);
            csv.addImportListener(new CsvImportListener() {
              long time;
              @Override
              public void beforeImport(CsvImportEvent event) {
                time = System.currentTimeMillis();
              }
              @Override
              public void afterImport(CsvImportEvent event) {
              }
              @Override
              public void beforeImportRecord(CsvImportEvent event) {
                if (isCanceled()) {
                  event.setAction(CsvImportEvent.ABORT_IMORT);
                }
                else if (System.currentTimeMillis() -time > 1000) {
                  setDescription(description +" " +event.getRecNo());
                  time = System.currentTimeMillis();
                }
              }
              @Override
              public void afterImportRecord(CsvImportEvent event) {
              }
            });
            csv.process(database);
          } catch (final Exception ex) {
            if (ex instanceof CsvImportAbortedException) {
              MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("import"), stringManager.getString("import-aborted"), ModalResult.OK);
            }
            else {
              ExceptionUtil.processException(ex);
              MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
            }
          }
        }
      });
    }
  }

}
