package pl.mpak.util.rac;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessContentFile implements RandomAccessContent {

  private RandomAccessFile raf;
  
  public RandomAccessContentFile(RandomAccessFile raf) {
    this.raf = raf;
  }

  public RandomAccessContentFile(String fileName, RandomAccessMode ram) throws FileNotFoundException {
    this(new RandomAccessFile(fileName, ram.toString()));
  }

  public RandomAccessContentFile(File file, RandomAccessMode ram) throws FileNotFoundException {
    this(new RandomAccessFile(file, ram.toString()));
  }

  public void close() throws IOException {
    raf.close();
  }

  public long getFilePointer() throws IOException {
    return raf.getFilePointer();
  }

  public long length() throws IOException {
    return raf.length();
  }

  public void seek(long pos) throws IOException {
    raf.seek(pos);
  }

  public void write(int b) throws IOException {
    raf.write(b);
  }

  public void write(byte[] b) throws IOException {
    raf.write(b);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    raf.write(b, off, len);
  }

  public void writeBoolean(boolean v) throws IOException {
    raf.writeBoolean(v);
  }

  public void writeByte(int v) throws IOException {
    raf.writeByte(v);
  }

  public void writeBytes(String s) throws IOException {
    raf.writeBytes(s);
  }

  public void writeChar(int v) throws IOException {
    raf.writeChar(v);
  }

  public void writeChars(String s) throws IOException {
    raf.writeChars(s);
  }

  public void writeDouble(double v) throws IOException {
    raf.writeDouble(v);
  }

  public void writeFloat(float v) throws IOException {
    raf.writeFloat(v);
  }

  public void writeInt(int v) throws IOException {
    raf.writeInt(v);
  }

  public void writeLong(long v) throws IOException {
    raf.writeLong(v);
  }

  public void writeShort(int v) throws IOException {
    raf.writeShort(v);
  }

  public void writeUTF(String s) throws IOException {
    raf.writeUTF(s);
  }

  public boolean readBoolean() throws IOException {
    return raf.readBoolean();
  }

  public byte readByte() throws IOException {
    return raf.readByte();
  }

  public char readChar() throws IOException {
    return raf.readChar();
  }

  public double readDouble() throws IOException {
    return raf.readDouble();
  }

  public float readFloat() throws IOException {
    return raf.readFloat();
  }

  public void readFully(byte[] b) throws IOException {
    raf.readFully(b);
  }

  public void readFully(byte[] b, int off, int len) throws IOException {
    raf.readFully(b, off, len);
  }

  public int readInt() throws IOException {
    return raf.readInt();
  }

  public String readLine() throws IOException {
    return raf.readLine();
  }

  public long readLong() throws IOException {
    return raf.readLong();
  }

  public short readShort() throws IOException {
    return raf.readShort();
  }

  public String readUTF() throws IOException {
    return raf.readUTF();
  }

  public int readUnsignedByte() throws IOException {
    return raf.readUnsignedByte();
  }

  public int readUnsignedShort() throws IOException {
    return raf.readUnsignedShort();
  }

  public int skipBytes(int n) throws IOException {
    return raf.skipBytes(n);
  }

  public void setLength(long newLength) throws IOException {
    raf.setLength(newLength);
  }

}
