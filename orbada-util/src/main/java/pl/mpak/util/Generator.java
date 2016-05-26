package pl.mpak.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantConnectable;

public class Generator implements VariantConnectable {
  private static final long serialVersionUID = 213009037879959109L;

  public final static int UTL_01001_NOT_INITED = 1001;
  public final static int UTL_01002_CANT_READ = 1002;
  public final static int UTL_01003_CANT_WRITE = 1003;
  
  private long nextVal;
  private long currVal;
  private long maxVal;
  private long startVal;
  private long increment;
  private boolean inited = false;
  
  public Generator(long startVal) {
    super();
    nextVal = startVal;
    maxVal = Long.MAX_VALUE;
    this.startVal = startVal;
    increment = 1;
  }
  
  public Generator() {
    this(1);
  }

  public long getNextVal() {
    synchronized (this) {
      inited = true;
      currVal = nextVal;
      nextVal+=increment;
      if (nextVal > maxVal) {
        nextVal = startVal;
      }
      return currVal;
    }
  }
  
  public void setNextVal(long nextVal) {
    synchronized (this) {
      inited = false;
      this.nextVal = nextVal;
      if (this.nextVal > maxVal) {
        this.nextVal = startVal;
      }
    }
  }
  
  public long getCurrVal() throws GeneratorException {
    synchronized (this) {
      if (!inited) {
        throw new GeneratorException(UTL_01001_NOT_INITED);
      }
      return currVal;
    }
  }

  public void setMaxVal(long maxVal) {
    synchronized (this) {
      this.maxVal = maxVal;
    }
  }

  public long getMaxVal() {
    return maxVal;
  }

  public void setStartVal(long startVal) {
    synchronized (this) {
      this.startVal = startVal;
    }
  }

  public long getStartVal() {
    return startVal;
  }

  public void setIncrement(long increment) {
    synchronized (this) {
      this.increment = increment;
    }
  }

  public long getIncrement() {
    return increment;
  }

  public void write(DataOutput raf) {
    try {
      raf.writeLong(nextVal);
      raf.writeLong(maxVal);
      raf.writeLong(startVal);
      raf.writeLong(increment);
    }
    catch (IOException e) {
      ExceptionUtil.processException(new GeneratorException(UTL_01003_CANT_WRITE, e));
    }
  }

  public void read(DataInput raf) {
    try {
      inited = false;
      nextVal = raf.readLong();
      maxVal = raf.readLong();
      startVal = raf.readLong();
      increment = raf.readLong();
    }
    catch (IOException e) {
      ExceptionUtil.processException(new GeneratorException(UTL_01002_CANT_READ, e));
    }
  }

  public int compareTo(Variant variant) {
    return 0;
  }

  public int getSize() {
    return 8 +8 +8 +8;
  }

  public Object castTo(int valueType) {
    return null;
  }
  
  public String toString() {
    return
      "[nextVal:" +nextVal +", " +
      "currVal:" +currVal +", " +
      "maxVal:" +maxVal +", " +
      "startVal:" +startVal +", " +
      "increment:" +increment +"]";
  }

}
