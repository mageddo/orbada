/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.snippets.db;

import pl.mpak.sky.gui.swing.syntax.Snippet;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class SnippetRecord extends DefaultBufferedRecord {

  public SnippetRecord(Database database) {
    super(database, "SNIPPETS", "SNP_ID");
    add("SNP_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("SNP_USR_ID", VariantType.varString, true);
    add("SNP_DTP_ID", VariantType.varString, true);
    add("SNP_NAME", VariantType.varString, true);
    add("SNP_CODE", VariantType.varString, true);
    add("SNP_EDITOR", new Variant("sql"), VariantType.varString, true);
    add("SNP_ACTIVE", new Variant("T"), VariantType.varString, true);
    add("SNP_IMMEDIATE", VariantType.varString, true);
  }

  public SnippetRecord(Database database, String snp_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(snp_id));
  }
  
  public SnippetRecord(Database database, String name, String code, String editor, boolean immediate) {
    this(database);
    fieldByName("SNP_NAME").setString(name);
    fieldByName("SNP_CODE").setString(code);
    fieldByName("SNP_EDITOR").setString(editor);
    fieldByName("SNP_IMMEDIATE").setString(immediate ? "T" : "F");
  }
  
  public SnippetRecord(Database database, String id, String name, String code, String editor, boolean immediate) {
    this(database, name, code, editor, immediate);
    fieldByName("SNP_ID").setString(id);
  }
  
  public Snippet getSnippet() {
    return new Snippet(getName(), getCode(), getImmediate());
  }
  
  public String getId() {
    return fieldByName("SNP_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("SNP_ID").setString(id);
  }
  
  public String getUsrId() {
    if (fieldByName("SNP_USR_ID").isNull()) {
      return null;
    }
    return fieldByName("SNP_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String usrId) {
    fieldByName("SNP_USR_ID").setString(usrId);
  }
  
  public String getDtpId() {
    if (fieldByName("SNP_DTP_ID").isNull()) {
      return null;
    }
    return fieldByName("SNP_DTP_ID").getValue().toString();
  }
  
  public void setDtpId(String drvId) {
    fieldByName("SNP_DTP_ID").setString(drvId);
  }
  
  public String getName() {
    return fieldByName("SNP_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("SNP_NAME").setString(name);
  }
  
  public String getCode() {
    return fieldByName("SNP_CODE").getValue().toString();
  }
  
  public void setCode(String code) {
    fieldByName("SNP_CODE").setString(code);
  }
  
  public String getEditor() {
    return fieldByName("SNP_EDITOR").getValue().toString();
  }
  
  public void setEditor(String editor) {
    fieldByName("SNP_EDITOR").setString(editor);
  }
  
  public boolean getActive() {
    return StringUtil.toBoolean(fieldByName("SNP_ACTIVE").getValue().toString());
  }
  
  public void setActive(boolean active) {
    fieldByName("SNP_ACTIVE").setString(active ? "T" : "F");
  }
  
  public boolean getImmediate() {
    return StringUtil.toBoolean(fieldByName("SNP_IMMEDIATE").getValue().toString());
  }
  
  public void setImmediate(boolean immediate) {
    fieldByName("SNP_IMMEDIATE").setString(immediate ? "T" : "F");
  }
  
}
