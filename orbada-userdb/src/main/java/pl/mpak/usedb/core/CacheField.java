package pl.mpak.usedb.core;

import java.io.DataInput;
import java.io.IOException;

import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;

public final class CacheField {
  Variant value = new Variant();
  Variant oldValue = null;
  private boolean changed = false;

  public CacheField() {
    super();
  }

  public Variant getValue() {
    return value;
  }
  
  public void setValue(Variant value) {
    if (oldValue == null) {
      oldValue = (Variant)this.value.clone();
    }
    this.value = value;
    changed = true;
  }
  
  public Variant getOldValue() {
    return oldValue;
  }
  
  void setChanged(boolean state) {
    changed  = state;
  }
  
  public boolean isChanged() {
    return changed;
  }
  
  public void cancel() {
    if (changed && oldValue != null) {
      value = (Variant)oldValue.clone();
      oldValue = null;
    }
  }
  
  public void apply() {
    if (changed) {
      oldValue = null;
      changed = false;
    }
  }
  
  void read(DataInput raf) throws VariantException, IOException {
    value.read(raf);
    oldValue = null;
    changed = false;
  }
}
