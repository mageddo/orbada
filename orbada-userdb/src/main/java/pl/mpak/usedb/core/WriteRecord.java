package pl.mpak.usedb.core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import pl.mpak.util.stream.BufferedOutputStream;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import sun.security.util.BitArray;

public final class WriteRecord {

  ArrayList<Variant> fields = new ArrayList<Variant>();
  BufferedOutputStream baos = new BufferedOutputStream();
  DataOutputStream dos = new DataOutputStream(baos);

  public WriteRecord() {
    super();
  }

  /**
   * <p> Tworzy WriteRecord z pustymi polami do wype³nienia 
   * @param fieldCount
   */
  public WriteRecord(int fieldCount) {
    this();
    for (int i=fieldCount; --i>=0; ) {
      add(new Variant());
    }
  }
  
  /**
   * <p> Tworzy obiekt i przepisuje do niego wartoœci z CacheRecord
   * @param cr
   * @see CacheRecord
   */
  public WriteRecord(CacheRecord cr) {
    this();
    for (int i=0; i<cr.fieldCount(); i++) {
      add(cr.getField(i).getValue());
    }
  }

  public void clear() {
    fields.clear();
  }

  public Variant getField(int index) {
    return fields.get(index);
  }

  public int fieldCount() {
    return fields.size();
  }

  public void add(Variant value) {
    fields.add(value);
  }

  public Variant add() {
    Variant result = new Variant();
    add(result);
    return result;
  }
  
   public long write(RandomAccessFile raf) throws IOException, VariantException {
     raf.seek(raf.length());
     long result = raf.getFilePointer();
        
     dos.writeShort(fieldCount());
     
     BitArray ba = new BitArray(fieldCount());
     for (int i=0; i<fieldCount(); i++) {
       ba.set(i, fields.get(i).isNullValue());
     }
     dos.write(ba.toByteArray());
     
     for (int i=0; i<fieldCount(); i++) {
       if (!ba.get(i)) {
         fields.get(i).write(dos);
       }
     }
     // write buffer (record) size before fieldCount
     raf.writeInt(baos.size());
     raf.write(baos.getBuffer(), 0, baos.size());
     baos.reset();
     return result;
   }

//  public synchronized long write(RandomAccessFile raf) throws IOException, VariantException {
//    raf.seek(raf.length());
//    long result = raf.getFilePointer();
//    int fc = fieldCount();
//    raf.writeShort(fc);
//    for (int i = 0; i < fc; i++) {
//      getField(i).write(raf);
//    }
//    return result;
//  }

}
