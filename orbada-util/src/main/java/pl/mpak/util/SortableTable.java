package pl.mpak.util;



public interface SortableTable {

  /**
   * @param modelIndex
   * @param order
   * @param modifiers is a a modifiers from InputEvent.getModifiersEx function
   */
  public void sortByColumn(int modelIndex, Order order, int modifiers);
  
}
