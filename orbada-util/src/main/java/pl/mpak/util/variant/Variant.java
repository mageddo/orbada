package pl.mpak.util.variant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import pl.mpak.util.Assert;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.stream.ContextObjectInputStream;

/**
 * @author Andrzej Ka³u¿a
 * 
 * <p>Uniwersalny typ ogólnego i ró¿norakiego zastosowania.
 * <p>Pozwala przechowywaæ, zapisywaæ, odczytywaæ wszelkiego typu
 * dane, a w tym obiekty java o specjalnym interfejsie
 * VariantConnectable oraz serializowane.
 * <p>Przy odczycie jest specjalnie traktowany typ varJavaObject,
 * jeœli "value" nie jest interfejsu VariantConnectable to 
 * wartoœæ bêdzie odczytana jako varBinary i taki typ zostanie 
 * ustawiony.<br>
 * Zobacz opis do interfejsu VariantConnectable aby temu zaradziæ.<br>
 * O ile przy zapisie dla tego typu obiektów wymagane jest aby
 * obiekt istnia³ to przy odczycie dopuszczalny jest brak zarejestrowanej
 * klasy, w tym przypadku odczytany bêdzie varBinary. Trzeba w tym
 * momencie uwa¿aæ na zapis gdy¿ zapis bêdzie ju¿ niezgodny z 
 * varJavaObject tylko varBinary 
 * <p>Przy zapisie jeœli typem jest varJavaObject, a "value" nie
 * jest zgodny z interfejsem VariantConnectable to Variant zwróci
 * b³ad.
 *<p>Variant potrafi zapisywaæ listy danych ale tylko takie, które
 *zawieraj¹ Variant-y.
 *<p>Variant obs³uguje typ oracle.sql.TIMESTAMP
 */
public final class Variant implements Cloneable, Comparable<Object> {
  
  private static HashMap<Long, Class<? extends VariantConnectable>> registeredClasses = new HashMap<Long, Class<? extends VariantConnectable>>();
  private static HashMap<Class<? extends VariantConnectable>, Long> registeredIds = new HashMap<Class<? extends VariantConnectable>, Long>();
  
  private static SimpleDateFormat dateFormat = null;
  private static SimpleDateFormat timeFormat = null;
  private static SimpleDateFormat timeStampFormat = null;
  private static DecimalFormat decimalFormat = null;
  private static DecimalFormat bigDecimalFormat = null;
  private static DecimalFormatSymbols unusualSymbols = null;
  private DecimalFormat localDecimalFormat = null;

  private static NumberFormat defaultNumberFormat;
  private static DateFormat defaultDateTimeFormat;
  private static DateFormat defaultTimeFormat;
  
  private int valueType = VariantType.varUnassigned;
  private Object value = null;
  private int size = -1;
  private byte[] binaryData = null;
  
  public final static Variant Unassigned = new Variant();
  public final static Variant Null = new Variant((Object)null);
  public final static Variant True = new Variant(true);
  public final static Variant False = new Variant(true);
  
  /**
   * <p>ignoreCase nie zapisywane jest do pliku ani nie jest odczytywane.
   * <p>Trzeba odtwarzaæ je za ka¿dym razem
   */
  private boolean ignoreCase = false;
  
  static {
    defaultNumberFormat = java.text.DecimalFormat.getNumberInstance();
    defaultNumberFormat.setMaximumFractionDigits(20);
    defaultDateTimeFormat = java.text.DateFormat.getDateTimeInstance();
    defaultTimeFormat = java.text.DateFormat.getTimeInstance();
  }
  
  public Variant() {
    //super();
  }
  
  public Variant(DecimalFormat decimalFormat) {
    localDecimalFormat = decimalFormat;
  }
  
  /**
   * <p>Tworzy Variant i od razu odczytuje wartoœæ ze strumienia
   * @param di
   * @throws VariantException
   * @throws IOException
   */
  public Variant(DataInput di) throws VariantException, IOException {
    read(di);
  }
  
  public Variant(Object value) {
    this();
    setValue(value);
  }
  
  public Variant(byte value) {
    this();
    setByte(value);
  }
  
  public Variant(short value) {
    this();
    setShort(value);
  }
  
  public Variant(int value) {
    this();
    setInteger(value);
  }
  
  public Variant(long value) {
    this();
    setLong(value);
  }
  
  public Variant(boolean value) {
    this();
    setBoolean(value);
  }
  
  public Variant(float value) {
    this();
    setFloat(value);
  }
  
  public Variant(double value) {
    this();
    setDouble(value);
  }
  
  public Variant(Date value) {
    this();
    setDate(value);
  }
  
  public Variant(Time value) {
    this();
    setTime(value);
  }
  
  public Variant(Timestamp value) {
    this();
    setTimestamp(value);
  }
  
  public Variant(byte[] value) {
    this();
    setBinary(value);
  }
  
  public Variant(BigInteger value) {
    this();
    setBigInteger(value);
  }
  
  public Variant(BigDecimal value) {
    this();
    setBigDecimal(value);
  }
  
  public Variant(String value) {
    this();
    setString(value);
  }
  
  public Variant(Object value, int valueType) throws VariantException, ParseException, IOException {
    this(value);
    cast(valueType);
  }
  
  public Variant(InputStream value, int size) {
    this();
    setInputStream(value, size);
  }
  
  public Variant(OutputStream value, int size) {
    this(); 
    setOutputStream(value, size);
  }
  
  public static void registerVariantClass(long id, Class<? extends VariantConnectable> clazz) {
    registeredClasses.put(id, clazz);
    registeredIds.put(clazz, id);
  }
  
  public static Variant[] createArrayOf(Object[] values) {
    Variant[] result = new Variant[values.length];
    for (int i=0; i<values.length; i++) {
      result[i] = new Variant(values[i]);
    }
    return result;
  }
  
  public static int getTypeSize() {
    return 1;
  }
  
  private void writeString(DataOutput dop, String str) throws IOException {
    writeByteArray(dop, str.getBytes("UTF-16"));
  }
  
  private String readString(DataInput dip) throws IOException {
    return new String(readByteArray(dip), "UTF-16");
  }

  private void writeByteArray(DataOutput dop, byte[] bytes) throws IOException {
    dop.writeInt(bytes.length);
    dop.write(bytes, 0, bytes.length);
  }
  
  private byte[] readByteArray(DataInput dip) throws IOException {
    byte[] buffer = new byte[dip.readInt()];
    try {
      dip.readFully(buffer);
    }
    catch(Exception e) {
      ;
    }
    return buffer;
  }
  
    public void write(DataOutput dop) throws VariantException, IOException {
  //    System.out.print("writeb:" +(new Long(raf.getFilePointer())).toString() +"\n");
      if (isNullValue()) {
        dop.writeByte(VariantType.varNull);
        return;
      }
      dop.writeByte(valueType);
      switch(valueType) {
        case VariantType.varByte:
          dop.writeByte(((Byte)value).byteValue());
          break;
        case VariantType.varShort:
          dop.writeShort(((Short)value).shortValue());
          break;
        case VariantType.varInteger:
          dop.writeInt(((Integer)value).intValue());
          break;
        case VariantType.varLong:
          dop.writeLong(((Long)value).longValue());
          break;
        case VariantType.varDate:
          dop.writeLong(((Date)value).getTime());
          break;
        case VariantType.varTime:
          dop.writeLong(((Time)value).getTime());
          break;
        case VariantType.varTimestamp:
          dop.writeLong(((Timestamp)value).getTime());
          break;
        case VariantType.varBoolean:
          dop.writeBoolean(((Boolean)value).booleanValue());
          break;
        case VariantType.varFloat:
          dop.writeFloat(((Float)value).floatValue());
          break;
        case VariantType.varDouble:
          dop.writeDouble(((Double)value).doubleValue());
          break;
        case VariantType.varBigInteger:
          writeByteArray(dop, ((BigInteger)value).toByteArray());
          break;
        case VariantType.varBigDecimal:
          dop.writeInt(((BigDecimal)value).scale());
          writeByteArray(dop, ((BigDecimal)value).unscaledValue().toByteArray());
          break;
        case VariantType.varVariant:
          ((Variant)value).write(dop);
          break;
        case VariantType.varString:
          writeString(dop,(String)value);
          break;
        case VariantType.varBinary:
          dop.writeInt(size);
          dop.write(binaryData,0,size);
          break;
        case VariantType.varJavaObject:
        {
          if (value instanceof VariantConnectable) {
            dop.writeInt(VariantType.varSubConnectable);
            Long id = registeredIds.get(((VariantConnectable)value).getClass());
            // TODO Tutaj zmiast Assert mo¿na w przysz³oœci u¿yæ bezpiecznego zapisu zerowej iloœci
            Assert.notNull(id, "id is null");
            dop.writeLong(id);
            dop.writeInt(size);
            ((VariantConnectable)value).write(dop);
          }
          else if (value instanceof Serializable) {
            dop.writeInt(VariantType.varSubSerializable);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            size = baos.size();
            dop.writeInt(size);
            dop.write(baos.toByteArray(), 0, size);
            oos.close();
          }
          else {
            writeThrow(value.getClass().getName());
          }
          break;
        }
        case VariantType.varList: {
          AbstractCollection<?> ac = (AbstractCollection<?>)value;
          dop.writeLong(ac.size());
          writeString(dop, ac.getClass().getName());
          Iterator<?> i = ac.iterator();
          while (i.hasNext()) {
            Object o = i.next();
            if (!(o instanceof Variant)) {
              if (o instanceof Serializable) {
                dop.writeInt(VariantType.varSubSerializable);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(o);
                size = baos.size();
                dop.writeInt(size);
                dop.write(baos.toByteArray(), 0, size);
                oos.close();
              }
              else if (o == null) {
                dop.writeInt(VariantType.varSubVariant);
                Variant.Null.write(dop);
              }
              else {
                writeThrow(o.getClass().getName());
              }
            }
            else {
              dop.writeInt(VariantType.varSubVariant);
              ((Variant)o).write(dop);
            }
          }
          break;
        }
        default:
          writeThrow(value.getClass().getName());
      }
    }
    
