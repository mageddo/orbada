package pl.mpak.orbada.dmlformat.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

import pl.mpak.orbada.dmlformat.Messages;
import pl.mpak.orbada.dmlformat.OrbadaDmlFormatPlugin;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class UniversalDmlFormatProvider extends UniversalActionProvider {
  private static final long serialVersionUID = 1L;

  public UniversalDmlFormatProvider() {
    super();
    setShortCut(KeyEvent.VK_B, KeyEvent.CTRL_MASK);
    setText(Messages.getString("UniversalDmlFormatProvider.text")); //$NON-NLS-1$
    setSmallIcon(new ImageIcon(getClass().getResource("/pl/mpak/orbada/dmlformat/dmlformat.gif"))); //$NON-NLS-1$
    setActionCommandKey("UniversalDmlFormatProvider"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return true;
    //return "Oracle".equals(database.getDriverType());
  }

  public boolean addToolButton() {
    return true;
  }

  public boolean addMenuItem() {
    return true;
  }

  public boolean addToEditor() {
    return true;
  }

  public String getDescription() {
    return Messages.getString("UniversalDmlFormatProvider.description"); //$NON-NLS-1$
  }
  
  private String breakSQLText(String aText) {
    final String keywordso[] = {
        "select ", "(\nselect ", " from ", " where ", " group ", " and ",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        " union ", " order ", " having ", " connect ", " start ",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        ", decode(", ", to_char(", ", translate(", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ", to_number(", ", substr(", ", to_date("}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String keywordsn[] = {
        "\nselect ", "\n(select ", "\n  from ", "\n where ", "\n group ", "\n   and ",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        "\nunion ", "\n order ", "\nhaving ", "\nconnect ", "\n start ", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        ",\n       decode(", ",\n       to_char(", ",\n       translate(", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ",\n       to_number(", ",\n       substr(", ",\n       to_date("}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    aText = SQLUtil.removeComments(aText);
//    aText = SQLUtil.removeLineComment(aText);
    aText = SQLUtil.removeWhiteSpaces(aText);
    aText = SQLUtil.sqlChangeCharCase(aText, StringUtil.CharCase.ecUpperCase);
    for (int i = 0; i<keywordso.length; i++) {
      aText = StringUtil.replaceString(aText, keywordso[i].toUpperCase(), keywordsn[i].toUpperCase());
    }
    if (aText.length() > 0 && aText.charAt(0) == '\n') {
      aText = aText.substring(1);
    }
    return aText;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String sqlCode = breakSQLText(accessibilities.getSyntaxEditor().getCurrentText());
        accessibilities.getSyntaxEditor().replaceCurrentText(sqlCode);
      }
    };
  }

  public String getGroupName() {
    return OrbadaDmlFormatPlugin.pluginGroupName;
  }

}
