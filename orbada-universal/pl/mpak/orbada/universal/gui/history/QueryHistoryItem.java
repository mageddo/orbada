package pl.mpak.orbada.universal.gui.history;

import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;

public class QueryHistoryItem {
  
  private long executedTime = 0;
  private long executingTime = 0;
  private int fetchedRows = 0;
  private String sqlText = null;

  public QueryHistoryItem() {
    super();
  }

  /**
   * 
   * @param query 
   */
  public QueryHistoryItem(Query query) {
    this(query.getOpenTime(), query.getOpeningTime(), query.getRecordCount(), query.getSqlText());
  }

  /**
   * 
   * @param command 
   */
  public QueryHistoryItem(Command command) {
    this(command.getExecutedTime(), command.getExecutionTime(), command.getUpdateCount(), command.getSqlText());
  }

  public QueryHistoryItem(long executedTime, long executingTime, int fetchedRows, String sqlText) {
    super();
    setExecutedTime(executedTime);
    setExecutingTime(executingTime);
    setFetchedRows(fetchedRows);
    setSqlText(sqlText);
  }

  public void setExecutingTime(long executingTime) {
    this.executingTime = executingTime;
  }

  public long getExecutingTime() {
    return executingTime;
  }

  public void setFetchedRows(int fetchedRows) {
    this.fetchedRows = fetchedRows;
  }

  public int getFetchedRows() {
    return fetchedRows;
  }

  public void setSqlText(String sqlText) {
    this.sqlText = new String(sqlText).trim();
  }

  public String getSqlText() {
    return sqlText;
  }

  public void setExecutedTime(long executedTime) {
    this.executedTime = executedTime;
  }

  public long getExecutedTime() {
    return executedTime;
  }

}
