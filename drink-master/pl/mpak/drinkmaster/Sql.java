/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.drinkmaster;

/**
 *
 * @author akaluza
 */
public class Sql {
  
  public static String getDrinkList(String filter) {
    return String.format(
      "select dnk_id, dnk_name, dnk_make_up, components\n" +
      "  from (select dnk_id, dnk_name, dnk_make_up,\n" +
      "               case when comp_1 is null then '' else comp_1 end||\n" +
      "               case when comp_2 is null then '' else ','||comp_2 end||\n" +
      "               case when comp_3 is null then '' else ','||comp_3 end||\n" +
      "               case when comp_4 is null then '' else ','||comp_4 end||\n" +
      "               case when comp_5 is null then '' else ','||comp_5 end||\n" +
      "               case when comp_6 is null then '' else ','||comp_6 end||\n" +
      "               case when comp_7 is null then '' else ','||comp_7 end||\n" +
      "               case when comp_8 is null then '' else ','||comp_8 end||\n" +
      "               case when comp_9 is null then '' else ','||comp_9 end components\n" +
      "          from (select dnk_id, dnk_name, dnk_make_up, \n" +
      "                       c1.dnc_component comp_1, c2.dnc_component comp_2, c3.dnc_component comp_3,\n" +
      "                       c4.dnc_component comp_4, c5.dnc_component comp_5, c6.dnc_component comp_6,\n" +
      "                       c7.dnc_component comp_7, c8.dnc_component comp_8, c9.dnc_component comp_9\n" +
      "                  from drinks \n" +
      "                         left outer join drink_compos c1 on (c1.dnc_dnk_id = dnk_id and c1.dnc_no = 1)\n" +
      "                         left outer join drink_compos c2 on (c2.dnc_dnk_id = dnk_id and c2.dnc_no = 2)\n" +
      "                         left outer join drink_compos c3 on (c3.dnc_dnk_id = dnk_id and c3.dnc_no = 3)\n" +
      "                         left outer join drink_compos c4 on (c4.dnc_dnk_id = dnk_id and c4.dnc_no = 4)\n" +
      "                         left outer join drink_compos c5 on (c5.dnc_dnk_id = dnk_id and c5.dnc_no = 5)\n" +
      "                         left outer join drink_compos c6 on (c6.dnc_dnk_id = dnk_id and c6.dnc_no = 6)\n" +
      "                         left outer join drink_compos c7 on (c7.dnc_dnk_id = dnk_id and c7.dnc_no = 7)\n" +
      "                         left outer join drink_compos c8 on (c8.dnc_dnk_id = dnk_id and c8.dnc_no = 8)\n" +
      "                         left outer join drink_compos c9 on (c9.dnc_dnk_id = dnk_id and c9.dnc_no = 9)))\n" +
      "%s" +
      " order by dnk_name",
      new Object[]{filter != null ? " where " + filter + "\n" : ""});
  }
  
  public static String getComponentList() {
    return
      "select dnc_component\n" +
      "  from drink_compos\n" +
      " where dnc_dnk_id = :DNK_ID\n" +
      " order by dnc_no";
  }
  
  public static String getLetterList() {
    return
      "select substr(dnk_name, 1, 1) letter, count( 0 ) cnt\n" +
      "  from drinks\n" +
      " where substr(dnk_name, 1, 1) in ('A','B','C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')\n" +
      " group by substr(dnk_name, 1, 1)\n" +
      "union all\n" +
      "select '.' letter, count( 0 ) cnt\n" +
      "  from drinks\n" +
      " where substr(dnk_name, 1, 1) not in ('A','B','C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')\n" +
      " group by '.'\n" +
      " order by 1";
  }

}
