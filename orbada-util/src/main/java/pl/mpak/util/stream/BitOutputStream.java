package pl.mpak.util.stream;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream {

  /**
   * Count of bits written
   */
  private int bitCount = 0;

  /**
   * Buffer to store one byte's worth of data
   */
  private int buffer = 0;

  /**
   * Mask for the bits that are unwritten in the buffer.
   */
  private int mask = 0xFF;

  /**
   * The count of the number of unused bits in the current byte.
   */
  private int bitsLeft = 8;

  /**
   * The underlying output stream to which bytes are written
   */
  private final OutputStream out;

  /**
   * Create BitOutputStream to write boolean data to the given output stream.
   * 
   * @param out
   *          OutputStream to which boolean data is written.
   */
  public BitOutputStream(OutputStream out) {
    this.out = out;
  }

  /**
   * Write any pending bits to the output.
   * 
   * @exception IOException
   *              If an I/O Error occurs.
   */
  public synchronized void flush() throws IOException {
    // Do nothing if no bits are pending in the buffer
    if (bitsLeft < 8) {
      // Write the buffer to the output
      out.write(buffer);

      // Set the mask for the start of next byte
      mask = 0xFF;

      // Clear the buffer
      buffer = 0;

      // Round the bitCount up to the next multiple of a byte
      bitCount += bitsLeft;

      // Set for 8 bits left in the next byte.
      bitsLeft = 8;
    }
  }

  /**
   * Get the number of bits written already.
   * 
   * @return Number of bits written.
   */
  public synchronized int getBitCount() {
    return bitCount;
  }

  /**
   * Write some of the bits from the given value to the output stream. The least
   * significant bits of the given value are written.
   * 
   * @param value
   *          The value containing the bits to be written.
   * @param bits
   *          The number of bits to write.
   * @exception IOException
   *              If an I/O Error occurs.
   */
  public synchronized void write(int value, int bits) throws IOException {
    // Loop for all complete bytes to be written out
    while (bits >= bitsLeft) {
      // Decrease the number of bits left to write
      bits -= bitsLeft;

      // Shift it and merge into the buffer
      buffer |= (value >>> bits) & mask;

      // Keep track of the number of bits written
      bitCount += bitsLeft;

      // No more bits left in this byte
      bitsLeft = 0;

      // Write it out
      flush();
    }

    // Now we are left with not enough bits to fill the byte.
    // Check to see if we have any more bits to write.
    if (bits > 0) {
      // Decrease the number of bits left in this byte.
      bitsLeft -= bits;

      // Keep track of number of bits written
      bitCount += bits;

      // Shift the bits and merge them with the byte.
      buffer |= (value << bitsLeft) & mask;

      // Shift the mask to write to the next bit
      mask >>= bits;
    }
  }

  /**
   * Write a boolean value to the output stream.
   * 
   * @param bit
   *          boolean value to be written.
   * @exception IOException
   *              If an I/O Error occurs.
   */
  public void write(boolean bit) throws IOException {
    int value = 0;

    if (bit) {
      value = 1;
    }

    write(value, 1);
  }

}
