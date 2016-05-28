/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports;

/**
 *
 * @author akaluza
 */
public class Sql {
  
  public static String getGlobalGroupList(String filter) {
    return String.format(
      "select *\n" +
      "  from orep_groups\n" +
      " where orepg_dtp_id = :DTP_ID\n" +
      "   and orepg_usr_id is null\n" +
      "   and orepg_sch_id is null\n" +
      "   and (orepg_orepg_id = :OREPG_ID or (orepg_orepg_id is null and :OREPG_ID is null))\n" +
      "%1$s" +
      " order by orepg_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getSharedGroupList(String filter) {
    return String.format(
      "select *\n" +
      "  from orep_groups\n" +
      " where orepg_dtp_id = :DTP_ID\n" +
      "   and (orepg_usr_id = :USR_ID or exists (select 0 from orep_users, orep_reports where orepu_orep_id = orep_id and orepu_usr_id = :USR_ID and orep_orepg_id = orepg_id))\n" +
      "   and (orepg_sch_id = :SCH_ID or (orepg_sch_id is null and :SCH_ID is null))\n" +
      "   and orepg_orepg_id is null\n" +
      "   and orepg_shared = 'T'\n" +
      "%1$s" +
      " order by orepg_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getUserGroupList(String filter) {
    return String.format(
      "select *\n" +
      "  from orep_groups\n" +
      " where orepg_dtp_id = :DTP_ID\n" +
      "   and orepg_usr_id = :USR_ID\n" +
      "   and (orepg_sch_id = :SCH_ID or (orepg_sch_id is null and :SCH_ID is null))\n" +
      "   and (orepg_shared = 'F' or orepg_shared is null)\n" +
      "   and orepg_orepg_id is null\n" +
      "%1$s" +
      " order by orepg_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getGroupByGroupList(String filter) {
    return String.format(
      "select *\n" +
      "  from orep_groups\n" +
      " where orepg_dtp_id = :DTP_ID\n" +
      "   and orepg_orepg_id = :OREPG_ID\n" +
      "%1$s" +
      " order by orepg_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getReportList(String filter) {
    return String.format(
      "select *\n" +
      "  from orep_reports\n" +
      " where orep_orepg_id = :OREPG_ID\n" +
      "%1$s" +
      " order by orep_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

  public static String getDetailReportList(String filter) {
    return String.format(
      "select *\n" +
      "  from orep_reports\n" +
      " where orep_orep_id = :OREP_ID\n" +
      "%1$s" +
      " order by orep_name",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }

}
