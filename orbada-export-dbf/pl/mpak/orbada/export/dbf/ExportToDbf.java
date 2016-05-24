/*
 * ExportToDbf.java
 *
 * Created on 2008-10-26, 18:04:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.export.dbf;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import pl.mpak.util.variant.Variant;

/**
 *
 * @author proznicki
 */
public class ExportToDbf extends ExportTableActionProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaExportDbfPlugin.class);

  private String nullDataValue;
  private String fileName;
  private String charset;

  public ExportToDbf() {
    setText(stringManager.getString("ExportToDbf-text"));
    setActionCommandKey("ExportToDbf");
    addActionListener(createActionListener());
  }

  public String getDescription() {
    return stringManager.getString("ExportToDbf-description");
  }

  private ISettings getConfig() {
    return application.getSettings("export-to-dbf-file");
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
    File file = new File(fileName);
    if (file.exists()) {
      if (MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("ExportToDbf-dbf-save"), stringManager.getString("ExportToDbf-file-exists-rewrite-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.NO) {
        return;
      }
      file.delete();
    }
    try {
      //PrintWriter out = new PrintWriter(new File(fileName), charset);
      DBFWriter writer = new DBFWriter(file);
      DBFField fields[] = new DBFField[table.getColumnCount()];
      for (int c = 0; c < table.getColumnCount(); c++) {
        if (table.getColumnModel().getColumn(c) instanceof QueryTableColumn) {
          QueryTableColumn column = (QueryTableColumn) table.getColumnModel().getColumn(c);
          String fieldName = new String(column.getFieldName().getBytes(), charset);
          fields[c] = new DBFField();
          fields[c].setName(fieldName.substring(0, Math.min(10, fieldName.length())));
          fields[c].setDataType(DBFField.FIELD_TYPE_C);
          fields[c].setFieldLength(column.getField().getOptimalSize());
        } else {
          String fieldName = new String(table.getColumnModel().getColumn(c).getHeaderValue().toString().getBytes(), charset);
          fields[c] = new DBFField();
          fields[c].setName(fieldName.substring(0, Math.min(10, fieldName.length())));
          fields[c].setDataType(DBFField.FIELD_TYPE_C);
          fields[c].setFieldLength(table.getColumnModel().getColumn(c).getWidth());
        }
      }
      writer.setFields(fields);
      for (int r = 0; r < table.getRowCount(); r++) {
        Object rowData[] = new Object[table.getColumnCount()];
        for (int c = 0; c < table.getColumnCount(); c++) {
          String s;
          Object o = table.getValueAt(r, c);
          if (o != null) {
            if (o instanceof Variant) {
              s = ((Variant)o).getString();
            }
            else {
              s = o.toString();
            }
            rowData[c] = new String(s.getBytes(), charset);
          }
          else {
            rowData[c] = new String(nullDataValue.getBytes(), charset);
          }
        }
        writer.addRecord(rowData);
      }
//      FileOutputStream fos = new FileOutputStream(fileName);
      writer.write();
//      fos.close();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show((Frame) null, stringManager.getString("error"), ex.getMessage(), new int[]{ModalResult.OK});
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToDbfDialog.showDialog(getConfig())) {
            localConfig();
            exportTable(table);
          }
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaExportDbfPlugin.exportGroupName;
  }
}
