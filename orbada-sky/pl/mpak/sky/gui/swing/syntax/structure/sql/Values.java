package pl.mpak.sky.gui.swing.syntax.structure.sql;

import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;

public class Values extends SqlElement {
  
  private WithList withList;
  private ValuesList values;
  private Union union;
  private OrderBy orderBy;

  public Values(CodeElement owner) {
    super(owner, "Values");
  }

  public void setWithList(WithList withList) {
    this.withList = withList;
  }

  public ValuesList getValues() {
    if (values == null) {
      values = new ValuesList(this);
    }
    return values;
  }

  public void setValues(ValuesList values) {
    this.values = values;
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

  public Union getUnion() {
    return union;
  }

  public void setUnion(Union union) {
    this.union = union;
  }

  public CodeElement getElementAt(int offset) {
    return getElementAt(new CodeElement[] {withList, values, union, orderBy}, offset);
  }

  public CodeElement[] find(Class<?> clazz) {
    return find(new CodeElement[] {withList, values, union, orderBy}, clazz);
  }

  public WithList getWithList() {
    return withList;
  }

  public String toSource(int level) {
    return
      (withList != null ? withList.toSource(level) +"\n" : "") +
      keywordsToSource(level, "") +" " +
      (values != null ? values.toSource(level) : "") +
      (union != null ? "\n" +union.toSource(level) : "") +
      (orderBy != null ? "\n" +orderBy.toSource(level) : "")
      ;
  }

}
