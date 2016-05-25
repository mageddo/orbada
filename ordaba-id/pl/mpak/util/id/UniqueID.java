package pl.mpak.util.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Calendar;

import pl.mpak.util.Assert;
import pl.mpak.util.BitUtil;
import pl.mpak.util.CommaDelimiter;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantConnectable;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Klasa s³u¿y do nadawania unikalnego numeru dowolnemu obiektowi
 * 
 * Unikalny identyfikator sk³ada siê z:
 * YYYYMMDDHHNNSS-TTTTTTTTTTTTTTTT-RRRRRRRR (40chars)
 * YYYY - rok
 * MM   - miesci¹c
 * DD   - dzieñ
 * HH   - godzina
 * NN   - minut
 * SS   - sekund
 * TT.. - czas systemowy "TickCount"
 * RR.. - liczba losowa
 * 
 * Aby pobraæ identyfikator z obiektu tej klasy nale¿y wywo³aæ toString()
 */
public class UniqueID implements VariantConnectable, Cloneable {
  private static final long serialVersionUID = 1770104267023582150L;

  static {
    Variant.registerVariantClass(serialVersionUID, UniqueID.class);
  }
  
  private long time;
  private long tick;
  private int random;

  public UniqueID() {
    super();
    setTime(System.currentTimeMillis());
    setTick(System.nanoTime());
    setRandom((new SecureRandom()).nextInt());
  }
  
  public UniqueID(byte[] bytes) {
    super();
    Assert.notTrue(bytes != null && bytes.length == getSize(), "bytes is null or bytes.length != " +getSize());
    setTime(BitUtil.getLong(bytes, 0));
    setTick(BitUtil.getLong(bytes, 8));
    setRandom(BitUtil.getInt(bytes, 16));
  }
  
  public UniqueID(long time, long tick, int random) {
    super();
    this.setTime(time);
    this.setTick(tick);
    this.setRandom(random);
  }
  
  public UniqueID(String uniqueID) {
    super();
    parse(uniqueID);
  }
  
  public static String create() {
    return new UniqueID().toString();
  }
  
  public String toString() {
    return String.format(
        "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS-%2$016X-%3$08X", 
        new Object[] {time, tick, random});
  }
  
  public void parse(String uniqueID) {
    if (uniqueID == null || uniqueID.length() == 0) {
      throw new NumberFormatException("null or empty");
    }
    
    String time = CommaDelimiter.getCommaString(uniqueID, 1, "-");
    String tick = CommaDelimiter.getCommaString(uniqueID, 2, "-");
    String random = CommaDelimiter.getCommaString(uniqueID, 3, "-");

    // YYYYMMDDHHNNSS-TTTTTTTTTTTTTTTT-RRRRRRRR
    // YYYYMMDDHHNNSS-TTTTTTTTTT-RRRR - zgodnoœæ z wersj¹ w Delphi
    if (time.length() != 14 || 
        tick.length() < 10 || tick.length() > 16 || 
        random.length() < 4 && random.length() > 8) {
      throw new NumberFormatException("illegal length time, tick or random");
    }
    
    Calendar calendar = Calendar.getInstance();
    calendar.set(
        Integer.parseInt(time.substring(0, 4)),
        Integer.parseInt(time.substring(4, 6)),
        Integer.parseInt(time.substring(6, 8)),
        Integer.parseInt(time.substring(8, 10)),
        Integer.parseInt(time.substring(10, 12)),
        Integer.parseInt(time.substring(12, 14)));

    setTime(calendar.getTimeInMillis());
    setTick(Long.parseLong(tick, 16));
    setRandom((int)Long.parseLong(random, 16));
  }

  public void setTime(long time) {
    this.time = time;
  }

  public long getTime() {
    return time;
  }

  public void setTick(long tick) {
    this.tick = tick;
  }

  public long getTick() {
    return tick;
  }

  public void setRandom(int random) {
    this.random = random;
  }

  public int getRandom() {
    return random;
  }

  public void write(DataOutput dop) {
    try {
      dop.writeLong(getTime());
      dop.writeLong(getTick());
      dop.writeInt(getRandom());
    }
    catch (IOException e) {
      ExceptionUtil.processException(e);
    }
  }

  public void read(DataInput dip) {
    try {
      setTime(dip.readLong());
      setTick(dip.readLong());
      setRandom(dip.readInt());
    }
    catch (IOException e) {
      ExceptionUtil.processException(e);
    }
  }

  public int compareTo(Variant variant) {
    if (variant.getValue() instanceof UniqueID) {
      return compareTo((UniqueID)variant.getValue());
    }
    return -1;
  }

  public int getSize() {
    return getStructSize();
  }

  public static int getStructSize() {
    return 8 +8 +4;
  }

  public Object castTo(int valueType) {
    return toString();
  }
  
  public Object clone() {
    return new UniqueID(time, tick, random);
  }
  
  public int hashCode() {
    long value = time *tick *random;
    return (int)(value ^ (value >>> 32));
  }
  
  public boolean equals(Object o) {
    if (o instanceof UniqueID) {
      UniqueID oo = (UniqueID)o;
      return 
        oo.getTime() == time &&
        oo.getTick() == tick &&
        oo.getRandom() == random;
    }
    return false;
  }
  
  public int compareTo(UniqueID anotherID) {
    if (time < anotherID.getTime()) {
      return -1;
    }
    else if (time > anotherID.getTime()) {
      return 1;
    }
    if (tick < anotherID.getTick()) {
      return -1;
    }
    else if (tick > anotherID.getTick()) {
      return 1;
    }
    if (random < anotherID.getRandom()) {
      return -1;
    }
    else if (random > anotherID.getRandom()) {
      return 1;
    }
    return 0;
  }
  
  public static String next() {
    return new UniqueID().toString();
  }

}
