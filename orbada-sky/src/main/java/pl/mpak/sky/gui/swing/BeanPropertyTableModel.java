package pl.mpak.sky.gui.swing;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import pl.mpak.sky.Messages;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.Order;
import pl.mpak.util.SortableTable;
import pl.mpak.util.StringUtil;
import pl.mpak.util.array.VectorStringsComparator;

public class BeanPropertyTableModel extends DefaultTableModel implements SortableTable {
  private static final long serialVersionUID = -8779692040651977574L;

  private Object bean;
  private String[] exclude = null;

  public BeanPropertyTableModel() {
    super();
  }

  public BeanPropertyTableModel(Object bean) {
    this();
    setBean(bean);
  }

  public BeanPropertyTableModel(Object bean, String[] exclude) {
    super();
    if (exclude != null) {
      this.exclude = exclude.clone();
    }
    setBean(bean);
  }

  public void setBean(Object bean) {
    this.bean = bean;
    refresh();
  }

  public void refresh() {
    final Vector<String> columnNames = new Vector<String>();
    
    columnNames.add(Messages.getString("BeanPropertyTableModel.name")); //$NON-NLS-1$
    columnNames.add(Messages.getString("BeanPropertyTableModel.value")); //$NON-NLS-1$
    
    final Vector<Vector<?>> columnData = new Vector<Vector<?>>();
    if (this.bean != null) {
      try {
        BeanInfo info = Introspector.getBeanInfo(this.bean.getClass(),Introspector.USE_ALL_BEANINFO);
        processBeanInfo(info, columnData);
      }
      catch(Exception e) {
        ExceptionUtil.processException(e);
      }
    }

    Collections.sort(columnData, new VectorStringsComparator(0));

    setDataVector(columnData, columnNames);
  }

  private void processBeanInfo(BeanInfo info, Vector<Vector<?>> columnData) {
    BeanInfo[] extra = info.getAdditionalBeanInfo();
    if (extra != null) {
      for (int i = 0; i < extra.length; ++i) {
        processBeanInfo(extra[i], columnData);
      }
    }

    PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
    for (int i = 0; i < propDesc.length; ++i) {
      final String propName = propDesc[i].getName();
      final Method getter = propDesc[i].getReadMethod();
      if (propName != null && getter != null) {
        if (StringUtil.equalAnyOfString(propName, this.exclude)) {
          continue;
        }
        Vector<?> line = generateLine(propName, this.bean, getter);
        if (line != null) {
          columnData.add(line);
        }
      }
    }
  }

  protected Vector<?> generateLine(String propName, Object bean, Method getter) {
    final Vector<Object> line = new Vector<Object>();
    
    line.add(propName);
    try {
      line.add(executeGetter(bean, getter));
    }
    catch(Exception e) {
      line.add("<html><font color=red>" +e.getMessage() +"</font>"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    return line;
  }

  protected Object executeGetter(Object bean, Method getter)
      throws InvocationTargetException, IllegalAccessException {
    return getter.invoke(bean, (Object[])null);
  }

  @SuppressWarnings("unchecked")
  public void sortByColumn(int modelIndex, Order order, int modifiers) {
    Collections.sort(getDataVector(), new VectorStringsComparator(modelIndex, order));
  }

  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
