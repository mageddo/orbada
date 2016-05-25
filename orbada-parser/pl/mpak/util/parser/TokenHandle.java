package pl.mpak.util.parser;

import java.io.IOException;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Interfejs do tworzenia tokenu
 * W nim powinna nast¹piæ analiza jednego tokenu i wstawienie go do tokenBuffer
 * 
 * Przyk³ad: SimpleTokenHandle
 *
 */
public interface TokenHandle {

  public final static int NONE = 0;
  
  public int readToken(Tokenizer parser, StringBuffer tokenBuffer) throws IOException;
  
}
