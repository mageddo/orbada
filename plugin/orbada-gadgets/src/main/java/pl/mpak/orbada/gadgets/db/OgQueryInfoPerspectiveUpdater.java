/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gadgets.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class OgQueryInfoPerspectiveUpdater extends OgQueryInfoPerspective {

  private String gqiName;
  private String dtpName;

  public OgQueryInfoPerspectiveUpdater(Database database) {
    super(database);
  }
  
  public OgQueryInfoPerspectiveUpdater(Database database, String qip_id) throws UseDBException {
    super(database, qip_id);
  }
  
  public String getGqiName() {
    return gqiName;
  }

  public void setGqiName(String gqiName) {
    this.gqiName = gqiName;
  }
  
  public String getDtpName() {
    return dtpName;
  }

  public void setDtpName(String dtpName) {
    this.dtpName = dtpName;
  }
  
  public static String getSql() {
    return
      "select gqi_name, dtp_name, qip_id, qip_gqi_id, qip_pps_id, qip_interval_s, qip_order\n" +
      "  from og_queryinfos\n" +
      "         left outer join driver_types on (gqi_dtp_id = dtp_id),\n" +
      "       og_queryinfo_perspectives\n" +
      " where (gqi_usr_id = :USR_ID or gqi_usr_id is null)\n" +
      "   and (gqi_dtp_id = :DTP_ID or gqi_dtp_id is null)\n" +
      "   and gqi_id = qip_gqi_id \n" +
      "   and qip_pps_id = :PPS_ID\n" +
      "union all\n" +
      "select gqi_name, dtp_name, cast( null as varchar(40) ) qip_id, gqi_id qip_gqi_id, cast( :PPS_ID as varchar(40) ) qip_pps_id, cast( null as integer ) qip_interval_s, cast( null as integer ) qip_order\n" +
      "  from og_queryinfos\n" +
      "         left outer join driver_types on (gqi_dtp_id = dtp_id)\n" +
      " where (gqi_usr_id = :USR_ID or gqi_usr_id is null)\n" +
      "   and (gqi_dtp_id = :DTP_ID or gqi_dtp_id is null)\n" +
      "   and not exists (select 0 from og_queryinfo_perspectives where gqi_id = qip_gqi_id and qip_pps_id = :PPS_ID)\n" +
      " order by gqi_name";
  }
  
}
