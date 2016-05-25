package pl.mpak.usedb.util.access;


public interface IConnectionAccessor {

  public ResultColumnList tableColumnList(String[] tableName) throws AccessorException;
  
  public void reset();
  
}
