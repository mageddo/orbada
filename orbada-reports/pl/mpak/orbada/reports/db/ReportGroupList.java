/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.db;

import java.io.Serializable;
import java.util.ArrayList;
import pl.mpak.orbada.reports.bean.ReportsGroup;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class ReportGroupList extends ArrayList<ReportsGroup> implements Serializable {

  public ReportGroupList() {
  }
  
  public void createFromQuery(Query query) {
    try {
      while (!query.eof()) {
        ReportsGroup group = new ReportsGroup();
        DefaultBufferedRecord record = new DefaultBufferedRecord(query.getDatabase(), group);
        record.updateFrom(query);
        add(group);
        query.next();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
//  private synchronized void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
//    stream.writeInt(size());
//    for (ReportsGroup group : this) {
//      stream.writeObject(group);
//    }
//  }

}
