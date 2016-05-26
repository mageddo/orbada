/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.cm;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CopyTableColumnsAction extends ComponentAction {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public CopyTableColumnsAction() {
    super();
    setText(stringManager.getString("CopyTableColumnsAction-text"));
    setTooltip(stringManager.getString("CopyTableColumnsAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/copy.gif"));
    setActionCommandKey("CopyTableColumnsAction");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getComponent() instanceof ViewTable) {
          ViewTable vt = (ViewTable)getComponent();
          try {
            StringBuffer sb = new StringBuffer();
            int l = 0;
            vt.getQuery().first();
            while (!vt.getQuery().eof()) {
              if (sb.length() > 0) {
                sb.append(", ");
                l+= 2;
              }
              if (l > 100) {
                l = 0;
                sb.append('\n');
              }
              String column = SQLUtil.createSqlName(vt.getQuery().fieldByName("column_name").getString());
              sb.append(column);
              l+= column.length();
              vt.getQuery().next();
            }
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(sb.toString()), null);
          } catch (Exception ex) {
            MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
          }
        }
      }
    };
  }

  @Override
  public boolean isToolButton() {
    return true;
  }

}
