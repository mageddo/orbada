/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import java.util.Map;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.patt.Resolvers;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Template extends DefaultBufferedRecord {
  
  public Template(Database database) {
    super(database, "TEMPLATES", "TPL_ID");
    add("TPL_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("TPL_USR_ID", VariantType.varString, true);
    add("TPL_NAME", VariantType.varString, true);
    add("TPL_DESCRIPTION", VariantType.varString, true);
    add("TPL_BODY", VariantType.varString, true);
  }
  
  public Template(Database database, String tpl_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(tpl_id));
  }
  
  /**
   * <p>Pobiera odpowiedni szablon, jeœli jest to go zwróci w przeciwnym
   * przypadku lub w przypadku b³êdu zwróci null
   * @param name
   * @return
   * @throws pl.mpak.usedb.UseDBException
   */
  public Template loadByName(String name) {
    try {
      Query query = new Query(getDatabase());
      query.setSqlText("select templates.*, case when tpl_usr_id is null then 0 else 1 end ordering from templates where tpl_name = :tpl_name order by ordering");
      query.paramByName("tpl_name").setString(name);
      try {
        query.open();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
        return null;
      }
      try {
        if (!query.eof()) {
          updateFrom(query);
        } else {
          return null;
        }
      } finally {
        query.close();
      }
      return this;
    } catch (UseDBException ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }
  
  public String getTplId() {
    try {
      return fieldByName("tpl_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setTplId(String usrId) {
    fieldByName("tpl_id").setString(usrId);
  }
  
  public String getUsrId() {
    try {
      return fieldByName("tpl_usr_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setUsrId(String usrId) {
    fieldByName("tpl_usr_id").setString(usrId);
  }
  
  public String getName() {
    try {
      return fieldByName("tpl_name").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return "???";
  }
  
  public void setName(String name) {
    fieldByName("tpl_name").setString(name);
  }
  
  public String getDescription() {
    try {
      return fieldByName("tpl_description").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setDescription(String description) {
    fieldByName("tpl_description").setString(description);
  }
  
  public String getBody() {
    try {
      return fieldByName("tpl_body").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setBody(String body) {
    fieldByName("tpl_body").setString(body);
  }
  
  /**
   * <p>Przygotowuje Body do wstawienia, zamienia wszystkie sta³e zdefiniowane
   * $(...) na ich odpowiedniki.
   * 
   * @param values jest dodatkow¹ map¹ &lt;key, value&gt; sta³ych do zamiany
   *        key powinien byæ bez znaków $()
   * @return rozwiniêcie szablonu
   */
  public String expand(Map<String, String> values) {
    return Resolvers.expand(getBody(), values);
  }
  
}
