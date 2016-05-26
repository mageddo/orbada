/*
 * ExportToHtml.java
 *
 * Created on 2007-10-25, 18:04:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.export;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import javax.swing.JTable;
import orbada.Consts;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class ExportToHtml extends ExportTableActionProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaExportPlugin.class);

  private String metaCode;
  private String title;
  private String bodyAttrib;
  
  private String tableCode;
  private String tableAttrib;
  private String tableRowCode;
  private String tableRowAttrib;
  private String tableHeaderCode;
  private String tableHeaderAttrib;
  private String tableDataCode;
  private String tableDataAttrib;
  
  private String nullDataValue;
  private boolean firstLastAsNbsp;
    
  private String fileName;
  private boolean includeTitles;
  private String charset;

  public ExportToHtml() {
    setText(stringManager.getString("ExportToHtml-text"));
    setActionCommandKey("ExportToHtml");
    addActionListener(createActionListener());
  }
  
  public String getDescription() {
    return stringManager.getString("ExportToHtml-description");
  }
  
  private ISettings getConfig() {
    return application.getSettings("export-to-html-file");
  }
  
  private void localConfig() {
    try {
      title = getConfig().getValue("title").getString();
      metaCode = getConfig().getValue("meta-code").getString();
      bodyAttrib = getConfig().getValue("body-attrib").getString();
      
      tableCode = getConfig().getValue("table-code").getString();
      tableAttrib = getConfig().getValue("table-attrib").getString();
      tableRowCode = getConfig().getValue("table-row-code").getString();
      tableRowAttrib = getConfig().getValue("table-row-attrib").getString();
      tableHeaderCode = getConfig().getValue("table-header-code").getString();
      tableHeaderAttrib = getConfig().getValue("table-header-attrib").getString();
      tableDataCode = getConfig().getValue("table-data-code").getString();
      tableDataAttrib = getConfig().getValue("table-data-attrib").getString();

      nullDataValue = getConfig().getValue("null-data-value").getString();
      firstLastAsNbsp = getConfig().getValue("first-last-as-nbsp", false);

      includeTitles = getConfig().getValue("include-titles").getBoolean();
      charset = getConfig().getValue("charset").getString();
      fileName = getConfig().getValue("file-name").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void exportHeader(PrintWriter out, JTable table) {
    out.print("<" +tableRowCode +" " +tableRowAttrib +">");
    for (int c=0; c<table.getColumnCount(); c++) {
      out.print("<" +tableHeaderCode +" " +tableHeaderAttrib +">");
      String text = table.getColumnModel().getColumn(c).getHeaderValue().toString();
      if (firstLastAsNbsp && !"".equals(text)) {
        if (text.startsWith(" ")) {
          text = "&nbsp;" +text.substring(1);
        }
        if (text.endsWith(" ")) {
          text = text.substring(0, text.length() -1) +"&nbsp;";
        }
      }
      out.print(text);
      out.print("</" +tableHeaderCode +">");
    }
    out.println("</" +tableRowCode +">");
  }
  
  private void exportRow(PrintWriter out, JTable table, int row) {
    Variant v = new Variant(new DecimalFormat(Consts.defaultDataExportDecimalFormat));
    out.println("<" +tableRowCode +" " +tableRowAttrib +">");
    for (int c=0; c<table.getColumnCount(); c++) {
      out.print("<" +tableDataCode +" " +tableDataAttrib +">");
      v.setValue(table.getValueAt(row, c));
      if (v.isNullValue()) {
        out.print(nullDataValue);
      }
      else if (firstLastAsNbsp) {
        String text = v.toString();
        if (text.startsWith(" ")) {
          text = "&nbsp;" +text.substring(1);
        }
        if (text.endsWith(" ")) {
          text = text.substring(0, text.length() -1) +"&nbsp;";
        }
        out.print(text);
      }
      else {
        out.print(v.toString());
      }
      out.print("</" +tableDataCode +">");
    }
    out.println("</" +tableRowCode +">");
  }
  
  private void exportTable(JTable table) {
    try {
      PrintWriter out = new PrintWriter(new File(fileName), charset);
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<TITLE>" +title +"</TITLE>");
      out.println("<META NAME=\"Generator\" CONTENT=\"Orbada Export To Html\" />");
      out.println(String.format("<META HTTP-EQUIV=\"content-type\" CONTENT=\"text/html; charset=%s\" />", new Object[] {charset}));
      out.println(metaCode);
      out.println("</HEAD>");
      out.println("<BODY " +bodyAttrib +">");
      out.println("<" +tableCode +" " +tableAttrib +">");
      if (includeTitles) {
        exportHeader(out, table);
      }
      for (int r=0; r<table.getRowCount(); r++) {
        exportRow(out, table, r);
      }
      out.println("</" +tableCode +">");
      out.println("</BODY>");
      out.println("</HTML>");
      out.close();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show((Frame)null, "B³¹d", ex.getMessage(), new int[] {ModalResult.OK});
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToHtmlDialog.showDialog(getConfig())) {
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
