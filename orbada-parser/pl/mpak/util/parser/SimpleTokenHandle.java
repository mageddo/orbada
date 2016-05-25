package pl.mpak.util.parser;

import java.io.IOException;

public class SimpleTokenHandle implements TokenHandle {

  public final static int NONE = 0;
  public final static int BLANK = 1;
  public final static int SYMBOL = 2;
  public final static int STRING = 3;
  public final static int NUMBER = 4;
  public final static int CHAR = 5;
  public final static int COMMENT = 6;
  
  public SimpleTokenHandle() {
    super();
  }

  public int readToken(Tokenizer parser, StringBuffer tokenBuffer) throws IOException {
    int token = NONE;

    if (parser.readedCh() <= 32 && !parser.isEof()) {
      token = BLANK;
      do {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      } while (parser.readedCh() <= 32 && !parser.isEof());
    }
    else if (parser.areReadedCh("\"'")) {
      int ch = parser.readedCh();
      token = STRING;
      parser.readCh();
      while (!parser.isEof()) {
        if (parser.readedCh() == ch) {
          parser.readCh();
          break;
        }
        else if (parser.isReadedCh('\\')) {
          parser.readCh();
          tokenBuffer.append((char)parser.readedCh());
        }
        else {
          tokenBuffer.append((char)parser.readedCh());
        }
        parser.readCh();
      }
    }
    else if (Character.isJavaIdentifierStart(parser.readedCh()) && parser.readedCh() != 0) {
      token = SYMBOL;
      do {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      } while (parser.readedCh() != 0 && Character.isJavaIdentifierPart(parser.readedCh()));
    }
    else if (Character.isDigit(parser.readedCh()) || parser.isReadedCh('-')) {
      if (parser.isReadedCh('-')) {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
        if (!Character.isDigit(parser.readedCh())) {
          token = CHAR;
        }
      }
      if (token == NONE) {
        token = NUMBER;
        do {
          tokenBuffer.append((char)parser.readedCh());
          parser.readCh();
        } while (Character.isDigit(parser.readedCh()) || parser.areReadedCh(".-+eE"));
      }
    }
    else if (parser.isReadedCh('/')) {
      int ch = parser.readedCh();
      parser.readCh();
      if (parser.isReadedCh('*')) {
        token = COMMENT;
        parser.readCh();
        while (!parser.isEof()) {
          if (parser.isReadedCh('*')) {
            ch = parser.readedCh(); 
            parser.readCh();
            if (parser.isReadedCh('/')) {
              parser.readCh();
              break;
            }
            else {
              tokenBuffer.append((char)ch);
            }
          }
          else {
            tokenBuffer.append((char)parser.readedCh());
            parser.readCh();
          }
        }
      }
      else {
        token = CHAR;
        tokenBuffer.append((char)ch);
      }
    }
    else {
      token = CHAR;
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
    }
    
    return token;
  }

}
