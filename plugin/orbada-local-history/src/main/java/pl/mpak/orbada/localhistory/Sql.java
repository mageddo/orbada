/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory;

/**
 *
 * @author akaluza
 */
public class Sql {
  
  public static String getLastLhObjectList(String filter) {
    return String.format(
      "select o.*\n" +
      "  from olhobjects o,\n" +
      "       (select max(olho_id) olho_id, olho_object_schema, olho_object_type, olho_object_name\n" +
      "          from olhobjects\n" +
      "         where olho_sch_id = :SCH_ID\n" +
      "         group by olho_object_schema, olho_object_type, olho_object_name) mx\n" +
      " where o.olho_id = mx.olho_id",
      "%1$s",
      new Object[]{filter != null ? "   and " + filter + "\n" : ""});
  }  
  
  public static String getLastLhObjectListOrdered(String filter) {
    return getLastLhObjectList(filter) +
      " order by o.object_schema, o.object_name";
  }

  public static String getLastLhObjectCount(String filter) {
    return "select count( 0 ) cnt from (" +getLastLhObjectList(filter) +") x";
  }
  
  public static String getDeleteOldest() {
    return
      "delete from olhobjects\n" +
      " where exists (select 0 from schemas where sch_id = olho_sch_id and sch_usr_id = :USR_ID)\n" +
      "   and olho_created < :OLHO_CREATED\n" +
      "   and olho_description is null";
  }
  
  public static String getObjectHistoryList() {
    return
      "select olho_id, olho_created, length(olho_source) source_size, olho_description\n" +
      "  from olhobjects\n" +
      " where olho_sch_id = :SCH_ID\n" +
      "   and (olho_object_schema = :SCHEMA_NAME or (:SCHEMA_NAME is null and olho_object_schema is null))\n" +
      "   and olho_object_type = :OBJECT_TYPE\n" +
      "   and olho_object_name = :OBJECT_NAME\n" +
      " order by olho_created desc";
  }

}
