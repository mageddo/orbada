/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui.comps.util;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import pl.mpak.orbada.db.Schema;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SchemasListCellRenderer extends DefaultListCellRenderer {

  public SchemasListCellRenderer() {
    super();
    setVerticalAlignment(JLabel.TOP);
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    if (value instanceof Schema) {
      Schema schema = (Schema)value;
      String text = "<html>";
      String name = schema.getPublicNameResolved();
      if (name.toUpperCase().startsWith("<HTML>")) {
        name = name.substring(6);
      }
      else {
        name = "<b>" +name +"</b>";
      }
      text = text +name;
      String url = schema.getUrl();
      if (StringUtil.isEmpty(url)) {
        url = schema.replacePatts(schema.fieldByName("drv_url_template").getValue().toString());
      }
      text = text +" (" +(url.length() > 50 ? url.substring(0, 49) +"..." : url) +")";
      value = text;
    }
    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
  }

}
