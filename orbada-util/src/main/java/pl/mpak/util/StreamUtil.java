package pl.mpak.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class StreamUtil {

  /**
   * Funkcja pozwala odczytaæ wszystkie dane ze strumienia i wstawiæ je do utworzonego
   * przez funkcjê bufora byte[]
   * 
   * @param strm strumieñ
   * @return odczytane dane w byte[]
   * @throws IOException
   */
  public static byte[] stream2Array(InputStream strm) throws IOException {
    return stream2Array(strm, Long.MAX_VALUE);
  }
  
  public static byte[] stream2Array(InputStream strm, long maxSize) throws IOException {
    if (strm == null) {
      return null;
    }
    
    ArrayList<byte[]> buflist = new ArrayList<byte[]>();
    byte[] buffer = new byte[8096];
    byte[] result = null;
    int readed, pos;
    
    /**
     * odczytujemy strumien i wstawiamy bufory do listy byte[]
     */
    try {
      while ((readed=strm.read(buffer,0,8096)) >= 0) {
        byte[] tbf = new byte[readed];
        System.arraycopy(buffer,0,tbf,0,readed);
        buflist.add(tbf);
        if ((maxSize -= readed) < 0) {
          break;
        }
      }
    }
    finally {
      strm.close();
    }
    
    /**
     * zliczamy rozmiar bufora który zostanie utworzony i zwrócony
     */
    readed = 0;
    for (int i=0; i<buflist.size(); i++) {
      readed+=buflist.get(i).length;
    }
    
    /**
     * tworzymy bufor i wype³niamy go odczytanymi danymi
     */
    if (readed > 0) {
      pos=0;
      result = new byte[readed];    
      for (int i=0; i<buflist.size(); i++) {
        byte[] b = buflist.get(i);
        System.arraycopy(b,0,result,pos,b.length);
        pos+=b.length;
      }
      buflist.clear();
    }
    

    return result;
  }
 
  /**
   * Funkcja odczytuje ze strumienia dane i wstawia je do ci¹gu znaków
   * 
   * @param inputStream
   * @return ci¹g znaków odczytany ze strumienia
   * @throws IOException
   */
  public static String stream2String(InputStream inputStream, String charset) throws IOException {
    int read;
    StringBuilder sb = new StringBuilder();
    
    InputStreamReader isr = new InputStreamReader(inputStream, charset);
    try {
      while ((read=isr.read()) != -1) {
        sb.append((char)read);
      }
    }
    finally {
      isr.close();
    }
    return sb.toString();
  }
  
  public static String stream2String(InputStream inputStream) throws IOException {
    return stream2String(inputStream, Charset.defaultCharset().name());
  }
  
}
