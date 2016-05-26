package pl.mpak.orbada.universal.gui.history;

import java.util.ArrayList;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;

public class QueryHistory {

  public static int maxHistoryCount = 50;
  
  private ArrayList<QueryHistoryItem> history = new ArrayList<QueryHistoryItem>(); 
  
  public QueryHistory() {
    super();
  }
  
  public ArrayList<QueryHistoryItem> getHistory() {
    return history;
  }
  
  public void clear() {
    history.clear();
  }
  
  public int getCount() {
    return history.size();
  }

  public QueryHistoryItem get(int index) {
    return history.get(index);
  }
  
  private void removeOutItems() {
    for (int i=getCount() -1; i > maxHistoryCount; i--) {
      history.remove(i);
    }
  }
  
  public QueryHistoryItem add(Query query) {
    QueryHistoryItem result = new QueryHistoryItem(query);
    history.add(0, result);
    removeOutItems();
    return result;
  }
  
  public QueryHistoryItem add(Command command) {
    QueryHistoryItem result = new QueryHistoryItem(command);
    history.add(0, result);
    removeOutItems();
    return result;
  }
  
}
