/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.util;

import javax.swing.DefaultComboBoxModel;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.Schema;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class SchemasComboBoxModel extends DefaultComboBoxModel {

  private String driverTypeName;

  public SchemasComboBoxModel() {
    super();
    init();
    setDriverTypeName(null);
  }

  public SchemasComboBoxModel(String driverTypeName) {
    super();
    init();
    setDriverTypeName(driverTypeName);
  }

  private void init() {

  }

  public String getDriverTypeName() {
    return driverTypeName;
  }

  public void setDriverTypeName(String driverTypeName) {
    this.driverTypeName = driverTypeName;
    createModel();
  }

  private void createModel() {
    if (Application.get() != null) {
      Query query = Application.get().getOrbadaDatabase().createQuery();
      try {
        query.setSqlText(
          "select schemas.*, drv_url_template\n" +
          "  from schemas, drivers\n" +
          " where sch_drv_id = drv_id\n" +
          "   and (upper(drv_type_name) = :DRIVER_TYPE_NAME or :DRIVER_TYPE_NAME is null)\n" +
          "   and (sch_usr_id is null or sch_usr_id = :USR_ID)\n" +
          " order by sch_name");
        if (driverTypeName == null) {
          query.paramByName("DRIVER_TYPE_NAME").setString(null);
        }
        else {
          query.paramByName("DRIVER_TYPE_NAME").setString(driverTypeName.toUpperCase());
        }
        query.paramByName("USR_ID").setString(Application.get().getUserId());
        query.open();
        while (!query.eof()) {
          Schema schema = new Schema(Application.get().getOrbadaDatabase());
          schema.add("DRV_URL_TEMPLATE", VariantType.varString);
          schema.updateFrom(query);
          addElement(schema);
          query.next();
        }
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
    }
  }

}
