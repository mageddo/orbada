package pl.mpak.usedb.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import pl.mpak.usedb.UseDBException;
import pl.mpak.util.variant.VariantException;
import sun.security.util.BitArray;

public final class CacheRecord {
  private ArrayList<CacheField> fields = new ArrayList<CacheField>();
  private int index;
  private Long time;
  private Query query;
  
  public CacheRecord(Query query, int index) {
    super();
    this.index = index;
    this.query = query;
    updateTime();
  }

  public CacheRecord(int index) {
    super();
    this.index = index;
    updateTime();
  }

  public CacheRecord() {
    this( -1 );
  }

  public void clear() {
    fields.clear();
  }
  
  public boolean isChanged() {
    for (int i=0; i<fields.size(); i++) {
      if (fields.get(i).isChanged()) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isNew() {
    return getIndex() == -1;
  }
  
  /**
   * <p> Zatwierdza zmiany dokonane w pamiêci - nie w bazie danych
   * @throws IOException 
   * @throws VariantException 
   */
  public void applyUpdates() {
    if (isChanged()) {
      for (int i=0; i<fields.size(); i++) {
        fields.get(i).apply();
      }
    }
  }
  
  /**
   * <p> Anuluje zmiany dokonane w pamiêci - nie w bazie danych
   */
  public void cancelUpdates() {
    for (int i=0; i<fields.size(); i++) {
      fields.get(i).cancel();
    }
  }
  
  public CacheField getField(int index) {
    return fields.get(index);
  }
  
  public CacheField getField(String fieldName) throws UseDBException {
    if (query != null) {
      QueryField qf = query.fieldByName(fieldName);
      return getField(qf.getIndex() -1);
    }
    return null;
  }
  
  public int fieldCount() {
    return fields.size();
  }
  
  public CacheField add(CacheField value) {
    fields.add(value);
    return value;
  }
  
  public CacheField add() {
    return add(new CacheField());
  }
  
  public void read(RandomAccessFile raf, long pos) throws IOException, VariantException {
    raf.seek(pos);
    int size = raf.readInt();
    byte[] buffer = new byte[size];
    raf.read(buffer);
    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buffer));
    try {
      int count = dis.readShort();
      byte[] bba = new byte[(count +8 -1) /8];
      dis.read(bba);
      BitArray ba = new BitArray(count, bba);
      for (int i=0; i<count; i++) {
        CacheField f = add();
        if (!ba.get(i)) {
          f.read(dis);
        }
      }
    }
    finally {
      dis.close();
    }
  }
  
//  public synchronized void read(RandomAccessFile raf, long pos) throws IOException, VariantException {
//    raf.seek(pos);
//    // read record size
//    int size = raf.readInt();
//    byte[] buffer = new byte[size];
//    raf.read(buffer);
//    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buffer));
//    try {
//      int count = dis.readShort();
//      for (int i=0; i<count; i++) {
//        add().read(dis);
//      }
//    }
//    finally {
//      dis.close();
//    }
//  }
  
  public int getIndex() {
    return this.index;
  }
  
  void setIndex(int index) {
    this.index = index;
  }
  
  public long getTime() {
    return time;
  }
  
  void updateTime() {
    this.time = System.currentTimeMillis();
  }

  public Query getQuery() {
    return query;
  }

}
