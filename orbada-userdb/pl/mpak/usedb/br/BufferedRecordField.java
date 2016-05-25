package pl.mpak.usedb.br;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;

public class BufferedRecordField {

  private Variant value = new Variant();
  private Variant oldValue = null;
  private boolean changed = false;
  private String fieldName = null;
  private int length;
  private int valueType;
  private boolean updatable = true;
  private String propertyName;
  private Method setterMethod;
  private Method getterMethod;

  public BufferedRecordField(String fieldName) {
    this(fieldName, null, VariantType.varString);
  }

  public BufferedRecordField(String fieldName, int valueType) {
    this(fieldName, null, valueType);
  }

  public BufferedRecordField(String fieldName, int valueType, boolean updatable) {
    this(fieldName, null, valueType);
    this.updatable = updatable;
  }

  public BufferedRecordField(String fieldName, Variant value, int valueType, boolean updatable) {
    this(fieldName, value, valueType);
    this.updatable = updatable;
  }

  public BufferedRecordField(String fieldName, Variant value, int valueType) {
    super();
    this.fieldName = fieldName;
    this.value = (value == null ? this.value : value);
    this.valueType = (value == null ? valueType : value.getValueType());
    if (value != null) {
      this.oldValue = new Variant();
      setChanged(true);
    }
  }

  public Variant getValue() {
    return value;
  }
  
  public void setFirstValue(Variant value) {
    oldValue = null;
    this.value = (value == null ? new Variant() : value);
    setChanged(false);
  }
  
  /**
   * <p>Ustawia wartoœæ na podan¹ i zachowuje poprzedni¹.
   * <p>Mo¿na przekazaæ null
   * @param value
   */
  public void setValue(Variant value) {
    if (oldValue == null) {
      oldValue = (Variant)this.value.clone();
    }
    this.value = (value == null ? new Variant() : value);
    setChanged(!this.value.equals(oldValue));
  }
  
  public void clear() {
    oldValue = null;
    value = new Variant();
    setChanged(false);
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
    if (changed) {
      value = (Variant)oldValue.clone();
      oldValue = null;
    }
  }
  
  public void apply() {
    if (changed) {
      oldValue = null;
      setChanged(false);
    }
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setValueType(int valueType) {
    this.valueType = valueType;
  }

  public int getValueType() {
    return valueType;
  }

  public void setUpdatable(boolean updatable) {
    this.updatable = updatable;
  }

  public boolean isUpdatable() {
    return updatable;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getLength() {
    return length;
  }
  
  public boolean isNull() {
    return value.isNullValue();
  }
  
  public int getInteger() throws VariantException {
    return value.getInteger();
  }
  
  public void setInteger(int value) {
    setValue(new Variant(value));
  }
  
  public byte getByte() throws VariantException {
    return value.getByte();
  }
  
  public void setByte(byte value) {
    setValue(new Variant(value));
  }
  
  public float getFloat() throws VariantException {
    return value.getFloat();
  }
  
  public void setFloat(float value) {
    setValue(new Variant(value));
  }
  
  public double getDouble() throws VariantException {
    return value.getDouble();
  }
  
  public void setDouble(double value) {
    setValue(new Variant(value));
  }
  
  public boolean getBoolean() throws VariantException {
    return value.getBoolean();
  }
  
  public void setBoolean(boolean value) {
    setValue(new Variant(value));
  }
  
  public short getShort() throws VariantException {
    return value.getShort();
  }
  
  public void setShort(short value) {
    setValue(new Variant(value));
  }
  
  public long getLong() throws VariantException {
    return value.getLong();
  }
  
  public void setLong(long value) {
    setValue(new Variant(value));
  }
  
  public String getString() throws VariantException, IOException {
    return value.getString();
  }
  
  public void setString(String value) {
    setValue(new Variant(value));
  }
  
  public Date getDate() throws VariantException, ParseException {
    return value.getDate();
  }
  
  public void setDate(Date value) {
    setValue(new Variant(value));
  }
  
  public Time getTime() throws VariantException, ParseException {
    return value.getTime();
  }
  
  public void setTime(Time value) {
    setValue(new Variant(value));
  }
  
  public Timestamp getTimestamp() throws VariantException, ParseException {
    return value.getTimestamp();
  }
  
  public void setTimestamp(Timestamp value) {
    setValue(new Variant(value));
  }
  
  public BigDecimal getBigDecimal() throws VariantException {
    return value.getBigDecimal();
  }
  
  public void setBigDecimal(BigDecimal value) {
    setValue(new Variant(value));
  }
  
  public BigInteger getBigInteger() throws VariantException {
    return value.getBigInteger();
  }
  
  public void setBigInteger(BigInteger value) {
    setValue(new Variant(value));
  }
  
  public byte[] getBinary() throws VariantException, IOException {
    return value.getBinary();
  }
  
  public void setBinary(byte[] value) {
    setValue(new Variant(value));
  }
  
  public Object getObject() throws VariantException {
    return value.getObject();
  }
  
  public void setObject(Object value) {
    setValue(new Variant(value));
  }
  
  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public Method getSetterMethod() {
    return setterMethod;
  }

  public void setSetterMethod(Method setterMethod) {
    this.setterMethod = setterMethod;
  }

  public Method getGetterMethod() {
    return getterMethod;
  }

  public void setGetterMethod(Method getterMethod) {
    this.getterMethod = getterMethod;
  }
  
  public void updateBean(Object beanObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, VariantException, ParseException, IOException {
    if (setterMethod != null && beanObject != null) {
      //System.out.println("updateBean:" +propertyName +"," +value.toString());
      if (value.isNullValue()) {
        setterMethod.invoke(beanObject, (Object)null);
      }
      else {
        setterMethod.invoke(beanObject, value.cast(valueType).getValue());
      }
    }
  }
  
  public void updateField(Object beanObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (getterMethod != null && beanObject != null) {
      setValue(new Variant(getterMethod.invoke(beanObject)));
    }
  }

  public String toString() {
    return 
      "[" +
      "value:" +value +
      ",oldValue:" +oldValue +
      ",changed:" +changed +
      ",fieldName:" +fieldName +
      ",valueType:" +valueType +
      ",updatable:" +updatable +
      "]";
  }
  
}
