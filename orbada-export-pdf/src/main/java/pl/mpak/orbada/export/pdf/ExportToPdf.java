/*
 * ExportToPdf.java
 *
 * Created on 2008-10-28, 18:04:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.export.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.util.Date;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author proznicki
 */
public class ExportToPdf extends ExportTableActionProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("export-pdf");

  private String nullDataValue;
  private String fileName;
  private String charset;
  private String pageSize;

  public ExportToPdf() {
    setText(stringManager.getString("ExportToPdf-text"));
    setActionCommandKey("ExportToPdf");
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return stringManager.getString("ExportToPdf-description");
  }

  private ISettings getConfig() {
    return application.getSettings("export-to-pdf-file");
  }

  private void localConfig() {
    try {
      nullDataValue = getConfig().getValue("null-data-value").getString();
      charset = getConfig().getValue("charset").getString();
      fileName = getConfig().getValue("file-name").getString();
      pageSize = getConfig().getValue("page-size").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  private int[] getColumnWidths(JTable table) {
    int[] array = new int[table.getColumnCount()];
    for (int i=0; i<table.getColumnCount(); i++) {
      array[i] = (int)((double)table.getColumnModel().getColumn(i).getWidth() /(double)table.getWidth() *100f);
    }
    return array;
  }

  private void exportTable(JTable table) {
    try {
      Document document;
      if ("A1".equals(pageSize)) {
        document = new Document(PageSize.A1.rotate(), 10, 10, 10, 10);
      }
      else if ("A2".equals(pageSize)) {
        document = new Document(PageSize.A2.rotate(), 10, 10, 10, 10);
      }
      else if ("A3".equals(pageSize)) {
        document = new Document(PageSize.A3.rotate(), 10, 10, 10, 10);
      }
      else if ("A4".equals(pageSize)) {
        document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
      }
      else if ("A5".equals(pageSize)) {
        document = new Document(PageSize.A5.rotate(), 10, 10, 10, 10);
      }
      else {
        document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
      }
      document.addAuthor(application.getUserName());
      document.addSubject("Orbada Data Export Plugin");
      PdfWriter.getInstance(document, new FileOutputStream(fileName));
      document.open();
      BaseFont bf;
      bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, true);
      Font stringFont = new Font(bf, Font.DEFAULTSIZE, Font.NORMAL, QueryTableCellRenderer.stringColor);
      Font boolFont = new Font(bf, Font.DEFAULTSIZE, Font.NORMAL, QueryTableCellRenderer.boolColor);
      Font dateFont = new Font(bf, Font.DEFAULTSIZE, Font.NORMAL, QueryTableCellRenderer.dateColor);
      Font numberFont = new Font(bf, Font.DEFAULTSIZE, Font.NORMAL, QueryTableCellRenderer.numberColor);
      Font nullFont = new Font(bf, Font.DEFAULTSIZE, Font.NORMAL, QueryTableCellRenderer.nullColor);
      
      PdfPTable datatable = new PdfPTable(table.getColumnCount());
      datatable.getDefaultCell().setPadding(3);
      datatable.setWidths(getColumnWidths(table));
      datatable.setWidthPercentage(100);
      datatable.getDefaultCell().setBorderWidth(2);
      datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
      for (int c = 0; c < table.getColumnCount(); c++) {
        Phrase ph = new Phrase(new String(table.getColumnModel().getColumn(c).getHeaderValue().toString().getBytes(), charset), stringFont);
        datatable.addCell(ph);
      }
      datatable.setHeaderRows(1);
      datatable.getDefaultCell().setBorderWidth(1);
      for (int r = 0; r < table.getRowCount(); r++) {
        for (int c = 0; c < table.getColumnCount(); c++) {
          Object o = table.getValueAt(r, c);
          Variant v;
          if (o instanceof Variant) {
            v = (Variant)o;
          }
          else {
            v = new Variant(o);
          }
          Phrase ph;
          Font f = stringFont;
          if (v.isNullValue()) {
            f = nullFont;
          }
          else {
            if (v.getValue() instanceof Number) {
              f = numberFont;
            } else if (v.getValue() instanceof Date) {
              f = dateFont;
            } else if (v.getValue() instanceof Boolean) {
              f = boolFont;
            }
          }
          if ("".equals(v.toString())) {
            ph = new Phrase(new String(nullDataValue.getBytes(), charset), f);
          } else {
            ph = new Phrase(new String(v.toString().getBytes(), charset), f);
          }
          PdfPCell cell = new PdfPCell(ph);
          if (!v.isNullValue() && v.getValue() instanceof Number) {
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          }
          else if (!v.isNullValue() && v.getValue() instanceof Date) {
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
          }
          else {
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
          }
          datatable.addCell(cell);
        }
      }
      document.add(datatable);
      document.close();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show((Frame) null, "B³¹d", ex.getMessage(), new int[]{ModalResult.OK});
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToPdfDialog.showDialog(getConfig())) {
            localConfig();
            exportTable(table);
          }
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaExportPdfPlugin.exportGroupName;
  }
}
