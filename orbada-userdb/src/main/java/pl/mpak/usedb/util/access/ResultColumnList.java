package pl.mpak.usedb.util.access;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultColumnList {

  private ArrayList<ResultColumn> columnList;
  
  public ResultColumnList() {
    columnList = new ArrayList<ResultColumn>();
  }

  public ArrayList<ResultColumn> getColumnList() {
    return columnList;
  }
  
  public int count() {
    return columnList.size();
  }
  
  public ResultColumn get(int index) {
    return columnList.get(index);
  }
  
  public void add(ResultColumn column) {
    columnList.add(column);
  }
  
  public String toString() {
    return Arrays.toString(columnList.toArray());
  }

}