    private void readBinary(DataInput dip, int size) throws IOException {
      byte[] buffer = new byte[size];
      try {
        dip.readFully(buffer,0,buffer.length);
      }
      catch (Exception e) {
        ;
      }
      setBinary(buffer);
    }
  
    @SuppressWarnings("unchecked")
    public Variant read(DataInput dip) throws VariantException, IOException {
  //    System.out.print("readb:" +(new Long(raf.getFilePointer())).toString() +"\n");
      int vt = dip.readByte();
      switch(vt) {
        case VariantType.varNull:
          setNull();
          break;
        case VariantType.varByte:
          setByte(dip.readByte());
          break;
        case VariantType.varShort:
          setShort(dip.readShort());
          break;
        case VariantType.varInteger:
          setInteger(dip.readInt());
          break;
        case VariantType.varLong:
          setLong(dip.readLong());
          break;
        case VariantType.varDate:
          setDate(new Date(dip.readLong()));
          break;
        case VariantType.varTime:
          setDate(new Time(dip.readLong()));
          break;
        case VariantType.varTimestamp:
          setDate(new Timestamp(dip.readLong()));
          break;
        case VariantType.varBoolean:
          setBoolean(dip.readBoolean());
          break;
        case VariantType.varFloat:
          setFloat(dip.readFloat());
          break;
        case VariantType.varDouble:
          setDouble(dip.readDouble());
          break;
        case VariantType.varBigInteger:
          setBigInteger(new BigInteger(readByteArray(dip)));
          break;
        case VariantType.varBigDecimal:
        {
          int scale = dip.readInt();
          setBigDecimal(new BigDecimal(new BigInteger(readByteArray(dip)), scale));
          break;
        }
        case VariantType.varVariant:
          setVariant(new Variant());
          ((Variant)value).read(dip);
          break;
        case VariantType.varString:
          setString(readString(dip));
          break;
        case VariantType.varBinary:
          readBinary(dip, dip.readInt());
          break;
        case VariantType.varJavaObject:
        {
          int subType = dip.readInt();
          if (subType == VariantType.varSubConnectable) {
            long id = dip.readLong();
            size = dip.readInt();
            if (value instanceof VariantConnectable) {
              ((VariantConnectable)value).read(dip);
            }
            else {
              Class<? extends VariantConnectable> clazz = registeredClasses.get(id);
              if (clazz == null) {
                readBinary(dip, size);
              }
              else {
                try {
                  value = clazz.newInstance();
                  ((VariantConnectable)value).read(dip);
                }
                catch (InstantiationException e) {
                  readBinary(dip, size);
                }
                catch (IllegalAccessException e) {
                  readBinary(dip, size);
                }
              }
            }
          }
          else if (subType == VariantType.varSubSerializable) {
            size = dip.readInt();
            byte[] buffer = new byte[size];
            dip.readFully(buffer, 0, buffer.length);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ContextObjectInputStream(bais);
            try {
              setObject(ois.readObject());
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            }
            ois.close();
          }
          break;
        }
        case VariantType.varList: {
          long count = dip.readLong();
          String className = readString(dip);
          AbstractCollection<Object> ac;
          try {
            ac = (AbstractCollection<Object>)(Class.forName(className).newInstance());
          }
          catch (InstantiationException e) {
            throw new VariantException(VariantException.ERR_02005_READ_ARRAY_PROBLEM, new Object[] {e.getMessage()});
          }
          catch (IllegalAccessException e) {
            throw new VariantException(VariantException.ERR_02005_READ_ARRAY_PROBLEM, new Object[] {e.getMessage()});
          }
          catch (ClassNotFoundException e) {
            throw new VariantException(VariantException.ERR_02005_READ_ARRAY_PROBLEM, new Object[] {e.getMessage()});
          }
          for (int i=0; i<count; i++) {
            int subType = dip.readInt();
            if (subType == VariantType.varSubSerializable) {
              size = dip.readInt();
              byte[] buffer = new byte[size];
              dip.readFully(buffer,0,buffer.length);
              ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
              ObjectInputStream ois = new ContextObjectInputStream(bais);
              try {
                ac.add(ois.readObject());
              } catch (ClassNotFoundException e) {
                ;
              }
              ois.close();
            }
            else if (subType == VariantType.varSubVariant) {
              Variant value = new Variant();
              value.read(dip);
              ac.add(value);
            }
          }
          setList(ac);
          break;
        }
        default:
          readThrow(vt);
      }
      return this;
    }
  
  private void writeThrow(String type) throws VariantException {
    throw new VariantException(VariantException.ERR_02001_CANT_WRITE, new Object[] {type});
  }
  
  private void readThrow(int type) throws VariantException {
    throw new VariantException(VariantException.ERR_02002_CANT_READ, new Object[] {(Integer.valueOf(type)).toString()});
  }
  
  private void noCastThrow(String from, String to) throws VariantException {
    throw new VariantException(VariantException.ERR_02003_CANT_CONVERT, new Object[] {from, to});
  }

  private void nullThrow(String to) throws VariantException {
    noCastThrow("null", to);
  }

