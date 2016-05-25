package pl.mpak.util.rac;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author akaluza
 *
 */
public interface RandomAccessContent extends DataOutput, DataInput
{
    public long getFilePointer() throws IOException;

    public void seek(long pos) throws IOException;

    public long length() throws IOException;
    
    public void setLength(long newLength) throws IOException;

    public void close() throws IOException;

}
