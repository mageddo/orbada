package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;


public class Select extends SqlElement {

  private WithList withList;
  private ColumnList columnList;
  private Into into;
  private TableList tableList;
  private Where where;
  private GroupBy groupBy;
  private Having having;
  private Union union;
  private OrderBy orderBy;

  public Select(CodeElement owner) {
    super(owner, "Select");
  }

  public void setWithList(WithList withList) {
    this.withList = withList;
  }

  public ColumnList getColumnList() {
    if (columnList == null) {
      columnList = new ColumnList(this);
    }
    return columnList;
  }

  public void setColumnList(ColumnList columnList) {
    this.columnList = columnList;
  }
  
  public Into getInto() {
    if (into == null) {
      into = new Into(this);
    }
    return into;
  }

  public void setInto(Into into) {
    this.into = into;
  }

  public TableList getTableList() {
    if (tableList == null) {
      tableList = new TableList(this);
    }
    return tableList;
  }

  public void setTableList(TableList tableList) {
    this.tableList = tableList;
  }

  public Where getWhere() {
    if (where == null) {
      where = new Where(this);
    }
    return where;
  }

  public void setWhere(Where where) {
    this.where = where;
  }

  public OrderBy getOrderBy() {
    if (orderBy == null) {
      orderBy = new OrderBy(this);
    }
    return orderBy;
  }

  public void setOrderBy(OrderBy orderBy) {
    this.orderBy = orderBy;
  }

  public GroupBy getGroupBy() {
    if (groupBy == null) {
      groupBy = new GroupBy(this);
    }
    return groupBy;
  }

  public void setGroupBy(GroupBy groupBy) {
    this.groupBy = groupBy;
  }

  public Having getHaving() {
    if (having == null) {
      having = new Having(this);
    }
    return having;
  }

  public void setHaving(Having having) {
    this.having = having;
  }

  public Union getUnion() {
    return union;
  }

  public void setUnion(Union union) {
    this.union = union;
  }

  public WithList getWithList() {
    return withList;
  }

  public CodeElement getElementAt(int offset) {
    return getElementAt(new CodeElement[] {withList, columnList, into, tableList, where, groupBy, having, union, orderBy}, offset);
  }
  
  public CodeElement[] find(Class<?> clazz) {
    return find(new CodeElement[] {withList, columnList, into, tableList, where, groupBy, having, union, orderBy}, clazz);
  }
  
  public void updateInfo() {
    if (columnList != null) {
      Table table = (Table)getOwner(Table.class, Union.class);
      if (table != null) {
        for (CodeElement e : columnList) {
          if (e != null) {
            Column c = (Column)e;
            if (c.getAlias() != null && e.getOwner() != null && e.getOwner().getOwner(ColumnList.class) == null) {
              table.getColumnList().add(c.getAlias());
            }
          }
        }
      }
    }
    if (tableList != null) {
      if (columnList != null) {
        for (CodeElement e : columnList) {
          if (e != null) {
            CodeElement[] iea = e.find(Identifier.class);
            for (CodeElement ie : iea) {
              if (((Identifier)ie).getTable() == null) {
                ((Identifier)ie).setTable(tableList.indexOf((Identifier)ie));
              }
            }
          }
        }
      }
      for (CodeElement e : tableList) {
        if (e != null) {
          CodeElement[] iea = e.find(Identifier.class);
          for (CodeElement ie : iea) {
            if (((Identifier)ie).getTable() == null) {
              ((Identifier)ie).setTable(tableList.indexOf((Identifier)ie));
            }
          }
        }
      }
      if (where != null) {
        for (CodeElement e : where) {
          if (e != null) {
            CodeElement[] iea = e.find(Identifier.class);
            for (CodeElement ie : iea) {
              if (((Identifier)ie).getTable() == null) {
                ((Identifier)ie).setTable(tableList.indexOf((Identifier)ie));
              }
            }
          }
        }
      }
      if (groupBy != null) {
        for (CodeElement e : groupBy) {
          if (e != null) {
            CodeElement[] iea = e.find(Identifier.class);
            for (CodeElement ie : iea) {
              if (((Identifier)ie).getTable() == null) {
                ((Identifier)ie).setTable(tableList.indexOf((Identifier)ie));
              }
            }
          }
        }
      }
      if (having != null) {
        for (CodeElement e : having) {
          if (e != null) {
            CodeElement[] iea = e.find(Identifier.class);
            for (CodeElement ie : iea) {
              if (((Identifier)ie).getTable() == null) {
                ((Identifier)ie).setTable(tableList.indexOf((Identifier)ie));
              }
            }
          }
        }
      }
      if (orderBy != null) {
        for (CodeElement e : orderBy) {
          if (e != null) {
            CodeElement[] iea = e.find(Identifier.class);
            for (CodeElement ie : iea) {
              if (((Identifier)ie).getTable() == null) {
                ((Identifier)ie).setTable(tableList.indexOf((Identifier)ie));
              }
            }
          }
        }
      }
    }
  }

  public String toString() {
    return
      (withList != null ? withList.toString() : "") +"\n" +
      (columnList != null ? columnList.toString() : "") +"\n" +
      (into != null ? into.toString() : "") +"\n" +
      (tableList != null ? tableList.toString() : "") +"\n" +
      (where != null ? where.toString() : "") +"\n" +
      (groupBy != null ? groupBy.toString() : "") +"\n" +
      (having != null ? having.toString() : "") +"\n" +
      (orderBy != null ? orderBy.toString() : "");
  }
  
  public String toSource(int level) {
    boolean brk = 
        getOwner() == null ||
        getOwner().getOwner(ColumnList.class) == null;
    return
      (withList != null ? withList.toSource(level) +"\n" : "") +
      keywordsToSource(level, "") +" " +
      (columnList != null ? columnList.toSource(level) : "") +
      (into != null ? (brk ? "\n  " : " ") +into.toSource(level) : "") +
      (tableList != null ? (brk ? "\n  " : " ") +tableList.toSource(level) : "") +
      (where != null ? (brk ? "\n " : " ") +where.toSource(level) : "") +
      (groupBy != null ? (brk ? "\n " : " ") +groupBy.toSource(level) : "") +
      (having != null ? (brk ? "\n" : " ") +having.toSource(level) : "") +
      (union != null ? (brk ? "\n" : " ") +union.toSource(level) : "") +
      (orderBy != null ? (brk ? "\n " : " ") +orderBy.toSource(level) : "")
      ;
  }

}
