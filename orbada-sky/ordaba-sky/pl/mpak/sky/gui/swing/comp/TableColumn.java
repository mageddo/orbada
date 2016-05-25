package pl.mpak.sky.gui.swing.comp;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TableColumn extends javax.swing.table.TableColumn {
  private static final long serialVersionUID = -1984725144672583968L;

  public TableColumn() {
    super();
  }

  public TableColumn(int modelIndex) {
    super(modelIndex);
  }

  public TableColumn(int modelIndex, int width) {
    super(modelIndex, width);
  }

  public TableColumn(int modelIndex, int width, String headerValue) {
    super(modelIndex, width);
    setHeaderValue(headerValue);
  }

  public TableColumn(int modelIndex, int width, TableCellRenderer cellRenderer, TableCellEditor cellEditor) {
    super(modelIndex, width, cellRenderer, cellEditor);
  }

  public TableColumn(int modelIndex, int width, String headerValue, TableCellRenderer cellRenderer) {
    super(modelIndex, width, cellRenderer, null);
    setHeaderValue(headerValue);
  }

}
