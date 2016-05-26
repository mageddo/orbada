package pl.mpak.util.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Andrzej Ka³u¿a
 * 
 * <p>Jest to klasa, która rejestruje wszystkie zapisy i odczyty.
 * <p>Jest to nak³adka na RandomAccessFile.
 *
 */
public class RandomAccessFile extends java.io.RandomAccessFile {

  private long readBytes;
  private long readBlocks;
  private long writeBytes;
  private long writeBlocks;
  
  public RandomAccessFile(String name, String mode) throws FileNotFoundException {
    super(name, mode);
  }

  public RandomAccessFile(File file, String mode)
      throws FileNotFoundException {
    super(file, mode);
  }
  
  public int read(byte b[], int off, int len) throws IOException {
    readBytes+=b.length;
    readBlocks++;
    return super.read(b, off, len);
  }
  
  public void write(byte b[], int off, int len) throws IOException {
    writeBytes+=b.length;
    writeBlocks++;
    super.write(b, off, len);
  }
  
  public int read() throws IOException {
    readBytes+=1;
    readBlocks++;
    return super.read();
  }
  
  public void write(int b) throws IOException {
    writeBytes+=1;
    writeBlocks++;
    super.write(b);
  }
  
  public void setLength(long newLength) throws IOException {
    writeBytes+=(newLength -length());
    writeBlocks++;
    super.setLength(newLength);
  }

  public long getReadBytes() {
    return readBytes;
  }

  public long getReadBlocks() {
    return readBlocks;
  }

  public long getWriteBytes() {
    return writeBytes;
  }

  public long getWriteBlocks() {
    return writeBlocks;
  }

}