  private int resolveSize() {
    switch(this.valueType) {
      case VariantType.varNull:
      case VariantType.varUnassigned:
        return 0;
      case VariantType.varByte:
        return Byte.SIZE /8;
      case VariantType.varShort:
        return Short.SIZE /8;
      case VariantType.varInteger:
        return Integer.SIZE /8;
      case VariantType.varLong:
        return Long.SIZE /8;
      case VariantType.varDate:
        return Long.SIZE /8;
      case VariantType.varTime:
        return Long.SIZE /8;
      case VariantType.varTimestamp:
        return Long.SIZE /8;
      case VariantType.varBoolean:
        return Byte.SIZE /8;
      case VariantType.varFloat:
        return Float.SIZE /8;
      case VariantType.varDouble:
        return Double.SIZE /8;
      case VariantType.varBigDecimal:
      case VariantType.varBigInteger:
        return value == null ? 0 : value.toString().length();
      case VariantType.varString:
        return value.toString().length();
      case VariantType.varVariant:
        return ((Variant)value).getSize();
      case VariantType.varBinary:
        return binaryData == null ? 0 : binaryData.length;
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return ((VariantConnectable)value).getSize();
        }
        else {
          return size;
        }
      default:
        return size;
    }
  }
  
  public int resolveObject(Object value) {
    if (value == null) {
      return VariantType.varNull;
    }
//    else if (value instanceof byte[]) {
//      return VariantType.varBinary;
//    }
    else if (value instanceof Short || Short.TYPE == value) {
      return VariantType.varShort;
    }
    else if (value instanceof Integer || Integer.TYPE == value) {
      return VariantType.varInteger;
    }
    else if (value instanceof Float || Float.TYPE == value) {
      return VariantType.varFloat;
    }
    else if (value instanceof Double || Double.TYPE == value) {
      return VariantType.varDouble;
    }
    else if (value instanceof Long || Long.TYPE == value) {
      return VariantType.varLong;
    }
    // Time i Timestamp musi byæ przed Date poniewa¿ oba typy go dziedzicz¹
    else if (value instanceof Time || Time.class == value) {
      return VariantType.varTime;
    }
    else if (value instanceof Timestamp || Timestamp.class == value) {
      return VariantType.varTimestamp;
    }
    else if (value instanceof Date || Date.class == value) {
      return VariantType.varDate;
    }
    else if (value instanceof Boolean || Boolean.TYPE == value) {
      return VariantType.varBoolean;
    }
    else if (value instanceof Byte || Byte.TYPE == value) {
      return VariantType.varByte;
    }
    else if (value instanceof String || String.class == value) {
      return VariantType.varString;
    }
    else if (value instanceof BigDecimal || BigDecimal.class == value) {
      return VariantType.varBigDecimal;
    }
    else if (value instanceof BigInteger || BigInteger.class == value) {
      return VariantType.varBigInteger;
    }
    else if (value instanceof Variant || Variant.class == value) {
      return VariantType.varVariant;
    }
    else if (value instanceof AbstractCollection) {
      return VariantType.varList;
    }
    else if (value instanceof InputStream) {
      return VariantType.varInputStream;
    }
    else if (value instanceof OutputStream) {
      return VariantType.varOutputStream;
    }
    else {
      return VariantType.varJavaObject;
    }
  }
  
  public void setValue(Object value) {
    this.valueType = resolveObject(value);
    this.value = value;
    this.binaryData = null;
    this.size = resolveSize();
  }
  
  public Object getValue() {
    return this.value;
  }
  
  public void clear() {
    this.valueType = VariantType.varUnassigned;
    this.value = null;
    this.size = -1;
    this.binaryData = null;
  }
  
  public void setNull() {
    this.valueType = VariantType.varNull;
    this.value = null;
    this.size = -1;
    this.binaryData = null;
  }

  /**
   * Zwraca informacjê czy wartoœæ jest przypisana value == null
   * 
   * @return
   */
  public boolean isNullValue() {
    return this.value == null;
  }
  
  /**
   * Zwraca informacjê czy wartoœæ jest typu null
   * 
   * @return
   */
  public boolean isNull() {
    return this.valueType == VariantType.varNull;
  }
  
  /**
   * Zwraca informacjê czy wartoœæ jest typu nieprzypisanego varUnassigned
   * 
   * @return
   */
  public boolean isEmpty() {
    return this.valueType == VariantType.varUnassigned;
  }
  
  private void checkNull(String t) throws VariantException {
    if (isNullValue() || valueType == VariantType.varNull || valueType == VariantType.varUnassigned) {
      nullThrow(t);
    }
  }
  
  /**
   * @return data size in bytes (chars for expl String)
   */
  public int getSize() {
    return this.size;
  }
  
  public int getValueType() {
    return this.valueType;
  }
  
  public void setByte(byte value) {
    this.valueType = VariantType.varByte;
    this.value = Byte.valueOf(value);
    this.size = resolveSize();
    this.binaryData = null;
  }  
  
  public byte getByte() throws VariantException {
    checkNull("byte");
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).byteValue();
      case VariantType.varShort:
        return ((Short)value).byteValue();
      case VariantType.varInteger:
        return ((Integer)value).byteValue();
      case VariantType.varLong:
        return ((Long)value).byteValue();
      case VariantType.varDate:
        return Long.valueOf(((Date)value).getTime()).byteValue();
      case VariantType.varTime:
        return Long.valueOf(((Time)value).getTime()).byteValue();
      case VariantType.varTimestamp:
        return Long.valueOf(((Timestamp)value).getTime()).byteValue();
      case VariantType.varBoolean:
        return (byte)(((Boolean)value).booleanValue() ? 1 : 0);
      case VariantType.varFloat:
        return ((Float)value).byteValue();
      case VariantType.varDouble:
        return ((Double)value).byteValue();
      case VariantType.varBigInteger:
        return ((BigInteger)value).byteValue();
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).byteValue();
      case VariantType.varVariant:
        return ((Variant)value).getByte();
      case VariantType.varString:
        return Byte.parseByte((String)value);
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Byte)((VariantConnectable)value).castTo(VariantType.varByte);
        }
        else {
          noCastThrow(value.getClass().getName(), "byte");
        }
      default:
        noCastThrow(value.getClass().getName(), "byte");
    }
    return 0;
  }
  
  public void setShort(short value) {
    this.valueType = VariantType.varShort;
    this.value = Short.valueOf(value);
    this.size = resolveSize();
    this.binaryData = null;
  }  
  
  public short getShort() throws VariantException {
    checkNull("short");
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).shortValue();
      case VariantType.varShort:
        return ((Short)value).shortValue();
      case VariantType.varInteger:
        return ((Integer)value).shortValue();
      case VariantType.varLong:
        return ((Long)value).shortValue();
      case VariantType.varDate:
        return Long.valueOf(((Date)value).getTime()).shortValue();
      case VariantType.varTime:
        return Long.valueOf(((Time)value).getTime()).shortValue();
      case VariantType.varTimestamp:
        return Long.valueOf(((Timestamp)value).getTime()).shortValue();
      case VariantType.varBoolean:
        return (short)(((Boolean)value).booleanValue() ? 1 : 0);
      case VariantType.varFloat:
        return ((Float)value).shortValue();
      case VariantType.varDouble:
        return ((Double)value).shortValue();
      case VariantType.varBigInteger:
        return ((BigInteger)value).shortValue();
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).shortValue();
      case VariantType.varVariant:
        return ((Variant)value).getShort();
      case VariantType.varString:
        return Short.parseShort((String)value);
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Short)((VariantConnectable)value).castTo(VariantType.varShort);
        }
        else {
          noCastThrow(value.getClass().getName(), "short");
        }
      default:
        noCastThrow(value.getClass().getName(), "short");
    }
    return 0;
  }
  
  public void setInteger(int value) {
    this.valueType = VariantType.varInteger;
    this.value = Integer.valueOf(value);
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public int getInteger() throws VariantException {
    checkNull("int");
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).intValue();
      case VariantType.varShort:
        return ((Short)value).intValue();
      case VariantType.varInteger:
        return ((Integer)value).intValue();
      case VariantType.varLong:
        return ((Long)value).intValue();
      case VariantType.varDate:
        return Long.valueOf(((Date)value).getTime()).intValue();
      case VariantType.varTime:
        return Long.valueOf(((Time)value).getTime()).intValue();
      case VariantType.varTimestamp:
        return Long.valueOf(((Timestamp)value).getTime()).intValue();
      case VariantType.varBoolean:
        return (int)(((Boolean)value).booleanValue() ? ~0 : 0);
      case VariantType.varFloat:
        return ((Float)value).intValue();
      case VariantType.varDouble:
        return ((Double)value).intValue();
      case VariantType.varBigInteger:
        return ((BigInteger)value).intValue();
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).intValueExact();
      case VariantType.varVariant:
        return ((Variant)value).getInteger();
      case VariantType.varString:
        return ((Double)Double.parseDouble((String)value)).intValue();
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Integer)((VariantConnectable)value).castTo(VariantType.varInteger);
        }
        else {
          noCastThrow(value.getClass().getName(), "int");
        }
      default:
        noCastThrow(value.getClass().getName(), "int");
    }
    return 0;
  }
  
  public void setLong(long value) {
    this.valueType = VariantType.varLong;
    this.value = Long.valueOf(value);
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public long getLong() throws VariantException {
    checkNull("long");
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).longValue();
      case VariantType.varShort:
        return ((Short)value).longValue();
      case VariantType.varInteger:
        return ((Integer)value).longValue();
      case VariantType.varLong:
        return ((Long)value).longValue();
      case VariantType.varDate:
        return ((Date)value).getTime();
      case VariantType.varTime:
        return ((Time)value).getTime();
      case VariantType.varTimestamp:
        return ((Timestamp)value).getTime();
      case VariantType.varBoolean:
        return (long)(((Boolean)value).booleanValue() ? ~0 : 0);
      case VariantType.varFloat:
        return ((Float)value).longValue();
      case VariantType.varDouble:
        return ((Double)value).longValue();
      case VariantType.varBigInteger:
        return ((BigInteger)value).longValue();
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).longValue();
      case VariantType.varVariant:
        return ((Variant)value).getLong();
      case VariantType.varString: {
        return ((Double)Double.parseDouble((String)value)).longValue();
      }  
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Long)((VariantConnectable)value).castTo(VariantType.varLong);
        }
        else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
          try {
            Method method = value.getClass().getDeclaredMethod("timestampValue", new Class[] {});
            return ((Timestamp)method.invoke(value, new Object[] {})).getTime();
          } catch (Exception e) {
            throw new VariantException(e);
          }
        }  
        else {
          noCastThrow(value.getClass().getName(), "long");
        }
      default:
        noCastThrow(value.getClass().getName(), "long");
    }
    return 0;
  }
  
  public void setFloat(float value) {
    this.valueType = VariantType.varFloat;
    this.value = Float.valueOf(value);
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public float getFloat() throws VariantException {
    checkNull("float");
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).floatValue();
      case VariantType.varShort:
        return ((Short)value).floatValue();
      case VariantType.varInteger:
        return ((Integer)value).floatValue();
      case VariantType.varLong:
        return ((Long)value).floatValue();
      case VariantType.varDate:
        return Long.valueOf(((Date)value).getTime()).floatValue();
      case VariantType.varTime:
        return Long.valueOf(((Time)value).getTime()).floatValue();
      case VariantType.varTimestamp:
        return Long.valueOf(((Timestamp)value).getTime()).floatValue();
      case VariantType.varBoolean:
        return (float)(((Boolean)value).booleanValue() ? ~0 : 0);
      case VariantType.varFloat:
        return ((Float)value).floatValue();
      case VariantType.varDouble:
        return ((Double)value).floatValue();
      case VariantType.varBigInteger:
        return ((BigInteger)value).floatValue();
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).floatValue();
      case VariantType.varVariant:
        return ((Variant)value).getFloat();
      case VariantType.varString:
        try {
          if (decimalFormat != null) {
            return decimalFormat.parse((String)value).floatValue();
          }
          return Float.parseFloat((String)value);
        }
        catch (Exception e) {
          return Float.parseFloat((String)value);
        }
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Float)((VariantConnectable)value).castTo(VariantType.varFloat);
        }
        else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
          try {
            Method method = value.getClass().getDeclaredMethod("timestampValue", new Class[] {});
            return ((Timestamp)method.invoke(value, new Object[] {})).getTime();
          } catch (Exception e) {
            throw new VariantException(e);
          }
        }  
        else {
          noCastThrow(value.getClass().getName(), "float");
        }
      default:
        noCastThrow(value.getClass().getName(), "float");
    }
    return 0;
  }
  
  public void setDouble(double value) {
    this.valueType = VariantType.varDouble;
    this.value = Double.valueOf(value);
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public double getDouble() throws VariantException {
    checkNull("double");
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).doubleValue();
      case VariantType.varShort:
        return ((Short)value).doubleValue();
      case VariantType.varInteger:
        return ((Integer)value).doubleValue();
      case VariantType.varLong:
        return ((Long)value).doubleValue();
      case VariantType.varDate:
        return Long.valueOf(((Date)value).getTime()).doubleValue();
      case VariantType.varTime:
        return Long.valueOf(((Time)value).getTime()).doubleValue();
      case VariantType.varTimestamp:
        return Long.valueOf(((Timestamp)value).getTime()).doubleValue();
      case VariantType.varBoolean:
        return (double)(((Boolean)value).booleanValue() ? ~0 : 0);
      case VariantType.varFloat:
        return ((Float)value).doubleValue();
      case VariantType.varDouble:
        return ((Double)value).doubleValue();
      case VariantType.varBigInteger:
        return ((BigInteger)value).doubleValue();
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).doubleValue();
      case VariantType.varVariant:
        return ((Variant)value).getDouble();
      case VariantType.varString:
        try {
          if (decimalFormat != null) {
            return decimalFormat.parse((String)value).doubleValue();
          }
          return Double.parseDouble((String)value);
        }
        catch (Exception e) {
          return Double.parseDouble((String)value);
        }
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Double)((VariantConnectable)value).castTo(VariantType.varDouble);
        }
        else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
          try {
            Method method = value.getClass().getDeclaredMethod("timestampValue", new Class[] {});
            return ((Timestamp)method.invoke(value, new Object[] {})).getTime();
          } catch (Exception e) {
            throw new VariantException(e);
          }
        }  
        else {
          noCastThrow(value.getClass().getName(), "double");
        }
      default:
        noCastThrow(value.getClass().getName(), "double");
    }
    return 0;
  }
  
  public void setBoolean(boolean value) {
    this.valueType = VariantType.varBoolean;
    this.value = Boolean.valueOf(value);
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public void setBoolean(long value) {
    setBoolean(value != 0);
  }
  
  public boolean getBoolean() throws VariantException {
    checkNull("boolean");
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).byteValue() != 0;
      case VariantType.varShort:
        return ((Short)value).shortValue() != 0;
      case VariantType.varInteger:
        return ((Integer)value).intValue() != 0;
      case VariantType.varLong:
        return ((Long)value).longValue() != 0;
      case VariantType.varDate:
        return ((Date)value).getTime() != 0;
      case VariantType.varTime:
        return ((Time)value).getTime() != 0;
      case VariantType.varTimestamp:
        return ((Timestamp)value).getTime() != 0;
      case VariantType.varBoolean:
        return ((Boolean)value).booleanValue();
      case VariantType.varFloat:
        return ((Float)value).floatValue() != 0;
      case VariantType.varDouble:
        return ((Double)value).doubleValue() != 0;
      case VariantType.varBigInteger:
        return ((BigInteger)value).longValue() != 0;
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).doubleValue() != 0;
      case VariantType.varVariant:
        return ((Variant)value).getBoolean();
      case VariantType.varString:
        return Boolean.parseBoolean((String)value);
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Boolean)((VariantConnectable)value).castTo(VariantType.varBoolean);
        }
        else {
          noCastThrow(value.getClass().getName(), "boolean");
        }
      default:
        noCastThrow(value.getClass().getName(), "boolean");
    }
    return false;
  }
  
  public void setDate(Date value) {
    this.valueType = VariantType.varDate;
    if (value == null) {
      this.value = null;
    }
    else {
//      this.value = new Date(value.getTime());
      this.value = value;
    }
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public Date getDate() throws VariantException, ParseException {
    checkNull("Date");
    switch(valueType) {
      case VariantType.varByte:
        return new Date(((Byte)value).longValue());
      case VariantType.varShort:
        return new Date(((Short)value).longValue());
      case VariantType.varInteger:
        return new Date(((Integer)value).longValue());
      case VariantType.varLong:
        return new Date(((Long)value).longValue());
      case VariantType.varDate:
        return new Date(((Date)value).getTime());
      case VariantType.varTime:
        return new Date(((Time)value).getTime());
      case VariantType.varTimestamp:
        return new Date(((Timestamp)value).getTime());
      case VariantType.varFloat:
        return new Date(((Float)value).longValue());
      case VariantType.varDouble:
        return new Date(((Double)value).longValue());
      case VariantType.varBigInteger:
        return new Date(((BigInteger)value).longValue());
      case VariantType.varBigDecimal:
        return new Date(((BigDecimal)value).longValue());
      case VariantType.varVariant:
        return ((Variant)value).getDate();
      case VariantType.varString: {
        try {
          if (dateFormat != null) {
            try {
              return dateFormat.parse((String)value);
            }
            catch (ParseException e) {
              return DateFormat.getDateInstance().parse((String)value);
            }
          }
          return DateFormat.getDateInstance().parse((String)value);
        }
        catch (ParseException e) {
          return DateFormat.getDateTimeInstance().parse((String)value);
        }
      }
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Date)((VariantConnectable)value).castTo(VariantType.varDate);
        }
        else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
          try {
            Method method = value.getClass().getDeclaredMethod("timestampValue", new Class[] {});
            return new Date(((Timestamp)method.invoke(value, new Object[] {})).getTime());
          } catch (Exception e) {
            throw new VariantException(e);
          }
        }  
        else {
          noCastThrow(value.getClass().getName(), "date");
        }
      default:
        noCastThrow(value.getClass().getName(), "date");
    }
    return new Date(0);
  }
  
  public void setTime(Time value) {
    this.valueType = VariantType.varTime;
    if (value == null) {
      this.value = null;
    }
    else {
//      this.value = new Time(value.getTime());
      this.value = value;
    }
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public Time getTime() throws VariantException, ParseException {
    checkNull("Time");
    switch(valueType) {
      case VariantType.varByte:
        return new Time(((Byte)value).longValue());
      case VariantType.varShort:
        return new Time(((Short)value).longValue());
      case VariantType.varInteger:
        return new Time(((Integer)value).longValue());
      case VariantType.varLong:
        return new Time(((Long)value).longValue());
      case VariantType.varDate:
        return new Time(((Date)value).getTime());
      case VariantType.varTime:
        return new Time(((Time)value).getTime());
      case VariantType.varTimestamp:
        return new Time(((Timestamp)value).getTime());
      case VariantType.varFloat:
        return new Time(((Float)value).longValue());
      case VariantType.varDouble:
        return new Time(((Double)value).longValue());
      case VariantType.varBigInteger:
        return new Time(((BigInteger)value).longValue());
      case VariantType.varBigDecimal:
        return new Time(((BigDecimal)value).longValue());
      case VariantType.varVariant:
        return ((Variant)value).getTime();
      case VariantType.varString: {
        try {
          if (timeFormat != null) {
            return new Time(timeFormat.parse((String)value).getTime());
          }
          return new Time(DateFormat.getTimeInstance().parse((String)value).getTime());
        }
        catch (ParseException e) {
          return new Time(DateFormat.getDateTimeInstance().parse((String)value).getTime());
        }
      }
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Time)((VariantConnectable)value).castTo(VariantType.varTime);
        }
        else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
          try {
            Method method = value.getClass().getDeclaredMethod("timestampValue", new Class[] {});
            return new Time(((Timestamp)method.invoke(value, new Object[] {})).getTime());
          } catch (Exception e) {
            throw new VariantException(e);
          }
        }  
        else {
          noCastThrow(value.getClass().getName(), "time");
        }
      default:
        noCastThrow(value.getClass().getName(), "time");
    }
    return new Time(0);
  }
  
  public void setTimestamp(Timestamp value) {
    this.valueType = VariantType.varTimestamp;
    if (value == null) {
      this.value = null;
    }
    else {
//      this.value = new Timestamp(value.getTime());
      this.value = value;
    }
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public Timestamp getTimestamp() throws VariantException, ParseException {
    checkNull("Timestamp");
    switch(valueType) {
      case VariantType.varByte:
        return new Timestamp(((Byte)value).longValue());
      case VariantType.varShort:
        return new Timestamp(((Short)value).longValue());
      case VariantType.varInteger:
        return new Timestamp(((Integer)value).longValue());
      case VariantType.varLong:
        return new Timestamp(((Long)value).longValue());
      case VariantType.varDate:
        return new Timestamp(((Date)value).getTime());
      case VariantType.varTime:
        return new Timestamp(((Time)value).getTime());
      case VariantType.varTimestamp:
        return new Timestamp(((Timestamp)value).getTime());
      case VariantType.varFloat:
        return new Timestamp(((Float)value).longValue());
      case VariantType.varDouble:
        return new Timestamp(((Double)value).longValue());
      case VariantType.varBigInteger:
        return new Timestamp(((BigInteger)value).longValue());
      case VariantType.varBigDecimal:
        return new Timestamp(((BigDecimal)value).longValue());
      case VariantType.varVariant:
        return ((Variant)value).getTimestamp();
      case VariantType.varString: {
        try {
          if (timeStampFormat != null) {
            return new Timestamp(timeStampFormat.parse((String)value).getTime());
          }
          return new Timestamp(DateFormat.getDateTimeInstance().parse((String)value).getTime());
        }
        catch (ParseException e) {
          return new Timestamp(DateFormat.getDateInstance().parse((String)value).getTime());
        }
      }
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Timestamp)((VariantConnectable)value).castTo(VariantType.varTimestamp);
        }
        else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
          try {
            Method method = value.getClass().getDeclaredMethod("timestampValue", new Class[] {});
            return (Timestamp)method.invoke(value, new Object[] {});
          } catch (Exception e) {
            throw new VariantException(e);
          }
        }  
        else {
          noCastThrow(value.getClass().getName(), "time");
        }
      default:
        noCastThrow(value.getClass().getName(), "time");
    }
    return new Timestamp(0);
  }
  
  public void setString(byte[] value) {
    this.valueType = VariantType.varString;
    if (value == null) {
      this.value = "";
    }
    else {
      this.value = new String(value);
    }
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public void setString(String value) {
    this.valueType = VariantType.varString;
    if (value == null) {
      this.value = "";
    }
    else {
      this.value = value;
    }
    this.size = resolveSize();
    this.binaryData = null;
  }
  
  public String getString() throws VariantException, IOException {
    if (isNullValue()) {
      return "";
    }
    else {
      switch (valueType) {
        case VariantType.varVariant: 
          return ((Variant)value).getString();
        case VariantType.varDate:
          try {
            if (dateFormat != null) {
              return dateFormat.format(value);
            }
            return defaultDateTimeFormat.format(value);
          } catch (Exception e) {
            return value.toString();
          }
        case VariantType.varTime:
          try {
            if (timeFormat != null) {
              return timeFormat.format(value);
            }
            return defaultTimeFormat.format(value);
          } catch (Exception e) {
            return value.toString();
          }
        case VariantType.varTimestamp:
          try {
            if (timeStampFormat != null) {
              return timeStampFormat.format(value);
            }
            return defaultDateTimeFormat.format(value);
          } catch (Exception e) {
            return value.toString();
          }
        case VariantType.varBinary:
          return new String(binaryData);
        case VariantType.varInputStream:
          return new String(getBinary());
        default:
          return value.toString();
      }
    }
  }
  
  public String toString() {
    try {
      if (isNullValue()) {
        return "";
      }
      else {
        switch (valueType) {
          case VariantType.varVariant: 
            return ((Variant)value).toString();
          case VariantType.varBinary:
          case VariantType.varInputStream:
          case VariantType.varDate:
          case VariantType.varTime:
          case VariantType.varTimestamp:
            return getString();
          case VariantType.varFloat:
          case VariantType.varDouble:
            try {
              if (localDecimalFormat != null) {
                return localDecimalFormat.format(value);
              }
              if (decimalFormat != null) {
                return decimalFormat.format(value);
              }
              return defaultNumberFormat.format(value);
            } catch (Exception e) {
              return value.toString();
            }
          case VariantType.varBigDecimal:
            try {
              if (localDecimalFormat != null) {
                return localDecimalFormat.format(value);
              }
              if (bigDecimalFormat != null) {
                return bigDecimalFormat.format(value);
              }
              return defaultNumberFormat.format(value);
            } catch (Exception e) {
              return value.toString();
            }
          default:
            return value.toString();
        }
      }
    }
    catch (Exception e) {
      ExceptionUtil.processException(e);
      return "";
    }
  }
  
  public void setBigInteger(BigInteger value) {
    this.valueType = VariantType.varBigInteger;
    if (value == null) {
      this.value = null;
    }
    else {
//      this.value = new BigInteger(value.toByteArray());
      this.value = value;
    }
    this.binaryData = null;
    this.size = resolveSize();
  }
  
  public void setBigInteger(long value) {
    this.valueType = VariantType.varBigInteger;
    this.value = BigInteger.valueOf(value);
    this.binaryData = null;
    this.size = resolveSize();
  }
  
  public BigInteger getBigInteger() throws VariantException {
    checkNull("BigInteger");
    switch(valueType) {
      case VariantType.varByte:
      case VariantType.varShort:
      case VariantType.varInteger:
      case VariantType.varLong:
      case VariantType.varFloat:
      case VariantType.varDouble:
      case VariantType.varBigDecimal:
      case VariantType.varString:
        return new BigInteger(value.toString());
      case VariantType.varDate:
        return new BigInteger(String.valueOf(((Date)value).getTime()));
      case VariantType.varTime:
        return new BigInteger(String.valueOf(((Time)value).getTime()));
      case VariantType.varTimestamp:
        return new BigInteger(String.valueOf(((Timestamp)value).getTime()));
      case VariantType.varBoolean:
        return new BigInteger(((Boolean)value).booleanValue() ? "1" : "0");
      case VariantType.varBigInteger:
        return new BigInteger(((BigInteger)value).toByteArray());
      case VariantType.varVariant:
        return ((Variant)value).getBigInteger();
      case VariantType.varBinary:
        return new BigInteger(binaryData);
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (BigInteger)((VariantConnectable)value).castTo(VariantType.varBigInteger);
        }
        else {
          noCastThrow(value.getClass().getName(), "BigInteger");
        }
      default:
        noCastThrow(value.getClass().getName(), "BigInteger");
    }
    return new BigInteger("");
  }

  public void setBigDecimal(BigInteger value) {
    if (value == null) {
      setBigDecimal((BigDecimal)null);
    }
    else {
      setBigDecimal(new BigDecimal(value));
    }
  }

  public void setBigDecimal(BigDecimal value) {
    this.valueType = VariantType.varBigDecimal;
    if (value == null) {
      this.value = null;
    }
    else {
//      this.value = new BigDecimal(value.toString());
      this.value = value;
    }
    this.binaryData = null;
    this.size = resolveSize();
  }
  
  public BigDecimal getBigDecimal() throws VariantException {
    checkNull("BigDecimal");
    switch(valueType) {
      case VariantType.varByte:
        return new BigDecimal((Byte)value);
      case VariantType.varShort:
        return new BigDecimal((Short)value);
      case VariantType.varInteger:
        return new BigDecimal((Integer)value);
      case VariantType.varLong:
        return new BigDecimal((Long)value);
      case VariantType.varFloat:
        return new BigDecimal((Float)value);
      case VariantType.varDouble:
        return new BigDecimal((Double)value);
      case VariantType.varBigDecimal:
        // TODO: Clone!, for now value.toString() is not acceptable
        return (BigDecimal)value;
      case VariantType.varDate:
        return new BigDecimal(((Date)value).getTime());
      case VariantType.varTime:
        return new BigDecimal(((Time)value).getTime());
      case VariantType.varTimestamp:
        return new BigDecimal(((Timestamp)value).getTime());
      case VariantType.varBoolean:
        return new BigDecimal(((Boolean)value).booleanValue() ? "1" : "0");
      case VariantType.varBigInteger:
        return new BigDecimal((BigInteger)value);
      case VariantType.varString:
        try {
          if (localDecimalFormat != null) {
            return new BigDecimal(localDecimalFormat.parse((String)value).doubleValue());
          }
          if (bigDecimalFormat != null) {
            return new BigDecimal(bigDecimalFormat.parse((String)value).doubleValue());
          }
          return new BigDecimal(value.toString());
        }
        catch (Exception e) {
          return new BigDecimal(value.toString());
        }
      case VariantType.varVariant:
        return ((Variant)value).getBigDecimal();
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (BigDecimal)((VariantConnectable)value).castTo(VariantType.varBigDecimal);
        }
        else {
          noCastThrow(value.getClass().getName(), "BigDecimal");
        }
      default:
        noCastThrow(value.getClass().getName(), "BigDecimal");
    }
    return new BigDecimal("");
  }

  public void setList(AbstractCollection<?> value) {
    this.valueType = VariantType.varList;
    this.value = value;
    this.binaryData = null;
    this.size = 0;
  }
  
  public AbstractCollection<?> getList() throws VariantException {
    checkNull("List");
    switch(valueType) {
      case VariantType.varVariant:
        return ((Variant)value).getList();
      case VariantType.varList:
        return (AbstractCollection<?>)value;
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (AbstractCollection<?>)((VariantConnectable)value).castTo(VariantType.varList);
        }
        else {
          noCastThrow(value.getClass().getName(), "List");
        }
      default:
        noCastThrow(value.getClass().getName(), "List");
    }
    return null;
  }
  
  public void setObject(Object value) {
    this.valueType = VariantType.varJavaObject;
    this.value = value;
    this.binaryData = null;
    this.size = resolveSize();
  }
  
  public Object getObject() throws VariantException {
    //if (valueType != VariantType.varBinary) { 
      return value;
//    }
//    else {
//      noCastThrow("binary", "Object");
//    }
//    return null;
  }
  
  public void setBinary(byte[] value) {
    this.valueType = VariantType.varBinary;
    if (value == null) {
      this.value = null;
      this.binaryData = null;
      this.size = resolveSize();
    }
    else {
      this.value = value;
      this.binaryData = value;
      this.size = this.binaryData.length;
    }
  }
  
  public void setBinary(InputStream value) throws IOException {
    this.valueType = VariantType.varBinary;
    this.binaryData = StreamUtil.stream2Array(value);
    this.value = this.binaryData;
    if (this.binaryData != null) {
      this.size = this.binaryData.length;
    }
    else {
      this.size = 0;
    }
  }
  
  public byte[] getBinary() throws VariantException, IOException {
    if (valueType == VariantType.varBinary) { 
      return binaryData;
    }
    else if (valueType == VariantType.varBigInteger) { 
      return ((BigInteger)value).toByteArray();
    }
    else if (valueType == VariantType.varInputStream) {
      byte[] buffer = new byte[this.size];
      ((InputStream)value).read(buffer);
      return buffer;
    }
    else if (valueType == VariantType.varJavaObject) {
      if (value instanceof VariantConnectable) {
        return (byte[])((VariantConnectable)value).castTo(VariantType.varBinary);
      }
      else {
        noCastThrow(value.getClass().getName(), "binary");
      }
    }
    else {
      noCastThrow(value.getClass().getName(), "binary");
    }
    return null;
  }
  
  public void setVariant(Variant value) {
    this.valueType = VariantType.varVariant;
    this.value = value;
    this.binaryData = null;
    this.size = resolveSize();
  }
  
  public Variant getVariant() throws VariantException, IOException {
    switch(valueType) {
      case VariantType.varUnassigned:
      case VariantType.varNull:
        return new Variant((Object)null);
      case VariantType.varByte:
        return new Variant(((Byte)value).byteValue());
      case VariantType.varShort:
        return new Variant(((Short)value).shortValue());
      case VariantType.varInteger:
        return new Variant(((Integer)value).intValue());
      case VariantType.varLong:
        return new Variant(((Long)value).longValue());
      case VariantType.varDate:
        return new Variant((Date)value);
      case VariantType.varTime:
        return new Variant((Time)value);
      case VariantType.varTimestamp:
        return new Variant((Timestamp)value);
      case VariantType.varFloat:
        return new Variant(((Float)value).floatValue());
      case VariantType.varDouble:
        return new Variant(((Double)value).doubleValue());
      case VariantType.varBigDecimal:
        return new Variant((BigDecimal)value);
      case VariantType.varString:
        return new Variant((String)value);
      case VariantType.varBoolean:
        return new Variant(((Boolean)value).booleanValue());
      case VariantType.varBigInteger:
        return new Variant((BigInteger)value);
      case VariantType.varVariant:
        return (Variant)value;
      case VariantType.varInputStream:
        return new Variant((InputStream)value);
      case VariantType.varOutputStream:
        return new Variant((OutputStream)value);
      case VariantType.varBinary:
        return new Variant(getBinary());
      case VariantType.varList:
        return new Variant(value);
      case VariantType.varJavaObject:
        if (value instanceof VariantConnectable) {
          return (Variant)((VariantConnectable)value).castTo(VariantType.varVariant);
        }
//        else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
//          try {
//            Method method = value.getClass().getDeclaredMethod("timestampValue", new Class[] {});
//            return new Variant(method.invoke(value, new Object[] {}));
//          } catch (Exception e) {
//            throw new VariantException(e);
//          }
//        }
        else {
          return new Variant(value);
          //noCastThrow(value.getClass().getName(), "Variant");
        }
      default:
        noCastThrow(value.getClass().getName(), "Variant");
    }
    return new Variant();
  }
  
  public Variant cast(int valueType) throws VariantException, ParseException, IOException {
    if (this.valueType == valueType) {
      return this;
    }
    switch(valueType) {
      case VariantType.varNull:
      case VariantType.varUnassigned:
        setNull();
        break;
      case VariantType.varByte:
        setByte(getByte());
        break;
      case VariantType.varShort:
        setShort(getShort());
        break;
      case VariantType.varInteger:
        setInteger(getInteger());
        break;
      case VariantType.varLong:
        setLong(getLong());
        break;
      case VariantType.varDate:
        setDate(getDate());
        break;
      case VariantType.varTime:
        setTime(getTime());
        break;
      case VariantType.varTimestamp:
        setTimestamp(getTimestamp());
        break;
      case VariantType.varFloat:
        setFloat(getFloat());
        break;
      case VariantType.varDouble:
        setDouble(getDouble());
        break;
      case VariantType.varBigDecimal:
        setBigDecimal(getBigDecimal());
        break;
      case VariantType.varString:
        setString(getString());
        break;
      case VariantType.varBoolean:
        setBoolean(getBoolean());
        break;
      case VariantType.varBigInteger:
        setBigInteger(getBigInteger());
        break;
      case VariantType.varVariant:
        setVariant(getVariant());
        break;
      case VariantType.varBinary:
        setBinary(getBinary());
        break;
      case VariantType.varJavaObject:
        setObject(getObject());
        break;
      case VariantType.varList: {
        ArrayList<Variant> list = new ArrayList<Variant>();
        list.add(getVariant());
        setList(list);
        break;
      }
      default:
        noCastThrow(value.getClass().getName(), "[cast]");
    }
    return this;
  }
  
  public Variant negate() throws VariantException, ParseException, IOException {
    switch(valueType) {
      case VariantType.varByte:
        setByte((byte)-getByte());
        break;
      case VariantType.varShort:
        setShort((short)-getShort());
        break;
      case VariantType.varInteger:
        setInteger(-getInteger());
        break;
      case VariantType.varLong:
        setLong(-getLong());
        break;
      case VariantType.varDate:
        setDate(new Date(-getDate().getTime()));
        break;
      case VariantType.varTime:
        setTime(new Time(-getTime().getTime()));
        break;
      case VariantType.varTimestamp:
        setTimestamp(new Timestamp(-getTimestamp().getTime()));
        break;
      case VariantType.varFloat:
        setFloat(-getFloat());
        break;
      case VariantType.varDouble:
        setDouble(-getDouble());
        break;
      case VariantType.varBigDecimal:
        setBigDecimal(getBigDecimal().negate());
        break;
      case VariantType.varString:
        setBigDecimal(getBigDecimal().negate());
        break;
      case VariantType.varBoolean:
        setBoolean(getBoolean() ? false : true);
        break;
      case VariantType.varBigInteger:
        setBigInteger(getBigInteger().negate());
        break;
      case VariantType.varVariant:
        setVariant(getVariant().negate());
        break;
      default:
        noCastThrow(value.getClass().getName(), "[negate]");
    }
    return this;
  }

  public Variant add(Variant value) throws VariantException, ParseException, IOException {
    if (isNullValue() || value.isNullValue()) {
      setNull();
    }
    else {
      switch(valueType) {
        case VariantType.varByte:
          setLong(getByte() +value.getByte());
          break;
        case VariantType.varShort:
          setLong(getShort() +value.getShort());
          break;
        case VariantType.varInteger:
          setLong(getInteger() +value.getInteger());
          break;
        case VariantType.varLong:
          setDouble(getLong() +value.getLong());
          break;
        case VariantType.varDate:
          setDate(new Date((long)(((((double)getLong() /86400000L) +value.getDouble())) *86400000L)));
          break;
        case VariantType.varTime:
          setTime(new Time((long)(((((double)getLong() /86400000L) +value.getDouble())) *86400000L)));
          break;
        case VariantType.varTimestamp:
          setTimestamp(new Timestamp((long)(((((double)getLong() /86400000L) +value.getDouble())) *86400000L)));
          break;
        case VariantType.varFloat:
          setDouble(getFloat() +value.getFloat());
          break;
        case VariantType.varDouble:
          setBigDecimal(new BigDecimal(getDouble() +value.getDouble()));
          break;
        case VariantType.varString:
        case VariantType.varBigDecimal:
          setBigDecimal(getBigDecimal().add(value.getBigDecimal()));
          break;
        case VariantType.varBoolean:
          setBoolean(getBoolean() | value.getBoolean());
          break;
        case VariantType.varBigInteger:
          setBigInteger(getBigInteger().add(value.getBigInteger()));
          break;
        case VariantType.varVariant:
          setVariant(getVariant().add(value));
          break;
        default:
          noCastThrow(value.getClass().getName(), "[add]");
      }
    }
    return this;
  }

  /**
   * <p>Pozwala odj¹æ wartoœæ podan¹ w value od bierz¹cej wartoœci Variant
   * <p>Dla Date, Timestamp drug¹ wartoœci¹ mo¿e byæ iloœæ dni do odjêcia
   * @param value
   * @return
   * @throws VariantException
   * @throws ParseException
   * @throws IOException
   */
  public Variant subtract(Variant value) throws VariantException, ParseException, IOException {
    if (isNullValue() || value.isNullValue()) {
      setNull();
    }
    else {
      switch(valueType) {
        case VariantType.varByte:
          setByte((byte)(getByte() -value.getByte()));
          break;
        case VariantType.varShort:
          setShort((short)(getShort() -value.getShort()));
          break;
        case VariantType.varInteger:
          setInteger(getInteger() -value.getInteger());
          break;
        case VariantType.varLong:
          setLong(getLong() -value.getLong());
          break;
        case VariantType.varDate:
          setDate(new Date((long)(((((double)getLong() /86400000L) -value.getDouble())) *86400000L)));
          break;
        case VariantType.varTime:
          setTime(new Time((long)(((((double)getLong() /86400000L) -value.getDouble())) *86400000L)));
          break;
        case VariantType.varTimestamp:
          setTimestamp(new Timestamp((long)(((((double)getLong() /86400000L) -value.getDouble())) *86400000L)));
          break;
        case VariantType.varFloat:
          setFloat(getFloat() -value.getFloat());
          break;
        case VariantType.varDouble:
          setDouble(getDouble() -value.getDouble());
          break;
        case VariantType.varString:
        case VariantType.varBigDecimal:
          setBigDecimal(getBigDecimal().subtract(value.getBigDecimal()));
          break;
        case VariantType.varBoolean:
          setBoolean(getBoolean() | value.getBoolean());
          break;
        case VariantType.varBigInteger:
          setBigInteger(getBigInteger().subtract(value.getBigInteger()));
          break;
        case VariantType.varVariant:
          setVariant(getVariant().subtract(value));
          break;
        default:
          noCastThrow(value.getClass().getName(), "[subtract]");
      }
    }
    return this;
  }

  public Variant multiply(Variant value) throws VariantException, ParseException, IOException {
    if (isNullValue() || value.isNullValue()) {
      setNull();
    }
    else {
      switch(valueType) {
        case VariantType.varByte:
          setLong(getByte() *value.getByte());
          break;
        case VariantType.varShort:
          setLong(getShort() *value.getShort());
          break;
        case VariantType.varInteger:
          setLong(getInteger() *value.getInteger());
          break;
        case VariantType.varLong:
          setDouble(getLong() *value.getLong());
          break;
        case VariantType.varDate:
          setDate(new Date(getLong() *value.getLong()));
          break;
        case VariantType.varTime:
          setTime(new Time(getLong() *value.getLong()));
          break;
        case VariantType.varTimestamp:
          setTimestamp(new Timestamp(getLong() *value.getLong()));
          break;
        case VariantType.varFloat:
          setDouble(getFloat() *value.getFloat());
          break;
        case VariantType.varDouble:
          setBigDecimal(new BigDecimal(getDouble() *value.getDouble()));
          break;
        case VariantType.varString:
        case VariantType.varBigDecimal:
          setBigDecimal(getBigDecimal().multiply(value.getBigDecimal()));
          break;
        case VariantType.varBoolean:
          setBoolean(getBoolean() & value.getBoolean());
          break;
        case VariantType.varBigInteger:
          setBigInteger(getBigInteger().multiply(value.getBigInteger()));
          break;
        case VariantType.varVariant:
          setVariant(getVariant().multiply(value));
          break;
        default:
          noCastThrow(value.getClass().getName(), "[multiply]");
      }
    }
    return this;
  }
  
  private void checkZeroDivider(Variant value) throws VariantException {
    if (value.getBigDecimal().equals(new BigDecimal(0))) {
      throw new VariantException(VariantException.ERR_02004_DIVIDE_BY_ZERO);
    }
  }

  public Variant divide(Variant value) throws VariantException, ParseException, IOException {
    if (isNullValue() || value.isNullValue()) {
      setNull();
    }
    else {
      checkZeroDivider(value);
      switch(valueType) {
        case VariantType.varByte:
          setDouble(getByte() /(double)value.getByte());
          break;
        case VariantType.varShort:
          setDouble(getShort() /(double)value.getShort());
          break;
        case VariantType.varInteger:
          setDouble(getInteger() /(double)value.getInteger());
          break;
        case VariantType.varLong:
          setDouble(getLong() /(double)value.getLong());
          break;
        case VariantType.varDate:
          setDate(new Date(getLong() /value.getLong()));
          break;
        case VariantType.varTime:
          setTime(new Time(getLong() /value.getLong()));
          break;
        case VariantType.varTimestamp:
          setTimestamp(new Timestamp(getLong() /value.getLong()));
          break;
        case VariantType.varFloat:
          setFloat(getFloat() /value.getFloat());
          break;
        case VariantType.varDouble:
          setDouble(getDouble() /value.getDouble());
          break;
        case VariantType.varString:
        case VariantType.varBigDecimal:
          setBigDecimal(getBigDecimal().divide(value.getBigDecimal(), 30, RoundingMode.CEILING));
          break;
        case VariantType.varBoolean:
          setBoolean(getBoolean() & value.getBoolean());
          break;
        case VariantType.varBigInteger:
          setBigInteger(getBigInteger().divide(value.getBigInteger()));
          break;
        case VariantType.varVariant:
          setVariant(getVariant().divide(value));
          break;
        default:
          noCastThrow(value.getClass().getName(), "[divide]");
      }
    }
    return this;
  }
  
  public Variant remainder(Variant value) throws VariantException, ParseException, IOException {
    if (isNullValue() || value.isNullValue()) {
      setNull();
    }
    else {
      checkZeroDivider(value);
      switch(valueType) {
        case VariantType.varByte:
          setLong(getByte() % value.getByte());
          break;
        case VariantType.varShort:
          setLong(getShort() % value.getShort());
          break;
        case VariantType.varInteger:
          setLong(getInteger() % value.getInteger());
          break;
        case VariantType.varLong:
          setBigInteger(getLong() % value.getLong());
          break;
        case VariantType.varDate:
          setBigInteger(getLong() % value.getLong());
          break;
        case VariantType.varTime:
          setBigInteger(getLong() % value.getLong());
          break;
        case VariantType.varTimestamp:
          setBigInteger(getLong() % value.getLong());
          break;
        case VariantType.varFloat:
          setDouble(getFloat() % value.getFloat());
          break;
        case VariantType.varDouble:
          setDouble(getDouble() % value.getDouble());
          break;
        case VariantType.varString:
        case VariantType.varBigDecimal:
          setBigDecimal(getBigDecimal().remainder(value.getBigDecimal()));
          break;
        case VariantType.varBoolean:
          setBoolean(getByte() % value.getByte());
          break;
        case VariantType.varBigInteger:
          setBigInteger(getBigInteger().remainder(value.getBigInteger()));
          break;
        case VariantType.varVariant:
          setVariant(getVariant().remainder(value));
          break;
        default:
          noCastThrow(value.getClass().getName(), "[remainder]");
      }
    }
    return this;
  }
  
  public boolean contains(Variant value) throws VariantException, ParseException, IOException {
    if (isNullValue() || value.isNullValue()) {
      return false;
    }
    else {
      if (valueType == VariantType.varList) {
        return getList().contains(value);
      }
      else {
        return compareTo(value) == 0;
      }
    }
  }

  public void setInputStream(InputStream value, int size) {
    this.valueType = VariantType.varInputStream;
    this.value = value;
    this.binaryData = null;
    this.size = size;
  }
  
  public InputStream getInputStream() throws VariantException {
    checkNull("InputStream");
    if (valueType == VariantType.varInputStream) { 
      return (InputStream)value;
    }
    else if (valueType == VariantType.varBinary) {
      return new ByteArrayInputStream(binaryData);
    }
    else {
      noCastThrow(value.getClass().getName(), "InputStream");
    }
    return null;
  }

  public void setOutputStream(OutputStream value, int size) {
    this.valueType = VariantType.varOutputStream;
    this.value = value;
    this.binaryData = null;
    this.size = size;
  }
  
  public OutputStream getOutputStream() throws VariantException {
    checkNull("OutputStream");
    if (valueType == VariantType.varOutputStream) { 
      return (OutputStream)value;
    }
    else {
      noCastThrow(value.getClass().getName(), "OutputStream");
    }
    return null;
  }
  
  public String getValueClassName() {
    if (value != null) {
      return value.getClass().getName();
    }
    return "null";
  }
  
  public void setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  /**
   * <p>Funkcja porównuje varianty i zwraca -1 jeœli variant > this, 0 jeœli ==, 1 jeœli variant < this.
   * <p>Funkcja sprowadza obie wartoœci do najwiêkszego typu.
   * <p>W przypadku wartoœci null, funkcja traktuje null jako 0 lub pusty ci¹g znaków, wiêc jeœli
   * zaistnieje potrzeba (wartoœæ == null) = null to trzeba to zrobiæ na w³asn¹ rêkê. 
   * @param variant
   * @return
   * @throws VariantException
   * @throws ParseException
   * @throws IOException
   */
  public int compareTo(Variant variant) throws VariantException, ParseException, IOException {
    if (isNullValue()) {
      if (variant.isNullValue()) {
        return 0;
      }
      return -1; 
    }
    else if (variant.isNullValue()) {
      if (isNullValue()) {
        return 0;
      }
      return 1;
    }
    else {
      switch(valueType) {
        case VariantType.varByte:
        case VariantType.varShort:
        case VariantType.varInteger:
        case VariantType.varLong:
          return ((Long)getLong()).compareTo(variant.getLong());
        case VariantType.varFloat:
        case VariantType.varDouble:
          return ((Double)getDouble()).compareTo(variant.getDouble());
        case VariantType.varBigDecimal:
          return getBigDecimal().compareTo(variant.getBigDecimal());
        case VariantType.varDate:
          return ((Date)value).compareTo(variant.getDate());
        case VariantType.varTime:
          return ((Time)value).compareTo(variant.getTime());
        case VariantType.varTimestamp:
          return ((Timestamp)value).compareTo(variant.getTimestamp());
        case VariantType.varBoolean:
          return ((Boolean)value).compareTo(variant.getBoolean());
        case VariantType.varBigInteger:
          return ((BigInteger)value).compareTo(variant.getBigInteger());
        case VariantType.varVariant:
          return ((Variant)value).compareTo(variant.getVariant());
        case VariantType.varJavaObject:
        {
          if (ignoreCase) {
            if (value instanceof VariantConnectable) {
              return ((VariantConnectable)value).compareTo(variant);
            }
            else {
              return Integer.signum(toString().compareToIgnoreCase(variant.toString()));
            }
          }
          if (value instanceof VariantConnectable) {
            return ((VariantConnectable)value).compareTo(variant);
          }
          else {
            return Integer.signum(toString().compareTo(variant.toString()));
          }
        }
        case VariantType.varList: {
          if (variant.getValueType() != VariantType.varList) {
            return -1;
          }
          if (getList().size() != variant.getList().size()) {
            return (getList().size() > variant.getList().size() ? 1 : -1); 
          }
          Iterator<?> left = getList().iterator();
          Iterator<?> right = variant.getList().iterator();
          while (left.hasNext()) {
            int retValue = (new Variant(left.next())).compareTo(new Variant(right.next()));
            if (retValue != 0) {
              return retValue;
            }
          }
          return 0;
        }
        case VariantType.varBinary:
        {
          byte[] temp = variant.getBinary();
          if (binaryData.length > temp.length) {
            return 1;
          }
          else if (binaryData.length < temp.length) {
            return -1;
          }
          else {
            for (int i=0; i<temp.length; i++) {
              if (binaryData[i] > temp[i]) {
                return 1;
              }
              else if (binaryData[i] < temp[i]) {
                return -1;
              }
            }
            return 0;
          }
        }
        default:
        {
          if (ignoreCase) {
            return Integer.signum(toString().compareToIgnoreCase(variant.toString()));
          }
          return Integer.signum(toString().compareTo(variant.toString()));
        }
      }
    }
  }
  
  public static void setDateFormat(SimpleDateFormat dateFormat) {
    Variant.dateFormat = dateFormat;
  }

  public static void setDateFormat(String dateFormat) {
    if (StringUtil.isEmpty(dateFormat)) {
      Variant.dateFormat = null;
    }
    else {
      try {
        Variant.dateFormat = new SimpleDateFormat(dateFormat);
      }
      catch (java.lang.IllegalArgumentException e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public static void setTimeFormat(SimpleDateFormat timeFormat) {
    Variant.timeFormat = timeFormat;
  }

  public static void setTimeFormat(String timeFormat) {
    if (StringUtil.isEmpty(timeFormat)) {
      Variant.timeFormat = null;
    }
    else {
      try {
        Variant.timeFormat = new SimpleDateFormat(timeFormat);
      }
      catch (java.lang.IllegalArgumentException e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public static void setTimeStampFormat(SimpleDateFormat timeStampFormat) {
    Variant.timeStampFormat = timeStampFormat;
  }

  public static void setTimeStampFormat(String timeStampFormat) {
    if (StringUtil.isEmpty(timeStampFormat)) {
      Variant.timeStampFormat = null;
    }
    else {
      try {
        Variant.timeStampFormat = new SimpleDateFormat(timeStampFormat);
      }
      catch (java.lang.IllegalArgumentException e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public static void setDecimalFormat(DecimalFormat decimalFormat) {
    Variant.decimalFormat = decimalFormat;
  }

  public static void setDecimalFormat(String decimalFormat) {
    if (StringUtil.isEmpty(decimalFormat)) {
      Variant.decimalFormat = null;
    }
    else {
      try {
        if (Variant.unusualSymbols != null) {
          Variant.decimalFormat = new DecimalFormat(decimalFormat, Variant.unusualSymbols);
        }
        else {
          Variant.decimalFormat = new DecimalFormat(decimalFormat);
        }
      }
      catch (java.lang.IllegalArgumentException e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public static void setBigDecimalFormat(DecimalFormat bigDecimalFormat) {
    Variant.bigDecimalFormat = bigDecimalFormat;
  }

  public static void setBigDecimalFormat(String bigDecimalFormat) {
    if (StringUtil.isEmpty(bigDecimalFormat)) {
      Variant.bigDecimalFormat = null;
    }
    else {
      try {
        if (Variant.unusualSymbols != null) {
          Variant.bigDecimalFormat = new DecimalFormat(bigDecimalFormat, Variant.unusualSymbols);
        }
        else {
          Variant.bigDecimalFormat = new DecimalFormat(bigDecimalFormat);
        }
      }
      catch (java.lang.IllegalArgumentException e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  /**
   * <p>Musi byæ wywo³any przed setDecimalFormat i setBigDecimalFormat by odniós³ skutek.
   * @param decimalSeparator
   * @see setDecimalFormat
   * @see setBigDecimalFormat
   */
  public static void setDecimalSeparator(String decimalSeparator) {
    if (StringUtil.isEmpty(decimalSeparator)) {
      Variant.unusualSymbols = null;
    }
    else {
      Variant.unusualSymbols = new DecimalFormatSymbols();
      Variant.unusualSymbols.setDecimalSeparator(decimalSeparator.charAt(0));
    }
  }

  public static SimpleDateFormat getDateFormat() {
    return dateFormat;
  }

  public static SimpleDateFormat getTimeFormat() {
    return timeFormat;
  }

  public static SimpleDateFormat getTimeStampFormat() {
    return timeStampFormat;
  }

  public static DecimalFormat getDecimalFormat() {
    return decimalFormat;
  }

  public static DecimalFormat getBigDecimalFormat() {
    return bigDecimalFormat;
  }
  
  public static void setDefaultNumberFormatMaximumFractionDigits(int value) {
    defaultNumberFormat.setMaximumFractionDigits(value);
  }
  
  public static int getDefaultNumberFormatMaximumFractionDigits() {
    return defaultNumberFormat.getMaximumFractionDigits();
  }
  
  public static NumberFormat getDefaultNumberFormat() {
    return defaultNumberFormat;
  }

  /**
   * <p>Funkcja analogiczna do compareTo(Variant variant) z obs³ug¹ wyj¹tków.
   * @param o
   * @return
   * @see compareTo(Variant variant)
   */
  public int compareTo(Object o) {
    if (o != null) {
      if ((o instanceof Variant)) {
        try {
          return compareTo((Variant)o);
        }
        catch (Exception e) {
          ExceptionUtil.processException(e);
          return -1;
        }
      }
      else {
        try {
          return compareTo(new Variant(o));
        }
        catch (Exception e) {
          ExceptionUtil.processException(e);
          return -1;
        }
      }
    }
    else {
      return -1;
    }
  }
  
  public boolean equals(Object o) {
    if (o != null) {
      return compareTo(o) == 0;
    }
    else {
      return false;
    }
  }

  public boolean equals(String o) {
    if (o != null) {
      return compareTo(o) == 0;
    }
    else {
      return false;
    }
  }

  public boolean equalsIgnoreCase(Object o) {
    if (o != null) {
      boolean oldIgnoreCase = ignoreCase;
      ignoreCase = true;
      try {
        return compareTo(o) == 0;
      }
      finally {
        ignoreCase = oldIgnoreCase;
      }
    }
    else {
      return false;
    }
  }

  public Object clone() {
    try {
      return getVariant();
    }
    catch (VariantException e) {
      ExceptionUtil.processException(e);
    }
    catch (IOException e) {
      ExceptionUtil.processException(e);
    }
    return new Variant();
  }
  
  public int hashCode() {
    switch(valueType) {
      case VariantType.varByte:
        return ((Byte)value).hashCode();
      case VariantType.varShort:
        return ((Short)value).hashCode();
      case VariantType.varInteger:
        return ((Integer)value).hashCode();
      case VariantType.varLong:
        return ((Long)value).hashCode();
      case VariantType.varDate:
        return ((Date)value).hashCode();
      case VariantType.varTime:
        return ((Time)value).hashCode();
      case VariantType.varTimestamp:
        return ((Timestamp)value).hashCode();
      case VariantType.varFloat:
        return ((Float)value).hashCode();
      case VariantType.varDouble:
        return ((Double)value).hashCode();
      case VariantType.varBigDecimal:
        return ((BigDecimal)value).hashCode();
      case VariantType.varString:
        return ((String)value).hashCode();
      case VariantType.varBoolean:
        return ((Boolean)value).hashCode();
      case VariantType.varBigInteger:
        return ((BigInteger)value).hashCode();
      case VariantType.varVariant:
        return ((Variant)value).hashCode();
      case VariantType.varJavaObject:
        return value.hashCode();
    }
    return 0;
  }

  public static String toString(Variant[] values) {
    StringBuffer result = new StringBuffer("[");
    if (values != null) {
      for (int i=0; i<values.length; i++) {
        if (i > 0) {
          result.append(", ");
        }
        result.append(values[i]);
      }
    }
    return result.append("]").toString();
  }
  
}
