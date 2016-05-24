/*
 * ExportTableToClipboard.java
 *
 * Created on 2007-10-09, 22:04:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.export;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class ExportToClipboard extends ExportTableActionProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaExportPlugin.class);

  public final static int RANGE_CURRENT = 0;
  public final static int RANGE_ALL = 1;
  
  private static ISettings config;
  
  private boolean fullEOL;
  private String separator;
  private int columnRange;
  private int rowRange;
  private boolean includeTitles;
  private boolean removeSeparator;
  private String charset;
  private boolean horizontal;
  private boolean decSepGlobSettings;
  private Variant valueGlobSet;
  
  public ExportToClipboard() {
    DecimalFormat format = new DecimalFormat();
    format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.getDefault()));
    format.setMaximumFractionDigits(Variant.getDefaultNumberFormatMaximumFractionDigits());
    format.setGroupingUsed(false);
    valueGlobSet = new Variant(format);
    setShortCut(KeyEvent.VK_C, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    setText(stringManager.getString("ExportToClipboard-text"));
    setActionCommandKey("ExportToClipboard");
    addActionListener(createActionListener());
  }
  
  private ISettings getConfig() {
    if (config == null) {
      config = application.getSettings("export-to-clipboard");
    }
    return config;
  }
  
  private String newLine() {
    if (fullEOL) {
      return "\r\n";
    }
    return "\n";
  }
  
  private String separator() {
    if ("TAB".equals(separator)) {
      return "\t";
    } else if ("ENTER".equals(separator)) {
      return "\n";
    }
    return separator;
  }
  
  private void exportRow(StringBuilder sb, JTable table, int row) {
    if (columnRange == RANGE_CURRENT && table.getSelectedColumn() != -1) {
      append(sb, table.getValueAt(row, table.getSelectedColumn()));
    } else {
      for (int c=0; c<table.getColumnCount(); c++) {
        append(sb, table.getValueAt(row, c));
        if (c < table.getColumnCount() -1) {
          sb.append(separator());
        }
      }
    }
  }
  
  private void append(StringBuilder sb, Object str) {
    if (str != null) {
      String s;
      if (str instanceof Variant) {
        try {
          if (decSepGlobSettings) {
            valueGlobSet.setValue(((Variant)str).getValue());
            s = valueGlobSet.toString();
          }
          else {
            s = ((Variant)str).getString();
          }
        } catch (Exception ex) {
          s = "";
        }
      }
      else {
        s = str.toString();
      }
      if (removeSeparator) {
        s = s.replace(separator(), "");
      }
      if (!"".equals(charset)) {
        try {
          s = new String(s.getBytes(), charset);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        }
      }
      sb.append(s);
    }
  }
  
  private void localConfig() {
    try {
      fullEOL = getConfig().getValue("full-eol").getBoolean();
      separator = getConfig().getValue("separator").getString();
      columnRange = getConfig().getValue("column-range").getInteger();
      rowRange = getConfig().getValue("row-range").getInteger();
      includeTitles = getConfig().getValue("include-titles").getBoolean();
      removeSeparator = getConfig().getValue("remove-separator").getBoolean();
      charset = getConfig().getValue("charset").getString();
      horizontal = getConfig().getValue("horizontal").getBoolean();
      decSepGlobSettings = getConfig().getValue("dec-sep-glob-settings").getBoolean();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToClipboardDialog.showDialog(getConfig())) {
            localConfig();
            StringBuilder sb = new StringBuilder();
            if (includeTitles) {
              if (columnRange == RANGE_CURRENT && table.getSelectedColumn() != -1) {
                sb.append(table.getColumnModel().getColumn(table.getSelectedColumn()).getHeaderValue().toString());
              } else {
                for (int c=0; c<table.getColumnCount(); c++) {
                  sb.append(table.getColumnModel().getColumn(c).getHeaderValue().toString() +(c < table.getColumnCount() -1 ? separator() : ""));
                }
              }
              if (columnRange == RANGE_CURRENT && horizontal) {
                sb.append(separator());
              }
              else {
                sb.append(newLine());
              }
            }
            if (rowRange == RANGE_CURRENT && table.getSelectedRow() != -1) {
              exportRow(sb, table, table.getSelectedRow());
            } else {
              for (int r=0; r<table.getRowCount(); r++) {
                exportRow(sb, table, r);
                if (columnRange == RANGE_CURRENT && horizontal && r < table.getRowCount() -1) {
                  sb.append(separator());
                }
                else {
                  sb.append(newLine());
                }
              }
            }
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(sb.toString());
            clipboard.setContents(data, data);
          }
        }
      }
    };
  }
  
  public String getDescription() {
    return stringManager.getString("ExportToClipboard-description");
  }

  public String getGroupName() {
    return OrbadaExportPlugin.exportGroupName;
  }
  
}
