package pl.mpak.orbada.export;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import javax.swing.JTable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.HexUtils;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ExportToSqlInsert extends ExportTableActionProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("export");

  private String fileName;
  private String charset;
  private String tableName;
  private boolean toClipboard;
  private ExportToSqlInsertDefinition eti;
  
  private String columnNameList;
  
  public ExportToSqlInsert() {
    setText(stringManager.getString("ExportToSqlInsert-text"));
    setActionCommandKey("ExportToSqlInsert");
    addActionListener(createActionListener());
  }
  
  public String getDescription() {
    return stringManager.getString("ExportToSqlInsert-description");
  }
  
  private ISettings getConfig() {
    return application.getSettings("export-to-sql-insert-file");
  }
  
  private void localConfig() {
    try {
      tableName = getConfig().getValue("table-name").getString();
      charset = getConfig().getValue("charset").getString();
      fileName = getConfig().getValue("file-name").getString();
      toClipboard = getConfig().getValue("to-clipboard", false);
      eti = ExportToSqlInsertDefinition.toDefinition(getConfig().getValue("def-user", (String)null));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private void prepareColumnNameList(JTable table) {
    StringBuilder sb = new StringBuilder();
    for (int c=0; c<table.getColumnCount(); c++) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      String columnName = StringUtil.replaceString(eti.getColumnName(), "$(column-name)", table.getColumnModel().getColumn(c).getHeaderValue().toString());
      columnName = StringUtil.replaceString(columnName, "$(COLUMN-NAME)", table.getColumnModel().getColumn(c).getHeaderValue().toString().toUpperCase());
      sb.append(columnName);
    }
    columnNameList = sb.toString();
  }
  
  private String convertValue(Variant value) throws VariantException, IOException {
    if (value.isNullValue()) {
      return eti.getNullValue();
    }
    switch (value.getValueType()) {
      case VariantType.varShort:
      case VariantType.varInteger:
      case VariantType.varLong:
      case VariantType.varByte:
      case VariantType.varBigInteger:
        return StringUtil.replaceString(eti.getNumericValue(), "$(value)", value.getString());
        
      case VariantType.varBigDecimal:
      case VariantType.varDouble:
      case VariantType.varFloat: {
        String s = value.getString();
        if (eti.isDotToComma()) {
          s = StringUtil.replaceString(s, ".", ",");
        }
        return StringUtil.replaceString(eti.getNumericValue(), "$(value)", s);
      }
      
      case VariantType.varBoolean: {
        String v;
        if (value.getBoolean()) {
          v = StringUtil.replaceString(eti.getTrueValue(), "$(value)", value.toString());
        }
        else {
          v = StringUtil.replaceString(eti.getFalseValue(), "$(value)", value.toString());
        }
        return v;
      }
      
      case VariantType.varDate:
      case VariantType.varTime:
      case VariantType.varTimestamp:
        return StringUtil.replaceString(eti.getTimestampValue(), "$(value)", value.toString());
        
      case VariantType.varString: {
        String v = value.toString();
        for (int i=0; i<eti.getVarcharChars().length(); i++) {
          v = StringUtil.replaceString(v, "" +eti.getVarcharChars().charAt(i), eti.getCharPrefix() +eti.getVarcharChars().charAt(i));
        }
        v = StringUtil.replaceString(eti.getVarcharValue(), "$(value)", v);
        return v;
      }
      
      case VariantType.varBinary:
      case VariantType.varJavaObject: {
        if (eti.isBinaryHex()) {
          return StringUtil.replaceString(eti.getBinaryValue(), "$(value)", HexUtils.convert(value.getBinary()));
        }
        return StringUtil.replaceString(eti.getBinaryValue(), "$(value)", value.toString());
      }
      
      case VariantType.varVariant:
        return convertValue(value.getVariant());
        
      default:
        return value.toString();
    }
  }
  
  private void exportRow(Writer out, JTable table, int row) throws IOException, VariantException, SQLException, UseDBException {
    if (table instanceof QueryTable) {
      ((QueryTable)table).getQuery().getRecord(row);
    }
    StringBuilder sb = new StringBuilder();
    for (int c=0; c<table.getColumnCount(); c++) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      if (table.getColumnModel().getColumn(c) instanceof QueryTableColumn) {
        QueryTableColumn column = (QueryTableColumn)table.getColumnModel().getColumn(c);
        sb.append(convertValue(column.getField().getValue()));
      }
      else {
        sb.append(convertValue(new Variant(table.getValueAt(row, c))));
      }
    }
    String command = StringUtil.replaceString(eti.getCommand(), "$(table-name)", tableName);
    command = StringUtil.replaceString(command, "$(TABLE-NAME)", tableName.toUpperCase());
    command = StringUtil.replaceString(command, "$(column-name-list)", columnNameList);
    command = StringUtil.replaceString(command, "$(value-list)", sb.toString());
    command = StringUtil.replaceString(command, "\\n", "\n");
    command = StringUtil.replaceString(command, "\\t", "\t");
    command = StringUtil.replaceString(command, "\\r", "\r");
    out.write(command);
    out.write("\n");
  }
  
  private void exportTable(JTable table) {
    try {
      prepareColumnNameList(table);
      Writer out = null;
      if (toClipboard) {
        out = new StringWriter();
      }
      else {
        out = new PrintWriter(new File(fileName), charset);
      }
      for (int r=0; r<table.getRowCount(); r++) {
        exportRow(out, table, r);
      }
      out.flush();
      if (toClipboard) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(new String(out.toString().getBytes(), charset));
        clipboard.setContents(data, data);
      }
      out.close();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(null, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        JTable table = getTable(event);
        if (table != null) {
          if (ExportToSqlInsertDialog.showDialog(getConfig())) {
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
