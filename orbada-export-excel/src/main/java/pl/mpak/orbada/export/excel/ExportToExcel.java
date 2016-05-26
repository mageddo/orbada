/*
 * ExportToExcel.java
 *
 * Created on 2008-10-28, 18:04:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.export.excel;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import javax.swing.JTable;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author proznicki
 */
public class ExportToExcel extends ExportTableActionProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("export-excel");

  private String nullDataValue;
  private String fileName;
  private String charset;

  public ExportToExcel() {
    setText(stringManager.getString("ExportToExcel-text"));
    setActionCommandKey("ExportToExcel");
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return stringManager.getString("ExportToExcel-description");
  }

  private ISettings getConfig() {
    return application.getSettings("export-to-excel-file");
  }

  private void localConfig() {
    try {
      nullDataValue = getConfig().getValue("null-data-value").getString();
      charset = getConfig().getValue("charset").getString();
      fileName = getConfig().getValue("file-name").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  private void exportTable(JTable table) {
    try {
      Workbook wb = null;
      String ext = FileUtil.fileExtension(fileName);
      if ("xls".equalsIgnoreCase(ext)) {
        wb = new HSSFWorkbook();
      }
      else {
        wb = new SXSSFWorkbook();
      }
      Sheet s = wb.createSheet();
      wb.setSheetName(0, "Orbada Export");

      Row row = s.createRow(0);
      for (int c = 0; c < table.getColumnCount(); c++) {
        Cell cell = row.createCell((short) c);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(new String(table.getColumnModel().getColumn(c).getHeaderValue().toString().getBytes(), charset));
      }
      for (int r = 0; r < table.getRowCount(); r++) {
        Row trow = s.createRow(r + 1);
        for (int c = 0; c < table.getColumnCount(); c++) {
          Variant v;
          Object o = table.getValueAt(r, c);
          if (o instanceof Variant) {
            v = (Variant)o;
          }
          else {
            v = new Variant(o);
          }
          if (v.isNullValue()) {
            Cell cell = trow.createCell((short) c);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(new String(nullDataValue.getBytes(), charset));
          } else {
            Cell cell = trow.createCell((short) c);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(new String(v.toString().getBytes(), charset));
          }
        }
      }
      FileOutputStream fileOut = new FileOutputStream(fileName);
      wb.write(fileOut);
      fileOut.close();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show((Frame) null, stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToExcelDialog.showDialog(getConfig())) {
            localConfig();
            exportTable(table);
          }
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaExportExcelPlugin.exportGroupName;
  }
}
