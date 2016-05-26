/*
 * ExportToDbf.java
 *
 * Created on 2008-10-26, 18:04:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.export.csv;

import com.csvreader.CsvWriter;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author proznicki
 */
public class ExportToCsv extends ExportTableActionProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("export-csv");

  private String nullDataValue;
  private String fileName;
  private String charset;
  private String fieldDelimiter;
  private Boolean columnNames;
  private String textQualifier;
  private Boolean forceTextQualifier;
  private String escapeMode;
  private String endOfLineChar;
  
  public ExportToCsv() {
    setText(stringManager.getString("ExportToCsv-text"));
    setActionCommandKey("ExportToCsv");
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return stringManager.getString("ExportToCsv-description");
  }

  private ISettings getConfig() {
    return application.getSettings("export-to-csv-file");
  }

  private void localConfig() {
    try {
      nullDataValue = getConfig().getValue("null-data-value").getString();
      charset = getConfig().getValue("charset").getString();
      fileName = getConfig().getValue("file-name").getString();
      fieldDelimiter = getConfig().getValue("field-delimiter", ",");
      if (StringUtil.isEmpty(fieldDelimiter)) {
        fieldDelimiter = ",";
      }
      else if (StringUtil.equals("TAB", fieldDelimiter)) {
        fieldDelimiter = "\t";
      }
      else if (StringUtil.equals("SPACE", fieldDelimiter)) {
        fieldDelimiter = " ";
      }
      columnNames = getConfig().getValue("column-names", true);
      textQualifier = getConfig().getValue("text-qualifier", "\"").trim();
      forceTextQualifier = getConfig().getValue("force-text-qualifier", false);
      escapeMode = getConfig().getValue("escape-mode", "\\");
      endOfLineChar = getConfig().getValue("end-of-line-char", "\\n");
      if (StringUtil.equals("\\n", endOfLineChar)) {
        endOfLineChar = "\n";
      }
      else if (StringUtil.equals("\\r", endOfLineChar)) {
        endOfLineChar = "\r";
      }
      else if (StringUtil.equals("\\f", endOfLineChar)) {
        endOfLineChar = "\f";
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  private void exportTable(JTable table) {
    File file = new File(fileName);
    if (file.exists()) {
      if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("ExportToCsv-csv-save"), stringManager.getString("ExportToCsv-file-exist-rewrite-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.NO) {
        return;
      }
      file.delete();
    }
    CsvWriter writer = null;
    try {
      writer = new CsvWriter(new FileOutputStream(file), fieldDelimiter.charAt(0), Charset.forName(charset));
      if ("BACKSLASH".equals(escapeMode)) {
        writer.setEscapeMode(CsvWriter.ESCAPE_MODE_BACKSLASH);
      }
      else if ("DOUBLED".equals(escapeMode)) {
        writer.setEscapeMode(CsvWriter.ESCAPE_MODE_DOUBLED);
      }
      writer.setRecordDelimiter(endOfLineChar.charAt(0));
      if (!"".equals(textQualifier)) {
        writer.setTextQualifier(textQualifier.charAt(0));
        writer.setForceQualifier(forceTextQualifier);
      }

      if (columnNames) {
        String[] names = new String[table.getColumnCount()];
        for (int c = 0; c < table.getColumnCount(); c++) {
          if (table.getColumnModel().getColumn(c) instanceof QueryTableColumn) {
            QueryTableColumn column = (QueryTableColumn) table.getColumnModel().getColumn(c);
            names[c] = column.getFieldName();
          } else {
            names[c] = table.getColumnModel().getColumn(c).getHeaderValue().toString();
          }
        }
        writer.writeRecord(names);
      }
      String[] values = new String[table.getColumnCount()];
      for (int r = 0; r < table.getRowCount(); r++) {
        for (int c = 0; c < table.getColumnCount(); c++) {
          String s = "";
          Object o = table.getValueAt(r, c);
          if (o != null) {
            if (o instanceof Variant) {
              s = ((Variant)o).getString();
            }
            else {
              s = o.toString();
            }
            values[c] = s;
          }
          else {
            values[c] = nullDataValue;
          }
        }
        writer.writeRecord(values);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show((Frame) null, stringManager.getString("error"), ex.getMessage(), new int[]{ModalResult.OK});
    }
    finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToCsvDialog.showDialog(getConfig(), table)) {
            localConfig();
            exportTable(table);
          }
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaExportCsvPlugin.exportGroupName;
  }
}
