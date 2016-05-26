/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.export;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JTable;
import orbada.Consts;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class ExportToXml extends ExportTableActionProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaExportPlugin.class);

  private String resultsCode;
  private String rowCode;
  private String columnCode;
  private String columnAttrCode;
  private String dataBeginCode;
  private String dataEndCode;
  private String fileName;
  private String charset;

  private DecimalFormat df;

  public ExportToXml() {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    dfs.setDecimalSeparator(Consts.defaultSomeDataExportDecimalSeparator.charAt(0));
    df = new DecimalFormat(Consts.defaultDataExportDecimalFormat, dfs);
    
    setText(stringManager.getString("ExportToXml-text"));
    setActionCommandKey("ExportToXml");
    addActionListener(createActionListener());
  }
  
  public String getDescription() {
    return stringManager.getString("ExportToXml-description");
  }
  
  private ISettings getConfig() {
    return application.getSettings("export-to-xml-file");
  }
  
  private void localConfig() {
    try {
      resultsCode = getConfig().getValue("results").getString();
      rowCode = getConfig().getValue("row").getString();
      columnCode = getConfig().getValue("column").getString();
      columnAttrCode = getConfig().getValue("column-attr").getString();
      dataBeginCode = getConfig().getValue("data-begin").getString();
      dataEndCode = getConfig().getValue("data-end").getString();
      
      charset = getConfig().getValue("charset").getString();
      fileName = getConfig().getValue("file-name").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void exportRow(PrintWriter out, JTable table, int row) {
    Variant v = new Variant(df);
    out.println("  <" +rowCode +">");
    for (int c=0; c<table.getColumnCount(); c++) {
      String cc = StringUtil.replaceString(columnCode, "$(column-name)", table.getColumnModel().getColumn(c).getHeaderValue().toString());
      String ca = StringUtil.replaceString(columnAttrCode, "$(column-name)", table.getColumnModel().getColumn(c).getHeaderValue().toString());
      out.print("    <" +cc +(!"".equals(ca) ? " " +ca : "") +">" +dataBeginCode);
      v.setValue(table.getValueAt(row, c));
      out.print(v.toString());
      out.println(dataEndCode +"</" +cc +">");
    }
    out.println("  </" +rowCode +">");
  }
  
  private void exportTable(JTable table) {
    try {
      PrintWriter out = new PrintWriter(new File(fileName), charset);
      out.println(String.format("<?xml version='1.0' encoding='%s' ?>", new Object[] {charset}));
      out.println("<" +resultsCode +">");
      for (int r=0; r<table.getRowCount(); r++) {
        exportRow(out, table, r);
      }
      out.println("</" +resultsCode +">");
      out.close();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show((Frame)null, stringManager.getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToXmlDialog.showDialog(getConfig())) {
            localConfig();
            exportTable(table);
          }
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaExportPlugin.exportGroupName;
  }
  
}
