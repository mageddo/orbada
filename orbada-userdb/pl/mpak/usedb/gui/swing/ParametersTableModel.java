package pl.mpak.usedb.gui.swing;

import java.sql.Types;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import pl.mpak.usedb.Messages;
import pl.mpak.usedb.core.Parameter;
import pl.mpak.usedb.core.ParameterList;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

public class ParametersTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  private ParameterList parameterList;
  
  public ParametersTableModel(ParametrizedCommand command) {
    this(command.getParameterList());
  }

  public ParametersTableModel(ParameterList parameterList) {
    super();
    this.parameterList = parameterList;
  }

  protected TableColumn createColumn(int index, int width, String title) {
    TableColumn tc = new TableColumn(index, width);
    
    tc.setHeaderValue(title);
    tc.setCellRenderer(new QueryTableCellRenderer(true));
    
    return tc;
  }

  public boolean isCellEditable(int row, int column) {
    return false;
  }
  
  public int getRowCount() {
    return parameterList == null ? 0 : parameterList.parameterCount();
  }

  public int getColumnCount() {
    return 0;
  }

  @SuppressWarnings("unchecked")
  public Class getColumnClass(int columnIndex) {
    return (new Class[] {String.class, String.class, String.class, Variant.class})[columnIndex];
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    Parameter parameter = parameterList.getParameter(rowIndex);
    try {
      switch (columnIndex) {
        case 0: return parameter.getParamName();
        case 1: return SQLUtil.typeToString(parameter.getParamDataType());
        case 2: return SQLUtil.paramModeToString(parameter.getParamMode());
        case 3: return (parameter.getValue() == null || parameter.getValue().isNullValue()) ? parameter.getValue() : parameter.getValue().getString();
        default: return ""; //$NON-NLS-1$
      }
    }
    catch (Exception e) {
      ExceptionUtil.processException(e);
      return "";
    }
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Parameter parameter = parameterList.getParameter(rowIndex);
    switch (columnIndex) {
      case 1:
        parameter.setParamDataType(SQLUtil.stringToType((String)aValue));
        break;
      case 2:
        parameter.setParamMode(SQLUtil.stringToParamMode((String)aValue));
        break;
      case 3:
        parameter.setValue(aValue, parameter.getParamDataType() == Types.NULL ? Types.VARCHAR : parameter.getParamDataType());
        break;
    }
  }

  public void configureTable(JTable table) {
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    DefaultTableColumnModel tableColumnModel = new DefaultTableColumnModel();
    table.setColumnModel(tableColumnModel);
    tableColumnModel.addColumn(createColumn(0, 150, Messages.getString("ParametersTableModel.parameter-name"))); //$NON-NLS-1$
    tableColumnModel.addColumn(createColumn(1, 100, Messages.getString("ParametersTableModel.type"))); //$NON-NLS-1$
    tableColumnModel.addColumn(createColumn(2, 60, Messages.getString("ParametersTableModel.mode"))); //$NON-NLS-1$
    tableColumnModel.addColumn(createColumn(3, 220, Messages.getString("ParametersTableModel.value"))); //$NON-NLS-1$
    
    if (parameterList.parameterCount() > 0) {
      table.changeSelection(0, 0, false, false);
    }
    else {
      table.changeSelection(-1, 0, false, false);
    }
  }

}
