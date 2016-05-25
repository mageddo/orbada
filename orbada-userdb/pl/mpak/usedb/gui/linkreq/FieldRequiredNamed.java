package pl.mpak.usedb.gui.linkreq;

public abstract class FieldRequiredNamed implements IFieldRequires {

  protected String publicFieldName;

  public FieldRequiredNamed(String publicFieldName) {
    this.publicFieldName = publicFieldName;
  }

}
