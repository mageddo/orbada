/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util;

import java.math.BigInteger;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.GeneratorErrorMessages;
import pl.mpak.orbada.plugins.GeneratorException;
import pl.mpak.orbada.plugins.IGenerator;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.TaskUtil;

/**
 *
 * @author akaluza
 */
public class Generator implements IGenerator {

  private String name;
  private BigInteger currValue;
  private BigInteger startValue;
  private BigInteger minValue;
  private BigInteger maxValue;
  private BigInteger increment;
  private Boolean cycle;
  
  public Generator(String name, BigInteger startValue, BigInteger minValue, BigInteger maxValue, BigInteger increment, Boolean cycle) throws GeneratorException {
    this.name = name;
    this.startValue = startValue == null ? new BigInteger("1") : startValue;
    this.minValue = minValue == null ? new BigInteger("1") : minValue;
    this.maxValue = maxValue == null ? new BigInteger("99999999999999999999") : maxValue;
    this.increment = increment == null ? new BigInteger("1") : increment;
    this.cycle = cycle == null ? false : cycle;
    init();
  }
  
  private void init() throws GeneratorException {
    try {
      pl.mpak.orbada.db.Generator generator = new pl.mpak.orbada.db.Generator(InternalDatabase.get(), name);
      if (generator.isExists()) {
        this.minValue = new BigInteger(generator.getMinValue());
        this.maxValue = new BigInteger(generator.getMaxValue());
        this.increment = new BigInteger(generator.getIncrement());
        this.cycle = generator.isCycle();
      }
      else {
        generator.setName(name);
        if (startValue != null) {
          generator.setValue(startValue.toString());
        }
        if (minValue != null) {
          generator.setMinValue(minValue.toString());
        }
        if (maxValue != null) {
          generator.setMaxValue(maxValue.toString());
        }
        if (increment != null) {
          generator.setIncrement(increment.toString());
        }
        if (cycle != null) {
          generator.setCycle(cycle);
        }
        generator.applyInsert();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      throw new GeneratorException(GeneratorErrorMessages.OGEN_01001_INIT, new Object[] {name});
    }
  }
  
  public String getName() {
    return name;
  }
  
  private void lock() throws GeneratorException {
    int counter = 100;
    boolean getted = false;
    try {
      Command locked = InternalDatabase.get().createCommand();
      locked.setSqlText("update generators set gen_locked = 'T' where gen_name = :gen_name and case when gen_locked is null then 'N' else gen_locked end = 'N'");
      locked.paramByName("gen_name").setString(name);
      while (!getted) {
        if (--counter == 0) {
          break;
        }
        locked.execute();
        if (!(getted = locked.getUpdateCount() >= 1)) {
          TaskUtil.sleep(10);
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      throw new GeneratorException(GeneratorErrorMessages.OGEN_01002_NEXT_VALUE, new Object[] {name});
    }
    if (counter == 0) {
      throw new GeneratorException(GeneratorErrorMessages.OGEN_01002_NEXT_VALUE, new Object[] {name});
    }
  }
  
  private void unlock() {
    try {
      Command unlock = InternalDatabase.get().createCommand();
      unlock.setSqlText("update generators set gen_locked = null where gen_name = :gen_name");
      unlock.paramByName("gen_name").setString(name);
      unlock.execute();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  private BigInteger getValue() throws GeneratorException {
    Query query = InternalDatabase.get().createQuery();
    try {
      query.setSqlText("select gen_value from generators where gen_name = :gen_name");
      query.paramByName("gen_name").setString(name);
      query.open();
      return query.fieldByName("gen_value").getBigDecimal().toBigInteger();
    } catch (Exception ex) {
      throw new GeneratorException(GeneratorErrorMessages.OGEN_01002_NEXT_VALUE, new Object[] {name});
    } finally {
      query.close();
    }
  }
  
  private BigInteger getNextValue(BigInteger currValue) throws GeneratorException {
    BigInteger next = new BigInteger(currValue.toString());
    next = next.add(increment);
    if (cycle) {
      if (next.compareTo(maxValue) > 0) {
        next = new BigInteger(minValue.toString());
      }
      else if (next.compareTo(minValue) < 0) {
        next = new BigInteger(maxValue.toString());
      }
    }
    else if (next.compareTo(maxValue) > 0 || next.compareTo(minValue) < 0) {
      throw new GeneratorException(GeneratorErrorMessages.OGEN_01004_MIN_MAX_VALUE, new Object[] {name});
    }
    return next;
  }
  
  private void updateValue(BigInteger value) throws GeneratorException {
    try {
      Command update = InternalDatabase.get().createCommand();
      update.setSqlText("update generators set gen_value = :gen_value where gen_name = :gen_name");
      update.paramByName("gen_name").setString(name);
      update.paramByName("gen_value").setString(value.toString());
      update.execute();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      throw new GeneratorException(GeneratorErrorMessages.OGEN_01002_NEXT_VALUE, new Object[] {name});
    }
  }

  public BigInteger getNextValue() throws GeneratorException {
    lock();
    try {
      currValue = getValue();
      BigInteger next = getNextValue(currValue);
      updateValue(next);
    }
    finally {
      unlock();
    }
    return getCurrValue();
  }

  public BigInteger getCurrValue() throws GeneratorException {
    if (currValue == null) {
      throw new GeneratorException(GeneratorErrorMessages.OGEN_01003_NO_CURR_VALUE, new Object[] {name});
    }
    return new BigInteger(currValue.toString());
  }

  public BigInteger getMinValue() {
    return new BigInteger(minValue.toString());
  }

  public BigInteger getMaxValue() {
    return new BigInteger(maxValue.toString());
  }

  public BigInteger getIncrement() {
    return new BigInteger(increment.toString());
  }

  public boolean getCycle() {
    return cycle;
  }

}
