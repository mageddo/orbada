/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.patt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.LoginInfo;
import pl.mpak.orbada.gui.patt.PasswordDialog;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.HexUtils;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.patt.ResolvableModel;

/**
 *
 * @author akaluza
 */
public class SHA1_Password_AESResolver implements ResolvableModel {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  @Override
  public String getModel() {
    return "sha1.password.aes";
  }

  @Override
  public String getResolve() {
    try {
      LoginInfo li = PasswordDialog.show(new LoginInfo(), stringManager.getString("sha1-password-aes-resolver-prompt"));
      if (!StringUtil.isEmpty(li.getPassword())) {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1 = md.digest(("orbada-sha-1:" +li.getPassword()).getBytes());
        return HexUtils.convert(sha1).substring(2, 34);
      }
    } catch (NoSuchAlgorithmException ex) {
      ExceptionUtil.processException(ex);
    }
    return "";
  }

}
