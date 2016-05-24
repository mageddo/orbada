/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Tool extends DefaultBufferedRecord {
  
  protected static Interpreter interpreter = new Interpreter();
  
  private Icon icon;

  public Tool(Database database) {
    super(database, "TOOLS", "TO_ID");
    add("TO_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("TO_USR_ID", VariantType.varString, true);
    add("TO_COMMAND", VariantType.varString, true);
    add("TO_SOURCE", VariantType.varString, true);
    add("TO_ARGUMENTS", VariantType.varString, true);
    add("TO_TITLE", VariantType.varString, true);
    add("TO_MENU", VariantType.varString, true);
    add("TO_TOOLBUTTON", VariantType.varString, true);
    add("TO_BSH_GET_ARGUMENTS", VariantType.varString, true);
    add("TO_BSH_BEFORE_EXEC", VariantType.varString, true);
    add("TO_BSH_AFTER_EXEC", VariantType.varString, true);
    add("TO_ICON", VariantType.varBinary, true);
  }
  
  public Tool(Database database, String to_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(to_id));
  }
  
  public String getUsrId() {
    return fieldByName("TO_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("TO_USR_ID").setString(id);
  }
  
  public String getCommand() {
    return fieldByName("to_command").getValue().toString();
  }
  
  public String getSource() {
    return fieldByName("to_source").getValue().toString();
  }
  
  public String getTitle() {
    return fieldByName("to_title").getValue().toString();
  }
  
  public String getArguments() {
    return fieldByName("to_arguments").getValue().toString();
  }
  
  public String getBshGetArguments() {
    return fieldByName("to_bsh_get_arguments").getValue().toString();
  }
  
  public String getBshBeforeExec() {
    return fieldByName("to_bsh_before_exec").getValue().toString();
  }
  
  public String getBshAfterExec() {
    return fieldByName("to_bsh_after_exec").getValue().toString();
  }
  
  public boolean isMenu() {
    return "Y".equals(fieldByName("to_menu").getValue().toString());
  }
  
  public boolean isToolButton() {
    return "Y".equals(fieldByName("to_toolbutton").getValue().toString());
  }
  
  public Icon getIcon() {
    if (icon != null) {
      return icon;
    }
    if (!fieldByName("to_icon").getValue().isNullValue()) {
      try {
        icon = new ImageIcon(fieldByName("to_icon").getValue().getBinary());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      return icon;
    }
    return null;
  }
  
  private String bshArguments() {
    String cmd = getBshGetArguments();
    if (!StringUtil.isEmpty(cmd)) {
      try {
        Object o = interpreter.eval(cmd);
        if (o != null) {
          return o.toString();
        }
        return null;
      } catch (EvalError ex) {
        ExceptionUtil.processException(ex);
      }
    }
    return "";
  }
  
  private void bshBeforeExec() {
    String cmd = getBshBeforeExec();
    if (!StringUtil.isEmpty(cmd)) {
      try {
        Object o = interpreter.eval(cmd);
        interpreter.set("result", o);
      } catch (EvalError ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  private void bshAfterExec() {
    String cmd = getBshAfterExec();
    if (!StringUtil.isEmpty(cmd)) {
      try {
        interpreter.eval(cmd);
      } catch (EvalError ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  public void exec(Database activeDatabase, Object[] args) {
    String arguments = "";
    if (args != null) {
      arguments = String.format(getArguments(), args);
    }
    try {
      if (activeDatabase != null) {
        interpreter.set("database", activeDatabase);
      } else {
        interpreter.set("database", null);
      }
    } catch (EvalError ex) {
      ExceptionUtil.processException(ex);
    }
    String bshArguments = bshArguments();
    if (bshArguments == null) {
      return;
    }
    try {
      bshBeforeExec();
      String source = getSource();
      if (!StringUtil.isEmpty(source)) {
        Runtime.getRuntime().exec(source + " " + arguments +" " +bshArguments);
      }
    } 
    catch (IOException ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(null, "B³¹d", ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    finally {
      bshAfterExec();
    } 
  }
  
}
