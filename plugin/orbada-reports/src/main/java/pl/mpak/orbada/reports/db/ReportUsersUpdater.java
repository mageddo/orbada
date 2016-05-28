/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class ReportUsersUpdater extends ReportUserRecord {

  private String usrName;

  public ReportUsersUpdater(Database database) {
    super(database);
  }
  
  public ReportUsersUpdater(Database database, String orepu_id) throws UseDBException {
    super(database, orepu_id);
  }
  
  public String getUsrName() {
    return usrName;
  }

  public void setUsrName(String usrName) {
    this.usrName = usrName;
  }
  
  public static String getSql() {
    return
      "select orepu_id, orepu_usr_id, orepu_orep_id, usr_name\n" +
      "  from orep_users, users\n" +
      " where orepu_usr_id = usr_id\n" +
      "   and orepu_orep_id = :OREP_ID\n" +
      "union all\n" +
      "select cast( null as varchar(40) ) orepu_id, usr_id orepu_usr_id, cast( :OREP_ID as varchar(40) ) orepu_orep_id, usr_name\n" +
      "  from users\n" +
      " where not exists (select 0 from orep_users where orepu_usr_id = usr_id and orepu_orep_id = :OREP_ID)\n" +
      "   and case when usr_orbada is null then 'N' else usr_orbada end = 'N'\n" +
      " order by usr_name";
  }
  
}
